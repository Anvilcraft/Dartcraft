package ley.modding.dartcraft.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IBreakable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class EventHandler {

    @SubscribeEvent
    public void onItemDestroyed(PlayerDestroyItemEvent event) {
        ItemStack stack = event.original;
        EntityPlayer player = event.entityPlayer;
        if (stack != null && stack.getItem() instanceof IBreakable) {
            ItemStack ret = ((IBreakable)stack.getItem()).itemReturned();
            if (Dartcraft.proxy.isSimulating(player.worldObj)) {
                player.inventory.addItemStackToInventory(ret);
            }
        }
    }

}
