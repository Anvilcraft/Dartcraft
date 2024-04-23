package ley.modding.dartcraft.client.fx;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FXWindWaker extends EntityFX {
    public static final int TYPE_FALL = 0;
    public static final int TYPE_CHANGE = 1;
    public static final int TYPE_BREAK = 2;
    public static final int TYPE_SKATE = 3;
    private int iconIndex;

    public FXWindWaker(
        World world, double x, double y, double z, double vx, double vy, double vz
    ) {
        super(world, x, y, z, vx, vy, vz);
    }

    public FXWindWaker(World world, double x, double y, double z, int color, int type) {
        super(world, x, y, z);
        Color col = new Color(color);
        this.particleRed = (float) col.getRed();
        this.particleGreen = (float) col.getGreen();
        this.particleBlue = (float) col.getBlue();
        this.setSize(0.005F, 0.005F);
        this.noClip = true;
        this.iconIndex = 9 + this.rand.nextInt(3);
        float velModifier;
        switch (type) {
            case 0:
                this.motionX = this.motionZ = 0.0D;
                this.motionY = -0.25D + this.rand.nextDouble() * 0.125D;
                this.particleMaxAge = (int) (20.0D
                                             * ((double) world.rand.nextFloat() * 0.2D
                                                + 0.8999999761581421D));
                break;
            case 1:
                velModifier = 0.25F;
                this.motionX = (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F)
                                         * velModifier);
                this.motionY = (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F)
                                         * velModifier);
                this.motionZ = (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F)
                                         * velModifier);
                this.particleMaxAge = (int) (10.0D
                                             * ((double) world.rand.nextFloat() * 0.2D
                                                + 0.8999999761581421D));
                break;
            case 2:
                velModifier = 0.1F;
                this.motionX = (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F)
                                         * velModifier);
                this.motionY = (double) velModifier;
                this.motionZ = (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F)
                                         * velModifier);
                this.particleMaxAge = (int) (10.0D
                                             * ((double) world.rand.nextFloat() * 0.2D
                                                + 0.8999999761581421D));
                this.particleGravity = 0.5F;
                break;
            case 3:
                this.particleScale *= 0.33F;
                velModifier = 0.1F;
                this.motionX = (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F)
                                         * velModifier);
                this.motionY = (double) velModifier;
                this.motionZ = (double) ((CommonProxy.rand.nextFloat() * 2.0F - 1.0F)
                                         * velModifier);
                this.particleMaxAge = (int) (10.0D
                                             * ((double) world.rand.nextFloat() * 0.2D
                                                + 0.8999999761581421D));
                this.particleGravity = 0.0F;
        }
    }

    public void onUpdate() {
        super.onUpdate();
    }

    public void renderParticle(
        Tessellator tessy,
        float par2,
        float par3,
        float par4,
        float par5,
        float par6,
        float par7
    ) {
        tessy.draw();
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        Dartcraft.proxy.bindTexture("darticles.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var8 = (float) (this.iconIndex % 8) / 8.0F;
        float var9 = var8 + 0.124875F;
        float var10 = (float) (this.iconIndex / 8) / 8.0F;
        float var11 = var10 + 0.124875F;
        float var12 = 0.1F * this.particleScale;
        float var13 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) par2
                               - interpPosX);
        float var14 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) par2
                               - interpPosY);
        float var15 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) par2
                               - interpPosZ);
        tessy.startDrawingQuads();
        tessy.setBrightness(240);
        tessy.setColorRGBA_F(
            this.particleRed, this.particleGreen, this.particleBlue, 1.0F
        );
        tessy.addVertexWithUV(
            (double) (var13 - par3 * var12 - par6 * var12),
            (double) (var14 - par4 * var12),
            (double) (var15 - par5 * var12 - par7 * var12),
            (double) var9,
            (double) var11
        );
        tessy.addVertexWithUV(
            (double) (var13 - par3 * var12 + par6 * var12),
            (double) (var14 + par4 * var12),
            (double) (var15 - par5 * var12 + par7 * var12),
            (double) var9,
            (double) var10
        );
        tessy.addVertexWithUV(
            (double) (var13 + par3 * var12 + par6 * var12),
            (double) (var14 + par4 * var12),
            (double) (var15 + par5 * var12 + par7 * var12),
            (double) var8,
            (double) var10
        );
        tessy.addVertexWithUV(
            (double) (var13 + par3 * var12 - par6 * var12),
            (double) (var14 - par4 * var12),
            (double) (var15 + par5 * var12 - par7 * var12),
            (double) var8,
            (double) var11
        );
        tessy.draw();
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(
            new ResourceLocation("textures/particle/particles.png")
        );
        tessy.startDrawingQuads();
    }
}
