package ley.modding.dartcraft.util;

import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IForceConsumer;
import ley.modding.dartcraft.api.inventory.ItemInventory;
import ley.modding.dartcraft.item.DartItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Random;
import java.util.logging.Logger;

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

    public static boolean useForce(ItemStack stack, int amount, boolean use)
    {
        if ((stack == null) || (!stack.hasTagCompound()) || (amount < 0) || (stack.getItem() == null) || (!(stack.getItem() instanceof IForceConsumer)))
        {
            return false;
        }
        boolean canUse = stack.getTagCompound().getInteger("storedForce") >= amount;

        try
        {
            if (use)
            {
                stack.getTagCompound().setInteger("storedForce", stack.getTagCompound().getInteger("storedForce") - amount);


                ItemInventory inv = new ItemInventory(1, stack, "consumerContents");
                IForceConsumer consumer = (IForceConsumer)stack.getItem();
                ItemStack invStack = inv.getStackInSlot(0);

                int defecit = consumer.getMaxStored(stack) - stack.getTagCompound().getInteger("storedForce");


                if ((defecit > 0) && (invStack != null) && (invStack.stackSize > 0))
                {
                    FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(new ItemStack(invStack.getItem(), 1, invStack.getItemDamage()));


                    if ((liquid == null) && (invStack.getItem() == DartItems.forcegem)) {
                        liquid = new FluidStack(FluidRegistry.getFluid("liquidforce"), (int)(1000.0F * Config.gemValue));
                    }

                    if ((liquid != null) && (liquid.getFluid().getName().equalsIgnoreCase("liquidforce")))
                    {

                        while ((defecit >= liquid.amount) && (invStack.stackSize > 0))
                        {
                            stack.getTagCompound().setInteger("storedForce", stack.getTagCompound().getInteger("storedForce") + liquid.amount);
                            invStack.stackSize -= 1;
                            if (invStack.stackSize <= 0) {
                                inv.setInventorySlotContents(0, invStack.getItem().hasContainerItem() ? new ItemStack(invStack.getItem().getContainerItem()) : (ItemStack)null);
                            }
                            inv.save();
                            defecit = consumer.getMaxStored(stack) - stack.getTagCompound().getInteger("storedForce");
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            Logger.getLogger("DartCraft").info("There was a problem in IForceConsumer implementation.");
            e.printStackTrace();
        }

        return canUse;
    }

    public static boolean isForceContainer(ItemStack stack)
    {
        if (stack == null) {
            return false;
        }
        FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(stack);
        Fluid liquidForce = FluidRegistry.getFluid("liquidforce");

        if ((liquid != null) && (liquidForce != null) && (liquid.getFluidID() == liquidForce.getID())) {
            return true;
        }
        return (stack.getItem() == DartItems.forcegem) || (stack.getItem() == DartItems.forceshard);
    }

    public static boolean openForceConsumerGui(EntityPlayer player, ItemStack stack)
    {
        if ((stack == null) || (stack.getItem() == null) || (!stack.hasTagCompound()) || (!(stack.getItem() instanceof IForceConsumer)) || (player == null))
        {
            return false;
        }

        player.openGui(Dartcraft.instance, 16, player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
        return true;
    }

}
