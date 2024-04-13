package ley.modding.dartcraft.item;

import ley.modding.dartcraft.util.Util;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockForceBrick extends ItemBlock {
    public ItemBlockForceBrick(Block block) {
        super(block);
        Util.configureItem(this, "forcebrick");
        this.setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile.forcebrick" + stack.getItemDamage();
    }
}
