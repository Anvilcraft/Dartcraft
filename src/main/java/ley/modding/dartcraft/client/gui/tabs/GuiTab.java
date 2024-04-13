package ley.modding.dartcraft.client.gui.tabs;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public abstract class GuiTab extends GuiContainer {
    protected enum SlotColor {
        BLUE,
        RED,
        YELLOW,
        ORANGE,
        GREEN,
        PURPLE;
    }

    protected enum SlotType {
        //SINGLE, OUTPUT, DOUBLEOUTPUT;
        TOP,
        BOTTOM,
        FULL
    }

    protected enum SlotRender {
        TOP,
        BOTTOM,
        FULL;
    }

    protected static int SCALE_ENERGY = 42;

    protected static int SCALE_LIQUID = 60;

    protected static int SCALE_PROGRESS = 24;

    protected static int SCALE_SPEED = 16;

    protected ArrayList<Tab> tabListLeft = new ArrayList<Tab>();

    protected ArrayList<Tab> tabListRight = new ArrayList<Tab>();

    protected int mouseX = 0;

    protected int mouseY = 0;

    public static boolean enableGuiBorders;

    public static boolean enableInfoTabs;

    public static boolean enableTutorialTabs;

    public GuiTab(Container container) {
        super(container);
    }

    protected void func_74189_g(int i, int j) {
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        drawTabs(this.mouseX, this.mouseY);
        drawTooltips();
        GL11.glEnable(2896);
        GL11.glEnable(2929);
    }

    protected abstract void drawTooltips();

    protected void
    drawColoredSlot(int x, int y, SlotColor color, SlotType type, SlotRender render) {
        if (enableGuiBorders) {
            drawColoredSlotWithBorder(x, y, color, type, render);
        } else {
            drawColoredSlotNoBorder(x, y, color, type, render);
        }
    }

    protected void drawColoredSlotNoBorder(
        int x, int y, SlotColor color, SlotType type, SlotRender render
    ) {
        int sizeX = 0;
        int sizeY = 0;
        int offsetX = color.ordinal() / 3 * 128;
        int offsetY = color.ordinal() % 3 * 32;
        switch (type) {
            case TOP:
                sizeX = 16;
                sizeY = 16;
                offsetX += 8;
                offsetY += 8;
                break;
            case BOTTOM:
                sizeX = 24;
                sizeY = 24;
                offsetX += 36;
                offsetY += 4;
                break;
            case FULL:
                sizeX = 42;
                sizeY = 24;
                offsetX += 75;
                offsetY += 4;
                break;
        }
        switch (render) {
            case TOP:
                sizeY /= 2;
                break;
            case BOTTOM:
                sizeY /= 2;
                y += sizeY;
                offsetY += sizeY;
                break;
        }
        drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
    }

    protected void drawColoredSlotWithBorder(
        int x, int y, SlotColor color, SlotType type, SlotRender render
    ) {
        int sizeX = 32;
        int sizeY = 32;
        int offsetX = color.ordinal() / 3 * 128;
        int offsetY = color.ordinal() % 3 * 32;
        offsetX += type.ordinal() * 32;
        if (type.ordinal() == 2)
            sizeX = 64;
        switch (type) {
            case TOP:
                x -= 8;
                y -= 8;
                break;
            case BOTTOM:
                x -= 4;
                y -= 4;
                break;
            case FULL:
                x -= 11;
                y -= 4;
                break;
        }
        switch (render) {
            case TOP:
                sizeY /= 2;
                break;
            case BOTTOM:
                sizeY /= 2;
                y += sizeY;
                offsetY += sizeY;
                break;
        }
        drawTexturedModalRect(x, y, offsetX, offsetY, sizeX, sizeY);
    }

    protected void drawColoredLiquidSlot(int x, int y, SlotColor color) {
        if (enableGuiBorders) {
            drawColoredLiquidSlotWithBorder(x, y, color);
        } else {
            drawColoredLiquidSlotNoBorder(x, y, color);
        }
    }

    protected void drawColoredLiquidSlotNoBorder(int x, int y, SlotColor color) {
        int sizeX = 16;
        int sizeY = 60;
        int offsetX = color.ordinal() * 32;
        int offsetY = 96;
        drawTexturedModalRect(x, y, offsetX + 8, offsetY + 2, sizeX, sizeY);
    }

    protected void drawColoredLiquidSlotWithBorder(int x, int y, SlotColor color) {
        int sizeX = 32;
        int sizeY = 64;
        int offsetX = color.ordinal() * 32;
        int offsetY = 96;
        drawTexturedModalRect(x - 8, y - 2, offsetX, offsetY, sizeX, sizeY);
    }

    protected void
    drawLiquid(int j, int k, int liquidId, NBTTagCompound comp, int width, int height) {
        int liquidImgIndex = 0;
        try {
            FluidStack tempStack = new FluidStack(liquidId, 0, comp);
            liquidImgIndex = tempStack.getFluid().getSpriteNumber();
        } catch (Exception e) {
            return;
        }
        int imgLine = liquidImgIndex / 16;
        int imgColumn = liquidImgIndex - imgLine * 16;
        int x = 0;
        int y = 0;
        int drawHeight = 0;
        int drawWidth = 0;
        for (x = 0; x < width; x += 16) {
            for (y = 0; y < height; y += 16) {
                drawWidth = Math.min(width - x, 16);
                drawHeight = Math.min(height - y, 16);
                drawTexturedModalRect(
                    j + x, k + y, imgColumn * 16, imgLine * 16, drawWidth, drawHeight
                );
            }
        }
    }

    protected void drawTooltip(String tooltip) {
        drawCreativeTabHoveringText(tooltip, this.mouseX, this.mouseY);
    }

    protected int getCenteredOffset(String string) {
        return getCenteredOffset(string, this.xSize);
    }

    protected int getCenteredOffset(String string, int xWidth) {
        return (xWidth - fontRendererObj.getStringWidth(string)) / 2;
    }

    protected void mouseClicked(int x, int y, int mouseButton) {
        super.mouseClicked(x, y, mouseButton);
        Tab tab = getTabAtPosition(this.mouseX, this.mouseY);
        if (tab != null
            && !tab.handleMouseClicked(this.mouseX, this.mouseY, mouseButton)) {
            if (tab.leftSide) {
                for (Tab other : this.tabListLeft) {
                    if (other != tab && other.isOpen())
                        other.toggleOpen();
                }
            } else {
                for (Tab other : this.tabListRight) {
                    if (other != tab && other.isOpen())
                        other.toggleOpen();
                }
            }
            tab.toggleOpen();
        }
    }

    public void handleMouseInput() {
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        this.mouseX = x - (this.width - this.xSize) / 2;
        this.mouseY = y - (this.height - this.ySize) / 2;
        super.handleMouseInput();
    }

    public void addTab(Tab tab) {
        if (tab.leftSide) {
            this.tabListLeft.add(tab);
            if (TabVars.getOpenedLeftTab() != null
                && tab.getClass().equals(TabVars.getOpenedLeftTab()))
                tab.setFullyOpen();
        } else {
            this.tabListRight.add(tab);
            if (TabVars.getOpenedRightTab() != null
                && tab.getClass().equals(TabVars.getOpenedRightTab()))
                tab.setFullyOpen();
        }
    }

    protected void drawTabs(int mX, int mY) {
        int yPosRight = 4;
        int yPosLeft = 4;
        for (Tab tab1 : this.tabListLeft) {
            tab1.update();
            if (!tab1.isVisible())
                continue;
            tab1.draw(0, yPosLeft);
            yPosLeft += tab1.getHeight();
        }
        for (Tab tab1 : this.tabListRight) {
            tab1.update();
            if (!tab1.isVisible())
                continue;
            tab1.draw(this.xSize, yPosRight);
            yPosRight += tab1.getHeight();
        }
        Tab tab = getTabAtPosition(mX, mY);
        if (tab != null) {
            String tooltip = tab.getTooltip();
            if (tooltip != null)
                drawTooltip(tooltip);
        }
    }

    protected Tab getTabAtPosition(int mX, int mY) {
        int xShift = 0;
        int yShift = 4;
        int i;
        for (i = 0; i < this.tabListLeft.size(); i++) {
            Tab tab = this.tabListLeft.get(i);
            if (tab.isVisible()) {
                tab.currentShiftX = xShift;
                tab.currentShiftY = yShift;
                if (tab.intersectsWith(mX, mY, xShift, yShift))
                    return tab;
                yShift += tab.getHeight();
            }
        }
        xShift = this.xSize;
        yShift = 4;
        for (i = 0; i < this.tabListRight.size(); i++) {
            Tab tab = this.tabListRight.get(i);
            if (tab.isVisible()) {
                tab.currentShiftX = xShift;
                tab.currentShiftY = yShift;
                if (tab.intersectsWith(mX, mY, xShift, yShift))
                    return tab;
                yShift += tab.getHeight();
            }
        }
        return null;
    }
}
