package ley.modding.dartcraft.infusion;

import java.util.ArrayList;

import ley.modding.dartcraft.api.recipe.ForceWildCard;
import ley.modding.dartcraft.api.upgrades.ForceUpgrade;
import net.minecraft.item.ItemStack;

public class ForceWildCards {
    private static ArrayList<ForceWildCard> wildCards = new ArrayList<>();

    public static void addWildCard(ForceWildCard wildCard) {
        if (wildCard != null) {
            wildCards.add(wildCard);
        }
    }

    public static ForceWildCard getWildCard(ItemStack stack) {
        ForceWildCard card = null;
        for (ForceWildCard check : wildCards) {
            if (stack != null && !check.ignoreMeta()
                    && ItemStack.areItemStacksEqual(
                        check.getInput(),
                        new ItemStack(stack.getItem(), 1, stack.getItemDamage())
                    )
                || check.ignoreMeta() && stack.getItem() == check.getInput().getItem()) {
                card = check;
                break;
            }
        }

        return card;
    }

    public static boolean
    isUpgradeValidForWildCard(ForceUpgrade upgrade, ItemStack stack) {
        ForceWildCard card = getWildCard(stack);
        if (card != null && card.getUpgrades() != null && card.getUpgrades().length > 0) {
            ForceUpgrade[] arr$ = card.getUpgrades();
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                ForceUpgrade upCheck = arr$[i$];
                if (upCheck.equals(upgrade)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isWildCard(ItemStack stack) {
        return getWildCard(stack) != null;
    }
}
