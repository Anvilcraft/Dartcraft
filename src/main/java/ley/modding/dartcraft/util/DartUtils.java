package ley.modding.dartcraft.util;

import java.awt.Color;

import buildcraft.api.tools.IToolWrench;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IForceConsumer;
import ley.modding.dartcraft.api.upgrades.IForceUpgradable;
import ley.modding.dartcraft.entity.EntityInvincibleItem;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.item.ItemClipboard;
import ley.modding.dartcraft.network.PacketFX;
import ley.modding.dartcraft.network.PacketFX.Type;
import ley.modding.dartcraft.proxy.CommonProxy;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class DartUtils {
    public static boolean isHoldingWrench(EntityPlayer player) {
        if (player.getCurrentEquippedItem() == null) {
            return false;
        } else {
            Item equipped = player.getCurrentEquippedItem().getItem();
            return equipped instanceof IToolWrench;
        }
    }

    public static boolean
    fillTankWithContainer(IFluidHandler theTile, EntityPlayer player) {
        ItemStack theContainer = player.getCurrentEquippedItem();
        FluidStack theLiquid = FluidContainerRegistry.getFluidForFilledItem(theContainer);
        if (theLiquid != null
            && (theTile.fill(ForgeDirection.UNKNOWN, theLiquid, false) == theLiquid.amount
                || player.capabilities.isCreativeMode)) {
            theTile.fill(ForgeDirection.UNKNOWN, theLiquid, true);
            ItemStack returnStack = consumeItem(theContainer);
            if (!player.capabilities.isCreativeMode) {
                player.inventory.setInventorySlotContents(
                    player.inventory.currentItem, returnStack
                );
            }

            return true;
        } else {
            return false;
        }
    }

    public static ItemStack consumeItem(ItemStack theStack) {
        if (theStack.stackSize == 1) {
            return theStack.getItem().hasContainerItem()
                ? theStack.getItem().getContainerItem(theStack)
                : null;
        } else {
            theStack.splitStack(1);
            return theStack;
        }
    }

    public static void
    dropItem(ItemStack stack, World world, double x, double y, double z) {
        if (stack != null && world != null && Dartcraft.proxy.isSimulating(world)) {
            float xRand = CommonProxy.rand.nextFloat() * 0.2F + 0.1F;
            float yRand = CommonProxy.rand.nextFloat() * 0.8F + 0.1F;
            float zRand = CommonProxy.rand.nextFloat() * 0.2F + 0.1F;

            while (stack.stackSize > 0) {
                int randInt = CommonProxy.rand.nextInt(21) + 10;
                if (randInt > stack.stackSize) {
                    randInt = stack.stackSize;
                }

                stack.stackSize -= randInt;
                EntityItem droppedItem = new EntityItem(
                    world,
                    (double) ((float) x + xRand),
                    (double) ((float) y + yRand),
                    (double) ((float) z + zRand),
                    new ItemStack(stack.getItem(), randInt, stack.getItemDamage())
                );
                if (stack.hasTagCompound()) {
                    droppedItem.getEntityItem().setTagCompound(
                        (NBTTagCompound) stack.getTagCompound().copy()
                    );
                }

                float modifier = 0.025F;
                droppedItem.motionX
                    = (double) ((float) CommonProxy.rand.nextGaussian() * modifier);
                droppedItem.motionY
                    = (double) ((float) CommonProxy.rand.nextGaussian() * modifier + 0.2F
                    );
                droppedItem.motionZ
                    = (double) ((float) CommonProxy.rand.nextGaussian() * modifier);
                droppedItem.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(droppedItem);
            }
        }
    }

    public static float randomPitch() {
        return CommonProxy.rand.nextFloat() * 0.25F + 0.85F;
    }

    public static int getMcColor(int color) {
        int[] lookup = new int[] {
            0xff333333,
            Color.red.getRGB(),
            Color.green.getRGB(),
            0xff6e5334,
            Color.blue.getRGB(),
            0xff6b47b8,
            Color.cyan.getRGB(),
            Color.lightGray.getRGB(),
            Color.gray.getRGB(),
            Color.pink.getRGB(),
            0xff9ed843,
            Color.yellow.getRGB(),
            0xff51a8f4,
            Color.magenta.getRGB(),
            Color.orange.getRGB(),
            Color.white.getRGB(),
            Color.yellow.getRGB(),
        };

        return color >= 0 && color < lookup.length ? lookup[color] : 0;
    }

    public static boolean canUpgrade(ItemStack stack) {
        if (stack == null)
            return false;

        if (stack.getItem() == Items.bucket
            || stack.getItem() == DartItems.forceflask && stack.getItemDamage() == 0)
            return true;

        if ((!hasDartUpgrade(stack) /*|| stack.getItem() instanceof ItemForcePack*/
             || stack.getItem() instanceof IForceConsumer
             || stack.getItem() instanceof ItemClipboard)
            && (stack.getItem() instanceof IForceUpgradable
                || stack.getItem() instanceof IForceConsumer
                /*|| ForceWildCards.isWildCard(stack)*/))
            return true;

        return false;
    }

    public static boolean hasDartUpgrade(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound comp = stack.getTagCompound();
            if (comp.hasKey("upgrades") || comp.hasKey("ench")) {
                return true;
            }
        }

        return false;
    }

    public static void punishPlayer(EntityPlayer player, float amount) {
        try {
            player.attackEntityFrom(DamageSource.outOfWorld, amount);
            player.worldObj.playSoundAtEntity(
                player, "dartcraft:nope", 1.0F, randomPitch()
            );

            WorldVec pktPos = new WorldVec(player);
            pktPos.y += player.height / 2f;
            Dartcraft.channel.sendToAllAround(
                new PacketFX(pktPos, Type.CHARGE, 1, 0, 32), pktPos.targetPoint(32d)
            );
        } catch (Exception var3) {}
    }

    public static void dropInvincibleItem(
        ItemStack stack, World world, double x, double y, double z, int life
    ) {
        if (stack != null && Dartcraft.proxy.isSimulating(world)) {
            float xRand = CommonProxy.rand.nextFloat() * 0.2F + 0.1F;
            float yRand = CommonProxy.rand.nextFloat() * 0.8F + 0.1F;
            float zRand = CommonProxy.rand.nextFloat() * 0.2F + 0.1F;

            EntityInvincibleItem droppedItem = new EntityInvincibleItem(
                world,
                (double) ((float) x + xRand),
                (double) ((float) y + yRand),
                (double) ((float) z + zRand),
                stack,
                life
            );

            droppedItem.delayBeforeCanPickup
                = stack.getItem() == DartItems.forcetome ? 100 : 10;

            if (stack.hasTagCompound()) {
                droppedItem.getEntityItem().setTagCompound(
                    (NBTTagCompound) stack.getTagCompound().copy()
                );
            }

            float modifier = 0.025F;
            droppedItem.motionX
                = (double) ((float) CommonProxy.rand.nextGaussian() * modifier);
            droppedItem.motionY
                = (double) ((float) CommonProxy.rand.nextGaussian() * modifier + 0.2F);
            droppedItem.motionZ
                = (double) ((float) CommonProxy.rand.nextGaussian() * modifier);
            world.spawnEntityInWorld(droppedItem);
        }
    }

    public static void dropItem(ItemStack stack, WorldVec pos) {
        if (Dartcraft.proxy.isSimulating(pos.world)) {
            float xRand = CommonProxy.rand.nextFloat() * 0.2f + 0.1f;
            float yRand = CommonProxy.rand.nextFloat() * 0.8f + 0.1f;
            float zRand = CommonProxy.rand.nextFloat() * 0.2f + 0.1f;
            while (stack.stackSize > 0) {
                int randInt = CommonProxy.rand.nextInt(21) + 10;
                if (randInt > stack.stackSize) {
                    randInt = stack.stackSize;
                }
                stack.stackSize -= randInt;
                EntityItem droppedItem = new EntityItem(
                    pos.world,
                    (double) ((float) pos.x + xRand),
                    (double) ((float) pos.y + yRand),
                    (double) ((float) pos.z + zRand),
                    new ItemStack(stack.getItem(), randInt, stack.getItemDamage())
                );
                if (stack.hasTagCompound()) {
                    droppedItem.getEntityItem().setTagCompound(
                        (NBTTagCompound) stack.getTagCompound().copy()
                    );
                }
                float modifier = 0.025f;
                droppedItem.motionX = (float) CommonProxy.rand.nextGaussian() * modifier;
                droppedItem.motionY
                    = (float) CommonProxy.rand.nextGaussian() * modifier + 0.2f;
                droppedItem.motionZ = (float) CommonProxy.rand.nextGaussian() * modifier;
                droppedItem.delayBeforeCanPickup = 10;
                pos.world.spawnEntityInWorld((Entity) droppedItem);
            }
        }
    }
}
