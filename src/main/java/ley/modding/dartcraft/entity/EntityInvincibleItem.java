package ley.modding.dartcraft.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityInvincibleItem extends EntityItem {
    private boolean canDespawn;
    private boolean canBurn;
    private int life;

    public EntityInvincibleItem(World world) {
        super(world);
    }

    public EntityInvincibleItem(
        World world, double x, double y, double z, ItemStack contents, int timeout
    ) {
        super(world, x, y, z);
        this.delayBeforeCanPickup = 40;
        this.setEntityItemStack(contents);
        this.life = timeout;
        this.canDespawn = timeout > 0;
    }

    public void setCanBurn() {
        this.canBurn = true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound comp) {
        super.writeEntityToNBT(comp);
        comp.setBoolean("canDespawn", this.canDespawn);
        comp.setInteger("life", this.life);
        comp.setBoolean("canBurn", this.canBurn);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound comp) {
        super.readEntityFromNBT(comp);
        this.canDespawn = comp.getBoolean("canDespawn");
        this.life = comp.getInteger("life");
        this.canBurn = comp.getBoolean("canBurn");
    }

    @Override
    public void onUpdate() {
        if (this.delayBeforeCanPickup > 0) {
            --this.delayBeforeCanPickup;
        }

        if (this.canDespawn) {
            --this.life;
            if (this.life <= 0) {
                this.setDead();
            }
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;
        this.noClip = this.func_145771_j( // pushOutOfBlocks
            this.posX,
            (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D,
            this.posZ
        );
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        boolean var1 = (int) this.prevPosX != (int) this.posX
            || (int) this.prevPosY != (int) this.posY
            || (int) this.prevPosZ != (int) this.posZ;
        if (var1 || this.ticksExisted % 25 == 0) {
            if (this.worldObj
                    .getBlock(
                        MathHelper.floor_double(this.posX),
                        MathHelper.floor_double(this.posY),
                        MathHelper.floor_double(this.posZ)
                    )
                    .getMaterial()
                == Material.lava) {
                this.playSound(
                    "dartcraft:sparkle", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F
                );
            }

            if (!this.worldObj.isRemote) {
                this.aggregate();
            }
        }

        float motionMul = 0.98F;
        if (this.onGround) {
            motionMul = 0.58800006F;
            Block belowBlock = this.worldObj.getBlock(
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.boundingBox.minY) - 1,
                MathHelper.floor_double(this.posZ)
            );
            motionMul = belowBlock.slipperiness * 0.98F;
        }

        this.motionX *= (double) motionMul;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= (double) motionMul;
        if (this.onGround) {
            this.motionY *= -0.5D;
        }

        ItemStack var4 = this.getDataWatcher().getWatchableObjectItemStack(10);
        if (var4 != null && var4.stackSize <= 0) {
            this.setDead();
        }
    }

    @Override
    protected void dealFireDamage(int damage) {}

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    // TODO: WTF
    //@Override
    //protected void simulateLivingUpdate() {
    //    this.worldObj.theProfiler.startSection("entityBaseTick");
    //    if (this.ridingEntity != null && this.ridingEntity.isDead) {
    //        this.ridingEntity = null;
    //    }

    //    this.prevDistanceWalkedModified = this.distanceWalkedModified;
    //    this.prevPosX = this.posX;
    //    this.prevPosY = this.posY;
    //    this.prevPosZ = this.posZ;
    //    this.prevRotationPitch = this.rotationPitch;
    //    this.prevRotationYaw = this.rotationYaw;
    //    int var2;
    //    if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
    //        this.worldObj.theProfiler.startSection("portal");
    //        MinecraftServer var5 = ((WorldServer) this.worldObj).getMinecraftServer();
    //        var2 = this.getMaxInPortalTime();
    //        if (this.inPortal) {
    //            if (var5.getAllowNether()) {
    //                if (this.ridingEntity == null && this.timeInPortal++ >= var2) {
    //                    this.timeInPortal = var2;
    //                    this.timeUntilPortal = this.getPortalCooldown();
    //                    byte var6;
    //                    if (this.worldObj.provider.dimensionId == -1) {
    //                        var6 = 0;
    //                    } else {
    //                        var6 = -1;
    //                    }

    //                    this.travelToDimension(var6);
    //                }

    //                this.inPortal = false;
    //            }
    //        } else {
    //            if (this.timeInPortal > 0) {
    //                this.timeInPortal -= 4;
    //            }

    //            if (this.timeInPortal < 0) {
    //                this.timeInPortal = 0;
    //            }
    //        }

    //        if (this.timeUntilPortal > 0) {
    //            --this.timeUntilPortal;
    //        }

    //        this.worldObj.theProfiler.endSection();
    //    }

    //    if (this.isSprinting() && !this.isInWater()) {
    //        int var51 = MathHelper.floor_double(this.posX);
    //        var2 = MathHelper.floor_double(
    //            this.posY - 0.20000000298023224D - (double) this.yOffset
    //        );
    //        int var61 = MathHelper.floor_double(this.posZ);
    //        int var4 = this.worldObj.getBlockId(var51, var2, var61);
    //        if (var4 > 0) {
    //            this.worldObj.spawnParticle(
    //                "tilecrack_" + var4 + "_"
    //                    + this.worldObj.getBlockMetadata(var51, var2, var61),
    //                this.posX
    //                    + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width,
    //                this.boundingBox.minY + 0.1D,
    //                this.posZ
    //                    + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width,
    //                -this.motionX * 4.0D,
    //                1.5D,
    //                -this.motionZ * 4.0D
    //            );
    //        }
    //    }

    //    this.handleWaterMovement();
    //    if (this.handleLavaMovement()) {
    //        this.setOnFireFromLava();
    //        this.fallDistance *= 0.5F;
    //    }

    //    if (this.posY < -64.0D) {
    //        this.kill();
    //    }

    //    this.worldObj.theProfiler.endSection();
    //}

    @Override
    public void setFire(int time) {}

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    @Override
    public boolean canRenderOnFire() {
        return this.canBurn;
    }

    @SuppressWarnings("unchecked")
    private void aggregate() {
        for (EntityItem var2 : (List<EntityItem>) this.worldObj.getEntitiesWithinAABB(
                 EntityItem.class, this.boundingBox.expand(0.5D, 0.0D, 0.5D)
             )) {
            this.combineItems(var2);
        }
    }
}
