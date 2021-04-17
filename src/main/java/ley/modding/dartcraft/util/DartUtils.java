package ley.modding.dartcraft.util;

import buildcraft.api.tools.IToolWrench;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.proxy.CommonProxy;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class DartUtils {

    public static boolean isHoldingWrench(EntityPlayer player) {
        if(player.getCurrentEquippedItem() == null) {
            return false;
        } else {
            Item equipped = player.getCurrentEquippedItem().getItem();
            return equipped instanceof IToolWrench;
        }
    }

    public static boolean fillTankWithContainer(IFluidHandler theTile, EntityPlayer player) {
        ItemStack theContainer = player.getCurrentEquippedItem();
        FluidStack theLiquid = FluidContainerRegistry.getFluidForFilledItem(theContainer);
        if(theLiquid != null && (theTile.fill(ForgeDirection.UNKNOWN, theLiquid, false) == theLiquid.amount || player.capabilities.isCreativeMode)) {
            theTile.fill(ForgeDirection.UNKNOWN, theLiquid, true);
            ItemStack returnStack = consumeItem(theContainer);
            if(!player.capabilities.isCreativeMode) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, returnStack);
            }

            return true;
        } else {
            return false;
        }
    }

    public static ItemStack consumeItem(ItemStack theStack) {
        if(theStack.stackSize == 1) {
            return theStack.getItem().hasContainerItem()?theStack.getItem().getContainerItem(theStack):null;
        } else {
            theStack.splitStack(1);
            return theStack;
        }
    }

    public static void dropItem(ItemStack stack, World world, double x, double y, double z) {
        if(stack != null && world != null && Dartcraft.proxy.isSimulating(world)) {
            float xRand = CommonProxy.rand.nextFloat() * 0.2F + 0.1F;
            float yRand = CommonProxy.rand.nextFloat() * 0.8F + 0.1F;
            float zRand = CommonProxy.rand.nextFloat() * 0.2F + 0.1F;

            while(stack.stackSize > 0) {
                int randInt = CommonProxy.rand.nextInt(21) + 10;
                if(randInt > stack.stackSize) {
                    randInt = stack.stackSize;
                }

                stack.stackSize -= randInt;
                EntityItem droppedItem = new EntityItem(world, (double)((float)x + xRand), (double)((float)y + yRand), (double)((float)z + zRand), new ItemStack(stack.getItem(), randInt, stack.getItemDamage()));
                if(stack.hasTagCompound()) {
                    droppedItem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
                }

                float modifier = 0.025F;
                CommonProxy var10001 = Dartcraft.proxy;
                droppedItem.motionX = (double)((float)CommonProxy.rand.nextGaussian() * modifier);
                droppedItem.motionY = (double)((float)CommonProxy.rand.nextGaussian() * modifier + 0.2F);
                droppedItem.motionZ = (double)((float)CommonProxy.rand.nextGaussian() * modifier);
                droppedItem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(droppedItem);
            }
        }

    }

}
