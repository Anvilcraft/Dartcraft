package ley.modding.dartcraft.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ley.modding.dartcraft.client.gui.ContainerClipboard;
import ley.modding.dartcraft.util.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;

public class PacketClipButton extends DartPacket implements IMessage {
    protected int button;

    public PacketClipButton() {}

    public PacketClipButton(EntityPlayer player, int button) {
        this.button = button;
        this.receiver = player.getCommandSenderName();
    }

    public boolean getToClient() {
        return false;
    }

    public boolean isDimPacket() {
        return false;
    }

    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        this.button = buf.readByte();
    }

    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeByte(this.button);
    }

    public static class Handler implements IMessageHandler<PacketClipButton, IMessage> {
        public IMessage onMessage(PacketClipButton packet, MessageContext ctx) {
            EntityPlayer player = EntityUtils.getPlayerByName(packet.getReceiver());
            if (player != null) {
                ContainerClipboard clipboard
                    = (player.openContainer != null
                       && player.openContainer instanceof ContainerClipboard)
                    ? (ContainerClipboard) player.openContainer
                    : null;
                if (clipboard != null)
                    switch (packet.button) {
                        case 0:
                            clipboard.balanceItems();
                            break;
                        case 1:
                            clipboard.doDistribute();
                            break;
                        case 2:
                            clipboard.clearMatrix();
                            break;
                    }
            }
            return null;
        }
    }
}
