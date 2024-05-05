package ley.modding.dartcraft.client.renderer.tile;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.client.model.ModelInfuser;
import ley.modding.dartcraft.tile.TileEntityInfuser;
import ley.modding.dartcraft.util.DartUtils;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

public class RenderTileEntityInfuser extends TileEntitySpecialRenderer {
    private ModelInfuser model = new ModelInfuser();
    private ModelBook book = new ModelBook();

    public RenderTileEntityInfuser() {}

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
        if (!(te instanceof TileEntityInfuser)) {
            return;
        }
        TileEntityInfuser infuser = (TileEntityInfuser) te;
        this.render(infuser, x, y, z, f);
    }

    public void render(TileEntityInfuser infuser, double x, double y, double z, float f) {
        GL11.glPushMatrix();
        GL11.glDisable((int) 2896);
        GL11.glTranslated((double) (x + 0.5), (double) (y - 0.46), (double) (z + 0.5));
        Dartcraft.proxy.bindTexture("infuserModel.png");
        float scale = 0.058f;
        int color = infuser != null ? infuser.color : 0;
        Color col = new Color(DartUtils.getMcColor(color));
        GL11.glColor4f(
            (float) ((float) col.getRed() / 255.0f),
            (float) ((float) col.getGreen() / 255.0f),
            (float) ((float) col.getBlue() / 255.0f),
            (float) 1.0f
        );
        this.model.leg1.render(scale);
        this.model.leg2.render(scale);
        this.model.leg3.render(scale);
        this.model.leg4.render(scale);
        this.model.table_top.render(scale);
        this.model.tank.render(scale);
        GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        if (infuser != null && infuser.hasTome) {
            this.renderBook(infuser, x, y, z, f);
        }
        GL11.glEnable((int) 2896);
        GL11.glPopMatrix();
    }

    private void
    renderBook(TileEntityInfuser infuser, double x, double y, double z, float f) {
        float var10;
        float var9 = infuser != null && infuser.getWorldObj() != null
            ? (float) infuser.getWorldObj().getWorldTime() + f
            : 0.0f;
        GL11.glTranslated((double) 0.0, (double) 1.25, (double) 0.0);
        for (var10 = infuser.bookRotation2 - infuser.bookRotationPrev; var10 >= 3.141593f;
             var10 -= 6.283186f) {}
        while (var10 < -3.141593f) {
            var10 += 6.283186f;
        }
        float var11 = infuser.bookRotationPrev + var10 * f;
        GL11.glRotatef(
            (float) (-var11 * 180.0f / 3.141593f),
            (float) 0.0f,
            (float) 1.0f,
            (float) 0.0f
        );
        GL11.glRotatef((float) 80.0f, (float) 0.0f, (float) 0.0f, (float) 1.0f);
        Dartcraft.proxy.bindTexture("dartBook.png");
        float var12 = infuser.pageFlipPrev + (infuser.pageFlip - infuser.pageFlipPrev) * f
            + 0.25f;
        float var13 = infuser.pageFlipPrev + (infuser.pageFlip - infuser.pageFlipPrev) * f
            + 0.75f;
        var12 = (var12 - (float) MathHelper.truncateDoubleToInt((double) var12)) * 1.6f;
        var13 = (var13 - (float) MathHelper.truncateDoubleToInt((double) var13)) * 1.6f
            - 0.3f;
        if (var12 < 0.0f) {
            var12 = 0.0f;
        }
        if (var13 < 0.0f) {
            var13 = 0.0f;
        }
        if (var12 > 1.0f) {
            var12 = 1.0f;
        }
        if (var13 > 1.0f) {
            var13 = 1.0f;
        }
        float var14
            = infuser.bookSpreadPrev + (infuser.bookSpread - infuser.bookSpreadPrev) * f;
        this.book.render((Entity) null, var9, var12, var13, var14, 0.0f, 0.0625f);
    }
}
