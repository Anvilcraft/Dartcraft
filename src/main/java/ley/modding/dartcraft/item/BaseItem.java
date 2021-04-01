package ley.modding.dartcraft.item;

import ley.modding.dartcraft.Dartcraft;
import net.minecraft.item.Item;

public class BaseItem extends Item  {

    public BaseItem(String id) {
        setUnlocalizedName(id);
        setTextureName(Dartcraft.MODID + ":" + id);
        setCreativeTab(Dartcraft.tab);
    }
}
