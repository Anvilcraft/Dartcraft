package ley.modding.dartcraft.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IBottleRenderable;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.item.ItemEntityBottle;
import ley.modding.dartcraft.util.EntityUtils;
import ley.modding.dartcraft.util.ItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityBottle extends EntityLivingBase implements IBottleRenderable {

    private int timeout;

    public EntityBottle(World world) {
        super(world);
    }

    public EntityBottle(World world, double x, double y, double z, ItemStack contents) {
        super(world);
        setPosition(x, y, z);
        setEntityItem(contents);
        setSize(0.2F, 0.2F);
    }

    protected void entityInit() {
        super.entityInit();
        ((Entity)this).getDataWatcher().addObject(12, new ItemStack(DartItems.entitybottle));
    }

    public void writeToNBT(NBTTagCompound comp) {
        super.writeToNBT(comp);
        ItemStack contents = getEntityItem();
        if (contents != null)
            comp.setTag("bottleContents", (NBTBase)contents.writeToNBT(new NBTTagCompound()));
    }

    public void readFromNBT(NBTTagCompound comp) {
        super.readFromNBT(comp);
        if (comp.hasKey("bottleContents"))
            setEntityItem(ItemStack.loadItemStackFromNBT(comp
                    .getCompoundTag("bottleContents")));
    }

    public void writeEntityToNBT(NBTTagCompound comp) {}

    public void readEntityFromNBT(NBTTagCompound comp) {}

    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.timeout--;
        if (!Dartcraft.proxy.isSimulating(((Entity)this).worldObj)) {
            if (this.timeout <= 0)
                this.timeout = 40;
        } else {
            ItemStack contents = getEntityItem();
            if (contents != null && contents.getItem() instanceof ItemEntityBottle && this.timeout <= 0) {
                this.timeout = 40;
                NBTTagCompound comp = contents.getTagCompound();
                if (comp.hasKey("Fire") && comp.getShort("Fire") > 0)
                    comp.setShort("Fire", (short)-1);
                if (comp.hasKey("FallDistance") && comp.getFloat("FallDistance") > 0.0F)
                    comp.setFloat("FallDistance", 0.0F);
                short maxHealth = 0;
                Entity temp = EntityList.createEntityFromNBT((NBTTagCompound)comp.copy(), ((Entity)this).worldObj);
                EntityLivingBase bottled = null;
                if (temp instanceof EntityLivingBase)
                    bottled = (EntityLivingBase)temp;
                try {
                    maxHealth = (short)(int)bottled.getAttributeMap().getAttributeInstanceByName("generic.maxHealth").getAttributeValue();
                } catch (Exception e) {}
                if (maxHealth > 0 && comp.hasKey("Health") && comp.getShort("Health") < maxHealth)
                    comp.setShort("Health", (short)(comp.getShort("Health") + 1));
            }
        }
    }

    public String getBottledName() {
        String name = "";
        ItemStack bottleStack = null;
        int iterations = 0;
        try {
            ItemStack contents = getEntityItem();
            bottleStack = (contents != null) ? contents.copy() : null;
            if (bottleStack != null) {
                Entity entity = EntityList.createEntityFromNBT(bottleStack.getTagCompound(), ((Entity)this).worldObj);
                while (entity instanceof EntityBottle) {
                    EntityBottle bottle = (EntityBottle)entity;
                    bottleStack = bottle.getEntityItem();
                    entity = EntityList.createEntityFromNBT(bottleStack.getTagCompound(), ((Entity)this).worldObj);
                    iterations++;
                }
                switch (iterations) {
                    case 0:
                        name = name + "Twice ";
                        break;
                    case 1:
                        name = name + "Thrice ";
                        break;
                    default:
                        name = name + (iterations + 2) + "x ";
                        break;
                }
                name = name + "Bottled " + bottleStack.getTagCompound().getString("dartName");
            }
        } catch (Exception e) {
            return "Bottled Something";
        }
        return name;
    }

    public boolean canBePushed() {
        return false;
    }

    protected void dealFireDamage(int damage) {}

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (((Entity)this).isDead || !Dartcraft.proxy.isSimulating(((Entity)this).worldObj))
            return false;
        if (source.getEntity() != null && source.getEntity() instanceof EntityPlayer &&
                !source.isProjectile()) {
            ItemStack contents = getEntityItem();
            if (contents != null) {
                if (!contents.hasTagCompound())
                    contents.setTagCompound(new NBTTagCompound());
                contents.getTagCompound().setBoolean("wasDropped", true);
            }
            ((Entity)this).worldObj.playSoundAtEntity((Entity)this, "dartcraft:bottle", 2.0F,
                    EntityUtils.randomPitch());
            ItemUtils.dropItem(contents, ((Entity)this).worldObj, ((Entity)this).posX, ((Entity)this).posY, ((Entity)this).posZ);
            setDead();
        }
        return false;
    }

    public void onCollideWithPlayer(EntityPlayer player) {}

    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }

    public void setFire(int time) {}

    public ItemStack getHeldItem() {
        return null;
    }

    public void setCurrentItemOrArmor(int i, ItemStack itemstack) {}

    public ItemStack[] getLastActiveItems() {
        return null;
    }

    public ItemStack getEntityItem() {
        return ((Entity)this).getDataWatcher().getWatchableObjectItemStack(12);
    }

    public void setEntityItem(ItemStack stack) {
        ((Entity)this).getDataWatcher().updateObject(12, stack);
        try {
            String foundName = stack.getTagCompound().getString("id");
            getEntityData().setString("name", foundName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Dartcraft.proxy.isSimulating(((Entity)this).worldObj));
    }

    public ItemStack getEquipmentInSlot(int slot) {
        return null;
    }

}
