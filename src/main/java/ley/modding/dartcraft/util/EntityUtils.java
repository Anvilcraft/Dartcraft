package ley.modding.dartcraft.util;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.item.Items;
import ley.modding.dartcraft.proxy.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;

public class EntityUtils {

    public static NBTTagCompound getModComp(Entity entity) {
        if (entity == null || entity.getEntityData() == null)
            return new NBTTagCompound();
        NBTTagCompound comp = entity.getEntityData().hasKey(Dartcraft.MODID) ? entity.getEntityData().getCompoundTag(Dartcraft.MODID) : null;
        if (comp == null) {
            entity.getEntityData().setTag(Dartcraft.MODID, (NBTBase)new NBTTagCompound());
            comp = entity.getEntityData().getCompoundTag(Dartcraft.MODID);
        }
        return comp;
    }

    public static ItemStack bottleEntity(Entity victim) {
        try {
            String entityName = EntityList.getEntityString(victim);
            if (victim == null || entityName == null)
                return null;
            ItemStack bottleStack = new ItemStack(Items.entitybottle);
            NBTTagCompound bottleComp = new NBTTagCompound();
            victim.writeToNBT(bottleComp);
            bottleComp.removeTag("OnGround");
            bottleComp.removeTag("PersistenceRequired");
            bottleComp.removeTag("Dimension");
            bottleComp.removeTag("PortalCooldown");
            bottleComp.removeTag("UUIDLeast");
            bottleComp.removeTag("UUIDMost");
            bottleComp.removeTag("DropChances");
            bottleComp.removeTag("Motion");
            bottleComp.removeTag("Pos");
            bottleComp.removeTag("Rotation");
            NBTTagDouble dummyDouble = new NBTTagDouble(0.0D);
            NBTTagFloat dummyFloat = new NBTTagFloat(0.0F);
            NBTTagList motion = new NBTTagList();
            motion.appendTag(dummyDouble.copy());
            motion.appendTag(dummyDouble.copy());
            motion.appendTag(dummyDouble.copy());
            NBTTagList rot = new NBTTagList();
            rot.appendTag(dummyFloat.copy());
            rot.appendTag(dummyFloat.copy());
            bottleComp.setTag("Pos", motion.copy());
            bottleComp.setTag("Motion", motion.copy());
            bottleComp.setTag("Rotation", rot.copy());
            bottleComp.setString("id", entityName);
            bottleComp.setString("dartName", victim.getCommandSenderName());
            bottleStack.setTagCompound(bottleComp);
            return bottleStack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Entity getEntity(String name) {
        try {
            Class entityClass = (Class) EntityList.stringToClassMapping.get(name);
            if (entityClass != null) {
                Constructor<Entity> constructor = entityClass.getConstructor(new Class[] { World.class });
                Entity entity = constructor.newInstance(new Object[] { null });
                return entity;
            }
        } catch (Exception e) {}
        return null;
    }

    public static float randomPitch() {
        return CommonProxy.rand.nextFloat() * 0.25F + 0.85F;
    }


}
