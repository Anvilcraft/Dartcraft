package ley.modding.dartcraft.util;

import com.google.common.base.Objects;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.proxy.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemUtils {
    public static void
    dropItem(ItemStack stack, World world, double x, double y, double z) {
        if (stack != null && world != null && Dartcraft.proxy.isSimulating(world)) {
            float xRand = CommonProxy.rand.nextFloat() * 0.2F + 0.1F;
            float yRand = CommonProxy.rand.nextFloat() * 0.8F + 0.1F;
            float zRand = CommonProxy.rand.nextFloat() * 0.2F + 0.1F;
            while (stack.stackSize > 0) {
                int randInt = CommonProxy.rand.nextInt(21) + 10;
                if (randInt > stack.stackSize)
                    randInt = stack.stackSize;
                stack.stackSize -= randInt;
                EntityItem droppedItem = new EntityItem(
                    world,
                    ((float) x + xRand),
                    ((float) y + yRand),
                    ((float) z + zRand),
                    new ItemStack(stack.getItem(), randInt, stack.getItemDamage())
                );
                if (stack.hasTagCompound())
                    droppedItem.getEntityItem().setTagCompound(
                        (NBTTagCompound) stack.getTagCompound().copy()
                    );
                float modifier = 0.025F;
                ((Entity) droppedItem).motionX
                    = ((float) CommonProxy.rand.nextGaussian() * modifier);
                ((Entity) droppedItem).motionY
                    = ((float) CommonProxy.rand.nextGaussian() * modifier + 0.2F);
                ((Entity) droppedItem).motionZ
                    = ((float) CommonProxy.rand.nextGaussian() * modifier);
                droppedItem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld((Entity) droppedItem);
            }
        }
    }

    public static boolean areStacksSame(ItemStack stack1, ItemStack stack2) {
        return stack1 != null && stack2 != null ? stack1.getItem() == stack2.getItem()
                && stack1.getItemDamage() == stack2.getItemDamage()
                && Objects.equal(stack1.getTagCompound(), stack2.getTagCompound())
                                                : false;
    }
}
