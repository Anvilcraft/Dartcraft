package ley.modding.dartcraft.util;

import java.util.Random;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IForceConsumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ForceConsumerUtils {

    public static int getStoredForce(ItemStack stack) {
        return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("storedForce")?stack.getTagCompound().getInteger("storedForce"):0;
    }

    public static void initializeForceConsumer(ItemStack stack) {
        if(!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound comp = stack.getTagCompound();
        NBTTagList contents = new NBTTagList();

        for(int i = 0; i < 1; ++i) {
            NBTTagCompound itemComp = new NBTTagCompound();
            itemComp.setByte("Slot", (byte)i);
            contents.appendTag(itemComp);
        }

        comp.setTag("consumerContents", contents);
        comp.setInteger("ID", (new Random()).nextInt());
    }

    public static boolean attemptRepair(ItemStack stack) {
        boolean repaired = false;
        IForceConsumer consumer = null;
        if(stack != null && stack.getItemDamage() > 0 && stack.getItem() instanceof IForceConsumer) {
            consumer = (IForceConsumer)stack.getItem();
        }

        if(consumer != null) {
            for(int use = consumer.amountUsedBase(stack); stack.getItemDamage() > 0 && consumer.getStored(stack) >= use; repaired = true) {
                stack.setItemDamage(stack.getItemDamage() - 1);
                consumer.useForce(stack, use, true);
            }
        }

        return repaired;
    }
}
