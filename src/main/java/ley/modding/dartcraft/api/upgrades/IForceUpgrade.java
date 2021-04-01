package ley.modding.dartcraft.api.upgrades;

import java.util.ArrayList;

public class IForceUpgrade {

   private int id = getUniqueUpgradeID();
   private String name;
   private String description;
   private int tier;
   private int maxLevel;
   private ArrayList materials = new ArrayList();


   public IForceUpgrade(int tier, String name, int maxLevel, String description) {
      this.tier = tier;
      this.name = name;
      this.maxLevel = maxLevel;
      this.description = description;
   }

   public void addMat(IForceUpgradeMaterial mat) {
      if(mat != null && this.materials != null) {
         this.materials.add(mat);
      }

   }

   public int getID() {
      return this.id;
   }

   public int getTier() {
      return this.tier;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public int getMaxLevel() {
      return this.maxLevel;
   }

   public int getNumMats() {
      return this.materials != null?this.materials.size():0;
   }

   public static int getUniqueUpgradeID() {
      try {
         return 1; //((Integer)ReflectionHelper.getMethod(DartAPI.manager, "getFirstUniqueUpgradeID", new Class[0]).invoke(DartAPI.manager, new Object[0])).intValue();
      } catch (Exception var1) {
         var1.printStackTrace();
         return -1;
      }
   }

   public IForceUpgradeMaterial getMaterialAt(int index) {
      return this.materials != null && this.materials.size() > index?(IForceUpgradeMaterial)this.materials.get(index):null;
   }

   public int getMaterialIndex(IForceUpgradeMaterial mat) {
      if(this.materials != null && this.materials.size() > 0) {
         for(int i = 0; i < this.materials.size(); ++i) {
            IForceUpgradeMaterial check = (IForceUpgradeMaterial)this.materials.get(i);
            if(check.getUpgradeID() == mat.getUpgradeID() && check.getItemID() == mat.getItemID() && check.getItemMeta() == mat.getItemMeta()) {
               return i;
            }
         }
      }

      return -1;
   }
}
