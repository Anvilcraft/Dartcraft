package ley.modding.dartcraft.network;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import ley.modding.dartcraft.client.gui.ContainerMachine;
import net.anvilcraft.anvillib.network.AnvilPacket;
import net.anvilcraft.anvillib.network.IAnvilPacket;
import net.minecraft.entity.player.EntityPlayer;

@AnvilPacket(Side.SERVER)
public class PacketDesocket implements IAnvilPacket {
    int index;

    public PacketDesocket() {}

    public PacketDesocket(int index) {
        this.index = index;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.index);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.index = buf.readInt();
    }

    @Override
    public void handle(MessageContext ctx) {
        EntityPlayer pl = ctx.getServerHandler().playerEntity;

        if (pl.openContainer instanceof ContainerMachine) {
            ((ContainerMachine) pl.openContainer).desocket();
        }
    }
}
