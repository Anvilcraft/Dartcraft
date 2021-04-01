package ley.modding.dartcraft.api.upgrades;


public interface IForceUpgradeMaterial {

   int getItemID();

   int getItemMeta();

   int getUpgradeID();

   int getBonus();

   float getEfficiency();

   String getDescription();

   boolean isRequired();
}
