package ley.modding.dartcraft.client.gui;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.network.DartPacket;
import ley.modding.dartcraft.network.PacketClipButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiClipboard extends GuiContainer {
    public static RenderItem itemRenderer = new RenderItem();

    private int posX;

    private int posY;

    private final int DELAY = 8;

    private ContainerClipboard container;

    private EntityPlayer player;

    private Rectangle distBounds;

    private Rectangle balanceBounds;

    private Rectangle clearBounds;

    private int distRender;

    private int clearRender;

    private int balanceRender;

    private boolean useInventory;

    public GuiClipboard(ContainerClipboard cont) {
        super(cont);
        this.container = cont;
        this.player = cont.user;
        this.xSize = 176;
        this.ySize = 156;
    }

    public void initGui() {
        super.initGui();
        this.balanceBounds = new Rectangle(this.guiLeft + 88, this.guiTop + 11, 11, 11);
        this.distBounds = new Rectangle(this.guiLeft + 81, this.guiTop + 52, 11, 11);
        this.clearBounds = new Rectangle(this.guiLeft + 95, this.guiTop + 52, 11, 11);
        this.distRender = this.clearRender = this.balanceRender = 0;
    }

    public void updateScreen() {
        super.updateScreen();
        if (this.container == null || !this.container.canStayOpen(this.container.user))
            this.player.closeScreen();
    }

    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
        if (button != 0)
            return;
        Point pos = new Point(x, y);
        if (this.balanceBounds.contains(pos)) {
            getClass();
            this.balanceRender = 8;
            Dartcraft.proxy.sendPacketToServer((DartPacket
            ) new PacketClipButton((EntityPlayer) ((GuiScreen) this).mc.thePlayer, 0));
            this.container.balanceItems();
        }
        if (this.distBounds.contains(pos)) {
            getClass();
            this.distRender = 8;
            Dartcraft.proxy.sendPacketToServer((DartPacket
            ) new PacketClipButton((EntityPlayer) ((GuiScreen) this).mc.thePlayer, 1));
            this.container.doDistribute();
        }
        if (this.clearBounds.contains(pos)) {
            getClass();
            this.clearRender = 8;
            Dartcraft.proxy.sendPacketToServer((DartPacket
            ) new PacketClipButton((EntityPlayer) ((GuiScreen) this).mc.thePlayer, 2));
            this.container.clearMatrix();
        }
    }

    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Dartcraft.proxy.bindTexture("clipGui.png");
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if (this.distRender > 0) {
            drawTexturedModalRect(this.distBounds.x, this.distBounds.y, 176, 11, 11, 11);
            this.distRender--;
        }
        if (this.balanceRender > 0) {
            drawTexturedModalRect(
                this.balanceBounds.x, this.balanceBounds.y, 176, 0, 11, 11
            );
            this.balanceRender--;
        }
        if (this.clearRender > 0) {
            drawTexturedModalRect(
                this.clearBounds.x, this.clearBounds.y, 176, 22, 11, 11
            );
            this.clearRender--;
        }
    }

    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        fontRendererObj.drawString("Clipboard", 105, 11, 4210752);
    }

    private void drawSlot(ItemStack stack, int x, int y) {
        this.zLevel = 50.0F;
        itemRenderer.renderWithColor = false;
        float value = 0.8F;
        GL11.glColor4f(value, value, value, 0.65F);
        GL11.glEnable(3042);
        GL11.glDisable(2896);
        itemRenderer.renderItemIntoGUI(
            this.fontRendererObj, ((GuiScreen) this).mc.renderEngine, stack, x, y, false
        );
        GL11.glDisable(3042);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.zLevel = 0.0F;
    }

    public boolean
    handleDragNDrop(GuiContainer gui, int x, int y, ItemStack stack, int button) {
        return false;
    }

    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
        return false;
    }

    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
        return null;
    }
}
