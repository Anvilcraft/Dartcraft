package ley.modding.dartcraft.util;

import ley.modding.dartcraft.Dartcraft;
import net.minecraft.item.Item;

public class Util {
    public static void configureItem(Item item, String id) {
        item.setUnlocalizedName(id);
        item.setTextureName(Dartcraft.MODID + ":" + id);
        item.setCreativeTab(Dartcraft.tab);
    }
}
