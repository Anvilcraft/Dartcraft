package ley.modding.dartcraft.network;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import ley.modding.dartcraft.client.gui.ContainerClipboard;
import net.anvilcraft.anvillib.network.AnvilPacket;
import net.anvilcraft.anvillib.network.IAnvilPacket;
import net.minecraft.entity.player.EntityPlayer;

@AnvilPacket(Side.SERVER)
public class PacketClipButton implements IAnvilPacket {
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

    @Override
    public void handle(MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        if (player != null) {
            ContainerClipboard clipboard
                = (player.openContainer != null
                   && player.openContainer instanceof ContainerClipboard)
                ? (ContainerClipboard) player.openContainer
                : null;
            if (clipboard != null)
                switch (this.button) {
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
    }
}
