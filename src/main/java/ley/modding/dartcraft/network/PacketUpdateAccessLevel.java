package ley.modding.dartcraft.network;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import ley.modding.dartcraft.api.AccessLevel;
import ley.modding.dartcraft.client.gui.ContainerMachine;
import net.anvilcraft.anvillib.network.AnvilPacket;
import net.anvilcraft.anvillib.network.IAnvilPacket;
import net.anvilcraft.anvillib.util.AnvilUtil;
import net.minecraft.entity.player.EntityPlayer;

@AnvilPacket(Side.SERVER)
public class PacketUpdateAccessLevel implements IAnvilPacket {
    AccessLevel access;

    public PacketUpdateAccessLevel() {}

    public PacketUpdateAccessLevel(AccessLevel access) {
        this.access = access;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte((byte) this.access.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.access = AnvilUtil.enumFromInt(AccessLevel.class, buf.readByte());
    }

    @Override
    public void handle(MessageContext ctx) {
        EntityPlayer pl = ctx.getServerHandler().playerEntity;

        if (pl.openContainer instanceof ContainerMachine) {
            ((ContainerMachine) pl.openContainer).machine.access = this.access;
            ((ContainerMachine) pl.openContainer).machine.markDirty();
        }
    }
}
