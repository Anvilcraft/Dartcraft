package ley.modding.dartcraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.AccessLevel;
import ley.modding.dartcraft.client.gui.GuiType;
import ley.modding.dartcraft.tile.TileEntityInfuser;
import ley.modding.dartcraft.tile.TileEntityMachine;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.FXUtils;
import ley.modding.dartcraft.util.Util;
import ley.modding.tileralib.api.ICustomItemBlockProvider;
import ley.modding.tileralib.api.ITEProvider;
import net.anvilcraft.anvillib.vector.Vec3;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockInfuser
    extends BlockContainer implements ITEProvider, ICustomItemBlockProvider {
    public int shineColor = 0x3c3056;

    public BlockInfuser() {
        super(Material.iron);
        Util.configureBlock(this, "infuser");
        this.setHardness(30.0F);
        this.setResistance(2000.0F);
        this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.7F, 0.9F);
    }

    @Override
    public void onBlockPlacedBy(
        World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack
    ) {
        try {
            if (stack.hasTagCompound()) {
                TileEntityInfuser infuser = (TileEntityInfuser
                ) TileEntity.createAndLoadEntity(stack.getTagCompound());
                world.setTileEntity(x, y, z, infuser);
            }
            TileEntityMachine e = (TileEntityMachine) world.getTileEntity(x, y, z);
            e.owner = ((EntityPlayer) entity).getUniqueID();
            e.access = AccessLevel.OPEN;
        } catch (Exception var9) {}

        world.markBlockForUpdate(x, y, z);
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
        return -1;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int alec) {
        return new TileEntityInfuser();
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
        try {
            if (Dartcraft.proxy.isSimulating(world) && !player.isSneaking()) {
                TileEntity e = world.getTileEntity(x, y, z);
                if (e != null && e instanceof TileEntityInfuser) {
                    TileEntityInfuser infuser = (TileEntityInfuser) e;
                    ItemStack dyeStack = player.getCurrentEquippedItem();
                    if (dyeStack != null && dyeStack.getItem() == Items.dye) {
                        if (!player.getUniqueID().equals(infuser.getOwner())) {
                            DartUtils.punishPlayer(player, 2.0F);
                            return true;
                        }

                        if (dyeStack.getItemDamage() != infuser.color) {
                            infuser.color = dyeStack.getItemDamage();
                            world.markBlockForUpdate(x, y, z);
                            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                            --dyeStack.stackSize;
                            if (dyeStack.stackSize <= 0) {
                                player.destroyCurrentEquippedItem();
                            }
                        }
                    } else {
                        player.openGui(
                            Dartcraft.instance, GuiType.INFUSER.ordinal(), world, x, y, z
                        );
                    }
                }
            }
        } catch (Exception var13) {
            ;
        }

        return true;
    }

    @Override
    public float getPlayerRelativeBlockHardness(
        EntityPlayer player, World world, int x, int y, int z
    ) {
        float hardness = super.getPlayerRelativeBlockHardness(player, world, x, y, z);

        try {
            TileEntityMachine e = (TileEntityMachine) world.getTileEntity(x, y, z);
            if (!player.getUniqueID().equals(e.getOwner())) {
                return 1.0E-6F;
            }
        } catch (Exception var8) {
            ;
        }

        return hardness;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
        if (world != null && Dartcraft.proxy.isSimulating(world)) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile != null && tile instanceof TileEntityMachine) {
                ItemStack toDrop = new ItemStack(this);
                NBTTagCompound nbt = new NBTTagCompound();
                tile.writeToNBT(nbt);
                toDrop.setTagCompound(nbt);
                world.removeTileEntity(x, y, z);
                DartUtils.dropInvincibleItem(
                    toDrop, world, (double) x, (double) y, (double) z, 6000
                );
            }
        }
    }

    @Override
    public ArrayList<ItemStack>
    getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<>();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(
        World world, int x, int y, int z, int meta, EffectRenderer renderer
    ) {
        FXUtils.makeShiny(new WorldVec(world, x, y, z), 2, this.shineColor, 16, true);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean
    addHitEffects(World world, MovingObjectPosition mop, EffectRenderer renderer) {
        if (world != null && mop != null) {
            FXUtils.makeShiny(
                new Vec3(mop).withWorld(world), 2, this.shineColor, 3, true
            );
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TileEntityInfuser) {
            TileEntityInfuser infuser = (TileEntityInfuser) tile;
            if (infuser.isActive) {
                FXUtils.makeShiny(
                    new WorldVec(world, x, y, z), 2, this.shineColor, 10, true
                );
                world.playSound(
                    (float) x,
                    (float) y,
                    (float) z,
                    "dartcraft:sparkle",
                    0.5F,
                    DartUtils.randomPitch(),
                    true
                );
            }
        }
    }

    @Override
    public Class<? extends TileEntity> getTEClass() {
        return TileEntityInfuser.class;
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return BlockItem.class;
    }

    public static class BlockItem extends ItemBlock {
        public BlockItem(Block block) {
            super(block);
        }

        @SideOnly(value = Side.CLIENT)
        @SuppressWarnings({ "unchecked", "rawtypes", "ALEC" })
        public void
        addInformation(ItemStack stack, EntityPlayer player, List list, boolean thing) {
            if (stack == null || !stack.hasTagCompound()) {
                return;
            }
            try {
                TileEntityInfuser infuser = (TileEntityInfuser
                ) TileEntity.createAndLoadEntity((NBTTagCompound) stack.getTagCompound());
                list.add("Tier " + infuser.getActiveTier());
                list.add("Liquid Force: " + infuser.getFluidAmount());
                list.add("RF: " + (int) infuser.storedRF);
                TileEntityInfuser inv = infuser;
                int free = 0;
                for (int i = 0; i < inv.getSizeInventory(); ++i) {
                    if (inv.getStackInSlot(i) != null)
                        continue;
                    ++free;
                }
                list.add(
                    "" + (inv.getSizeInventory() - free) + "/" + inv.getSizeInventory()
                    + " Slots"
                );
            } catch (Exception e) {}
        }
    }
}
