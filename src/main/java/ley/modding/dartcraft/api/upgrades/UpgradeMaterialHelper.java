package ley.modding.dartcraft.api.upgrades;

import ley.modding.dartcraft.util.ForceUpgradeManager;
import net.minecraft.item.Item;

public class UpgradeMaterialHelper {
    public static IForceUpgradeMaterial createMaterial(
        Item item,
        int upgradeID,
        int bonus,
        float efficiency,
        String description,
        boolean required
    ) {
        try {
            if (ForceUpgradeManager.getFromID(upgradeID) == null)
                return null;
            return new UpgradeAdapter(
                item, 0, upgradeID, bonus, efficiency, description, required
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static IForceUpgradeMaterial createMaterial(
        Item item,
        int meta,
        int upgradeID,
        int bonus,
        float efficiency,
        String description,
        boolean required
    ) {
        try {
            if (ForceUpgradeManager.getFromID(upgradeID) == null)
                return null;
            return new UpgradeAdapter(
                item, meta, upgradeID, bonus, efficiency, description, required
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class UpgradeAdapter implements IForceUpgradeMaterial {
        private Item item;

        private int meta;

        private int upgradeID;
        private int bonus;
        private float efficiency;
        private String description;
        private boolean required;

        public UpgradeAdapter(
            Item item,
            int meta,
            int upgradeID,
            int bonus,
            float efficiency,
            String description,
            boolean required
        ) {
            this.item = item;
            this.meta = meta;
            this.upgradeID = upgradeID;
            this.bonus = bonus;
            this.efficiency = efficiency;
            this.description = description;
            this.required = required;
        }

        public int getUpgradeID() {
            return this.upgradeID;
        }

        public int getBonus() {
            return this.bonus;
        }

        public float getEfficiency() {
            return this.efficiency;
        }

        public Item getItem() {
            return this.item;
        }

        public int getItemMeta() {
            return this.meta;
        }

        public String getDescription() {
            return this.description;
        }

        public boolean isRequired() {
            return this.required;
        }
    }
}
