package ley.modding.dartcraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class UpgradeHelper {
    public static NBTTagCompound getUpgradeCompound(ItemStack stack) {
        return stack != null && stack.hasTagCompound()
                && stack.getTagCompound().hasKey("upgrades")
            ? stack.getTagCompound().getCompoundTag("upgrades")
            : new NBTTagCompound();
    }

    public static ItemStack setUpgradeData(ItemStack stack, String type, int level) {
        if (stack == null)
            return null;

        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());

        if (!stack.getTagCompound().hasKey("upgrades"))
            stack.getTagCompound().setTag("upgrades", new NBTTagCompound());

        NBTTagCompound upgrades = stack.getTagCompound().getCompoundTag("upgrades");
        upgrades.setInteger(type, level);
        return stack;
    }

    public static NBTTagCompound getDartData(Entity entity) {
        if (entity == null)
            return new NBTTagCompound();
        NBTTagCompound entdat = entity.getEntityData();
        if (entdat == null)
            return new NBTTagCompound();

        NBTTagCompound dartdat = entdat.getCompoundTag("dartcraft");

        // getCompoundTag may create a new tag, here we make sure it is also contained
        // within entdat.
        entdat.setTag("dartcraft", dartdat);

        return dartdat;
    }

    //public static NBTTagCompound getPlayerEquippedComp(EntityPlayer player) {
    //    if (player == null)
    //        return new NBTTagCompound();

    //    ItemStack stack = player.getCurrentEquippedItem();
    //    NBTTagCompound upgrades = new NBTTagCompound();
    //    if (stack != null) {
    //        if (stack.getItem() instanceof ItemForceSword) {
    //            upgrades = (NBTTagCompound) getUpgradeCompound(stack).copy();
    //        }

    //        if (stack.getItem() instanceof ItemPowerSaw) {
    //            upgrades = (NBTTagCompound)
    //            SocketHelper.getSocketCompound(stack).copy();
    //        }
    //    }

    //    return upgrades;
    //}
}
