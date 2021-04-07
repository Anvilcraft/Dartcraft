package ley.modding.dartcraft.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import ley.modding.dartcraft.util.ForceUpgradeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.Random;

public class CommonProxy {

    public static Random rand = new Random();

    public void sendChatToPlayer(EntityPlayer player, String message) {
        player.addChatMessage(new ChatComponentText(message));
    }

    public boolean isSimulating(World world) {
        return true;
    }

    public Minecraft getClientInstance() {
        return FMLClientHandler.instance().getClient();
    }

    public void bindTexture(String texture) {
    }

    public void init() {
        ForceUpgradeManager.load();
    }

}
