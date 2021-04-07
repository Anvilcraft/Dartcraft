package ley.modding.dartcraft.item.tool;

import ley.modding.dartcraft.api.IBreakable;
import ley.modding.dartcraft.api.IForceConsumer;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.util.ForceConsumerUtils;
import ley.modding.dartcraft.util.Util;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;

public class ItemForceShears extends ItemShears implements IBreakable, IForceConsumer {
    public ItemForceShears() {
        super();
        Util.configureItem(this, "forceshears");
    }

    @Override
    public ItemStack itemReturned() {
        return new ItemStack(DartItems.forceshard);
    }

    @Override
    public int getStored(ItemStack stack) {
        return ForceConsumerUtils.getStoredForce(stack);
    }

    @Override
    public int getMaxStored(ItemStack stack) {
        return 10000;
    }

    @Override
    public int amountUsedBase(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean useForce(ItemStack stack, int var2, boolean var3) {
        return false;
    }

    @Override
    public boolean attemptRepair(ItemStack stack) {
        return ForceConsumerUtils.attemptRepair(stack);
    }
}
