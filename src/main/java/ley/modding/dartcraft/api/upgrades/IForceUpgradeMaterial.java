package ley.modding.dartcraft.api.upgrades;

import net.minecraft.item.Item;

public interface IForceUpgradeMaterial {
    Item getItem();

    int getItemMeta();

    int getUpgradeID();

    int getBonus();

    float getEfficiency();

    String getDescription();

    boolean isRequired();
}
