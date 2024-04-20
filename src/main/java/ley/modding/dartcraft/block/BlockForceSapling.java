package ley.modding.dartcraft.block;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.util.FXUtils;
import ley.modding.dartcraft.util.Util;
import ley.modding.dartcraft.worldgen.GenForceTree;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BlockForceSapling extends BlockSapling {
    IIcon icon;

    public BlockForceSapling() {
        Util.configureBlock(this, "forcesapling");
        setBlockTextureName(Dartcraft.MODID + ":sapling");
        setStepSound(Block.soundTypeGrass);
    }

    // generate tree
    @Override
    public void func_149878_d(World world, int x, int y, int z, Random rand) {
        if (!TerrainGen.saplingGrowTree(world, rand, x, y, z))
            return;
        world.setBlockToAir(x, y, z);
        new GenForceTree(true).generate(world, rand, x, y, z);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tabs, List list) {
        list.add(new ItemStack(item));
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        this.icon = register.registerIcon(Dartcraft.MODID + ":sapling");
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return this.icon;
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
