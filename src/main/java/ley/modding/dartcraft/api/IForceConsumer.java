package ley.modding.dartcraft.api;

import net.minecraft.item.ItemStack;

public interface IForceConsumer {

   int getStored(ItemStack var1);

   int getMaxStored(ItemStack var1);

   int amountUsedBase(ItemStack var1);

   boolean useForce(ItemStack var1, int var2, boolean var3);

   boolean attemptRepair(ItemStack var1);
}
