package ley.modding.dartcraft.client.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import codechicken.nei.guihook.IContainerTooltipHandler;
import cpw.mods.fml.common.Loader;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.energy.EngineLiquid;
import ley.modding.dartcraft.client.gui.tabs.GuiTab;
import ley.modding.dartcraft.client.gui.tabs.Tab;
import ley.modding.dartcraft.tile.TileEntityForceEngine;
import ley.modding.dartcraft.util.ForceEngineLiquids;
import ley.modding.dartcraft.util.FortunesUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class GuiEngine extends GuiTab implements IContainerTooltipHandler {
    private TileEntityForceEngine engine;

    private ContainerForceEngine container;

    private Rectangle fuelBounds;

    private Rectangle throttleBounds;

    private Rectangle energyBounds;

    private Rectangle throttleMeterBounds;

    public GuiEngine(ContainerForceEngine container) {
        super(container);
        this.engine = container.engine;
        this.container = container;
        this.xSize = 176;
        this.ySize = 161;
        addTab(new EnergyTab(this));
        addTab(new InfoTab(this));
    }

    public void initGui() {
        super.initGui();
        this.fuelBounds = new Rectangle(this.guiLeft + 66, this.guiTop + 11, 16, 58);
        this.throttleBounds = new Rectangle(this.guiLeft + 94, this.guiTop + 11, 16, 58);
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

    @Override
    protected void drawTooltips() {}

    @Override
    public List<String> handleItemTooltip(
        GuiContainer gui, ItemStack itemstack, int x, int y, List<String> tooltip
    ) {
        Point pointerLoc = new Point(x, y);
        if (this.fuelBounds.contains(pointerLoc))
            try {
                tooltip.add(
                    "" + this.engine.fuelTank.getFluid().getFluid().getLocalizedName()
                    + " (" + (this.engine.fuelTank.getFluid()).amount + ")"
                );
            } catch (Exception e) {
                tooltip.add("Empty");
            }
        if (this.throttleBounds.contains(pointerLoc))
            try {
                tooltip.add(
                    "" + (this.engine.throttleTank.getInfo()).fluid.getFluid().getName()
                    + " (" + (this.engine.throttleTank.getFluid()).amount + ")"
                );
            } catch (Exception e) {
                tooltip.add("Empty");
            }
        return tooltip;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Dartcraft.proxy.bindTexture("engineGui.png");
        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(posX, posY, 0, 0, this.xSize, this.ySize);
        if (this.engine.fuelTank.getFluid() != null
            && (this.engine.fuelTank.getFluid()).amount > 0)
            this.displayGauge(
                this.fuelBounds.x, this.fuelBounds.y, this.engine.fuelTank.getFluid()
            );
        if (this.engine.throttleTank.getFluid() != null
            && (this.engine.throttleTank.getFluid()).amount > 0)
            this.displayGauge(
                this.throttleBounds.x,
                this.throttleBounds.y,
                this.engine.throttleTank.getFluid()
            );
    }

    private void displayGauge(int x, int y, FluidStack liquid) {
        int tempx;
        if (liquid == null)
            return;
        int start = 0;
        int squaled = (int) (58.0F * liquid.amount / 10000.0F);
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
            IIcon icon = liquid.getFluid().getStillIcon();
            if (icon == null)
                icon = FluidRegistry.LAVA.getStillIcon();
            this.drawTexturedModelRectFromIcon(
                x, y + 58 - tempx - start, icon, 16, 16 - (16 - tempx)
            );
            start += 16;
        } while (tempx != 0 && squaled != 0);
        Dartcraft.proxy.bindTexture("engineGui.png");
        this.drawTexturedModalRect(x, y, 176, 0, 16, 58);
    }

    private class EnergyTab extends Tab {
        public EnergyTab(Gui gui) {
            super(gui);
            this.leftSide = false;
            this.overlayColor = 13914449;
            this.maxHeight = 48;
            this.maxWidth = 90;
        }

        public void draw(int x, int y) {
            drawBackground(x, y);
            drawIcon("items.png", 49, x + 2, y + 2);
            if (!isFullyOpened())
                return;
            float output = GuiEngine.this.engine.getEnergyPerProcess();
            float throttle = 0.0F;
            if (GuiEngine.this.engine.throttleTank.getFluid() != null) {
                EngineLiquid throttleLiquid = ForceEngineLiquids.getEngineLiquid(
                    GuiEngine.this.engine.throttleTank.getFluid()
                );
                if (throttleLiquid != null)
                    throttle = throttleLiquid.getModifier();
            }
            if (!GuiEngine.this.engine.isActive)
                output = throttle = 0.0F;
            int subColor = 15000804;
            Tab.tabFontRenderer.drawStringWithShadow("Output", x + 22, y + 6, 16777215);
            Tab.tabFontRenderer.drawStringWithShadow("RF/t:", x + 8, y + 20, 16777215);
            if (output > 0.0F) {
                Tab.tabFontRenderer.drawStringWithShadow(
                    "" + GuiEngine.this.engine.getEnergyPerProcess(),
                    x + 36,
                    y + 20,
                    subColor
                );
            } else {
                Tab.tabFontRenderer.drawStringWithShadow(
                    "None", x + 36, y + 20, subColor
                );
            }
            Tab.tabFontRenderer.drawStringWithShadow(
                "Throttle:", x + 8, y + 30, 16777215
            );
            if (throttle > 0.0F) {
                Tab.tabFontRenderer.drawStringWithShadow(
                    "" + throttle, x + 55, y + 30, subColor
                );
            } else {
                Tab.tabFontRenderer.drawStringWithShadow(
                    "None", x + 55, y + 30, subColor
                );
            }
        }

        public String getTooltip() {
            return null;
        }
    }

    private class InfoTab extends Tab {
        public ArrayList<String> infoStrings = new ArrayList<String>();

        private String currentInfo;

        private int index;

        public InfoTab(Gui gui) {
            super(gui);
            this.leftSide = false;
            this.overlayColor = 1217260;
            this.maxHeight = 100;
            this.maxWidth = 120;
            initializeItems();
            this.currentInfo = getRandomItem();
            this.index = -1;
        }

        private void initializeItems() {
            this.infoStrings.add(
                "The Force Engine can be throttled with a few liquids, most notably water."
            );
            this.infoStrings.add(
                "The Force Engine's output is determined by the base output of the Fuel multiplied by the Throttle's value."
            );
            this.infoStrings.add(
                "The Force Engine will never explode or die of loneliness."
            );
            this.infoStrings.add("The Force Engine requires a redstone signal to run.");
            this.infoStrings.add(
                "You can right-click the Force Engine with a valid liquid container to add liquid quickly."
            );
            if (Loader.isModLoaded("BuildCraft|Energy")) {
                this.infoStrings.add(
                    "A wide variety of fuels are usable inside the Force Engine.  While Liquid Force is the most effective, BuildCraft Fuel or even lava is also usable."
                );
                this.infoStrings.add(
                    "Using Fuel or Lava in the Force Engine will yield the same output as the Combustion Engine if water is used as a throttle."
                );
            } else {
                this.infoStrings.add(
                    "Lava is also a valid Force Engine Fuel, although not as effective as Liquid Force."
                );
            }
            if (Loader.isModLoaded("Forestry")) {
                this.infoStrings.add(
                    "Liquid Force may also be obtained by squeezing Force Logs."
                );
                this.infoStrings.add("Milk is also an effective throttle.");
                this.infoStrings.add("Did someone say Glacial bees?");
            }
        }

        public void toggleOpen() {
            super.toggleOpen();
            if (isOpen()) {
                this.index++;
                if (this.index > this.infoStrings.size())
                    this.index = 0;
                try {
                    this.currentInfo = this.infoStrings.get(this.index);
                } catch (Exception e) {
                    this.currentInfo = getRandomItem();
                }
            }
        }

        public void draw(int x, int y) {
            drawBackground(x, y);
            drawIcon("items.png", 0, x + 2, y + 2);
            if (!isFullyOpened())
                return;
            Tab.tabFontRenderer.drawStringWithShadow(
                "Information", x + 22, y + 6, 16777215
            );
            Tab.tabFontRenderer.drawSplitString(
                this.currentInfo, x + 8, y + 20, this.maxWidth - 14, 0
            );
        }

        private String getRandomItem() {
            return FortunesUtil.getFortune();
        }

        public String getTooltip() {
            return null;
        }
    }
}
