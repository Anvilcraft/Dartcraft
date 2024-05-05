package ley.modding.dartcraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemFood;

public class ItemDartFood extends ItemFood {
    public String iconName;

    public ItemDartFood(int hunger, float sat, boolean wolves) {
        super(hunger, sat, wolves);
    }

    public ItemDartFood setUnlocalizedName(String name) {
        this.iconName = name;
        return (ItemDartFood) super.setUnlocalizedName(name);
    }

    @SideOnly(value = Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon("dartcraft:" + this.iconName);
    }
}
