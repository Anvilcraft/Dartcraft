package ley.modding.dartcraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.AccessLevel;
import ley.modding.dartcraft.client.IconDirectory;
import ley.modding.dartcraft.client.gui.GuiType;
import ley.modding.dartcraft.tile.TileEntityMachine;
import ley.modding.dartcraft.tile.TileEntityStorageUnit;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.FXUtils;
import ley.modding.dartcraft.util.Util;
import ley.modding.tileralib.api.ITEProvider;
import net.anvilcraft.anvillib.vector.Vec3;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockStorageUnit extends Block implements ITEProvider {
    public static final int STORAGE_META = 0;
    public static final int FRAME_META = 16;

    public BlockStorageUnit() {
        super(Material.rock);
        this.setHardness(2.0f);
        this.setResistance(25.0f);
        this.setStepSound(Block.soundTypeStone);
        this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.875f, 0.9375f);
        Util.configureBlock(this, "storage");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 22;
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntityMachine createTileEntity(World world, int meta) {
        if (world != null && meta == 0) {
            return new TileEntityStorageUnit();
        }
        return null;
    }

    @Override
    public ArrayList<ItemStack>
    getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<>();
    }

    @Override
    public int onBlockPlaced(
        World world,
        int x,
        int y,
        int z,
        int side,
        float hitX,
        float hitY,
        float hitZ,
        int meta
    ) {
        int type = 0;
        if (meta >= 0 && meta < 16) {
            type = 0;
        }
        if (meta >= 16) {
            type = 1;
        }
        return type;
    }

    @Override
    public void onBlockPlacedBy(
        World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack
    ) {
        ForgeDirection facing = DartUtils.getEntityFacing((Entity) entity);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null) {
            if (tile instanceof TileEntityMachine) {
                TileEntityMachine machine = (TileEntityMachine) tile;
                machine.color = stack.getItemDamage() % 16;
                machine.facing = facing;
                if (entity instanceof EntityPlayer) {
                    machine.owner = entity.getUniqueID();
                    machine.access = AccessLevel.OPEN;
                }
            }
            if (stack != null && stack.hasTagCompound()) {
                TileEntity stored = TileEntity.createAndLoadEntity((NBTTagCompound
                ) stack.getTagCompound());
                if (stored != null) {
                    world.setTileEntity(x, y, z, stored);
                }
                TileEntityMachine machine = (TileEntityMachine) stored;
                machine.facing = facing;
                if (entity instanceof EntityPlayer) {
                    machine.owner = entity.getUniqueID();
                }
            }
        }
        new WorldVec(world, x, y, z).markForUpdate();
    }

    @Override
    public boolean onBlockActivated(
        World world,
        int x,
        int y,
        int z,
        EntityPlayer player,
        int par6,
        float par7,
        float par8,
        float par9
    ) {
        if (player != null
            && (player.isSneaking() || DartUtils.isHoldingWrench(player))) {
            return false;
        }
        if (!Dartcraft.proxy.isSimulating(world)) {
            return true;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TileEntityStorageUnit) {
            player.openGui(Dartcraft.instance, GuiType.STORAGE.ordinal(), world, x, y, z);
            return true;
        }
        return true;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        TileEntityMachine machine = (TileEntityMachine) world.getTileEntity(x, y, z);
        int facing = machine.facing.ordinal();
        machine.facing
            = ForgeDirection.getOrientation((int) facing).getRotation(ForgeDirection.UP);
        return true;
    }

    @Override
    public float getPlayerRelativeBlockHardness(
        EntityPlayer player, World world, int x, int y, int z
    ) {
        float hardness = super.getPlayerRelativeBlockHardness(player, world, x, y, z);
        TileEntityMachine machine = (TileEntityMachine) world.getTileEntity(x, y, z);
        if (machine.getAccessLevel() == AccessLevel.CLOSED
            && !player.getUniqueID().equals(machine.getOwner())) {
            return 1.0E-6f;
        }
        return hardness;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int meta) {
        if (world == null || !Dartcraft.proxy.isSimulating(world)) {
            return;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityStorageUnit) {
            TileEntityStorageUnit storage = (TileEntityStorageUnit) tile;
            if (/*!Config.superStorage &&*/ !storage.sturdy) {
                ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
                for (int i = 0; i < storage.getSizeInventory(); ++i) {
                    ItemStack invStack = storage.getStackInSlot(i);
                    if (invStack != null) {
                        drops.add(invStack.copy());
                    }
                    storage.setInventorySlotContents(i, null);
                }
                for (ItemStack toDrop : drops) {
                    DartUtils.dropItem(toDrop, world, x, y, z);
                }
            }
            ItemStack toDrop = ((TileEntityStorageUnit) tile).getDropStack();
            world.removeTileEntity(x, y, z);
            if (toDrop != null) {
                DartUtils.dropInvincibleItem(toDrop, world, x, y, z, 6000);
            }
        }
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public boolean addDestroyEffects(
        World world, int x, int y, int z, int meta, EffectRenderer renderer
    ) {
        WorldVec pos = new WorldVec(world, x, y, z);
        int color = 11;
        TileEntityMachine machine = (TileEntityMachine) pos.getTileEntity();
        color = machine.color;
        FXUtils.makeShiny(pos, 2, DartUtils.getMcColor(color), 32, true);
        return true;
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public boolean
    addHitEffects(World world, MovingObjectPosition mop, EffectRenderer renderer) {
        WorldVec pos = new Vec3(mop).withWorld(world);

        TileEntityMachine machine = (TileEntityMachine) pos.getTileEntity();
        FXUtils.makeShiny(pos, 2, DartUtils.getMcColor(machine.color), 4, true);
        return true;
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public void registerBlockIcons(IIconRegister reggie) {
        this.blockIcon = IconDirectory.machines[11];
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes", "ALEC" })
    public void getSubBlocks(Item par1, CreativeTabs tab, List list) {
        IntStream.range(0, 16)
            .filter(n -> n != 8)
            .mapToObj(m -> new ItemStack(this, 1, m))
            .forEach(list::add);
        IntStream.range(0, 4)
            .mapToObj(i -> {
                ItemStack stack = new ItemStack(this, 1, 8);
                TileEntityStorageUnit tile = new TileEntityStorageUnit();
                tile.tier = i;
                tile.color = 8;
                stack.setTagCompound(new NBTTagCompound());
                tile.writeToNBT(stack.getTagCompound());
                return stack;
            })
            .forEach(list::add);
        if (/*Config.loadEnderStorage*/ true) {
            ItemStack stack = new ItemStack(this, 1, 8);
            TileEntityStorageUnit tile = new TileEntityStorageUnit();
            tile.ender = true;
            tile.color = 8;
            stack.setTagCompound(new NBTTagCompound());
            tile.writeToNBT(stack.getTagCompound());
            list.add(stack);
        }
    }

    @Override
    public Class<? extends TileEntity> getTEClass() {
        return TileEntityStorageUnit.class;
    }
}
