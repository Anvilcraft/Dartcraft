package ley.modding.dartcraft.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import ley.modding.dartcraft.util.FXUtils;
import net.anvilcraft.anvillib.network.AnvilPacket;
import net.anvilcraft.anvillib.network.IAnvilPacket;
import net.anvilcraft.anvillib.util.AnvilUtil;
import net.anvilcraft.anvillib.vector.Vec3;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.world.World;

@AnvilPacket(Side.CLIENT)
public class PacketFX implements IAnvilPacket {
    public Vec3 pos;

    public Type type;
    public int subType;
    public int area;
    public int amount;

    public PacketFX() {}

    public PacketFX(Vec3 pos, Type type, int subType, int area, int amount) {
        this.pos = pos;
        this.type = type;
        this.subType = subType;
        this.area = area;
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = Vec3.readFromByteBuf(buf);

        this.type = AnvilUtil.enumFromInt(Type.class, buf.readInt());
        this.subType = buf.readInt();
        this.area = buf.readInt();
        this.amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.pos.writeToByteBuf(buf);

        buf.writeInt(this.type.ordinal());
        buf.writeInt(this.subType);
        buf.writeInt(this.area);
        buf.writeInt(this.amount);
    }

    @Override
    public void handle(MessageContext ctx) {
        World world = FMLClientHandler.instance().getClientPlayerEntity().worldObj;
        WorldVec pos = this.pos.withWorld(world);
        switch (this.type) {
            case TIME:
                FXUtils.makeTimeEffects(pos, this.subType, this.amount, this.area);
                break;

            case CHANGE:
                FXUtils.makeWWEffects(
                    pos, 0xffffff, this.subType, this.amount, this.area
                );
                break;

            case CURE:
                FXUtils.makeCureEffects(pos, this.subType, 0x4bb218, this.amount);
                break;

            case HEAT:
                FXUtils.makeHeatEffects(pos, this.amount, this.area);
                break;

            case SPARKLES:
                FXUtils.makeShiny(pos, this.subType, 0xffff00, this.amount, true);

            case CHARGE:
                FXUtils.makeChargeEffects(pos, this.subType, 0xffffff, this.amount, true);

            case MAGIC:
                FXUtils.makeShiny(pos, this.subType, 0xffff00, this.amount, false);
        }
    }

    public static enum Type {
        TIME,
        CHANGE,
        CURE,
        HEAT,
        SPARKLES,
        CHARGE,
        MAGIC;
    }
}
