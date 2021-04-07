package ley.modding.dartcraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.entity.EntityBottle;
import ley.modding.dartcraft.entity.EntityFlyingFlask;
import ley.modding.dartcraft.util.EntityUtils;
import ley.modding.dartcraft.util.ItemUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class ItemEntityBottle extends Item {

    public static final int ENTITY_META = 0;

    public static final int AREA_META = 1;

    public static final int CHECK_TIME = 40;

    public ItemEntityBottle() {
        setCreativeTab(Dartcraft.tab);
        setMaxStackSize(64);
        setUnlocalizedName("entitybottle");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean thing) {
        if (stack.hasTagCompound()) {
            NBTTagCompound comp = (NBTTagCompound)stack.getTagCompound().copy();
            if (stack.getItemDamage() == 0) {
                Entity entity = null;
                if (comp.hasKey("CanPickUpLoot"))
                    entity = EntityList.createEntityFromNBT(comp, ((Entity)player).worldObj);
                if (comp != null && comp.hasKey("dartName")) {
                    String name = null;
                    if (entity instanceof EntityBottle)
                        name = ((EntityBottle)entity).getBottledName();
                    if (name != null) {
                        list.clear();
                        list.add(name);
                    }
                }
            } else if (stack.getItemDamage() == 1) {
                if (comp.hasKey("specialID")) {
                    if (comp.getString("specialID").equals("fountain"))
                        list.add("Fairy Fountain");
                    if (comp.getString("specialID").equals("darthome"))
                        list.add("Dart Home");
                }
            }
        }
    }

    private String retrieveName(String name) {
        if (name.contains("entity.Cat.name"))
            return "Cat";
        return name;
    }

    public String getItemStackDisplayName(ItemStack stack) {
        if (!stack.hasTagCompound())
            return super.getItemStackDisplayName(stack);
        if (stack.getItemDamage() == 0) {
            String name = stack.getTagCompound().getString("dartName");
            NBTTagCompound comp = (NBTTagCompound)stack.getTagCompound().copy();
            if (name.equals("entity.DartCraft.entityBeeSwarm.name"))
                return "Flask of Angry Bees";
            if (name.equals("entity.DartCraft.entityFairy.name"))
                return "Bottled Fairy";
            if (name != null && !name.equals(""))
                return "" + name;
        } else if (stack.getItemDamage() == 1) {
            return "Bottled Area";
        }
        return super.getItemStackDisplayName(stack);
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        if (stack == null || !Dartcraft.proxy.isSimulating(world))
            return;
        if (stack.getItemDamage() == 0) {
            if (stack.hasTagCompound())
                stack.getTagCompound().setInteger("timeout", stack.getTagCompound().getInteger("timeout") + 1);
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("wasDropped"))
                stack.getTagCompound().removeTag("wasDropped");
            if (stack != null && stack.getItem() instanceof ItemEntityBottle && stack.hasTagCompound() && stack
                    .getTagCompound().getInteger("timeout") >= 40) {
                stack.getTagCompound().setInteger("timeout", 0);
                NBTTagCompound comp = stack.getTagCompound();
                String name = comp.getString("dartName");
                if (name != null && name.equals("Creeper")) {
                    boolean bane = (comp.hasKey("ForgeData") && comp.getCompoundTag("ForgeData").hasKey("DartCraft") && comp.getCompoundTag("ForgeData").getCompoundTag("DartCraft").getBoolean("baned"));
                    if (!bane && entity instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer)entity;
                        world.createExplosion((Entity)new EntityCreeper(world), ((Entity)player).posX, ((Entity)player).posY, ((Entity)player).posZ, 0.25F, false);
                        comp.setBoolean("dartToDestroy", true);
                        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                            ItemStack tempStack = player.inventory.mainInventory[i];
                            if (tempStack != null && tempStack.getItem() instanceof ItemEntityBottle && tempStack
                                    .hasTagCompound() && tempStack.getTagCompound().hasKey("dartToDestroy") && tempStack
                                    .getTagCompound().getBoolean("dartToDestroy")) {
                                //player.inventory.setInventorySlotContents(i, new ItemStack(PAItems.resource, CommonProxy.rand.nextInt(3) + 1, 3));
                                break;
                            }
                        }
                        return;
                    }
                }
                if (comp.hasKey("Fire") && comp.getShort("Fire") > 0)
                    comp.setShort("Fire", (short)-1);
                if (comp.hasKey("FallDistance") && comp.getFloat("FallDistance") > 0.0F)
                    comp.setFloat("FallDistance", 0.0F);
                short maxHealth = 0;
                Entity temp = EntityList.createEntityFromNBT((NBTTagCompound)comp.copy(), world);
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

    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        try {
            if (!Dartcraft.proxy.isSimulating(world) || stack == null || stack
                    .getItemDamage() == 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        try {
            if (stack.getItemDamage() == 0) {
                boolean thrown = player.isSneaking();
                if (Dartcraft.proxy.isSimulating(world))
                    if (!thrown) {
                        spawnEntity(world, player, stack);
                    } else {
                        EntityFlyingFlask flask = new EntityFlyingFlask(world, (EntityLivingBase)player, stack);
                        world.spawnEntityInWorld((Entity)flask);
                        flask.setEntityItem(stack.copy());
                        world.playSoundAtEntity((Entity)player, "random.bow", 1.0F,
                                EntityUtils.randomPitch());
                    }
                stack.stackSize--;
                if (!thrown) {
                    if (stack.stackSize > 0) {
                        if (!player.inventory.addItemStackToInventory(new ItemStack(DartItems.forceflask)))
                            if (Dartcraft.proxy.isSimulating(world))
                                ItemUtils.dropItem(new ItemStack(DartItems.forceflask), world, ((Entity)player).posX, ((Entity)player).posY, ((Entity)player).posZ);
                        return stack;
                    }
                    return new ItemStack(DartItems.forceflask);
                }
            } else if (stack.getItemDamage() == 1) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stack;
    }

    private void spawnEntity(World world, EntityPlayer player, ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound comp = (NBTTagCompound)stack.getTagCompound().copy();
            Entity entity = EntityList.createEntityFromNBT(comp, world);
            if (entity != null && entity instanceof EntityLivingBase) {
                double xIncr = (-MathHelper.sin(((Entity)player).rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(((Entity)player).rotationPitch / 180.0F * 3.1415927F));
                double yIncr = -MathHelper.sin(((Entity)player).rotationPitch / 180.0F * 3.1415927F);
                double zIncr = (MathHelper.cos(((Entity)player).rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(((Entity)player).rotationPitch / 180.0F * 3.1415927F));
                double x = ((Entity)player).posX + xIncr * 2.0D;
                double y = ((Entity)player).posY;
                double z = ((Entity)player).posZ + zIncr * 2.0D;
                EntityLivingBase restored = (EntityLivingBase)entity;
                restored.setPosition(x, y, z);
                NBTTagCompound dartTag = EntityUtils.getModComp((Entity)restored);
                dartTag.removeTag("time");
                dartTag.removeTag("timeTime");
                dartTag.setInteger("timeImmune", 10);
                world.spawnEntityInWorld((Entity)restored);
                world.playSoundAtEntity((Entity)player, "dartcraft:bottle", 2.0F,
                        EntityUtils.randomPitch());
            }
        }
    }

    public boolean hasCustomEntity(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound())
            return false;
        return ((!stack.getTagCompound().getBoolean("wasDropped") && stack.getItemDamage() == 0) || (
                !stack.getTagCompound().hasKey("specialID") && stack.getItemDamage() == 1 && stack.stackSize < 2));
    }

    public Entity createEntity(World world, Entity entity, ItemStack stack) {
        if (stack == null || !stack.hasTagCompound())
            return null;
        try {
            if (stack.getItemDamage() == 0) {
                EntityBottle item = new EntityBottle(world, entity.posX, entity.posY, entity.posZ, stack);
                ((Entity)item).motionX = entity.motionX;
                ((Entity)item).motionY = entity.motionY;
                ((Entity)item).motionZ = entity.motionZ;
                return (Entity)item;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs tabs, List list) {
        try {
            list.add(EntityUtils.bottleEntity((Entity)new EntityCow(null)));
            list.add(EntityUtils.bottleEntity((Entity)new EntityPig(null)));
            list.add(EntityUtils.bottleEntity((Entity)new EntityChicken(null)));
            EntitySkeleton skeleton = new EntitySkeleton(null);
            skeleton.setSkeletonType(1);
            list.add(EntityUtils.bottleEntity((Entity)skeleton));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
