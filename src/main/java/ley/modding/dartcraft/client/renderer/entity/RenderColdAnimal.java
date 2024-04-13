package ley.modding.dartcraft.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderColdAnimal extends RenderLiving {
    private final ResourceLocation texture;

    public RenderColdAnimal(ModelBase par1ModelBase, float par2, String textureLocation) {
        super(par1ModelBase, par2);

        this.texture = new ResourceLocation("dartcraft", textureLocation);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
