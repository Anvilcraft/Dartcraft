package ley.modding.dartcraft.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import ley.modding.dartcraft.util.FXUtils;
import net.minecraft.world.World;

public class PacketFX implements IMessage {
    public double x;
    public double y;
    public double z;

    public Type type;
    public int subType;
    public int area;
    public int amount;

    public PacketFX() {}

    public PacketFX(
        double x, double y, double z, Type type, int subType, int area, int amount
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.subType = subType;
        this.area = area;
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();

        this.type = Type.fromInt(buf.readInt());
        this.subType = buf.readInt();
        this.area = buf.readInt();
        this.amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);

        buf.writeInt(this.type.ordinal());
        buf.writeInt(this.subType);
        buf.writeInt(this.area);
        buf.writeInt(this.amount);
    }

    public static enum Type {
        TIME,
        CHANGE,
        CURE,
        HEAT;

        public static Type fromInt(int i) {
            if (i >= 0 && i < Type.values().length)
                return Type.values()[i];
            return null;
        }
    }

    public static class Handler implements IMessageHandler<PacketFX, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketFX pkt, MessageContext ctx) {
            World world = FMLClientHandler.instance().getClientPlayerEntity().worldObj;
            switch (pkt.type) {
                case TIME:
                    FXUtils.makeTimeEffects(
                        world, pkt.x, pkt.y, pkt.z, pkt.subType, pkt.amount, pkt.area
                    );
                    break;

                case CHANGE:
                    FXUtils.makeWWEffects(
                        world,
                        pkt.x,
                        pkt.y,
                        pkt.z,
                        0xffffff,
                        pkt.subType,
                        pkt.amount,
                        pkt.area
                    );
                    break;

                case CURE:
                    FXUtils.makeCureEffects(
                        world, pkt.x, pkt.y, pkt.z, pkt.subType, 0x4bb218, pkt.amount
                    );
                    break;

                case HEAT:
                    FXUtils.makeHeatEffects(
                        world, pkt.x, pkt.y, pkt.z, pkt.amount, pkt.area
                    );
                    break;
            }

            return null;
        }
    }
}