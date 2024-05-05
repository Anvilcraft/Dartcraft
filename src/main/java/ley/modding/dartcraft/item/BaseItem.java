package ley.modding.dartcraft.item;

import ley.modding.dartcraft.client.IconDirectory;
import ley.modding.dartcraft.util.Util;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class BaseItem extends Item {
    public BaseItem(String id) {
        Util.configureItem(this, id);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        super.registerIcons(reg);
        IconDirectory.registerItemTextures(reg);
    }
}
