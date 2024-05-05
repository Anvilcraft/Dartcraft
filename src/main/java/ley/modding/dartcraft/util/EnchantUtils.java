package ley.modding.dartcraft.util;

import java.util.ArrayList;

import cpw.mods.fml.common.Loader;
import ley.modding.dartcraft.integration.ThaumCraftIntegration;
import ley.modding.dartcraft.item.tool.ItemForceAxe;
import ley.modding.dartcraft.item.tool.ItemForcePickaxe;
import ley.modding.dartcraft.item.tool.ItemForceShears;
import ley.modding.dartcraft.item.tool.ItemForceShovel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class EnchantUtils {
    public static NBTTagList getRealEnchants(ItemStack stack, NBTTagCompound upgrades) {
        NBTTagList realEnchants = new NBTTagList();
        ArrayList<String> buffer = new ArrayList<>();
        String[] names = new String[] { "Luck",   "Heat",  "Touch",  "Sturdy", "Force",
                                        "Damage", "Light", "Repair", "Soul" };
        int nnames = names.length;

        int level;
        for (level = 0; level < nnames; ++level) {
            String name1 = names[level];
            if (upgrades.hasKey(name1)) {
                buffer.add(name1);
            }
        }

        try {
            if (upgrades != null && buffer != null && buffer.size() > 0) {
                for (int var10 = 0; var10 < buffer.size(); ++var10) {
                    String enchName = buffer.get(var10);
                    level = upgrades.getInteger(enchName);
                    if (enchName.equals("Luck")) {
                        if (stack.getItem() instanceof ItemForcePickaxe
                            || stack.getItem() instanceof ItemForceShovel
                            || stack.getItem() instanceof ItemForceAxe) {
                            realEnchants.appendTag(simulateEnchant(35, level));
                        }

                        // TODO: sword
                        //if (stack.getItem() instanceof ItemForceSword) {
                        //    realEnchants.appendTag(simulateEnchant(21, level));
                        //}
                    }

                    //if (var11.equals("Heat")) { }

                    if (enchName.equals("Touch")
                        && (stack.getItem() instanceof ItemForcePickaxe
                            || stack.getItem() instanceof ItemForceShovel
                            || stack.getItem() instanceof ItemForceAxe)) {
                        realEnchants.appendTag(simulateEnchant(33, 1));
                    }

                    if (enchName.equals("Sturdy")
                        && (stack.getItem() instanceof ItemForcePickaxe
                            || stack.getItem() instanceof ItemForceShovel
                            || stack.getItem() instanceof ItemForceAxe)) {
                        realEnchants.appendTag(simulateEnchant(34, level));
                    }

                    // TODO: sword
                    //if (enchName.equals("Force")
                    //    && stack.getItem() instanceof ItemForceSword) {
                    //    realEnchants.appendTag(simulateEnchant(19, level));
                    //}

                    // TODO: bow
                    //if (enchName.equals("Damage")
                    //    && stack.getItem() instanceof ItemForceBow) {
                    //    realEnchants.appendTag(simulateEnchant(48, level));
                    //}

                    // TODO: sword
                    //if (enchName.equals("Light")
                    //    && stack.getItem() instanceof ItemForceSword) {
                    //    realEnchants.appendTag(simulateEnchant(17, level));
                    //}

                    if (enchName.equals("Repair") && Loader.isModLoaded("Thaumcraft")
                        && (stack.getItem() instanceof ItemForcePickaxe
                            || stack.getItem() instanceof ItemForceShovel
                            || stack.getItem() instanceof ItemForceAxe
                            // TODO: sword
                            //|| stack.getItem() instanceof ItemForceSword
                            || stack.getItem() instanceof ItemForceShears)) {
                        realEnchants.appendTag(simulateEnchant(
                            ThaumCraftIntegration.ENCH_REPAIR.effectId, level
                        ));
                    }
                }
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return realEnchants;
    }

    public static NBTTagCompound simulateEnchant(int type, int level) {
        NBTTagCompound realEnch = new NBTTagCompound();
        realEnch.setShort("id", (short) type);
        realEnch.setShort("lvl", (short) level);
        return realEnch;
    }
}
