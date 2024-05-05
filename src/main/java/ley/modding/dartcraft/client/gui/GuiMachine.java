package ley.modding.dartcraft.client.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.AccessLevel;
import ley.modding.dartcraft.client.gui.tabs.GuiTab;
import ley.modding.dartcraft.client.gui.tabs.Tab;
import ley.modding.dartcraft.network.PacketDesocket;
import ley.modding.dartcraft.network.PacketUpdateAccessLevel;
import ley.modding.dartcraft.plugin.DartPluginFortunes;
import net.anvilcraft.anvillib.usercache.UserCache;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GuiMachine extends GuiTab implements INEIGuiHandler {
    public ContainerMachine machine;
    protected InfoTab infoTab;
    protected AccessTab accessTab;
    protected boolean showTabs;

    public GuiMachine(ContainerMachine machine) {
        super(machine);
        this.machine = machine;
        this.infoTab = new InfoTab((Gui) this);
        this.accessTab = new AccessTab((Gui) this);
        this.showTabs = true;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.machine == null
            || !this.machine.canInteractWith((EntityPlayer) this.mc.thePlayer)
            || this.machine.machine == null || this.machine.machine.isInvalid()) {
            this.mc.thePlayer.closeScreen();
        }
    }

    @Override
    public void handleMouseClick(Slot slot, int index, int button, int par4) {
        super.handleMouseClick(slot, index, button, par4);
        if (this.machine == null || this.machine.machine == null) {
            return;
        }
        if (slot != null && slot.getHasStack()
            && button == 1
            //&& slot.getStack().getItem() instanceof ItemUpgradeCore
            && index == this.machine.machine.socketSlot) {
            if (Keyboard.isKeyDown((int) 42) || Keyboard.isKeyDown((int) 54)) {
                Dartcraft.channel.sendToServer(new PacketDesocket(index));
                this.machine.desocket();
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {}

    @Override
    public VisiblityData
    modifyVisiblity(GuiContainer gui, VisiblityData currentVisibility) {
        currentVisibility.showNEI = false;
        return currentVisibility;
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots(GuiContainer gui, ItemStack item) {
        return ImmutableList.of();
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas(GuiContainer gui) {
        return null;
    }

    @Override
    public boolean
    handleDragNDrop(GuiContainer gui, int x, int y, ItemStack stack, int button) {
        return false;
    }

    @Override
    public boolean hideItemPanelSlot(GuiContainer gui, int x, int y, int w, int h) {
        return false;
    }

    @Override
    protected void drawTooltips() {}

    protected class AccessTab extends Tab {
        private Rectangle accessBounds;

        public AccessTab(Gui gui) {
            super(gui);
            this.maxHeight = 90;
            this.maxWidth = 110;
            this.overlayColor = 0xffe49f;
            this.accessBounds = new Rectangle(10, 20, 85, 59);
        }

        @Override
        public void draw(int x, int y) {
            if (!GuiMachine.this.showTabs) {
                return;
            }
            this.drawBackground(x, y);
            if (this.leftSide) {
                this.drawIcon("items.png", 17, x - 18, y + 2);
                x -= this.maxWidth;
            } else {
                this.drawIcon("items.png", 17, x + 2, y + 2);
            }
            if (!this.isFullyOpened()) {
                return;
            }
            GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
            Dartcraft.proxy.bindTexture("furnaceGui.png");
            GuiMachine.this.drawTexturedModalRect(x + 10, y + 20, 0, 166, 85, 59);
            int posX = this.leftSide ? x
                    + ((this.maxWidth - 16) / 2
                       - GuiMachine.this.fontRendererObj.getStringWidth("Access") / 2)
                                     : x + 23;
            tabFontRenderer.drawStringWithShadow("Access", posX, y + 7, 0xFFFFFF);
            tabFontRenderer.drawString("Owner:", x + 18, y + 26, 0xFFFFFF);
            String ownerName
                = UserCache.INSTANCE.getCached(GuiMachine.this.machine.machine.getOwner()
                );
            tabFontRenderer.drawStringWithShadow(
                ownerName == null ? "<unknown>" : ownerName, x + 18, y + 36, 0x606060
            );
            AccessLevel access = GuiMachine.this.machine.machine.getAccessLevel();
            tabFontRenderer.drawStringWithShadow(
                access.displayName, x + 22, y + 50, 0x808080
            );
        }

        @Override
        public boolean handleMouseClicked(int x, int y, int mouseButton) {
            Point point;
            if (!GuiMachine.this.showTabs) {
                return false;
            }
            int posX = !this.leftSide
                ? this.currentShiftX + this.accessBounds.x
                : this.currentShiftX + this.accessBounds.x - this.maxWidth;
            Rectangle checkBounds = new Rectangle(
                posX,
                this.currentShiftY + this.accessBounds.y,
                this.accessBounds.width,
                this.accessBounds.height
            );
            if (checkBounds.contains(point = new Point(x, y))) {
                try {
                    if (GuiMachine.this.machine.machine.getOwner().equals(
                            GuiMachine.this.mc.thePlayer.getUniqueID()
                        )) {
                        AccessLevel level = GuiMachine.this.machine.machine.access.next();
                        GuiMachine.this.machine.machine.access = level;
                        Dartcraft.channel.sendToServer(new PacketUpdateAccessLevel(level)
                        );
                    }
                } catch (Exception e) {}
                return true;
            }
            return false;
        }

        @Override
        public String getTooltip() {
            return null;
        }
    }

    protected class InfoTab extends Tab {
        public ArrayList<String> infoStrings;
        private String currentInfo;
        private int index;

        public InfoTab(Gui gui) {
            super(gui);
            this.infoStrings = new ArrayList<>();
            this.leftSide = false;
            this.overlayColor = 1217260;
            this.maxHeight = 110;
            this.maxWidth = 110;
            this.currentInfo = this.getRandomItem();
            this.index = -1;
        }

        public void addInfoString(String info) {
            this.infoStrings.add(info);
        }

        @Override
        public void toggleOpen() {
            super.toggleOpen();
            if (this.isOpen()) {
                ++this.index;
                if (this.index > this.infoStrings.size()) {
                    this.index = 0;
                }
                try {
                    this.currentInfo = this.infoStrings.get(this.index);
                } catch (Exception e) {
                    this.currentInfo = this.getRandomItem();
                }
            }
        }

        @Override
        public void draw(int x, int y) {
            if (!GuiMachine.this.showTabs) {
                return;
            }
            this.drawBackground(x, y);
            if (this.leftSide) {
                this.drawIcon("items.png", 0, x - 18, y + 2);
                x -= this.maxWidth;
            } else {
                this.drawIcon("items.png", 0, x + 2, y + 2);
            }
            if (!this.isFullyOpened()) {
                return;
            }
            int posX = this.leftSide ? x
                    + ((this.maxWidth - 16) / 2
                       - GuiMachine.this.fontRendererObj.getStringWidth("Information") / 2
                    )
                                     : x + 22;
            tabFontRenderer.drawStringWithShadow("Information", posX, y + 6, 0xFFFFFF);
            tabFontRenderer.drawSplitString(
                this.currentInfo, x + 8, y + 20, this.maxWidth - 14, 0
            );
        }

        private String getRandomItem() {
            return DartPluginFortunes.getFortune();
        }

        @Override
        public String getTooltip() {
            return null;
        }
    }
}
