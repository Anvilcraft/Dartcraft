package ley.modding.dartcraft.block;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.item.Items;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockPowerOre extends Block {

    public BlockPowerOre() {
        super(Material.rock);
        setBlockName("powerore");
        setHardness(3.0F);
        setResistance(10.0F);
        setStepSound(soundTypeStone);
        setBlockTextureName(Dartcraft.MODID + ":powerore");
        setCreativeTab(Dartcraft.tab);
    }

    @Override
    public Item getItemDropped(int par1, Random rand, int par3) {
        return Items.forcegem;
    }

    @Override
    public int quantityDropped(Random rand) {
        return rand.nextInt(3) + 2;
    }

}
