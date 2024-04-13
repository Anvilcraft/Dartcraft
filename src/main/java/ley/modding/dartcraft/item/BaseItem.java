package ley.modding.dartcraft.item;

import ley.modding.dartcraft.util.Util;
import net.minecraft.item.Item;

public class BaseItem extends Item {
    public BaseItem(String id) {
        Util.configureItem(this, id);
    }
}
