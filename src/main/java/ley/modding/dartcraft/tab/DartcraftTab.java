package ley.modding.dartcraft.tab;

import ley.modding.dartcraft.item.DartItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class DartcraftTab extends CreativeTabs {
    public DartcraftTab() {
        super("dartcraft");
    }

    @Override
    public Item getTabIconItem() {
        return DartItems.forcegem;
    }
}
