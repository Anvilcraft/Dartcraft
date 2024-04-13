package ley.modding.dartcraft.item;

import ley.modding.dartcraft.util.Util;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public abstract class AbstractItemBlockMetadata extends ItemBlock {
    public AbstractItemBlockMetadata(Block block) {
        super(block);
        Util.configureItem(this, this.getID());
        this.setHasSubtypes(true);
    }

    public abstract String getID();

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + this.getID() + stack.getItemDamage();
    }
}
