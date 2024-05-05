package ley.modding.dartcraft.item;

import java.util.List;

import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.Util;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFortuneCookie extends ItemDartFood {
    public ItemFortuneCookie() {
        super(2, 0.25f, false);
        this.setMaxStackSize(16);
        this.setAlwaysEdible();
        Util.configureItem(this, "fortunecookie");
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes", "ALEC" })
    public void
    addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add("Eat me.");
    }

    @Override
    public void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        ItemStack fortuneStack = ((ItemFortune)DartItems.fortune).createNewStack();
        if (!player.inventory.addItemStackToInventory(fortuneStack)) {
            DartUtils.dropItem(
                fortuneStack,
                new WorldVec(player)
            );
        }
    }
}
