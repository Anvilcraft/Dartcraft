package ley.modding.dartcraft.block;

import java.util.List;
import java.util.Random;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.util.Util;
import ley.modding.dartcraft.worldgen.GenForceTree;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BlockForceSapling extends BlockSapling {
    IIcon icon;

    public BlockForceSapling() {
        Util.configureBlock(this, "forcesapling");
        setBlockTextureName(Dartcraft.MODID + ":sapling");
    }

    // generate tree
    @Override
    public void func_149878_d(World world, int x, int y, int z, Random rand) {
        if (!TerrainGen.saplingGrowTree(world, rand, x, y, z)) return;
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
}
