package ley.modding.dartcraft.client.model;

import net.minecraft.client.renderer.entity.RenderItem;

public class ModelEngine {
    public static float[][] angleBaseYNeg = new float[6][3];

    public static float[][] angleBaseYPos = new float[6][3];

    public static float[][] angleBaseXPos = new float[6][3];

    public static final float factor = 0.0625F;

    public static final RenderItem itemRenderer = new RenderItem();

    static {
        float pi = 3.141593F;
        angleBaseYNeg[0][2] = pi;
        angleBaseYNeg[2][0] = -pi / 2.0F;
        angleBaseYNeg[3][0] = pi / 2.0F;
        angleBaseYNeg[4][2] = pi / 2.0F;
        angleBaseYNeg[5][2] = -pi / 2.0F;
        angleBaseYPos[1][2] = pi;
        angleBaseYPos[2][0] = pi / 2.0F;
        angleBaseYPos[3][0] = -pi / 2.0F;
        angleBaseYPos[4][2] = -pi / 2.0F;
        angleBaseYPos[5][2] = pi / 2.0F;
        angleBaseXPos[0][0] = -pi / 2.0F;
        angleBaseXPos[1][0] = pi / 2.0F;
        angleBaseXPos[2][1] = pi;
        angleBaseXPos[4][1] = -pi / 2.0F;
        angleBaseXPos[5][1] = pi / 2.0F;
    }
}
