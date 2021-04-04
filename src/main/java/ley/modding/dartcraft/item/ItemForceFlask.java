package ley.modding.dartcraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.entity.EntityBottle;
import ley.modding.dartcraft.entity.EntityFlyingFlask;
import ley.modding.dartcraft.event.EntityBottleHandler;
import ley.modding.dartcraft.util.EntityUtils;
import ley.modding.dartcraft.util.ItemUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

public class ItemForceFlask extends Item {

    public static final int EMPTY_META = 0;

    public static final int MILK_META = 1;

    public static final int FORCE_META = 2;

    public static final int CHATEAU_META = 3;

    public static final int GREEN_META = 4;

    public static final int RED_META = 5;

    public static final int BLUE_META = 6;

    public static final float FLASK_AMOUNT = 250.0F;

    public static final float DRINK_AMOUNT = 1.0F;

    public ItemForceFlask() {
        setMaxDamage(0);
        setHasSubtypes(true);
        setUnlocalizedName("forceflask");
        setCreativeTab(Dartcraft.tab);
        setMaxStackSize(64);
        setContainerItem(this);
    }

    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
        return true;
    }

    public ItemStack getContainerItemStack(ItemStack stack) {
        if (stack != null)
            if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("triedCraft"))
                return new ItemStack(Blocks.glass);
        return new ItemStack(this, 1, 0);
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        try {
            if (Dartcraft.proxy.isSimulating(world) && stack != null && entity != null && entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)entity;
                if (stack.getItemDamage() == 2 || stack.getItemDamage() == 1) {
                    if (!stack.hasTagCompound())
                        stack.setTagCompound(new NBTTagCompound());
                    if (stack.getTagCompound().hasKey("triedCraft") && stack.getTagCompound()
                            .getBoolean("triedCraft"))
                        stack.getTagCompound().removeTag("triedCraft");
                } else if (stack.getItemDamage() != 0 || stack.stackSize == 0) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUnlocalizedName(ItemStack stack) {
        String name = "item.forceflask_";
        if (stack != null)
                switch (stack.getItemDamage()) {
                    case 0:
                        name = name + "empty";
                        break;
                    case 1:
                        name = name + "milk";
                        break;
                    case 4:
                        name = name + "potion_green";
                        break;
                    case 6:
                        name = name + "potion_blue";
                        break;
                    case 5:
                        name = name + "potion_red";
                        break;
                    case 3:
                        name = name + "potion_chateau";
                        break;
                    case 2:
                        name = name + "potion_force";
                        break;
                }
        return name;
    }

    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
        try {
            if (stack.getItemDamage() == 0)
                if (entity instanceof net.minecraft.entity.passive.EntityCow) {
                    player.swingItem();
                    if (stack.stackSize == 1)
                        stack.setItemDamage(1);
                    return true;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        try {
                switch (stack.getItemDamage()) {
                    case 1:
                    case 5:
                    case 4:
                    case 2:
                    case 6:
                    case 3:
                        return EnumAction.drink;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EnumAction.none;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        try {
            if (stack.getItemDamage() == 0 && player.isSneaking()) {
                    if (Dartcraft.proxy.isSimulating(world)) {
                        EntityFlyingFlask flask = new EntityFlyingFlask(world, (EntityLivingBase)player, null);
                        world.spawnEntityInWorld((Entity)flask);
                        world.playSoundAtEntity((Entity)player, "random.bow", 1.0F,
                                EntityUtils.randomPitch());
                    }
                    stack.stackSize--;
                return stack;
            }
            NBTTagCompound dartTag = EntityUtils.getModComp((Entity)player);
            if ((stack.getItemDamage() >= 32 && stack.hasTagCompound() && stack
                    .getTagCompound().getFloat("amount") >= 1.0F) || stack.getItemDamage() == 1)
                player.setItemInUse(stack, getMaxItemUseDuration(stack));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stack;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        try {
            switch (stack.getItemDamage()) {
                case 1:
                    return 16;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    return 32;
            }
            if (stack.getItemDamage() >= 32)
                return 32;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (stack == null || world == null || player == null)
            return stack;
        NBTTagCompound dartTag = EntityUtils.getModComp((Entity)player);
        boolean server = Dartcraft.proxy.isSimulating(((Entity)player).worldObj);
        if (dartTag.hasKey("lastPotion"))
            return stack;
        if (dartTag.hasKey("combatTime"))
            return stack;
        if (stack != null) {
            int meta = stack.getItemDamage();
            switch (meta) {
                case 1:
                    if (server) {
                        player.clearActivePotions();
                        player.heal(4.0F);
                        world.playSoundAtEntity((Entity)player, "dartcraft:heart", 1.0F, 1.0F);
                        return reduceContainer(player, stack);
                    }
                    break;
            }
        }
        return stack;
    }

    private ItemStack reduceContainer(EntityPlayer player, ItemStack stack) {
        try {
            stack.stackSize--;
            if (stack.stackSize <= 0) {
                player.setCurrentItemOrArmor(0, new ItemStack(this));
                stack = new ItemStack(this);
            } else {
                player.inventory.addItemStackToInventory(new ItemStack(this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stack;
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!Dartcraft.proxy.isSimulating(((Entity)player).worldObj) || stack == null || stack
                .getItemDamage() != 0)
            return true;
        World world = ((Entity)player).worldObj;
        EntityLivingBase victim = null;
        if (entity instanceof EntityLivingBase)
            victim = (EntityLivingBase)entity;
        if (victim != null && !((Entity)victim).isDead && victim.getHealth() > 0.0F) {
            boolean nope = false;
            //TODO Whitelist
            /*boolean whitelisted = !Config.entityWhitelist;
            if (!whitelisted && PluginBottles.whitelist != null)
                for (String check : PluginBottles.whitelist) {
                    if (check != null && check.equals(victim.getClass().getCanonicalName())) {
                        whitelisted = true;
                        break;
                    }
                }
            if (!whitelisted)
                nope = true;*/
            if (victim instanceof EntityPlayer || victim instanceof EntityBottle)
                nope = true;
            if (!nope && (victim instanceof net.minecraft.entity.monster.EntityMob || victim instanceof net.minecraft.entity.monster.EntityGhast)) {
                float maxHealth = 0.0F;
                try {
                    maxHealth = (float)victim.getAttributeMap().getAttributeInstanceByName("generic.maxHealth").getAttributeValue();
                } catch (Exception e) {}
                float ratio = victim.getHealth() / maxHealth;
                float limit = 0.25F;
                if (ratio >= limit && maxHealth >= 5.0F)
                    nope = true;
                NBTTagCompound dartTag = EntityUtils.getModComp((Entity)victim);
                if (player.capabilities.isCreativeMode)
                    nope = false;
                dartTag.removeTag("time");
                dartTag.removeTag("timeTime");
                dartTag.setInteger("timeImmune", 5);
            }
            if (nope) {
                world.playSoundAtEntity((Entity) victim, "dartcraft:nope", 2.0F,
                        EntityUtils.randomPitch());
                return true;
            }
            ItemStack bottleStack = EntityUtils.bottleEntity((Entity)victim);
            world.playSoundAtEntity((Entity)victim, "dartcraft:swipe", 2.0F,
                    EntityUtils.randomPitch());
            world.removeEntity((Entity)victim);
            victim = null;
            stack.stackSize--;
            if (stack.stackSize <= 0) {
                player.setCurrentItemOrArmor(0, bottleStack);
            } else if (!EntityBottleHandler.meshBottles(player, bottleStack)) {
                if (!player.inventory.addItemStackToInventory(bottleStack))
                    if (Dartcraft.proxy.isSimulating(world))
                        ItemUtils.dropItem(bottleStack, world, ((Entity)player).posX, ((Entity)player).posY, ((Entity)player).posZ);
            }
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item meta, CreativeTabs tabs, List itemList) {
        int[] toAdd = { 0, 1, 2, 4, 5, 6 };
        for (int i : toAdd)
            itemList.add(new ItemStack(this, 1, i));
    }

}
