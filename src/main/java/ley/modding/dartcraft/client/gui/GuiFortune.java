package ley.modding.dartcraft.client.gui;

import org.lwjgl.opengl.GL11;

import ley.modding.dartcraft.Dartcraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

public class GuiFortune extends GuiScreen {
    private String text;
    private int xSize = 236;
    private int ySize = 69;
    private int posX;
    private int posY;

    public GuiFortune(ItemStack stack) {
        this.text
            = stack.hasTagCompound() ? stack.getTagCompound().getString("fortune") : "";
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        Dartcraft.proxy.bindTexture("fortuneGui.png");
        this.posX = (this.width - this.xSize) / 2;
        this.posY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(this.posX, this.posY, 0, 0, this.xSize, this.ySize);
        int rows = this.fontRendererObj.getStringWidth(this.text) / 203;
        GL11.glColor4f((float) 0.0f, (float) 0.0f, (float) 0.0f, (float) 1.0f);
        if (rows == 0) {
            this.posX = (this.width - this.fontRendererObj.getStringWidth(this.text)) / 2;
            this.posY = (this.height - this.fontRendererObj.FONT_HEIGHT) / 2;
            this.fontRendererObj.drawString(this.text, this.posX, this.posY, 0x404040);
        } else {
            this.posY
                = this.height / 2 - this.fontRendererObj.FONT_HEIGHT * (rows + 1) / 2;
            this.fontRendererObj.drawSplitString(
                this.text, this.posX + 15, this.posY, 203, 0x404040
            );
        }
    }
}
