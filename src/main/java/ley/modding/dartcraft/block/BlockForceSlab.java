package ley.modding.dartcraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.item.AbstractItemBlockMetadata;
import ley.modding.dartcraft.tile.TileEntityStairs;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.FXUtils;
import ley.modding.dartcraft.util.Util;
import ley.modding.tileralib.api.ICustomItemBlockProvider;
import ley.modding.tileralib.api.ITEProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockForceSlab
    extends BlockSlab implements ITEProvider, ICustomItemBlockProvider {
    public BlockForceSlab() {
        super(false, Material.rock);
        Util.configureBlock(this, "forceslab");
        this.setHardness(2.0F);
        this.setResistance(2000.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setLightOpacity(0);
        // TODO: WTF
        //Block.useNeighborBrightness[id] = true;
    }

    // TODO: WTF
    //@Override
    //public String getFullSlabName(int var1) {
    //    return "forceSlab";
    //}

    @Override
    public ItemStack getPickBlock(
        MovingObjectPosition arg0,
        World arg1,
        int arg2,
        int arg3,
        int arg4,
        EntityPlayer arg5
    ) {
        return new ItemStack(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void getSubBlocks(Item id, CreativeTabs tab, List list) {
        IntStream.range(0, 17)
            .mapToObj(i -> new ItemStack(this, 1, i))
            .forEach(list::add);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(
        IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5
    ) {
        return !(
            par5 != 1 && par5 != 0
            && !super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5)
        );
    }

    @Override
    public boolean
    canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TileEntityStairs();
    }

    public static TileEntityStairs getStairTile(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile != null && tile instanceof TileEntityStairs ? (TileEntityStairs) tile
                                                                : null;
    }

    @Override
    public boolean
    removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean alec) {
        int meta = world.getBlockMetadata(x, y, z);
        if (Dartcraft.proxy.isSimulating(world) && this.canHarvestBlock(player, meta)
            && !player.capabilities.isCreativeMode) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityStairs) {
                TileEntityStairs stairs = (TileEntityStairs) tile;
                ItemStack stack = new ItemStack(this, 1, stairs.color);
                DartUtils.dropItem(stack, world, (double) x, (double) y, (double) z);
            }
        }

        return world.setBlockToAir(x, y, z);
    }

    @Override
    public ArrayList<ItemStack>
    getDrops(World alec1, int alec2, int alec3, int alec4, int alec5, int alec6) {
        return new ArrayList<>();
    }

    @Override
    public int onBlockPlaced(
        World world,
        int x,
        int y,
        int z,
        int par5,
        float par6,
        float par7,
        float par8,
        int par9
    ) {
        TileEntityStairs stairs = getStairTile(world, x, y, z);
        if (stairs != null) {
            stairs.color = world.getBlockMetadata(x, y, z);
            stairs.markDirty();
        }

        return par5 != 0 && (par5 == 1 || (double) par7 <= 0.5D) ? 0 : 8;
    }

    @Override
    public void onBlockPlacedBy(
        World par1World,
        int par2,
        int par3,
        int par4,
        EntityLivingBase entity,
        ItemStack stack
    ) {
        TileEntityStairs stairs = getStairTile(par1World, par2, par3, par4);
        if (stack != null && stairs != null) {
            stairs.color = stack.getItemDamage();
            stairs.markDirty();
        }

        super.onBlockPlacedBy(par1World, par2, par3, par4, entity, stack);
    }

    @Override
    public IIcon getIcon(int alec, int meta) {
        return meta == 16 ? DartBlocks.forceplanks.getIcon(0, 0)
                          : ((BlockForceBrick) DartBlocks.forcebrick).icons[meta];
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityStairs stairs = getStairTile(world, x, y, z);
        if (stairs != null && stairs.color >= 0) {
            if (stairs.color < 16) {
                return ((BlockForceBrick) DartBlocks.forcebrick).icons[stairs.color];
            }

            if (stairs.color == 16) {
                return DartBlocks.forceplanks.getIcon(0, 0);
            }
        }

        return this.blockIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(
        World world, int x, int y, int z, int meta, EffectRenderer renderer
    ) {
        TileEntityStairs stairs = getStairTile(world, x, y, z);
        if (stairs != null) {
            FXUtils.makeShiny(
                world,
                (double) x,
                (double) y,
                (double) z,
                2,
                DartUtils.getMcColor(stairs.color),
                32,
                true
            );
            return true;
        } else {
            return false;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(
        World world, MovingObjectPosition target, EffectRenderer renderer
    ) {
        TileEntityStairs stairs
            = getStairTile(world, target.blockX, target.blockY, target.blockZ);
        if (stairs != null) {
            FXUtils.makeShiny(
                world,
                (double) target.blockX,
                (double) target.blockY,
                (double) target.blockZ,
                2,
                DartUtils.getMcColor(stairs.color),
                4,
                true
            );
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Class<? extends TileEntity> getTEClass() {
        return TileEntityStairs.class;
    }

    @Override
    public String func_150002_b(int arg0) {
        throw new UnsupportedOperationException("ALEC");
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return BlockItem.class;
    }

    public static class BlockItem extends AbstractItemBlockMetadata {
        public BlockItem(Block block) {
            super(block);
        }

        @Override
        public String getID() {
            return "forceslab";
        }
    }
}
