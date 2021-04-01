package ley.modding.dartcraft.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommonProxy {

    public void sendChatToPlayer(EntityPlayer player, String message) {
        player.addChatMessage(new ChatComponentText(message));
    }

    public boolean isSimulating(World world) {
        return true;
    }

}
