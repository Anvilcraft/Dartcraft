package ley.modding.dartcraft.block;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.util.Util;
import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

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
}
