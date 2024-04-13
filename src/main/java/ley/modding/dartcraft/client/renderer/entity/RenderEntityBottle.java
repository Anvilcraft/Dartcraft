package ley.modding.dartcraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IBottleRenderable;
import ley.modding.dartcraft.client.renderer.item.RenderItemForceFlask;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderEntityBottle extends RenderLiving {
    private static final ResourceLocation texture
        = new ResourceLocation(Dartcraft.MODID, "bottle.png");

    private static IModelCustom bottle = AdvancedModelLoader.loadModel(
        new ResourceLocation(Dartcraft.MODID, "models/bottle.obj")
    );

    public RenderEntityBottle() {
        super(null, 0.2F);
    }

    @Override
    public void doRender(
        Entity entity, double par2, double par4, double par6, float par8, float par9
    ) {
        if (entity instanceof IBottleRenderable) {
            ItemStack stack = ((IBottleRenderable) entity).getEntityItem();
            GL11.glPushMatrix();
            GL11.glTranslated(par2, par4 + 0.25D, par6);
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            RenderItemForceFlask.instance.renderItem(
                IItemRenderer.ItemRenderType.ENTITY, stack, new Object[0]
            );
            GL11.glPopMatrix();
        }
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
