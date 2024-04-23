package ley.modding.dartcraft.entity;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IBottleRenderable;
import ley.modding.dartcraft.proxy.CommonProxy;
import ley.modding.dartcraft.util.UpgradeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityFrozenItem extends EntityLiving implements IBottleRenderable {
    public int frozenTime;
    public int savedSpan;
    public float storedRotation;
    protected double storedMotionX;
    protected double storedMotionY;
    protected double storedMotionZ;
    private int timeout;
    public NBTTagCompound arrow;
    public NBTTagCompound dartArrow;

    public EntityFrozenItem(World world) {
        super(world);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.storedRotation = CommonProxy.rand.nextFloat() * 2.0F;
        float size = 0.1F;
        this.ySize = size;
        this.setSize(size, size);
        this.boundingBox.maxX = (double) size;
        this.boundingBox.maxY = (double) size;
        this.boundingBox.maxZ = (double) size;
    }

    public EntityFrozenItem(World world, Entity item, int time) {
        this(world);
        this.posX = item.posX;
        this.posY = item.posY;
        this.posZ = item.posZ;
        this.storedMotionX = item.motionX;
        this.storedMotionY = item.motionY;
        this.storedMotionZ = item.motionZ;
        this.frozenTime = time;
        if (item instanceof EntityItem) {
            EntityItem entityItem = (EntityItem) item;
            this.savedSpan = entityItem.lifespan;
            this.setEntityItem(entityItem.getEntityItem());
            //} else if (item instanceof EntityDartArrow) {
            //    this.dartArrow = new NBTTagCompound();
            //    item.writeToNBT(this.dartArrow);
            //    this.setEntityItem(new ItemStack(DartItem.forceArrow));
        } else if (item instanceof EntityArrow) {
            this.arrow = new NBTTagCompound();
            item.writeToNBT(this.arrow);
            this.setEntityItem(new ItemStack(Items.arrow));
        }
    }

    @Override
    protected void entityInit() {
        this.getDataWatcher().addObjectByDataType(12, 5);
        super.entityInit();
    }

    @Override
    public void onUpdate() {
        --this.timeout;
        if (!Dartcraft.proxy.isSimulating(this.worldObj) && this.timeout <= 0) {
            this.timeout = 40;
            //if (this.item == null) {
            //    //this.sendDescriptionPacket();
            //}
        }

        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        --this.frozenTime;
        if (this.frozenTime <= 0 && Dartcraft.proxy.isSimulating(this.worldObj)
            && this.getEntityItem() != null) {
            this.worldObj.removeEntity(this);
            this.setDead();

            try {
                //if (this.dartArrow != null) {
                //    EntityDartArrow e = new EntityDartArrow(this.worldObj);
                //    e.readFromNBT(this.dartArrow);
                //    this.worldObj.spawnEntityInWorld(e);
                //} else
                if (this.arrow != null) {
                    EntityArrow var4 = new EntityArrow(this.worldObj);
                    var4.readFromNBT(this.arrow);
                    this.worldObj.spawnEntityInWorld(var4);
                } else {
                    EntityItem var5 = new EntityItem(
                        this.worldObj,
                        this.posX,
                        this.posY,
                        this.posZ,
                        this.getEntityItem().copy()
                    );
                    var5.lifespan = this.savedSpan;
                    var5.delayBeforeCanPickup = 10;
                    var5.prevPosX = var5.posX;
                    var5.prevPosY = var5.posY;
                    var5.prevPosZ = var5.posZ;
                    var5.ticksExisted = 1;
                    var5.motionX = this.storedMotionX;
                    var5.motionY = this.storedMotionY;
                    var5.motionZ = this.storedMotionZ;
                    NBTTagCompound dartTag = UpgradeHelper.getDartData(var5);
                    dartTag.setInteger("timeImmune", 2);
                    this.worldObj.spawnEntityInWorld(var5);
                }

                this.setEntityItem(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound comp) {
        if (comp.hasKey("item")) {
            try {
                this.setEntityItem(
                    ItemStack.loadItemStackFromNBT(comp.getCompoundTag("item"))
                );
                this.frozenTime = comp.getInteger("frozenTime");
                this.savedSpan = comp.getInteger("savedSpan");
                this.storedMotionX = comp.getDouble("storedMotionX");
                this.storedMotionY = comp.getDouble("storedMotionY");
                this.storedMotionZ = comp.getDouble("storedMotionZ");
                if (comp.hasKey("dartArrow")) {
                    this.dartArrow = comp.getCompoundTag("dartArrow");
                }

                if (comp.hasKey("arrow")) {
                    this.arrow = comp.getCompoundTag("arrow");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound comp) {
        ItemStack item = this.getEntityItem();
        if (item != null) {
            try {
                NBTTagCompound e = new NBTTagCompound();
                item.writeToNBT(e);
                comp.setTag("item", e);
                comp.setInteger("frozenTime", this.frozenTime);
                comp.setInteger("savedSpan", this.savedSpan);
                comp.setDouble("storedMotionX", this.storedMotionX);
                comp.setDouble("storedMotionY", this.storedMotionY);
                comp.setDouble("storedMotionZ", this.storedMotionZ);
                if (this.dartArrow != null) {
                    comp.setTag("dartArrow", this.dartArrow);
                }

                if (this.arrow != null) {
                    comp.setTag("arrow", this.arrow);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ItemStack getEntityItem() {
        return this.getDataWatcher().getWatchableObjectItemStack(12);
    }

    @Override
    public void setEntityItem(ItemStack stack) {
        this.getDataWatcher().updateObject(12, stack);
        //if (Dartcraft.proxy.isSimulating(this.worldObj)) {
        //    if (this.item != null && (this.arrow != null || this.dartArrow != null)) {
        //        if (!this.item.hasTagCompound()) {
        //            this.item.setTagCompound(new NBTTagCompound());
        //        }

        //        if (this.dartArrow != null) {
        //            this.item.getTagCompound().setTag("storedDartArrow",
        //            this.dartArrow);
        //        } else if (this.arrow != null) {
        //            this.item.getTagCompound().setTag("storedArrow", this.arrow);
        //        }
        //    }

        //    //this.sendDescriptionPacket();
        //} else if (this.item.hasTagCompound()) {
        //    NBTTagCompound comp = this.item.getTagCompound();
        //    if (comp.hasKey("storedDartArrow")) {
        //        this.dartArrow = comp.getCompoundTag("storedDartArrow");
        //    } else if (comp.hasKey("storedArrow")) {
        //        this.arrow = comp.getCompoundTag("storedArrow");
        //    }
        //}
    }

    //@Override
    //public void sendDescriptionPacket() {
    //    NBTTagCompound comp;
    //    if (Dartcraft.proxy.isSimulating(this.worldObj)) {
    //        if (this.item != null) {
    //            comp = this.item.writeToNBT(new NBTTagCompound());
    //            if (comp != null) {
    //                comp.setInteger("bottleID", this.entityId);
    //                PacketDispatcher.sendPacketToAllInDimension(
    //                    (new PacketNBT(49, comp)).getPacket(), this.dimension
    //                );
    //            }
    //        }
    //    } else {
    //        comp = new NBTTagCompound();
    //        comp.setInteger("dim", this.dimension);
    //        comp.setInteger("bottleID", this.entityId);
    //        PacketDispatcher.sendPacketToServer((new PacketNBT(50, comp)).getPacket());
    //    }
    //}

    @Override
    protected boolean interact(EntityPlayer player) {
        try {
            ItemStack item = this.getEntityItem();
            player.swingItem();
            if (Dartcraft.proxy.isSimulating(this.worldObj) && player != null
                && item != null) {
                EntityItem e = new EntityItem(
                    this.worldObj, player.posX, player.posY, player.posZ, item
                );
                e.motionX = 0.0D;
                e.motionY = 0.0D;
                e.motionZ = 0.0D;
                e.delayBeforeCanPickup = 0;
                NBTTagCompound dartTag = UpgradeHelper.getDartData(e);
                dartTag.setInteger("timeImmune", 2);
                if (this.arrow != null || this.dartArrow != null) {
                    e.getEntityItem().setTagCompound((NBTTagCompound) null);
                }

                this.setDead();
                this.worldObj.spawnEntityInWorld(e);
                this.worldObj.removeEntity(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // TODO: WTF
    //@Override
    //public boolean func_85031_j(Entity entity) {
    //    if (entity instanceof EntityPlayer) {
    //        this.customInteract((EntityPlayer) entity);
    //    }

    //    return true;
    //}

    @Override
    public AxisAlignedBB getBoundingBox() {
        try {
            ItemStack item = this.getEntityItem();
            if (item != null
                && (item.getItem() instanceof ItemBlock || this.dartArrow != null
                    || this.arrow != null)) {
                return this.boundingBox;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
}
