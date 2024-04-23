package ley.modding.dartcraft.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ley.modding.dartcraft.api.IBaneable;
import ley.modding.dartcraft.util.UpgradeHelper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent.CanUpdate;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class TimeHandler {
    @SubscribeEvent
    public void canUpdate(CanUpdate e) {
        if (e.entity == null || e.entity.worldObj.isRemote)
            return;

        try {
            NBTTagCompound ex = UpgradeHelper.getDartData(e.entity);
            if (ex.getInteger("timeImmune") > 0) {
                return;
            }

            if (ex.hasKey("time")) {
                int type = ex.getInteger("time");
                int time = ex.getInteger("timeTime");
                switch (type) {
                    case 0:
                    default:
                        break;
                    case 1:
                        e.canUpdate = false;
                        break;
                    case 2:
                        if (time % 8 != 0) {
                            e.canUpdate = false;
                        }
                        break;
                    case 3:
                        e.canUpdate = true;
                        break;
                    case 4:
                        e.canUpdate = true;
                }
            }

            if (ex.hasKey("frozen")) {
                e.canUpdate = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public void updateEntity(LivingUpdateEvent e) {
        if (e.entity == null || e.entity.worldObj.isRemote)
            return;
        try {

            NBTTagCompound ex = UpgradeHelper.getDartData(e.entity);
            if (ex.getInteger("timeImmune") > 0) {
                return;
            }

            if (ex.hasKey("time") && !ex.getBoolean("updateCalling")) {
                int type = ex.getInteger("time");
                int time = ex.getInteger("timeTime");
                --time;
                int i;
                switch (type) {
                    case 0:
                    case 2:
                    default:
                        if (time % 8 != 0) {
                            e.entity.motionX = 0.0D;
                            e.entity.motionY = 0.0D;
                            e.entity.motionZ = 0.0D;
                            e.entity.posX = e.entity.prevPosX;
                            e.entity.posY = e.entity.prevPosY;
                            e.entity.posZ = e.entity.prevPosZ;
                            e.entity.rotationPitch = e.entity.prevRotationPitch;
                            e.entity.rotationYaw = e.entity.prevRotationYaw;
                            e.setCanceled(true);
                            --e.entity.hurtResistantTime;
                        }
                        break;
                    case 1:
                        e.entity.motionX = 0.0D;
                        e.entity.motionY = 0.0D;
                        e.entity.motionZ = 0.0D;
                        e.entity.posX = e.entity.prevPosX;
                        e.entity.posY = e.entity.prevPosY;
                        e.entity.posZ = e.entity.prevPosZ;
                        e.entity.rotationPitch = e.entity.prevRotationPitch;
                        e.entity.rotationYaw = e.entity.prevRotationYaw;
                        e.setCanceled(true);
                        --e.entity.hurtResistantTime;
                        if (e.entity instanceof EntityCreeper) {
                            EntityCreeper creep = (EntityCreeper) e.entity;
                            creep.fuseTime = 10;
                            creep.explosionRadius = 0;
                        }

                        if (e.entity instanceof IBaneable) {
                            ((IBaneable) e.entity).setBaned();
                        }
                        break;
                    case 3:
                        ex.setBoolean("updateCalling", true);

                        for (i = 0; i < 3; ++i) {
                            e.entity.onUpdate();
                        }

                        ex.removeTag("updateCalling");
                        break;
                    case 4:
                        ex.setBoolean("updateCalling", true);

                        for (i = 0; i < 11; ++i) {
                            e.entity.onUpdate();
                        }

                        ex.removeTag("updateCalling");
                }

                // TODO: WTF
                //if (e.entity instanceof EntityBeeSwarm && (type == 3 || type == 4)) {
                //    ((EntityBeeSwarm) e.entity).lifeTime += type == 3 ? 3 : 11;
                //}

                if (time > 0) {
                    ex.setInteger("timeTime", time);
                } else {
                    ex.removeTag("timeTime");
                    ex.removeTag("time");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
