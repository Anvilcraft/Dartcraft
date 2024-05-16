package ley.modding.dartcraft.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Loader;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.tile.TileEntityStorageUnit;

public class GuiStorageUnit extends GuiMachine {
    public TileEntityStorageUnit storage;
    public String texture;

    public GuiStorageUnit(ContainerStorageUnit storage) {
        super(storage);
        this.storage = storage.storage;
        this.xSize = 188;
        this.ySize = 158;
        this.texture = "storageGui0.png";
        if (this.storage != null && this.storage.getSizeInventory() > 24) {
            switch (this.storage.getSizeInventory()) {
                case 54: {
                    this.ySize = 208;
                    this.texture = "storageGui1.png";
                    break;
                }
                case 72: {
                    this.xSize = 242;
                    this.ySize = 208;
                    this.texture = "storageGui2.png";
                    break;
                }
                case 108: {
                    this.xSize = 242;
                    this.ySize = 256;
                    this.texture = "storageGui3.png";
                }
            }
        }
        this.infoTab.leftSide = true;
        this.accessTab.leftSide = true;
        this.addTab(this.infoTab);
        this.addTab(this.accessTab);
        this.addInfoStrings();
    }

    //public void initGui() {
    //    super.initGui();
    //    int posX = (this.width - this.xSize) / 2;
    //    int posY = (this.height - this.ySize) / 2;
    //}

    private void addInfoStrings() {
        if (Loader.isModLoaded((String) "IC2")) {
            this.infoTab.addInfoString(
                "Storage Units may be recolored with dyes or IC2 Painters."
            );
        } else {
            this.infoTab.addInfoString(
                "Storage Units may be recolored with Shapeless Recipes using dyes."
            );
        }
        this.infoTab.addInfoString(
            "Storage Units will not be accessible to other players if their access is set to Closed."
        );
        this.infoTab.addInfoString(
            "Storage Units can be upgraded with the Storage upgrade to increase their storage capacity."
        );
        this.infoTab.addInfoString(
            "Storage Units can use Item Cards in the same way as Force Packs, excepting that unupgraded Item Cards will do nothing."
        );
        this.infoTab.addInfoString(
            "Upgrading a Storage Unit with the Sturdy upgrade will allow its inventory to persist when broken."
        );
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;
        GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        Dartcraft.proxy.bindTexture(this.texture);
        this.drawTexturedModalRect(posX, posY, 0, 0, this.xSize, this.ySize);
    }
}
