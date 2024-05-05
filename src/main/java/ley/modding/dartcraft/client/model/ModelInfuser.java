package ley.modding.dartcraft.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelInfuser extends ModelBase {
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;
    public ModelRenderer table_top;
    public ModelRenderer tank;

    public ModelInfuser() {
        this.textureWidth = 64;
        this.textureHeight = 128;
        this.leg1 = new ModelRenderer((ModelBase) this, 56, 39);
        this.leg1.addBox(0.0f, -7.0f, 4.0f, 2, 5, 2);
        this.leg1.setRotationPoint(-7.0f, 15.0f, -11.0f);
        this.leg1.setTextureSize(64, 128);
        this.leg1.mirror = true;
        this.setRotation(this.leg1, 0.0f, 0.0f, 0.0f);
        this.leg2 = new ModelRenderer((ModelBase) this, 56, 12);
        this.leg2.addBox(0.0f, -7.0f, 4.0f, 2, 5, 2);
        this.leg2.setRotationPoint(5.0f, 15.0f, 1.0f);
        this.leg2.setTextureSize(64, 128);
        this.leg2.mirror = true;
        this.setRotation(this.leg2, 0.0f, 0.0f, 0.0f);
        this.leg3 = new ModelRenderer((ModelBase) this, 56, 0);
        this.leg3.addBox(0.0f, -7.0f, 4.0f, 2, 5, 2);
        this.leg3.setRotationPoint(-7.0f, 15.0f, 1.0f);
        this.leg3.setTextureSize(64, 128);
        this.leg3.mirror = true;
        this.setRotation(this.leg3, 0.0f, 0.0f, 0.0f);
        this.leg4 = new ModelRenderer((ModelBase) this, 56, 27);
        this.leg4.addBox(0.0f, 0.0f, 0.0f, 2, 5, 2);
        this.leg4.setRotationPoint(5.0f, 8.0f, -7.0f);
        this.leg4.setTextureSize(64, 128);
        this.leg4.mirror = true;
        this.setRotation(this.leg4, 0.0f, 0.0f, 0.0f);
        this.table_top = new ModelRenderer((ModelBase) this, 0, 0);
        this.table_top.addBox(0.0f, 0.0f, 0.0f, 14, 5, 14);
        this.table_top.setRotationPoint(-7.0f, 13.0f, -7.0f);
        this.table_top.setTextureSize(64, 128);
        this.table_top.mirror = true;
        this.setRotation(this.table_top, 0.0f, 0.0f, 0.0f);
        this.tank = new ModelRenderer((ModelBase) this, 0, 23);
        this.tank.addBox(0.0f, -1.0f, 0.0f, 12, 2, 12);
        this.tank.setRotationPoint(-6.0f, 19.0f, -6.0f);
        this.tank.setTextureSize(64, 128);
        this.tank.mirror = true;
        this.setRotation(this.tank, 0.0f, 0.0f, 0.0f);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
