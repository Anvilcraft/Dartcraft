package ley.modding.dartcraft.client.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerTooltipHandler;
import codechicken.nei.guihook.IGuiSlotDraw;
import cpw.mods.fml.common.Loader;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.upgrades.ForceUpgrade;
import ley.modding.dartcraft.api.upgrades.IForceUpgradeMaterial;
import ley.modding.dartcraft.block.BlockLiquidForce;
import ley.modding.dartcraft.block.DartBlocks;
import ley.modding.dartcraft.client.gui.tabs.Tab;
import ley.modding.dartcraft.network.PacketInfuserStart;
import ley.modding.dartcraft.tile.TileEntityInfuser;
import ley.modding.dartcraft.tile.TileEntityInfuser.ErrorType;
import ley.modding.dartcraft.util.ForceUpgradeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class GuiInfuser
    extends GuiMachine implements IContainerTooltipHandler, IGuiSlotDraw {
    private TileEntityInfuser infuser;
    private ContainerInfuser container;
    private ContainerInfoPage infoPage;
    private EntityPlayer player;
    private ItemStack lastItem;
    private Rectangle goBounds;
    private Rectangle infoBounds;
    private Rectangle escapeBounds;
    private Rectangle forwardBounds;
    private Rectangle backBounds;
    private Rectangle tankBounds;
    private Rectangle energyBounds;
    private boolean infoOpen;
    private boolean canUpgrade;
    private int infoWidth;
    private int infoHeight;
    private int forwardTime;
    private int backTime;
    private int lastPacket;
    private int upgradeCheck;
    private float expAlpha;
    private boolean alphaMode;
    private ErrorTab errorTab;

    public GuiInfuser(ContainerInfuser container) {
        super(container);
        this.infuser = container.infuser;
        this.container = container;
        this.xSize = 176;
        this.ySize = 209;
        this.infoWidth = 208;
        this.infoHeight = 175;
        this.infoTab.maxWidth = 130;
        this.infoTab.maxHeight = 120;
        this.loadInfoStrings();
        this.addTab(this.infoTab);
        this.addTab(this.accessTab);
        this.errorTab = new ErrorTab((Gui) this);
        this.player = container.usingPlayer;
        this.upgradeCheck = 0;
        this.lastPacket = 0;
        this.canUpgrade = false;
        this.lastItem = null;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.goBounds = new Rectangle(this.guiLeft + 39, this.guiTop + 100, 13, 13);
        this.infoBounds = new Rectangle(this.guiLeft + 123, this.guiTop + 16, 13, 13);
        this.tankBounds = new Rectangle(this.guiLeft + 10, this.guiTop + 60, 16, 58);
        this.energyBounds = new Rectangle(this.guiLeft + 152, this.guiTop + 11, 12, 107);
        int posX = (this.width - this.infoWidth) / 2;
        int posY = (this.height - this.infoHeight) / 2;
        this.escapeBounds = new Rectangle(posX + 190, posY + 5, 13, 13);
        this.backBounds = new Rectangle(posX + 160, posY + 5, 13, 13);
        this.forwardBounds = new Rectangle(posX + 175, posY + 5, 13, 13);
    }

    private void loadInfoStrings() {
        this.infoTab.addInfoString(
            "The Force Infuser can be used to imbue your Force Tools with special upgrades that range from vanilla Fortune to auto grinding or even wacky side-effects."
        );
        this.infoTab.addInfoString(
            "The Force Infuser requires both Liquid Force and a significant amount of Minecraft Joules to run."
        );
        // TODO
        if (/*Config.requireExp*/ true) {
            this.infoTab.addInfoString(
                "The Force Infuser will take one level of experience from you for each upgrade material you want to use."
            );
        }
        this.infoTab.addInfoString(
            "Healing seems to have the opposite effect on undead creatures."
        );
        this.infoTab.addInfoString(
            "Force Bows now have toggleable modes, just grab one and shift right click to change modes."
        );
        this.infoTab.addInfoString(
            "Most Force Tools now have an \"Area\" mode that can be toggled by right-clicking with the tool in your hands.  It may be slower, but it sure breaks a lot of blocks!  Be sure to mind your durability."
        );
        this.infoTab.addInfoString(
            "Upgrade materials show their upgrade in this Gui as a secondary tooltip in yellow.  If a third tooltip is active that is the Tier at which the upgrade is unlocked."
        );
        this.infoTab.addInfoString(
            "Yellow question marks in a tooltip means the Upgrade Tome has not yet seen this upgrade."
        );
        this.infoTab.addInfoString(
            "Upgrade Tomes are created by right clicking a Book with a Force Rod."
        );
        this.infoTab.addInfoString(
            "Experience Tomes can hold any amount of experience for the player, and are tradable to boot."
        );
        if (Loader.isModLoaded((String) "IC2")) {
            this.infoTab.addInfoString(
                "Try using your Force Rod on an Experience Tome, but don't expect to get it back..."
            );
        }
        this.infoTab.addInfoString(
            "Be careful with that button!  Once you press it the only way to stop it is with a quick Force-Wrenching."
        );
        this.infoTab.addInfoString("Force Armor must be crafted out of Force Ingots.");
        this.infoTab.addInfoString(
            "Additional upgrade slots are unlocked by advancing your Upgrade Tome in tier."
        );
        this.infoTab.addInfoString("Ender Bows only work on players if PVP is enabled.");
        this.infoTab.addInfoString(
            "You're going to need Force Sticks, why not try your Force Rod on a sapling?"
        );
        this.infoTab.addInfoString("Use your Force Shears on other animals.");
        if (Loader.isModLoaded((String) "Forestry")) {
            this.infoTab.addInfoString(
                "You can place Force Gems directly into the Force Infuser to get some Liquid Force, but you're better off squeezing them."
            );
        } else {
            this.infoTab.addInfoString(
                "You can place Force Gems directly into the Force Infuser and Force Engine to get some Liquid Force, although engines are terribly ineffective at extracting Liquid Force."
            );
        }
        this.infoTab.addInfoString(
            "Force Shard?  What's all this junk?  Force Shards provide 1 bucket of Liquid Force inside all DartCraft devices, but in the infuser they also give the Upgrade Tome a bonus 10 points."
        );
        this.infoTab.addInfoString(
            "Using differing upgrades will help level your Upgrade Tome faster."
        );
        this.infoTab.addInfoString(
            "Force Rods are upgraded directly in the Force Infuser."
        );
        if (Loader.isModLoaded((String) "BuildCraft|Transport")) {
            this.infoTab.addInfoString(
                "Did you know?  You can pump Liquid Force back out of the Force Infuser!"
            );
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (this.lastPacket > 0) {
            --this.lastPacket;
        }
        if (this.infuser != null
            && !ItemStack.areItemStacksEqual(
                (ItemStack) this.infuser.getStackInSlot(2), (ItemStack) this.lastItem
            )) {
            this.lastItem = this.infuser.getStackInSlot(2) != null
                ? this.infuser.getStackInSlot(2).copy()
                : null;
            this.upgradeCheck = 0;
        }
        if (this.upgradeCheck > 0) {
            --this.upgradeCheck;
        } else {
            if (this.infuser != null) {
                this.canUpgrade
                    = this.infuser.canUpgrade((EntityPlayer) this.mc.thePlayer);
            }
            this.upgradeCheck = 20;
        }
        if (this.player != null && this.infuser == null) {
            this.player.closeScreen();
        }
        if (this.infuser == null) {
            return;
        }
        if (this.infuser.errorType != ErrorType.NONE
            && !this.tabListRight.contains(this.errorTab)) {
            this.tabListRight.add(0, this.errorTab);
        }
        if (this.infuser.errorType == ErrorType.NONE
            && this.tabListRight.contains(this.errorTab)) {
            this.tabListRight.remove(this.errorTab);
            if (this.errorTab.isOpen()) {
                this.errorTab.toggleOpen();
                this.errorTab.close();
            }
        }
        if (this.forwardTime > 0) {
            --this.forwardTime;
        }
        if (this.backTime > 0) {
            --this.backTime;
        }
    }

    @Override
    public void drawSlotItem(Slot slot, ItemStack stack, int i, int j, String s) {
        GuiContainerManager.drawItems.renderItemAndEffectIntoGUI(
            GuiDraw.fontRenderer, this.mc.getTextureManager(), stack, i, j
        );
        GuiContainerManager.drawItems.renderItemOverlayIntoGUI(
            GuiDraw.fontRenderer, this.mc.getTextureManager(), stack, i, j, s
        );
        if (this.infoOpen && this.infoPage != null && this.infoPage.comp != null) {
            try {
                IForceUpgradeMaterial mat
                    = ForceUpgradeManager.getMaterialFromItemStack(stack);
                ForceUpgrade upgrade = ForceUpgradeManager.getFromID(mat.getUpgradeID());
                int index = upgrade.getMaterialIndex(mat);
                if (index >= 0
                    && this.infoPage.comp.getInteger(upgrade.getName() + index) <= 0
                    && mat.isRequired()) {
                    float value = 0.0f;
                    GL11.glColor4f(
                        (float) value, (float) value, (float) value, (float) 1.0f
                    );
                    GuiScreen.itemRender.renderWithColor = false;
                }
            } catch (Exception e) {}
        }
        GuiScreen.itemRender.renderWithColor = true;
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        Point pointerLoc = new Point(x, y);
        if (this.infoOpen) {
            if (this.escapeBounds.contains(pointerLoc) && this.infuser != null) {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(
                    new ResourceLocation("random.click")
                ));
                this.setInfoVisible(false);
            }
            if (this.forwardBounds.contains(pointerLoc)) {
                this.forwardTime = 5;
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(
                    new ResourceLocation("random.click")
                ));
                if (this.infoPage != null) {
                    this.infoPage.forwardPage();
                }
            }
            if (this.backBounds.contains(pointerLoc)) {
                this.backTime = 5;
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(
                    new ResourceLocation("random.click")
                ));
                if (this.infoPage != null) {
                    this.infoPage.backPage();
                }
            }
            if (this.infoPage != null && this.infoPage.inventorySlots != null
                && this.infoPage.inventorySlots.size() > 0) {
                for (Object obj : this.infoPage.inventorySlots.toArray()) {
                    if (!(obj instanceof Slot) || !this.isMouseOverSlot((Slot) obj, x, y))
                        continue;
                    Slot slot = (Slot) obj;
                    this.infoPage.setSelected(slot.slotNumber);
                }
            }
        } else {
            super.mouseClicked(x, y, button);
            if (button == 0) {
                if (this.goBounds.contains(pointerLoc) && this.infuser.totalProgress == 0
                    && this.lastPacket <= 0 && this.canUpgrade) {
                    this.mc.getSoundHandler().playSound(
                        PositionedSoundRecord.func_147673_a(
                            new ResourceLocation("random.click")
                        )
                    );
                    this.lastPacket = 20;
                    Dartcraft.channel.sendToServer(new PacketInfuserStart());
                }
                if (this.infoBounds.contains(pointerLoc) && this.infuser != null) {
                    this.mc.getSoundHandler().playSound(
                        PositionedSoundRecord.func_147673_a(
                            new ResourceLocation("random.click")
                        )
                    );
                    this.setInfoVisible(true);
                }
            }
        }
    }

    private void setInfoVisible(boolean visible) {
        if (visible) {
            this.showTabs = false;
            this.infoOpen = true;
            int offsetX = this.xSize / 2 - this.infoWidth / 2 + 1;
            int offsetY = this.ySize / 2 - this.infoHeight / 2 + 1;
            this.infoPage = new ContainerInfoPage(this.infuser, offsetX, offsetY);
            this.inventorySlots = this.infoPage;
        } else {
            this.inventorySlots = this.container;
            this.mc.thePlayer.openContainer = this.container;
            this.infoOpen = false;
            this.showTabs = true;
        }
    }

    @Override
    public List<String> handleItemTooltip(
        GuiContainer gui, ItemStack stack, int x, int y, List<String> tooltip
    ) {
        IForceUpgradeMaterial mat;
        Point pointerLoc = new Point(x, y);
        if (this.infoOpen) {
            if (this.escapeBounds.contains(pointerLoc)) {
                tooltip.add("Return");
            }
            if (this.forwardBounds.contains(pointerLoc)) {
                tooltip.add("Next Page");
            }
            if (this.backBounds.contains(pointerLoc)) {
                tooltip.add("Previous Page");
            }
        } else {
            if (this.goBounds.contains(pointerLoc)) {
                tooltip.add("Go");
            }
            if (this.infoBounds.contains(pointerLoc)) {
                tooltip.add("Info");
            }
            if (this.tankBounds.contains(pointerLoc) && this.infuser != null
                && this.infuser.getFluid() != null) {
                tooltip.add("§eLiquid Force§f (" + this.infuser.getFluid().amount + ")");
            }
            if (this.energyBounds.contains(pointerLoc)) {
                tooltip.add(this.infuser.storedRF + " RF");
            }
        }
        if (stack != null
            && (mat = ForceUpgradeManager.getMaterialFromItemStack(stack)) != null) {
            if (tooltip.size() > 1) {
                for (int i = tooltip.size() - 1; i > 0; --i) {
                    tooltip.remove(i);
                }
            }
            NBTTagCompound comp = new NBTTagCompound();
            if (this.infuser != null && this.infuser.getStackInSlot(0) != null
                && this.infuser.getStackInSlot(0).hasTagCompound()) {
                comp = this.infuser.getStackInSlot(0).getTagCompound();
            }
            ForceUpgrade upgrade = ForceUpgradeManager.getFromID(mat.getUpgradeID());
            int upgradeIndex = upgrade.getMaterialIndex(mat);
            if (upgrade.getTier() > 0) {
                String name = "";
                if (comp.getInteger(upgrade.getName() + upgradeIndex) <= 0
                    && mat.isRequired()) {
                    if (this.infoOpen) {
                        tooltip.clear();
                    }
                    name = "????";
                } else {
                    name = upgrade.getName();
                }
                tooltip.add("\u00a7e" + name);
                int tier = ForceUpgradeManager.getFromID(mat.getUpgradeID()).getTier();
                if (tier > this.infuser.getActiveTier()) {
                    tooltip.add("\u00a7bTier " + tier);
                }
            }
        }
        return tooltip;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        int height;
        GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        Dartcraft.proxy.bindTexture("infuserGui.png");
        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(posX, posY, 0, 0, this.xSize, this.ySize);
        if (this.infuser != null && this.infuser.getFluid() != null
            && this.infuser.getFluid().amount > 0) {
            this.displayGauge(
                this.tankBounds.x, this.tankBounds.y, this.infuser.getFluid()
            );
        }
        if (this.infuser != null) {
            height = (int) (107.0f * this.infuser.storedRF
                            / (float) this.infuser.getMaxEnergyStored(null));
            this.drawTexturedModalRect(
                this.guiLeft + 152,
                this.guiTop + 11 + (107 - height),
                176,
                20 + (107 - height),
                12,
                height
            );
        }
        if (this.infuser != null) {
            this.drawTexturedModalRect(
                this.infoBounds.x,
                this.infoBounds.y,
                201,
                0,
                this.infoBounds.width,
                this.infoBounds.height
            );
            if (this.infuser.errorType == ErrorType.EXP) {
                float step = (this.alphaMode ? 1.0f : -1.0f) * 0.01f;
                this.expAlpha += step;
                if (this.expAlpha <= 0.33f || this.expAlpha >= 1.0f) {
                    this.alphaMode = !this.alphaMode;
                }
                GL11.glColor4f(
                    (float) 1.0f, (float) 1.0f, (float) 1.0f, (float) this.expAlpha
                );
                this.fontRendererObj.drawString(
                    "EXP: " + this.infuser.getRequiredExp(),
                    posX + 39,
                    posY + 15,
                    0x606060,
                    true
                );
                GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
                Dartcraft.proxy.bindTexture("infusuerGui.png");
            }
        }
        if (this.infuser != null && this.canUpgrade || this.infuser.progress > 0) {
            if (this.infuser.progress > 0) {
                this.drawTexturedModalRect(
                    this.guiLeft + 39, this.guiTop + 101, 188, 13, 13, 13
                );
                height = this.infuser.totalProgress != 0
                    ? 20 * this.infuser.progress / this.infuser.totalProgress
                    : 20;
                this.drawTexturedModalRect(
                    this.guiLeft + 134,
                    this.guiTop + 93 + (20 - ++height),
                    176,
                    0 + (20 - height),
                    2,
                    height
                );
            } else {
                this.drawTexturedModalRect(
                    this.guiLeft + 39, this.guiTop + 101, 188, 0, 13, 13
                );
            }
        }
        if (this.infoOpen && this.infuser != null) {
            GL11.glColor4f((float) 0.25f, (float) 0.25f, (float) 0.25f, (float) 1.0f);
            this.drawTexturedModalRect(posX, posY, 0, 0, this.xSize, this.ySize);
            GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
            Dartcraft.proxy.bindTexture("infuserInfo.png");
            NBTTagCompound comp = new NBTTagCompound();
            if (this.infuser.getStackInSlot(0) != null
                && this.infuser.getStackInSlot(0).hasTagCompound()) {
                comp = this.infuser.getStackInSlot(0).getTagCompound();
            }
            posX = (this.width - this.infoWidth) / 2;
            posY = (this.height - this.infoHeight) / 2;
            this.drawTexturedModalRect(posX, posY, 0, 0, this.infoWidth, this.infoHeight);
            if (this.forwardTime > 0) {
                this.drawTexturedModalRect(
                    this.forwardBounds.x,
                    this.forwardBounds.y,
                    0,
                    229,
                    this.forwardBounds.width,
                    this.forwardBounds.height
                );
            }
            if (this.backTime > 0) {
                this.drawTexturedModalRect(
                    this.backBounds.x,
                    this.backBounds.y,
                    0,
                    216,
                    this.backBounds.width,
                    this.backBounds.height
                );
            }
            if (this.infoPage != null) {
                Slot slot;
                IForceUpgradeMaterial mat = null;
                if (this.infoPage.selected >= 0
                    && this.infoPage.selected < this.inventorySlots.inventorySlots.size()
                    && (slot = this.inventorySlots.getSlot(this.infoPage.selected))
                        != null
                    && slot.getHasStack()) {
                    this.drawTexturedModalRect(
                        posX - this.infoPage.offsetX + slot.xDisplayPosition - 3,
                        posY - this.infoPage.offsetY + slot.yDisplayPosition - 3,
                        208,
                        0,
                        24,
                        24
                    );
                    mat = ForceUpgradeManager.getMaterialFromItemStack(slot.getStack());
                }
                boolean usedMaterial = false;
                ForceUpgrade upgrade = null;
                if (mat != null) {
                    upgrade = ForceUpgradeManager.getFromID(mat.getUpgradeID());
                    int color = 0xFFFFFF;
                    int index = upgrade.getMaterialIndex(mat);
                    usedMaterial = comp.getInteger(upgrade.getName() + index) > 0
                        || !mat.isRequired();
                    String descr = "";
                    descr = usedMaterial && upgrade.getTier() > 0
                        ? upgrade.getDescription()
                        : mat.getDescription();
                    int maxWidth = 163;
                    int rows = this.fontRendererObj.getStringWidth(descr) / maxWidth;
                    int remainder = this.fontRendererObj.getStringWidth(descr) % maxWidth;
                    GL11.glColor4f(
                        (float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f
                    );
                    if (rows == 0) {
                        this.fontRendererObj.drawString(
                            descr, posX + 23, posY + 30, color
                        );
                    } else {
                        this.fontRendererObj.drawSplitString(
                            descr, posX + 23, posY + 30, maxWidth, color
                        );
                    }
                }
                String name = "Unknown Upgrade";
                if (usedMaterial && upgrade != null && upgrade.getTier() > 0) {
                    name = "[" + upgrade.getName() + "] Tier: " + upgrade.getTier()
                        + " Mlvl: " + upgrade.getMaxLevel();
                }
                if (upgrade != null && upgrade.getTier() == 0) {
                    name = "Information";
                }
                this.fontRendererObj.drawString(
                    name, posX + 15, posY + 7, 0x636363, false
                );
            }
        }
    }

    private void displayGauge(int x, int y, FluidStack liquid) {
        int tempx;
        if (liquid == null) {
            return;
        }
        int start = 0;
        int squaled = (int) (58.0f * (float) liquid.amount / 50000.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(
            TextureMap.locationBlocksTexture
        );
        do {
            tempx = 0;
            if (squaled > 16) {
                tempx = 16;
                squaled -= 16;
            } else {
                tempx = squaled;
                squaled = 0;
            }
            IIcon icon = liquid != null && liquid.getFluid() != null
                ? liquid.getFluid().getIcon()
                : null;
            if (icon == null) {
                icon = ((BlockLiquidForce) DartBlocks.liquidforce).still;
            }
            this.drawTexturedModelRectFromIcon(
                x, y + 58 - tempx - start, icon, 16, 16 - (16 - tempx)
            );
            start += 16;
        } while (tempx != 0 && squaled != 0);
        Dartcraft.proxy.bindTexture("infuserGui.png");
        this.drawTexturedModalRect(this.guiLeft + 10, this.guiTop + 60, 188, 26, 16, 58);
    }

    private class ErrorTab extends Tab {
        public static final int ERROR_TOME = 1;
        private int type;

        public ErrorTab(Gui gui) {
            super(gui);
            this.leftSide = false;
            this.overlayColor = 0xFF0000;
            this.maxHeight = 90;
            this.maxWidth = 100;
        }

        public void close() {
            this.currentHeight = this.minHeight;
            this.currentWidth = this.minWidth;
        }

        @Override
        public void draw(int x, int y) {
            if (GuiInfuser.this.infoOpen) {
                return;
            }
            this.drawBackground(x, y);
            this.drawIcon("items.png", 18, x + 2, y + 2);
            if (!this.isFullyOpened()) {
                return;
            }
            tabFontRenderer.drawStringWithShadow("Error", x + 22, y + 6, 0xFFFFFF);
            tabFontRenderer.drawSplitString(
                this.getInfo(), x + 8, y + 20, this.maxWidth - 14, 0
            );
        }

        private String getInfo() {
            try {
                switch (((GuiInfuser) GuiInfuser.this).infuser.errorType) {
                    case ENERGY: {
                        return "Insufficient Energy.";
                    }
                    case EXP: {
                        return "Insufficient Experience.";
                    }
                    case FORBIDDEN: {
                        return "This action is not permitted.";
                    }
                    case FORCE: {
                        return "Insufficient Liquid Force.";
                    }
                    case INVALID: {
                        return "Cannot apply these upgrades.";
                    }
                    case USER: {
                        return "You cannot use someone else's infuser!";
                    }
                }
            } catch (Exception exception) {
                // empty catch block
            }
            return "You'll need an Upgrade Tome in the top-leftmost slot to get anything useful done.";
        }

        @Override
        public String getTooltip() {
            return null;
        }
    }

    @Override
    public List<String>
    handleTooltip(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
        return currenttip;
    }

    @Override
    public List<String> handleItemDisplayName(
        GuiContainer gui, ItemStack itemstack, List<String> currenttip
    ) {
        return currenttip;
    }
}
