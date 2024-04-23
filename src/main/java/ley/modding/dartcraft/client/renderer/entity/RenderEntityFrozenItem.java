package ley.modding.dartcraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.entity.EntityFrozenItem;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderEntityFrozenItem extends RenderLiving {
    public RenderItem itren = new RenderItem();

    public RenderEntityFrozenItem() {
        super((ModelBase) null, 0.2F);
    }

    @Override
    public void setRenderManager(RenderManager rm) {
        super.setRenderManager(rm);
        this.itren.setRenderManager(rm);
    }

    @Override
    public void doRender(
        Entity entity, double par2, double par4, double par6, float par8, float par9
    ) {
        EntityFrozenItem item = (EntityFrozenItem) entity;
        ItemStack stack = item.getEntityItem();
        GL11.glPushMatrix();
        GL11.glTranslated(par2, par4, par6);
        GL11.glScalef(0.5F, 0.5F, 0.5F);

        try {
            if (item != null && stack != null) {
                //if (item.dartArrow != null) {
                //    EntityDartArrow e = new EntityDartArrow(item.worldObj);
                //    e.readFromNBT(item.dartArrow);
                //    item.worldObj.spawnEntityInWorld(e);
                //    item.worldObj.removeEntity(e);
                //} else
                if (item.arrow != null) {
                    EntityArrow e1 = new EntityArrow(item.worldObj);
                    e1.readFromNBT(item.arrow);
                    //item.worldObj.spawnEntityInWorld(e1);
                    //item.worldObj.removeEntity(e1);
                } else {
                    EntityItem e2 = new EntityItem(
                        item.worldObj, item.posX, item.posY, item.posZ, stack.copy()
                    );
                    e2.delayBeforeCanPickup = 100;
                    e2.hoverStart = item.storedRotation;
                    itren.doRender(e2, par2, par4, par6, par8, par9);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TextureMap.locationItemsTexture;
    }
}
