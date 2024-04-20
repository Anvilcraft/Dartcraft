package ley.modding.dartcraft.block;

import java.awt.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.util.FXUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BaseBlock extends Block {
    public static final int DEFAULT_COLOR = Color.yellow.getRGB();

    protected BaseBlock(Material arg0) {
        super(arg0);
    }

    public int getColor() {
        return DEFAULT_COLOR;
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
            this.getColor(),
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
            this.getColor(),
            4,
            true
        );
        return true;
    }
}
