package ley.modding.dartcraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.client.gui.GuiType;
import ley.modding.dartcraft.tile.TileEntityForceEngine;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.FXUtils;
import ley.modding.tileralib.api.ITEProvider;
import net.anvilcraft.anvillib.vector.Vec3;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;

public class BlockForceEngine extends BlockContainer implements ITEProvider {
    public BlockForceEngine() {
        super(Material.iron);
        setHardness(3.0F);
        setResistance(50.0F);
        setCreativeTab(Dartcraft.tab);
        setBlockName("forceengine");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int var2) {
        return new TileEntityForceEngine();
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
        if (!Dartcraft.proxy.isSimulating(world)) {
            return true;
        } else {
            TileEntity tile = world.getTileEntity(x, y, z);
            TileEntityForceEngine engine = null;
            if (tile instanceof TileEntityForceEngine) {
                engine = (TileEntityForceEngine) tile;
            }

            if (engine != null) {
                if (DartUtils.isHoldingWrench(player)) {
                    engine.rotateBlock();
                    return true;
                }

                if (player.getCurrentEquippedItem() != null
                    && FluidContainerRegistry.getFluidForFilledItem(
                           player.getCurrentEquippedItem()
                       ) != null) {
                    return DartUtils.fillTankWithContainer(engine, player);
                }

                if (!player.isSneaking() && !DartUtils.isHoldingWrench(player)) {
                    player.openGui(
                        Dartcraft.instance, GuiType.ENGINE.ordinal(), world, x, y, z
                    );
                }
            }

            return true;
        }
    }

    @Override
    public void onBlockPlacedBy(
        World world, int x, int y, int z, EntityLivingBase living, ItemStack stack
    ) {
        TileEntityForceEngine tile = (TileEntityForceEngine) world.getTileEntity(x, y, z);
        if (tile != null) {
            tile.setFacing(ForgeDirection.UP);
            tile.rotateBlock();
        }
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof TileEntityForceEngine
            ? ((TileEntityForceEngine) te).getLightValue()
            : 0;
    }

    @Override
    public int getRenderType() {
        return -1;
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
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
        if (Dartcraft.proxy.isSimulating(world)) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityForceEngine) {
                TileEntityForceEngine engine = (TileEntityForceEngine) tile;

                for (int i = 0; i < engine.liquidInventory.getSizeInventory(); ++i) {
                    ItemStack tempStack = engine.liquidInventory.getStackInSlot(i);
                    if (tempStack != null) {
                        DartUtils.dropItem(tempStack, world, x, y, z);
                    }
                }
            }

            super.breakBlock(world, x, y, z, par5, par6);
        }
    }

    @Override
    public boolean
    isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        return tile instanceof TileEntityForceEngine
            ? ((TileEntityForceEngine) tile).facing.getOpposite() == side
            : false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(
        World world, int x, int y, int z, int meta, EffectRenderer renderer
    ) {
        FXUtils.makeShiny(
            new WorldVec(world, x, y, z), 2, BaseBlock.DEFAULT_COLOR, 32, true
        );
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean
    addHitEffects(World world, MovingObjectPosition mop, EffectRenderer renderer) {
        FXUtils.makeShiny(
            new Vec3(mop).withWorld(world), 2, BaseBlock.DEFAULT_COLOR, 4, true
        );
        return true;
    }

    @Override
    public Class<? extends TileEntity> getTEClass() {
        return TileEntityForceEngine.class;
    }
}
