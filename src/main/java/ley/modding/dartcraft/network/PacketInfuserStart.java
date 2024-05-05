package ley.modding.dartcraft.network;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import ley.modding.dartcraft.client.gui.ContainerMachine;
import ley.modding.dartcraft.tile.TileEntityInfuser;
import net.anvilcraft.anvillib.network.AnvilPacket;
import net.anvilcraft.anvillib.network.IAnvilPacket;
import net.minecraft.entity.player.EntityPlayer;

@AnvilPacket(Side.SERVER)
public class PacketInfuserStart implements IAnvilPacket {
    public PacketInfuserStart() {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void handle(MessageContext ctx) {
        EntityPlayer pl = ctx.getServerHandler().playerEntity;

        if (pl.openContainer instanceof ContainerMachine
            && ((ContainerMachine) pl.openContainer).machine
                instanceof TileEntityInfuser) {
            ((TileEntityInfuser) ((ContainerMachine) pl.openContainer).machine).go(pl);
        }
    }
}
