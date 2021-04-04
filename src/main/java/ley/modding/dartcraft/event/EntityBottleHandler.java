package ley.modding.dartcraft.event;

import cpw.mods.fml.common.Mod.EventHandler;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.item.Items;
import ley.modding.dartcraft.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class EntityBottleHandler {

    @EventHandler
    public void handleTaggedPickup(EntityItemPickupEvent event) {
        try {
            if (((PlayerEvent)event).entityPlayer == null || event.item == null || event.item.getEntityItem() == null)
                return;
            boolean trigger = false;
            if (event.item.getEntityItem().getItem() == Items.entitybottle)
                if (meshBottles(((PlayerEvent)event).entityPlayer, event.item.getEntityItem())) {
                    event.setCanceled(true);
                    trigger = true;
                }
            if (Dartcraft.proxy.isSimulating(((Entity)((PlayerEvent)event).entityPlayer).worldObj) && trigger) {
                ((Entity)((PlayerEvent)event).entityPlayer).worldObj.playSoundAtEntity((Entity)((PlayerEvent)event).entityPlayer, "random.pop", 0.25F,
                        EntityUtils.randomPitch());
                ((Entity)((PlayerEvent)event).entityPlayer).worldObj.removeEntity((Entity)event.item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean meshBottles(EntityPlayer player, ItemStack item) {
        try {
            EntityLivingBase entityOne = (EntityLivingBase)EntityList.createEntityFromNBT(item
                    .getTagCompound(), null);
            if (!getHandleEntity((Entity)entityOne))
                return false;
            String nameOne = entityOne.getClass().getCanonicalName();
            ItemStack[] equipOne = new ItemStack[5];
            int color = 0;
            boolean child = false;
            boolean villager = false;
            String owner = "";
            String nameTagName = entityOne.getEntityData().getString("nameTagName");
            if (entityOne instanceof EntitySheep)
                color = ((EntitySheep)entityOne).getFleeceColor();
            if (entityOne instanceof EntityAgeable && ((EntityAgeable)entityOne).isChild())
                child = true;
            if (entityOne instanceof EntityZombie && ((EntityZombie)entityOne).isChild())
                child = true;
            if (entityOne instanceof EntityZombie && ((EntityZombie)entityOne).isVillager())
                villager = true;
            if (entityOne instanceof EntityTameable) {
                EntityLivingBase owningEntity = ((EntityTameable) entityOne).getOwner();
                owner = (owningEntity != null) ? owningEntity.getCommandSenderName() : null;
            }
            int i;
            for (i = 0; i < 5; i++) {
                equipOne[i] = entityOne.getEquipmentInSlot(i);
                if (equipOne[i] != null && (!(entityOne instanceof EntityPigZombie) || i != 0 || equipOne[i]
                        .getItem() != net.minecraft.init.Items.golden_sword))
                    return false;
            }
            for (i = 0; i < player.inventory.mainInventory.length; i++) {
                ItemStack invStack = player.inventory.mainInventory[i];
                if (invStack != null && invStack.getItem() == Items.entitybottle && invStack
                        .hasTagCompound() && invStack.stackSize < invStack.getMaxStackSize()) {
                    EntityLivingBase entityTwo = (EntityLivingBase)EntityList.createEntityFromNBT(invStack
                            .getTagCompound(), null);
                    if (nameOne.equals(entityTwo.getClass().getCanonicalName()))
                        if (!(entityTwo instanceof EntitySheep) || ((EntitySheep)entityTwo).getFleeceColor() == color)
                            if (!(entityTwo instanceof EntityAgeable) || ((EntityAgeable)entityTwo).isChild() == child)
                                if (!(entityTwo instanceof EntityZombie) || ((EntityZombie)entityTwo).isChild() == child)
                                    if (!(entityTwo instanceof EntityZombie) || ((EntityZombie)entityTwo).isVillager() == villager)
                                        if (!(entityTwo instanceof EntityTameable) || ((EntityTameable)entityTwo).getOwner()
                                                .equals(owner)) {
                                            for (int j = 0; j < 5; j++) {
                                                ItemStack checkStack = entityTwo.getEquipmentInSlot(j);
                                                if (checkStack != null && (!(entityOne instanceof EntityPigZombie) || j != 0 || checkStack
                                                        .getItem() != net.minecraft.init.Items.golden_sword))
                                                    return false;
                                            }
                                            invStack.stackSize++;
                                            invStack.animationsToGo = 10;
                                            return true;
                                        }
                }
            }
        } catch (Exception e) {}
        return false;
    }

    private static boolean getHandleEntity(Entity entity) {
        try {
            String name = entity.getClass().getCanonicalName();
            Class[] accepted = {
                    EntityCow.class, EntityChicken.class, EntitySheep.class, EntityPig.class, EntityWolf.class, EntityZombie.class, EntityEnderman.class, EntityGhast.class, EntityBlaze.class, EntityPigZombie.class,
                    EntitySkeleton.class };
            for (Class check : accepted) {
                if (name.equals(check.getCanonicalName()))
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
