package ley.modding.dartcraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.util.Util;
import net.minecraft.block.BlockSlab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemSlab;
import net.minecraft.util.IIcon;

public class ItemBlockForceSlab extends ItemSlab {
    public ItemBlockForceSlab(BlockSlab single, BlockSlab doub, int type) {
        super(single, single, doub, false);
        Util.configureItem(this, "forceslab" + type);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int p_77617_1_) {
        return this.itemIcon;
    }

    @Override
    public int getMetadata(int p_77647_1_) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {
        return Dartcraft.tab;
    }
}
