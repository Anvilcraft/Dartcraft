package ley.modding.dartcraft.entity;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IBottleRenderable;
import ley.modding.dartcraft.item.Items;
import ley.modding.dartcraft.util.EntityUtils;
import ley.modding.dartcraft.util.ItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityFlyingFlask extends EntityThrowable implements IBottleRenderable {

    public EntityLivingBase contained;

    private boolean creative = false;

    private boolean capture = false;

    public EntityFlyingFlask(World world) {
        super(world);
    }

    public EntityFlyingFlask(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public EntityFlyingFlask(World world, EntityLivingBase thrower, ItemStack flaskStack) {
        super(world, thrower);
        try {
            if (thrower instanceof EntityPlayer && ((EntityPlayer)thrower).capabilities.isCreativeMode)
                this.creative = true;
            if (flaskStack != null) {
                NBTTagCompound comp = (NBTTagCompound)flaskStack.getTagCompound().copy();
                Entity entity = EntityList.createEntityFromNBT(comp, world);
                NBTTagCompound dartTag = EntityUtils.getModComp(entity);
                dartTag.removeTag("time");
                dartTag.removeTag("timeTime");
                dartTag.setInteger("timeImmune", 10);
                this.contained = (EntityLivingBase)entity;
            } else {
                this.capture = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void entityInit() {
        super.entityInit();
        ((Entity)this).getDataWatcher().addObject(12, new ItemStack(Items.entitybottle));
    }

    protected void onImpact(MovingObjectPosition pos) {
        if (Dartcraft.proxy.isSimulating(((Entity)this).worldObj)) {
            if (this.contained != null) {
                this.contained.setPosition(((Entity)this).posX, ((Entity)this).posY, ((Entity)this).posZ);
                ((Entity)this).worldObj.spawnEntityInWorld((Entity)this.contained);
                if (!this.creative) {
                    ItemStack flaskStack = new ItemStack(Items.forceflask);
                    ItemUtils.dropItem(flaskStack, ((Entity)this).worldObj, ((Entity)this).posX, ((Entity)this).posY, ((Entity)this).posZ);
                }
                ((Entity)this).worldObj.playSoundAtEntity((Entity)this, "random.pop", 1.0F,
                        EntityUtils.randomPitch());
            } else if (this.capture) {
                if (!bottleEntity(pos.entityHit)) {
                    ((Entity)this).worldObj.playSoundAtEntity((Entity)this, "random.pop", 1.0F,
                            EntityUtils.randomPitch());
                    ItemUtils.dropItem(new ItemStack(Items.forceflask), ((Entity)this).worldObj, ((Entity)this).posX, ((Entity)this).posY, ((Entity)this).posZ);
                }
            }
            setDead();
        }
    }

    private boolean bottleEntity(Entity entity) {
        try {
            if (entity == null)
                return false;
            EntityLivingBase victim = null;
            if (entity instanceof EntityLivingBase)
                victim = (EntityLivingBase)entity;
            if (victim != null && !((Entity)victim).isDead && victim.getHealth() > 0.0F) {
                boolean nope = false;
                /*boolean whitelisted = !Config.entityWhitelist;
                if (!whitelisted && PluginBottles.whitelist != null)
                    for (String check : PluginBottles.whitelist) {
                        if (check != null && check.equals(victim.getClass().getCanonicalName())) {
                            whitelisted = true;
                            break;
                        }
                    }
                if (!whitelisted)
                    nope = true;*/
                if (victim instanceof EntityPlayer || victim instanceof EntityBottle)
                    nope = true;
                if (!nope && (victim instanceof net.minecraft.entity.monster.EntityMob || victim instanceof net.minecraft.entity.monster.EntityGhast)) {
                    float maxHealth = 0.0F;
                    try {
                        maxHealth = (float)victim.getAttributeMap().getAttributeInstanceByName("generic.maxHealth").getAttributeValue();
                    } catch (Exception e) {}
                    float ratio = victim.getHealth() / maxHealth;
                    float limit = 0.25F;
                    if (ratio >= limit && maxHealth >= 5.0F)
                        nope = true;
                    NBTTagCompound dartTag = EntityUtils.getModComp(entity);
                    dartTag.removeTag("time");
                    dartTag.removeTag("timeTime");
                    dartTag.setInteger("timeImmune", 5);
                }
                if (nope) {
                    ((Entity)this).worldObj.playSoundAtEntity((Entity)victim, "dartcraft:nope", 2.0F,
                            EntityUtils.randomPitch());
                    return false;
                }
                ItemStack bottleStack = EntityUtils.bottleEntity((Entity)victim);
                ((Entity)this).worldObj.playSoundAtEntity((Entity)victim, "dartcraft:swipe", 2.0F,
                        EntityUtils.randomPitch());
                ((Entity)this).worldObj.removeEntity((Entity)victim);
                victim = null;
                ItemUtils.dropItem(bottleStack, ((Entity)this).worldObj, ((Entity)this).posX, ((Entity)this).posY, ((Entity)this).posZ);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ItemStack getEntityItem() {
        return ((Entity)this).getDataWatcher().getWatchableObjectItemStack(12);
    }

    public void setEntityItem(ItemStack stack) {
        ((Entity)this).getDataWatcher().updateObject(12, stack);
        try {
            getEntityData().setString("name", stack.getTagCompound().getString("id"));
        } catch (Exception e) {}
    }

}
