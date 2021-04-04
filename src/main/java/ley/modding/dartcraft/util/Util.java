package ley.modding.dartcraft.util;

import ley.modding.dartcraft.Dartcraft;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class Util {
    public static void configureItem(Item item, String id) {
        item.setUnlocalizedName(id);
        item.setTextureName(Dartcraft.MODID + ":" + id);
        item.setCreativeTab(Dartcraft.tab);
    }

    public static void configureBlock(Block block, String id) {
        block.setBlockName(id);
        block.setCreativeTab(Dartcraft.tab);
    }
}
