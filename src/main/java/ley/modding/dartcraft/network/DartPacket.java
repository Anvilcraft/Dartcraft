package ley.modding.dartcraft.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public abstract class DartPacket implements IMessage {
    protected String receiver;

    protected int dimensionID;

    protected NetworkRegistry.TargetPoint point;

    public abstract boolean getToClient();

    public abstract boolean isDimPacket();

    public int getDimID() {
        return this.dimensionID;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public NetworkRegistry.TargetPoint getLocation() {
        return this.point;
    }

    public void fromBytes(ByteBuf buf) {
        try {
            if (isDimPacket())
                this.dimensionID = buf.readByte();
            int nameSize = buf.readByte();
            if (nameSize > 0) {
                this.receiver = "";
                for (int i = 0; i < nameSize; i++)
                    this.receiver += buf.readChar();
            }
            if (buf.readBoolean())
                this.point = new NetworkRegistry.TargetPoint(
                    buf.readInt(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble()
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toBytes(ByteBuf buf) {
        if (this.receiver == null)
            this.receiver = "";
        try {
            if (isDimPacket())
                buf.writeByte(this.dimensionID);
            buf.writeByte((this.receiver != null) ? this.receiver.length() : 0);
            for (int i = 0; i < this.receiver.length(); i++)
                buf.writeChar(this.receiver.charAt(i));
            buf.writeBoolean((this.point != null));
            if (this.point != null) {
                buf.writeInt(this.point.dimension);
                buf.writeDouble(this.point.x);
                buf.writeDouble(this.point.y);
                buf.writeDouble(this.point.z);
                buf.writeDouble(this.point.range);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
