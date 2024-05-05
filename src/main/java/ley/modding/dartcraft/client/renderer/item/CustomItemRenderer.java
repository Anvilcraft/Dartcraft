package ley.modding.dartcraft.client.renderer.item;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class CustomItemRenderer extends RenderItem {
    @Override
    public void renderItemOverlayIntoGUI(
        FontRenderer fonty,
        TextureManager manager,
        ItemStack stack,
        int par4,
        int par5,
        String name
    ) {
        if (stack != null) {
            if (stack.stackSize > 1 || name != null) {
                String s1 = name == null ? String.valueOf(stack.stackSize) : name;
                GL11.glDisable((int) 2896);
                GL11.glDisable((int) 2929);
                fonty.drawStringWithShadow(
                    s1, par4 + 19 - 2 - fonty.getStringWidth(s1), par5 + 6 + 3, 0xFFFFFF
                );
                GL11.glEnable((int) 2896);
            }
            if (stack.isItemDamaged()) {
                int k = (int) Math.round(
                    13.0
                    - (double) stack.getItemDamageForDisplay() * 13.0
                        / (double) stack.getMaxDamage()
                );
                int l = (int) Math.round(
                    255.0
                    - (double) stack.getItemDamageForDisplay() * 255.0
                        / (double) stack.getMaxDamage()
                );
                GL11.glDisable((int) 2896);
                GL11.glDisable((int) 2929);
                GL11.glDisable((int) 3553);
                Tessellator tessellator = Tessellator.instance;
                int i1 = 255 - l << 16 | l << 8;
                int j1 = (255 - l) / 4 << 16 | 0x3F00;
                this.renderQuad(tessellator, par4 + 2, par5 + 13, 13, 2, 0);
                this.renderQuad(tessellator, par4 + 2, par5 + 13, 12, 1, j1);
                this.renderQuad(tessellator, par4 + 2, par5 + 13, k, 1, i1);
                GL11.glEnable((int) 3553);
                GL11.glEnable((int) 2896);
                GL11.glEnable((int) 2929);
                GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
            }
        }
    }

    private void
    renderQuad(Tessellator tessy, int par2, int par3, int par4, int par5, int par6) {
        tessy.startDrawingQuads();
        tessy.setColorOpaque_I(par6);
        tessy.addVertex((double) (par2 + 0), (double) (par3 + 0), 0.0);
        tessy.addVertex((double) (par2 + 0), (double) (par3 + par5), 0.0);
        tessy.addVertex((double) (par2 + par4), (double) (par3 + par5), 0.0);
        tessy.addVertex((double) (par2 + par4), (double) (par3 + 0), 0.0);
        tessy.draw();
    }
}
