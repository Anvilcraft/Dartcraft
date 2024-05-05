package ley.modding.dartcraft.util;

import ley.modding.dartcraft.api.upgrades.ForceUpgrade;
import ley.modding.dartcraft.api.upgrades.IForceUpgradeMaterial;
import ley.modding.dartcraft.item.DartItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TomeUtils {
    public static final int TIER_TWO = 96;
    public static final int TIER_THREE = 270;
    public static final int TIER_FOUR = 600;
    public static final int TIER_FIVE = 1020;
    public static final int TIER_SIX = 1440;
    public static final int TIER_SEVEN = 2400;
    public static final int TYPE_UPGRADE = 0;
    public static final int TYPE_CRAFT = 1;
    public static final int TYPE_EXP = 2;

    public static int getTomeType(ItemStack stack) {
        if (stack != null && stack.getItem() == DartItems.forcetome
            && stack.hasTagCompound() && stack.getTagCompound().hasKey("type")) {
            return stack.getTagCompound().getInteger("type");
        }
        return -1;
    }

    public static int getPoints(ItemStack stack) {
        int points = 0;
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound comp = stack.getTagCompound();
            for (ForceUpgrade upgrade : ForceUpgradeManager.upgrades) {
                for (int i = 0; i < upgrade.getNumMats(); ++i) {
                    if (!comp.hasKey(upgrade.getName() + i))
                        continue;
                    points += TomeUtils.pointsFromTier(upgrade.getTier())
                        * comp.getInteger(upgrade.getName() + i);
                    if (comp.getInteger(upgrade.getName() + i) <= 0)
                        continue;
                    points += 25;
                }
            }
            points += comp.getInteger("bonus");
        }
        return points;
    }

    public static int getStoredTier(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            return stack.getTagCompound().getInteger("storedTier");
        }
        return 1;
    }

    public static void advanceTier(ItemStack stack) {
        if (stack != null) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setInteger(
                "storedTier", stack.getTagCompound().getInteger("storedTier") + 1
            );
            TomeUtils.canTomeAdvance(stack);
        }
    }

    private static int pointsFromTier(int tier) {
        switch (tier) {
            case 0: {
                return 1;
            }
            case 1: {
                return 5;
            }
            case 2: {
                return 15;
            }
            case 3: {
                return 20;
            }
            case 4: {
                return 25;
            }
            case 5:
            case 6: {
                return 40;
            }
        }
        return 0;
    }

    public static boolean canTomeAdvance(ItemStack stack) {
        int tier = TomeUtils.getStoredTier(stack);
        int newTier = TomeUtils.getTierFromPoints(TomeUtils.getPoints(stack));
        if (stack == null || !stack.hasTagCompound() || newTier <= tier) {
            return false;
        }
        NBTTagCompound comp = stack.getTagCompound();
        // TODO
        //if (!Config.requiredMaterials) {
        //    return true;
        //}
        for (ForceUpgrade upgrade : ForceUpgradeManager.upgrades) {
            for (int i = 0; i < upgrade.getNumMats(); ++i) {
                IForceUpgradeMaterial mat;
                if (upgrade.getTier() > tier
                    || (mat = upgrade.getMaterialAt(i)) != null
                        && mat.getDescription() == null
                    || comp.hasKey(upgrade.getName() + i)
                        && comp.getInteger(upgrade.getName() + i) > 0
                    || !mat.isRequired())
                    continue;
                stack.getTagCompound().setBoolean("canAdvance", false);
                return false;
            }
        }
        stack.getTagCompound().setBoolean("canAdvance", true);
        return true;
    }

    public static boolean getCanAdvanceStored(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            return stack.getTagCompound().getBoolean("canAdvance");
        }
        return false;
    }

    public static int getTierFromPoints(int points) {
        if (points < 96) {
            return 1;
        }
        if (points < 270) {
            return 2;
        }
        if (points < 600) {
            return 3;
        }
        if (points < 1020) {
            return 4;
        }
        if (points < 1440) {
            return 5;
        }
        if (points < 2400) {
            return 6;
        }
        return 7;
    }

    public static int getPointsToNext(ItemStack stack) {
        int points = TomeUtils.getPoints(stack);
        int tier = TomeUtils.getStoredTier(stack);
        if (tier < 2) {
            return 96 - points;
        }
        if (tier < 3) {
            return 270 - points;
        }
        if (tier < 4) {
            return 600 - points;
        }
        if (tier < 5) {
            return 1020 - points;
        }
        if (tier < 6) {
            return 1440 - points;
        }
        if (tier < 7) {
            return 2400 - points;
        }
        return 0;
    }

    public static NBTTagCompound initUpgradeComp(boolean debug) {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("type", 0);
        if (debug) {
            data.setInteger("storedTier", 7);
            data.setInteger("bonus", 2400);
            for (ForceUpgrade upgrade : ForceUpgradeManager.upgrades) {
                for (int i = 0; i < upgrade.getNumMats(); ++i) {
                    data.setInteger(upgrade.getName() + i, 1);
                }
            }
        } else {
            data.setInteger("storedTier", 1);
            data.setInteger("bonus", 0);
        }
        return data;
    }

    public static NBTTagCompound initExpComp() {
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("type", 2);
        data.setInteger("stored", 0);
        return data;
    }
}
