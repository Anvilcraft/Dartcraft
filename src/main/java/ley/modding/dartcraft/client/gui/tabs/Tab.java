package ley.modding.dartcraft.client.gui.tabs;

import cpw.mods.fml.client.FMLClientHandler;
import ley.modding.dartcraft.Dartcraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

public abstract class Tab {
    protected static FontRenderer tabFontRenderer = (FMLClientHandler.instance().getClient()).fontRenderer;

    private boolean open;

    protected Gui myGui;

    public boolean leftSide;

    protected int overlayColor = 16777215;

    public int currentShiftX = 0;

    public int currentShiftY = 0;

    protected int limitWidth = 128;

    public int maxWidth = 124;

    protected int minWidth = 22;

    protected int currentWidth = this.minWidth;

    public int maxHeight = 22;

    protected int minHeight = 22;

    protected int currentHeight = this.minHeight;

    public Tab(Gui gui) {
        this.myGui = gui;
    }

    public abstract void draw(int paramInt1, int paramInt2);

    protected void drawBackground(int x, int y) {
        float colorR = (this.overlayColor >> 16 & 0xFF) / 255.0F;
        float colorG = (this.overlayColor >> 8 & 0xFF) / 255.0F;
        float colorB = (this.overlayColor & 0xFF) / 255.0F;
        GL11.glColor4f(colorR, colorG, colorB, 1.0F);
        if (this.leftSide) {
            Dartcraft.proxy.bindTexture("tab_left.png");
            this.myGui.drawTexturedModalRect(x - this.currentWidth, y + 4, 0, 256 - this.currentHeight + 4, 4, this.currentHeight - 4);
            this.myGui.drawTexturedModalRect(x - this.currentWidth + 4, y, 256 - this.currentWidth + 4, 0, this.currentWidth - 4, 4);
            this.myGui.drawTexturedModalRect(x - this.currentWidth, y, 0, 0, 4, 4);
            this.myGui.drawTexturedModalRect(x - this.currentWidth + 4, y + 4, 256 - this.currentWidth + 4, 256 - this.currentHeight + 4, this.currentWidth - 4, this.currentHeight - 4);
        } else {
            Dartcraft.proxy.bindTexture("tab_right.png");
            this.myGui.drawTexturedModalRect(x, y, 0, 256 - this.currentHeight, 4, this.currentHeight);
            this.myGui.drawTexturedModalRect(x + 4, y, 256 - this.currentWidth + 4, 0, this.currentWidth - 4, 4);
            this.myGui.drawTexturedModalRect(x, y, 0, 0, 4, 4);
            this.myGui.drawTexturedModalRect(x + 4, y + 4, 256 - this.currentWidth + 4, 256 - this.currentHeight + 4, this.currentWidth - 4, this.currentHeight - 4);
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void drawIcon(String texture, int iconIndex, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Dartcraft.proxy.bindTexture(texture);
        int textureRow = iconIndex >> 4;
        int textureColumn = iconIndex - 16 * textureRow;
        this.myGui.drawTexturedModalRect(x, y, 16 * textureColumn, 16 * textureRow, 16, 16);
    }

    public int getHeight() {
        return this.currentHeight;
    }

    public abstract String getTooltip();

    public boolean handleMouseClicked(int x, int y, int mouseButton) {
        return false;
    }

    public boolean intersectsWith(int mouseX, int mouseY, int shiftX, int shiftY) {
        if (this.leftSide) {
            if (mouseX <= shiftX && mouseX >= shiftX - this.currentWidth && mouseY >= shiftY && mouseY <= shiftY + this.currentHeight)
                return true;
        } else if (mouseX >= shiftX && mouseX <= shiftX + this.currentWidth && mouseY >= shiftY && mouseY <= shiftY + this.currentHeight) {
            return true;
        }
        return false;
    }

    protected boolean isFullyOpened() {
        return (this.currentWidth >= this.maxWidth);
    }

    public boolean isOpen() {
        return this.open;
    }

    public boolean isVisible() {
        return true;
    }

    public void setFullyOpen() {
        this.open = true;
        this.currentWidth = this.maxWidth;
        this.currentHeight = this.maxHeight;
    }

    public void toggleOpen() {
        if (this.open) {
            this.open = false;
            if (this.leftSide) {
                TabVars.setOpenedLeftTab(null);
            } else {
                TabVars.setOpenedRightTab(null);
            }
        } else {
            this.open = true;
            if (this.leftSide) {
                TabVars.setOpenedLeftTab(getClass());
            } else {
                TabVars.setOpenedRightTab(getClass());
            }
        }
    }

    public void update() {
        if (this.open && this.currentWidth < this.maxWidth) {
            this.currentWidth += 8;
        } else if (!this.open && this.currentWidth > this.minWidth) {
            this.currentWidth -= 8;
        }
        if (this.currentWidth > this.maxWidth) {
            this.currentWidth = this.maxWidth;
        } else if (this.currentWidth < this.minWidth) {
            this.currentWidth = this.minWidth;
        }
        if (this.open && this.currentHeight < this.maxHeight) {
            this.currentHeight += 8;
        } else if (!this.open && this.currentHeight > this.minHeight) {
            this.currentHeight -= 8;
        }
        if (this.currentHeight > this.maxHeight) {
            this.currentHeight = this.maxHeight;
        } else if (this.currentHeight < this.minHeight) {
            this.currentHeight = this.minHeight;
        }
    }
}
