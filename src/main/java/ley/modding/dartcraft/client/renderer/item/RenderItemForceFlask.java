package ley.modding.dartcraft.client.renderer.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.entity.EntityBottle;
import ley.modding.dartcraft.item.ItemEntityBottle;
import ley.modding.dartcraft.util.EntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderItemForceFlask implements IItemRenderer {

    private static RenderItem renderer = new RenderItem();

    private IModelCustom bottle = AdvancedModelLoader.loadModel(new ResourceLocation(Dartcraft.MODID, "models/bottle.obj"));

    private IModelCustom liquid = AdvancedModelLoader.loadModel(new ResourceLocation(Dartcraft.MODID, "models/liquid.obj"));

    public static RenderItemForceFlask instance = new RenderItemForceFlask();

    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return (type == IItemRenderer.ItemRenderType.INVENTORY || type == IItemRenderer.ItemRenderType.ENTITY);
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        FontRenderer fontRenderer = (Minecraft.getMinecraft()).fontRenderer;
        Entity entity = null;
        GL11.glPushMatrix();
        if (item != null && item.getItem() instanceof ItemEntityBottle && item.hasTagCompound())
            try {
                if (item.getItemDamage() == 0) {
                    EntityLivingBase entityLivingBase = null;
                    NBTTagCompound comp = (NBTTagCompound)item.getTagCompound().copy();
                    Entity temp = null;
                    if (comp.hasKey("CanPickUpLoot")) {
                        temp = EntityList.createEntityFromNBT(comp, (World)(Minecraft.getMinecraft()).theWorld);
                    } else {
                        temp = EntityUtils.getEntity(comp.getString("id"));
                    }
                    if (temp instanceof EntityLivingBase)
                        entityLivingBase = (EntityLivingBase)temp;
                    if (entityLivingBase != null && (Dartcraft.proxy.getClientInstance()).theWorld != null) {
                        entityLivingBase.setWorld((World)(Dartcraft.proxy.getClientInstance()).theWorld);
                        entityLivingBase.setLocationAndAngles(0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                    }
                    entity = entityLivingBase;
                } else if (item.getItemDamage() == 1) {
                    EntityItem entityItem  = new EntityItem((World)(Dartcraft.proxy.getClientInstance()).theWorld);
                    entityItem.setLocationAndAngles(0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                    entityItem.hoverStart = 1.0F;
                    entityItem.setEntityItemStack(new ItemStack(Blocks.brick_block));
                    entity = entityItem;
                }
            } catch (Exception e) {}
        float value = 0.0F;
        float angle = 0.0F;
        float scale = 0.0F;
        scale(type);
        if (type == IItemRenderer.ItemRenderType.INVENTORY && (entity instanceof net.minecraft.entity.monster.EntityEnderman || entity instanceof net.minecraft.entity.monster.EntitySpider || entity instanceof net.minecraft.entity.monster.EntityCaveSpider))
            entity = null;
        if (entity != null) {
            float coef = 1.0F;
            if (entity instanceof net.minecraft.entity.monster.EntitySilverfish || entity instanceof net.minecraft.entity.passive.EntityOcelot || entity instanceof net.minecraft.entity.passive.EntityWolf)
                coef = 2.5F;
            scale = 8.0F * ((((Entity)entity).height > ((Entity)entity).width * coef) ? (1.3F / ((Entity)entity).height) : (0.5F / ((Entity)entity).width));
            value = -((Entity)entity).height;
            angle = 0.0F;
            if (entity instanceof net.minecraft.entity.passive.EntityPig)
                scale = 6.5F / ((Entity)entity).height;
            if (entity instanceof EntityBottle) {
                scale = 30.0F;
                value += 1.5F;
            }
            if (entity instanceof net.minecraft.entity.passive.EntityAnimal) {
                int age = item.getTagCompound().getInteger("Age");
                if (age < 0) {
                    scale /= 2.0F;
                    value -= ((Entity)entity).height * 0.5F;
                }
            }
            if (type == IItemRenderer.ItemRenderType.INVENTORY)
                angle = 45.0F;
            if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON)
                angle = 90.0F;
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0.0F, value, 0.0F);
            GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            try {
                boolean shouldRender = (type != IItemRenderer.ItemRenderType.INVENTORY);
                if (!shouldRender) {
                    Minecraft mc = Dartcraft.proxy.getClientInstance();
                    EntityClientPlayerMP entityClientPlayerMP = (mc != null) ? mc.thePlayer : null;
                    boolean found = false;
                    if (entityClientPlayerMP != null && ((EntityPlayer)entityClientPlayerMP).inventory != null && ((EntityPlayer)entityClientPlayerMP).inventory.mainInventory != null)
                        for (int i = 0; i < 9; i++) {
                            ItemStack invStack = ((EntityPlayer)entityClientPlayerMP).inventory.mainInventory[i];
                            if (invStack != null && invStack == item) {
                                found = true;
                                break;
                            }
                        }
                    if (!found)
                        shouldRender = true;
                }
                if (shouldRender) {
                    Render render = RenderManager.instance.getEntityRenderObject((Entity)entity);
                    if (render != null && entity != null)
                        render.doRender((Entity)entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                }
            } catch (Exception e) {}
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            scale(type);
        } else if (item != null && item.getItemDamage() > 0) {
            Dartcraft.proxy.bindTexture("liquid.png");
            GL11.glEnable(3042);
            float alpha = 1.0F;
            switch (item.getItemDamage()) {
                case 6:
                    GL11.glColor4f(0.0F, 0.5F, 1.0F, alpha);
                    break;
                case 5:
                    GL11.glColor4f(1.0F, 0.0F, 0.0F, alpha);
                    break;
                case 4:
                    GL11.glColor4f(0.0627F, 0.7176F, 0.4863F, alpha);
                    break;
                case 2:
                    GL11.glColor4f(1.0F, 1.0F, 0.0F, alpha);
                    break;
            }
            scale = 7.0F;
            float yScale = 1.0F;
            GL11.glScalef(scale, scale, scale);
            if (type == IItemRenderer.ItemRenderType.ENTITY) {
                GL11.glRotatef(0.44999766F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(168.90388F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, 0.65270597F, 0.0F);
            } else if (type == IItemRenderer.ItemRenderType.EQUIPPED || type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glRotatef(185.40428F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(6.599979F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, 0.65270597F, 0.0F);
            } else if (type == IItemRenderer.ItemRenderType.INVENTORY) {
                GL11.glRotatef(184.65428F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(6.749961F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, 0.65270597F, 0.0F);
            }
            GL11.glTranslatef(0.0F, 1.0F - yScale, 0.0F);
            GL11.glScalef(1.0F, yScale, 1.0F);
            this.liquid.renderAll();
            GL11.glDisable(3042);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            scale(type);
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Dartcraft.proxy.bindTexture("bottle.png");
        this.bottle.renderAll();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void scale(IItemRenderer.ItemRenderType type) {
        float value = 0.0F;
        float angle = 0.0F;
        float angle2 = 0.0F;
        float scale = 0.0F;
        value = ((Entity)(Minecraft.getMinecraft()).thePlayer).rotationYaw * 2.0F;
        angle = ((Entity)(Minecraft.getMinecraft()).thePlayer).rotationPitch * 2.0F;
        angle2 = ((EntityLivingBase)(Minecraft.getMinecraft()).thePlayer).rotationYawHead * 2.0F;
        if (Keyboard.isKeyDown(50));
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            scale = 0.04557239F;
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(1950.6F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(1950.6F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-178.5F, 0.0F, 1.0F, 0.0F);
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
            value = 0.5F;
            angle = -30.0F;
            scale = 0.036F;
            GL11.glTranslatef(0.35F, 0.65F, -0.1F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(angle, 1.0F, 0.0F, -1.0F);
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            value = 0.5F;
            angle = 180.0F;
            scale = 0.025F;
            GL11.glTranslatef(value, value, -0.1F);
            GL11.glScalef(scale, scale, scale);
        }
        if (type == IItemRenderer.ItemRenderType.ENTITY) {
            value = 0.5F;
            angle = 180.0F;
            scale = 0.035F;
            GL11.glScalef(scale, scale, scale);
        }
    }

}
