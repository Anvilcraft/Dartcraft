package ley.modding.dartcraft.client.renderer.block;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.client.model.ModelEngine;
import ley.modding.dartcraft.tile.TileEntityForceEngine;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class RenderTileForceEngine extends TileEntitySpecialRenderer {
    private static ModelRenderer base;

    static ModelBase model = new ModelBase() {

    };

    static ModelRenderer piston;

    static ModelRenderer extension;

    static ModelRenderer[] trunk = new ModelRenderer[2];

    static double[][] translate = new double[6][3];

    public void
    renderTileEntityAt(TileEntity tile, double x, double y, double z, float f) {
        TileEntityForceEngine engine;
        if (tile instanceof TileEntityForceEngine) {
            engine = (TileEntityForceEngine) tile;
        } else {
            return;
        }
        render(
            engine.isActive,
            engine.getCycleProgress(),
            engine.getFacing().ordinal(),
            x,
            y,
            z
        );
    }

    public void
    render(boolean active, float progress, int facing, double x, double y, double z) {
        float step;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        if (progress > 1.0F)
            progress = 1.0F;
        if (progress > 0.5D) {
            step = 8.0F - (progress - 0.5F) * 2.0F * 8.0F;
        } else {
            step = progress * 16.0F;
        }
        float translateFactor = step / 16.0F;
        base.rotateAngleX = ModelEngine.angleBaseYNeg[facing][0];
        base.rotateAngleY = ModelEngine.angleBaseYNeg[facing][1];
        base.rotateAngleZ = ModelEngine.angleBaseYNeg[facing][2];
        piston.rotateAngleX = ModelEngine.angleBaseYNeg[facing][0];
        piston.rotateAngleY = ModelEngine.angleBaseYNeg[facing][1];
        piston.rotateAngleZ = ModelEngine.angleBaseYNeg[facing][2];
        extension.rotateAngleX = ModelEngine.angleBaseYNeg[facing][0];
        extension.rotateAngleY = ModelEngine.angleBaseYNeg[facing][1];
        extension.rotateAngleZ = ModelEngine.angleBaseYNeg[facing][2];
        Dartcraft.proxy.bindTexture("forceEngine.png");
        base.render(0.0625F);
        GL11.glPushMatrix();
        GL11.glTranslated(
            translate[facing][0] * translateFactor,
            translate[facing][1] * translateFactor,
            translate[facing][2] * translateFactor
        );
        piston.render(0.0625F);
        GL11.glPopMatrix();
        if (active) {
            (trunk[1]).rotateAngleX = ModelEngine.angleBaseYNeg[facing][0];
            (trunk[1]).rotateAngleY = ModelEngine.angleBaseYNeg[facing][1];
            (trunk[1]).rotateAngleZ = ModelEngine.angleBaseYNeg[facing][2];
            trunk[1].render(0.0625F);
        } else {
            (trunk[0]).rotateAngleX = ModelEngine.angleBaseYNeg[facing][0];
            (trunk[0]).rotateAngleY = ModelEngine.angleBaseYNeg[facing][1];
            (trunk[0]).rotateAngleZ = ModelEngine.angleBaseYNeg[facing][2];
            trunk[0].render(0.0625F);
        }
        float extensionFactor = 0.125F;
        for (int i = 0; i < step + 2.0F; i += 2) {
            extension.render(0.0625F);
            GL11.glTranslated(
                translate[facing][0] * extensionFactor,
                translate[facing][1] * extensionFactor,
                translate[facing][2] * extensionFactor
            );
        }
        GL11.glPopMatrix();
    }

    static {
        translate[0][1] = -1.0D;
        translate[1][1] = 1.0D;
        translate[2][2] = -1.0D;
        translate[3][2] = 1.0D;
        translate[4][0] = -1.0D;
        translate[5][0] = 1.0D;
        base = new ModelRenderer(model, 0, 0);
        base.setTextureSize(128, 64);
        base.addBox(-8.0F, -8.0F, -8.0F, 16, 4, 16);
        base.rotationPointX = 8.0F;
        base.rotationPointY = 8.0F;
        base.rotationPointZ = 8.0F;
        piston = new ModelRenderer(model, 64, 0);
        piston.setTextureSize(128, 64);
        piston.addBox(-7.0F, -4.0F, -7.0F, 14, 4, 14);
        piston.rotationPointX = 8.0F;
        piston.rotationPointY = 8.0F;
        piston.rotationPointZ = 8.0F;
        extension = new ModelRenderer(model, 0, 20);
        extension.setTextureSize(128, 64);
        extension.addBox(-5.0F, -4.5F, -5.0F, 10, 2, 10);
        extension.rotationPointX = 8.0F;
        extension.rotationPointY = 8.0F;
        extension.rotationPointZ = 8.0F;
        for (int i = 0; i < 2; i++) {
            trunk[i] = new ModelRenderer(model, 32 * i, 44);
            trunk[i].setTextureSize(128, 64);
            trunk[i].addBox(-4.0F, -4.0F, -4.0F, 8, 12, 8);
            (trunk[i]).rotationPointX = 8.0F;
            (trunk[i]).rotationPointY = 8.0F;
            (trunk[i]).rotationPointZ = 8.0F;
        }
    }
}
