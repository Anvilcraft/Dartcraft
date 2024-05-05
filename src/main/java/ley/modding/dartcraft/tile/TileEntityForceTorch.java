package ley.modding.dartcraft.tile;

import java.util.List;

import cpw.mods.fml.common.Loader;
import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.entity.EntityTime;
import ley.modding.dartcraft.integration.ThaumCraftIntegration;
import ley.modding.dartcraft.network.PacketFX;
import ley.modding.dartcraft.util.DartUtils;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

public class TileEntityForceTorch extends TileEntity {
    public NBTTagCompound upgrades = new NBTTagCompound();
    public byte color = 0;
    public int timeType = 1;
    private int timeout;
    private int maxTimeout;

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        if (Dartcraft.proxy.isSimulating(this.worldObj) && this.upgrades != null
            && !this.upgrades.hasNoTags()) {
            ++this.timeout;
            if (this.timeout >= this.maxTimeout) {
                this.timeout = 0;
                this.maxTimeout = Config.torchFreq
                    + (int) (this.worldObj.rand.nextFloat() * (float) Config.torchFreq
                             / 2.0F);
                int time;
                if (this.upgrades.hasKey("Light")) {
                    time = this.upgrades.getInteger("Light");
                }

                if (this.upgrades.hasKey("Healing")) {
                    try {
                        AxisAlignedBB var13 = AxisAlignedBB.getBoundingBox(
                            (double) this.xCoord - (double) Config.torchDist,
                            (double) this.yCoord - (double) Config.torchDist,
                            (double) this.zCoord - (double) Config.torchDist,
                            (double) this.xCoord + (double) Config.torchDist,
                            (double) this.yCoord + (double) Config.torchDist,
                            (double) this.zCoord + (double) Config.torchDist
                        );
                        List<EntityLivingBase> j = this.worldObj.getEntitiesWithinAABB(
                            EntityLivingBase.class, var13
                        );
                        boolean k = false;
                        int tile = 1;

                        for (EntityLivingBase entity : j) {
                            if (entity != null) {
                                if (!entity.isEntityUndead()
                                    && !(entity instanceof EntityGhast)) {
                                    float entityUpgrades
                                        = (float) entity.getAttributeMap()
                                              .getAttributeInstanceByName(
                                                  "generic.maxHealth"
                                              )
                                              .getAttributeValue();
                                    if (entity.getHealth() < entityUpgrades) {
                                        entity.heal((float) (tile * 2));
                                        k = true;

                                        WorldVec pktPos = new WorldVec(entity);
                                        pktPos.y += entity.height / 2d;
                                        Dartcraft.channel.sendToAllAround(
                                            new PacketFX(
                                                pktPos, PacketFX.Type.CURE, 2, 0, 8 * tile
                                            ),
                                            pktPos.targetPoint(80d)
                                        );
                                    }
                                } else {
                                    entity.attackEntityFrom(DamageSource.magic, 2f);
                                }

                                if (k) {
                                    this.worldObj.playSoundEffect(
                                        (double) this.xCoord,
                                        (double) this.yCoord,
                                        (double) this.zCoord,
                                        "dartcraft:cure",
                                        0.5F,
                                        DartUtils.randomPitch()
                                    );
                                }
                            }
                        }
                    } catch (Exception var12) {
                        ;
                    }
                }

                if (this.upgrades.hasKey("Bane")) {
                    try {
                        AxisAlignedBB var13 = AxisAlignedBB.getBoundingBox(
                            (double) this.xCoord - (double) Config.torchDist,
                            (double) this.yCoord - (double) Config.torchDist,
                            (double) this.zCoord - (double) Config.torchDist,
                            (double) this.xCoord + (double) Config.torchDist,
                            (double) this.yCoord + (double) Config.torchDist,
                            (double) this.zCoord + (double) Config.torchDist
                        );
                        List<EntityLivingBase> j = this.worldObj.getEntitiesWithinAABB(
                            EntityLivingBase.class, var13
                        );
                        boolean k = false;

                        for (EntityLivingBase remEnt : j) {
                            if ((remEnt instanceof EntityMob
                                 || remEnt instanceof EntitySlime
                                 || remEnt instanceof EntityGhast)
                                && !(remEnt instanceof EntityWitch)
                                && !(remEnt instanceof EntityWither)) {
                                this.worldObj.removeEntity(remEnt);
                                k = true;

                                WorldVec pktPos = new WorldVec(remEnt);
                                pktPos.y += remEnt.height / 2d;
                                Dartcraft.channel.sendToAllAround(
                                    new PacketFX(pktPos, PacketFX.Type.CHANGE, 1, 0, 16),
                                    pktPos.targetPoint(80d)
                                );
                            }
                        }

                        if (k) {
                            this.worldObj.playSoundEffect(
                                (double) this.xCoord,
                                (double) this.yCoord,
                                (double) this.zCoord,
                                "random.pop",
                                1.0F,
                                DartUtils.randomPitch()
                            );
                        }
                    } catch (Exception var11) {
                        ;
                    }
                }

                if (this.upgrades.hasKey("Heat")) {
                    try {
                        AxisAlignedBB var13 = AxisAlignedBB.getBoundingBox(
                            (double) this.xCoord - (double) Config.torchDist,
                            (double) this.yCoord - (double) Config.torchDist,
                            (double) this.zCoord - (double) Config.torchDist,
                            (double) this.xCoord + (double) Config.torchDist,
                            (double) this.yCoord + (double) Config.torchDist,
                            (double) this.zCoord + (double) Config.torchDist
                        );
                        List<EntityLivingBase> j = this.worldObj.getEntitiesWithinAABB(
                            EntityLivingBase.class, var13
                        );
                        boolean k = false;
                        int tile = 1;

                        for (EntityLivingBase entity : j) {
                            if (entity != null && !entity.isImmuneToFire()) {
                                NBTTagCompound var20 = new NBTTagCompound();
                                if (entity instanceof EntityPlayer) {
                                    EntityPlayer player = (EntityPlayer) entity;
                                    // TODO: WTF
                                    //var20 = SocketHelper.getArmorCompound(player);
                                }

                                if (!var20.hasKey("Heat")
                                    || var20.getInteger("Heat") < 3) {
                                    entity.setFire(tile);
                                    entity.attackEntityFrom(
                                        DamageSource.inFire, 0.5F * (float) tile
                                    );
                                    k = true;

                                    WorldVec pktPos = new WorldVec(entity);
                                    pktPos.y += entity.height / 2d;
                                    Dartcraft.channel.sendToAllAround(
                                        new PacketFX(
                                            pktPos, PacketFX.Type.HEAT, 0, 0, 8 * tile
                                        ),
                                        pktPos.targetPoint(80d)
                                    );
                                }
                            }
                        }

                        if (k) {
                            this.worldObj.playSoundEffect(
                                (double) this.xCoord,
                                (double) this.yCoord,
                                (double) this.zCoord,
                                "dartcraft:ignite",
                                1.0F,
                                DartUtils.randomPitch()
                            );
                        }
                    } catch (Exception var10) {
                        ;
                    }
                }

                if (this.upgrades.hasKey("Repair") && Loader.isModLoaded("Thaumcraft")) {
                    try {
                    outer:
                        for (time = -Config.torchDist; time < Config.torchDist; ++time) {
                            for (int var15 = -Config.torchDist; var15 < Config.torchDist;
                                 ++var15) {
                                for (int var19 = -Config.torchDist;
                                     var19 < Config.torchDist;
                                     ++var19) {
                                    TileEntity tile = this.worldObj.getTileEntity(
                                        this.xCoord + time,
                                        this.yCoord + var15,
                                        this.zCoord + var19
                                    );

                                    if (ThaumCraftIntegration
                                            .isDeconstructorWithoutAspect(tile)) {
                                        ThaumCraftIntegration.setDeconAspect(tile);
                                        break outer;
                                    }
                                }
                            }
                        }
                    } catch (Exception var9) {
                        ;
                    }
                }

                if (this.upgrades.hasKey("Time") && Config.timeUpgradeTorch) {
                    EntityTime var14 = new EntityTime(this.worldObj, this.maxTimeout - 2);
                    var14.posX = (double) this.xCoord + 0.5D;
                    var14.posY = (double) this.yCoord + 0.5D;
                    var14.posZ = (double) this.zCoord + 0.5D;
                    var14.setType(this.timeType, false);
                    this.worldObj.spawnEntityInWorld(var14);
                }
            }
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();

        this.writeToNBT(nbt);

        return new S35PacketUpdateTileEntity(
            this.xCoord, this.yCoord, this.zCoord, this.getBlockMetadata(), nbt
        );
    }

    @Override
    public void onDataPacket(NetworkManager arg0, S35PacketUpdateTileEntity arg1) {
        this.readFromNBT(arg1.func_148857_g());
    }

    @Override
    public void readFromNBT(NBTTagCompound comp) {
        super.readFromNBT(comp);
        this.upgrades = comp.getCompoundTag("upgrades");
        this.color = comp.getByte("color");
        this.timeType = comp.getByte("timeType");
    }

    @Override
    public void writeToNBT(NBTTagCompound comp) {
        super.writeToNBT(comp);
        comp.setTag("upgrades", this.upgrades);
        comp.setByte("color", this.color);
        comp.setByte("timeType", (byte) this.timeType);
    }
}
