package ley.modding.dartcraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.FXUtils;
import ley.modding.dartcraft.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockForceStairs extends BlockStairs {
    int type;

    public BlockForceStairs(int type) {
        super(type == 16 ? DartBlocks.forceplanks : DartBlocks.forcebrick[type], 32);
        Util.configureBlock(this, "forcestairs" + type);
        this.setHardness(2.0F);
        this.setResistance(2000.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setLightOpacity(0);
        this.type = type;
    }

    @Override
    public IIcon getIcon(int alec, int meta) {
        if (this.blockIcon == null)
            this.blockIcon = (this.type == 16 ? DartBlocks.forceplanks
                                              : DartBlocks.forcebrick[this.type])
                                 .getIcon(0, 0);
        return this.blockIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister p_registerBlockIcons_1_) {}

    @Override
    public boolean canCreatureSpawn(
        EnumCreatureType arg0, IBlockAccess arg1, int arg2, int arg3, int arg4
    ) {
        return false;
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
            DartUtils.getMcColor(this.type),
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
            DartUtils.getMcColor(this.type),
            4,
            true
        );
        return true;
    }
}
