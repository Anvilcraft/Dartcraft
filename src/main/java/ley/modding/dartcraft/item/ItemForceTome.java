package ley.modding.dartcraft.item;

import java.util.List;
import java.util.stream.IntStream;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.TomeUtils;
import net.anvilcraft.anvillib.usercache.UserCache;
import net.anvilcraft.anvillib.util.AnvilUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemForceTome extends BaseItem {
    public IIcon[] icons;

    public ItemForceTome() {
        super("forcetome");
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void
    addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("type")) {
            return;
        }
        while (list.size() > 1) {
            list.remove(1);
        }
        if (stack.getTagCompound().getInteger("type") == 0) {
            int points = TomeUtils.getPoints(stack);
            int tier = TomeUtils.getStoredTier(stack);
            int next = TomeUtils.getPointsToNext(stack);
            list.add("§bTier " + tier);
            if (tier < 7) {
                list.add("§f" + points + "§b Force Points");
                if (next > 0) {
                    list.add("§bNext Tier: §f" + next);
                } else {
                    list.add("§bReady to Advance");
                }
            } else {
                list.add("§bMastered!");
            }
            if (stack.getTagCompound().hasKey("player")) {
                String name = UserCache.INSTANCE.getCached(
                    AnvilUtil.uuidFromNBT(stack.getTagCompound().getTagList("player", 4))
                );

                if (name != null)
                    list.add("Owner: " + name);
            }
        } else if (stack.getTagCompound().getInteger("type") == 2) {
            list.add("EXP: " + stack.getTagCompound().getInteger("stored"));
        }
    }

    @Override
    public boolean getShareTag() {
        return true;
    }

    @Override
    public void
    onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
    block11: {
        EntityPlayer player;
        if (stack == null) {
            return;
        }
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (stack.hasTagCompound() && !stack.getTagCompound().hasKey("type")) {
            stack.getTagCompound().setInteger("type", 0);
        }
        if (stack.hasTagCompound() && !stack.getTagCompound().hasKey("player")
            && entity instanceof EntityPlayer) {
            player = (EntityPlayer) entity;
            stack.getTagCompound().setTag(
                "player", AnvilUtil.uuidToNBT(player.getUniqueID())
            );
        }
        if (TomeUtils.getTomeType(stack) != 0) {
            return;
        }
        try {
            if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("player")
                || !(entity instanceof EntityPlayer)
                || !Dartcraft.proxy.isSimulating(world))
                break block11;
            player = (EntityPlayer) entity;
            boolean isOwner
                = AnvilUtil.uuidFromNBT(stack.getTagCompound().getTagList("player", 4))
                      .equals(((EntityPlayer) entity).getUniqueID());
            if (isOwner) {
                if (stack.getTagCompound().getInteger("lastMasteryCheck") <= 0) {
                    stack.getTagCompound().setInteger("lastMasteryCheck", 20);
                    // TODO
                    //DartPluginAchievements.addToPlayer("mastery", player);
                } else {
                    stack.getTagCompound().setInteger(
                        "lastMasteryCheck",
                        stack.getTagCompound().getInteger("lastMasteryCheck") - 1
                    );
                }
                break block11;
            }
            stack.getTagCompound().setBoolean("dropOnce", true);
            for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
                ItemStack invStack = player.inventory.mainInventory[i];
                if (invStack == null || invStack.getItem() != this
                    || !invStack.hasTagCompound()
                    || !invStack.getTagCompound().hasKey("dropOnce"))
                    continue;
                invStack.getTagCompound().removeTag("dropOnce");
                DartUtils.dropInvincibleItem(
                    invStack, world, player.posX, player.posY, player.posZ, 0
                );
                player.inventory.mainInventory[i] = null;
                DartUtils.punishPlayer(player, 2.0f);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.uncommon;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!stack.hasTagCompound() || stack.getTagCompound().getInteger("type") != 2) {
            return stack;
        }
        NBTTagCompound comp = stack.getTagCompound();
        if (player.isSneaking()) {
            for (int i = 0; i < 10 && player.experienceTotal > 0; ++i) {
                player.addExperience(-1);
                if (player.experience < 0.0f) {
                    player.addExperienceLevel(-1);
                    player.experience = 0.95f;
                }
                comp.setInteger("stored", comp.getInteger("stored") + 1);
                if (comp.getInteger("stored") < 100000 || comp.getBoolean("triggered"))
                    continue;
                // TODO
                //DartPluginAchievements.addToPlayer("fillTome", player);
                comp.setBoolean("triggered", true);
            }
        } else {
            for (int i = 0; i < 10 && comp.getInteger("stored") > 0; ++i) {
                player.addExperience(1);
                comp.setInteger("stored", comp.getInteger("stored") - 1);
            }
        }
        return stack;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubItems(Item id, CreativeTabs tabs, List itemList) {
        ItemStack stack = new ItemStack((Item) this, 1);
        stack.setTagCompound(TomeUtils.initExpComp());
        ItemStack stack2 = new ItemStack((Item) this, 1);
        stack2.setTagCompound(TomeUtils.initUpgradeComp(false));
        itemList.add(stack);
        itemList.add(stack2);
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        NBTTagCompound comp = stack.getTagCompound();
        if (comp == null)
            return this.itemIcon;

        return this.icons[comp.getInteger("type") == 0 ? 1 : 0];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            switch (stack.getTagCompound().getInteger("type")) {
                case 0: {
                    return "item.forcetome_upgrade";
                }
                case 2: {
                    return "item.forcetome_experience";
                }
            }
        }

        return super.getUnlocalizedName(stack);
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public void registerIcons(IIconRegister reggie) {
        this.icons = IntStream.range(0, 2)
                         .mapToObj(i -> "dartcraft:forceTome" + i)
                         .map(reggie::registerIcon)
                         .toArray(IIcon[] ::new);
        this.itemIcon = this.icons[0];
    }
}
