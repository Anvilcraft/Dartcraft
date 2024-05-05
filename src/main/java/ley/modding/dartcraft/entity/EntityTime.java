package ley.modding.dartcraft.entity;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.network.PacketFX;
import ley.modding.dartcraft.tile.TileEntityForceTorch;
import ley.modding.dartcraft.util.UpgradeHelper;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityTime extends Entity {
    public static final int TYPE_NONE = 0;
    public static final int TYPE_STOP = 1;
    public static final int TYPE_SLOW = 2;
    public static final int TYPE_FAST = 3;
    public static final int TYPE_HYPER = 4;
    public int type;
    public static double RANGE = 6.0D;
    private int lifeTime;
    private boolean checked;

    public EntityTime(World world) {
        super(world);
        this.lifeTime = 200;
        this.checked = false;
    }

    public EntityTime(World world, int life) {
        this(world);
        this.lifeTime = life;
    }

    @Override
    protected void entityInit() {}

    @Override
    public void onUpdate() {
        --this.lifeTime;
        if (this.lifeTime <= 0) {
            this.setDead();
        } else {
            this.customTick();
        }
    }

    private void customTick() {
        if (Dartcraft.proxy.isSimulating(this.worldObj) && this.lifeTime % 20 == 0
            || !this.checked) {
            this.spawnParticles();
        }

        if (this.lifeTime % 5 == 0 || !this.checked) {
            this.checkEntities();
        }
    }

    public void checkEntities() {
        try {
            if (!Config.timeAffectBlocks && !Config.timeAffectTiles
                && !Config.timeUpgradeRod && !Config.timeUpgradeSword
                && !Config.timeUpgradeTorch) {
                return;
            }

            AxisAlignedBB e = AxisAlignedBB.getBoundingBox(
                this.posX - RANGE,
                this.posY - RANGE,
                this.posZ - RANGE,
                this.posX + RANGE,
                this.posY + RANGE,
                this.posZ + RANGE
            );
            ((List<Entity>) this.worldObj.getEntitiesWithinAABB(Entity.class, e))
                .stream()
                .filter(
                    j
                    -> j != null && !(j instanceof EntityTime)
                        && !(j instanceof EntityPlayer) && !(j instanceof EntityBottle)
                )
                .forEach(j -> {
                    NBTTagCompound k = UpgradeHelper.getDartData(j);
                    if (!(j instanceof EntityFrozenItem) && !(j instanceof EntityItem)) {
                        if (k.getInteger("timeImmune") > 0) {
                            return;
                        }

                        k.setInteger("time", this.type);
                        k.setInteger("timeTime", this.lifeTime);
                    }

                    switch (this.type) {
                        case 1:
                            if (j instanceof EntityArrow
                                && (j.posX != j.prevPosX || j.posY != j.prevPosY
                                    || j.posZ != j.prevPosZ)) {
                                EntityFrozenItem chance = new EntityFrozenItem(
                                    this.worldObj, j, this.lifeTime
                                );
                                this.worldObj.removeEntity(j);
                                this.worldObj.spawnEntityInWorld(chance);
                            } else if (j instanceof EntityItem) {
                                EntityItem var19 = (EntityItem) j;
                                if (k.hasKey("timeImmune")) {
                                    k.setInteger(
                                        "timeImmune", k.getInteger("timeImmune") - 1
                                    );
                                    if (k.getInteger("timeImmune") <= 0) {
                                        k.removeTag("timeImmune");
                                    }
                                } else if(var19.getEntityItem() != null && var19.getEntityItem().stackSize >= 1) {
                                    short freq = 600;
                                    EntityFrozenItem e1 = new EntityFrozenItem(
                                        this.worldObj, var19, freq
                                    );
                                    var19.setEntityItemStack(
                                        var19.getEntityItem().splitStack(0)
                                    );
                                    this.worldObj.removeEntity(var19);
                                    this.worldObj.spawnEntityInWorld(e1);
                                }
                            }

                        //case 0:
                        //case 2:
                        //case 3:
                        //case 4:
                        default:
                            break;
                    }
                });
            ;

            if (this.type == 3 || this.type == 4) {
                for (int x = (int) (this.posX - RANGE); x < (int) (this.posX + RANGE);
                     ++x) {
                    for (int y = (int) (this.posY - RANGE); y < (int) (this.posY + RANGE);
                         ++y) {
                        for (int z = (int) (this.posZ - RANGE);
                             z < (int) (this.posZ + RANGE);
                             ++z) {
                            float var20 = this.type == 4 ? 0.5F : 0.1F;

                            try {
                                if (Config.timeAffectBlocks) {
                                    Block block = this.worldObj.getBlock(x, y, z);
                                    if (block != Blocks.air) {
                                        if (block != null
                                            && var20 >= this.rand.nextFloat()) {
                                            block.updateTick(
                                                this.worldObj, x, y, z, this.rand
                                            );
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            Block var21 = this.type == 4 ? Blocks.glass : Blocks.planks;

                            try {
                                TileEntity var23 = this.worldObj.getTileEntity(x, y, z);
                                if (var23 != null && !(var23 instanceof TileEntityForceTorch)
                                    && Config.timeAffectTiles
                                    /*&& (!Config.timeAffectWhitelist
                                        || DartPluginForceWrench.isTileBlacklisted(
                                            var23.getClass()
                                        ))*/) {
                                    // TODO: WTF
                                    //if (var23 instanceof TileEntityMobSpawner) {
                                    //    TileEntityMobSpawner l
                                    //        = (TileEntityMobSpawner) var23;
                                    //    if (!(l.getSpawnerLogic()
                                    //              instanceof CustomEntityLogic)) {
                                    //        String logic = l.getSpawnerLogic()
                                    //                           .getEntityNameToSpawn();
                                    //        CustomEntityLogic logic1
                                    //            = new CustomEntityLogic(l,
                                    //            this.type);
                                    //        SpawnerReflector.setLogic(l, logic1);
                                    //    } else {
                                    //        CustomEntityLogic var25
                                    //            = (CustomEntityLogic)
                                    //            l.getSpawnerLogic();
                                    //        var25.setHyper();
                                    //    }
                                    //}

                                    // TODO: WTF
                                    //if (!ForestryTimer.handleTile(var23, var21)) {
                                    //    for (int var24 = 0; var24 < var21; ++var24)
                                    //    {
                                    //        var23.updateEntity();
                                    //    }
                                    //}
                                }
                            } catch (Exception var14) {
                                var14.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception var15) {
            var15.printStackTrace();
        }

        this.checked = true;
    }

    public void spawnParticles() {
        if (this.lifeTime >= 20) {
            byte type = 4;
            switch (this.type) {
                case 0:
                    return;
                case 1:
                case 2:
                    type = 3;
                    break;
                case 3:
                    type = 2;
                    break;
                case 4:
                    type = 1;
            }

            WorldVec pos = new WorldVec(this);
            Dartcraft.channel.sendToAllAround(
                new PacketFX(pos, PacketFX.Type.TIME, type, 0, 1), pos.targetPoint(80d)
            );
        }
    }

    public boolean setType(int type, boolean playSound) {
        if (type >= 0 && type <= 4) {
            this.type = type;
            if (playSound) {
                String sound = "dartcraft:";
                switch (this.type) {
                    case 1:
                    case 2:
                        sound = sound + "slowDown";
                        break;
                    case 3:
                    case 4:
                        sound = sound + "speedUp";
                }

                if (this.worldObj != null) {
                    this.worldObj.playSoundAtEntity(this, sound, 1.0F, 1.0F);
                }
            }

            return true;
        } else {
            this.type = 0;
            return false;
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound comp) {
        try {
            this.setType(comp.getInteger("timeType"), false);
            this.lifeTime = comp.getInteger("lifeTime");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound comp) {
        try {
            comp.setInteger("timeType", this.type);
            comp.setInteger("lifeTime", this.lifeTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
