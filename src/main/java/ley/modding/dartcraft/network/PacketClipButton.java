package ley.modding.dartcraft.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ley.modding.dartcraft.client.gui.ContainerClipboard;
import net.minecraft.entity.player.EntityPlayer;

public class PacketClipButton implements IMessage {
    protected int button;

    public PacketClipButton() {}

    public PacketClipButton(int button) {
        this.button = button;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.button = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.button);
    }

    public static class Handler implements IMessageHandler<PacketClipButton, IMessage> {
        @Override
        public IMessage onMessage(PacketClipButton packet, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
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
