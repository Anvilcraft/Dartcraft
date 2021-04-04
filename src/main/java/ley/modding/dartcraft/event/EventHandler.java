package ley.modding.dartcraft.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IBreakable;
import ley.modding.dartcraft.entity.EntityColdChicken;
import ley.modding.dartcraft.entity.EntityColdCow;
import ley.modding.dartcraft.entity.EntityColdPig;
import ley.modding.dartcraft.item.Items;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class EventHandler {

    @SubscribeEvent
    public void onItemDestroyed(PlayerDestroyItemEvent event) {
        ItemStack stack = event.original;
        EntityPlayer player = event.entityPlayer;
        if (stack != null && stack.getItem() instanceof IBreakable) {
            ItemStack ret = ((IBreakable)stack.getItem()).itemReturned();
            if (Dartcraft.proxy.isSimulating(player.worldObj)) {
                player.inventory.addItemStackToInventory(ret);
            }
        }
    }

    @SubscribeEvent
    public void onEntityInteract(EntityInteractEvent event) {
        if (!Dartcraft.proxy.isSimulating(event.target.worldObj))
            return;

        ItemStack heldItem = event.entityPlayer.getHeldItem();
        if (heldItem != null && heldItem.getItem() != Items.forceshears)
            return;

        if (event.target instanceof EntityAnimal) {
            EntityAnimal entity = (EntityAnimal)event.target;
            ColdEntityType type = ColdEntityType.fromClass(entity.getClass());

            if (type == null)
                return;

            EntityAnimal newEntity = type.newInstance(entity.worldObj);

            double x = entity.posX;
            double y = entity.posY;
            double z = entity.posZ;
            float pitch = entity.rotationPitch;
            float yaw = entity.rotationYaw;
            float camPitch = entity.cameraPitch;
            float health = entity.getHealth();
            float fall = entity.fallDistance;
            boolean onFire = entity.isBurning();
            int air = entity.getAir();
            float speed = entity.getAIMoveSpeed();

            World world = entity.worldObj;

            entity.entityDropItem(type.getDrop(), 1.0f);
            world.playSoundAtEntity(entity, "random.pop", 1.0f, 1.0f);
            entity.setDead();
            world.removeEntity(entity);

            newEntity.setPositionAndRotation(x, y, z, yaw, pitch);
            newEntity.cameraPitch = camPitch;
            newEntity.setHealth(health);
            newEntity.fallDistance = fall;
            if (onFire)
                newEntity.setFire(1);
            newEntity.setAir(air);
            newEntity.setAIMoveSpeed(speed);

            world.spawnEntityInWorld(newEntity);
        }
    }

    enum ColdEntityType {
        COW(EntityCow.class),
        CHICKEN(EntityChicken.class),
        PIG(EntityPig.class);

        ColdEntityType(Class<? extends EntityAnimal> clazz) {
            this.clazz = clazz;
        }

        Class<? extends EntityAnimal> clazz;

        static ColdEntityType fromClass(Class<?> clazz) {
            for (ColdEntityType v : ColdEntityType.values()) {
                if (v.clazz == clazz) {
                    return v;
                }
            }
            return null;
        }

        EntityAnimal newInstance(World world) {
            switch (this) {
                case COW:
                    return new EntityColdCow(world);
                case PIG:
                    return new EntityColdPig(world);
                case CHICKEN:
                    return new EntityColdChicken(world);
                
                // why is the compiler so stupid to think that this is required?
                default:
                    return null;
            }
        }

        ItemStack getDrop() {
            // gotta use full names here, because tilera had the brilliant idea to call the mod item class "Items"
            switch (this) {
                case COW:
                    return new ItemStack(net.minecraft.init.Items.leather);
                case PIG:
                    // TODO: add bacon item
                    return new ItemStack(net.minecraft.init.Items.porkchop);
                case CHICKEN:
                    return new ItemStack(net.minecraft.init.Items.feather);
                
                // why is the compiler so stupid to think that this is required?
                default:
                    return null;
            }
        }
    }
}
