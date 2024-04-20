package ley.modding.dartcraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.util.FXUtils;
import ley.modding.dartcraft.util.Util;
import net.minecraft.block.BlockLog;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockForceLog extends BlockLog {
    private IIcon sideIcon;
    private IIcon topIcon;

    public BlockForceLog() {
        Util.configureBlock(this, "forcelog");
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        this.sideIcon = register.registerIcon(Dartcraft.MODID + ":logSide");
        this.topIcon = register.registerIcon(Dartcraft.MODID + ":logTop");
    }

    @Override
    protected IIcon getTopIcon(int p_150161_1_) {
        return this.topIcon;
    }

    @Override
    protected IIcon getSideIcon(int p_150163_1_) {
        return this.sideIcon;
    }

    @Override
    public boolean canSustainLeaves(IBlockAccess arg0, int arg1, int arg2, int arg3) {
        return true;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    @Override
    public boolean isWood(IBlockAccess arg0, int arg1, int arg2, int arg3) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(
        World world, int x, int y, int z, int meta, EffectRenderer renderer
    ) {
        FXUtils.makeShiny(
            world,
            (double) x,
            (double) y,
            (double) z,
            2,
            BaseBlock.DEFAULT_COLOR,
            32,
            true
        );
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean
    addHitEffects(World world, MovingObjectPosition target, EffectRenderer renderer) {
        FXUtils.makeShiny(
            world,
            (double) target.blockX,
            (double) target.blockY,
            (double) target.blockZ,
            2,
            BaseBlock.DEFAULT_COLOR,
            4,
            true
        );
        return true;
    }
}
