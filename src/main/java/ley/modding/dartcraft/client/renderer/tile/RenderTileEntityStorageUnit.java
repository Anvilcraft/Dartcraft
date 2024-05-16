package ley.modding.dartcraft.client.renderer.tile;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.tile.TileEntityStorageUnit;
import ley.modding.dartcraft.util.DartUtils;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(value = Side.CLIENT)
public class RenderTileEntityStorageUnit
    extends TileEntitySpecialRenderer implements IItemRenderer {
    private ModelChest chestModel = new ModelChest();

    public void render(
        double x, double y, double z, float f, int meta, TileEntityStorageUnit storage
    ) {
        float lidAngle = storage != null ? storage.lidAngle : 0.0f;
        float prevAngle = storage != null ? storage.prevLidAngle : 0.0f;
        ModelChest chestModel = this.chestModel;
        boolean sturdy = storage != null && (storage.sturdy /*|| Config.superStorage*/);
        if (storage.ender) {
            Dartcraft.proxy.bindTexture("storage1.png");
        } else if (sturdy) {
            Dartcraft.proxy.bindTexture("storage0.png");
        } else {
            Dartcraft.proxy.bindTexture("storage.png");
        }
        GL11.glPushMatrix();
        this.setColor(storage);
        GL11.glTranslatef(
            (float) ((float) x), (float) ((float) y + 1.0f), (float) ((float) z + 1.0f)
        );
        GL11.glScalef((float) 1.0f, (float) -1.0f, (float) -1.0f);
        GL11.glTranslatef((float) 0.5f, (float) 0.5f, (float) 0.5f);
        int rotation = 0;
        switch (meta) {
            case 2: {
                rotation = 180;
                break;
            }
            case 3: {
                rotation = 0;
                break;
            }
            case 4: {
                rotation = 90;
                break;
            }
            case 5: {
                rotation = -90;
            }
        }
        GL11.glRotatef((float) rotation, (float) 0.0f, (float) 1.0f, (float) 0.0f);
        GL11.glTranslatef((float) -0.5f, (float) -0.5f, (float) -0.5f);
        float var12 = prevAngle + (lidAngle - prevAngle) * f;
        var12 = 1.0f - var12;
        var12 = 1.0f - var12 * var12 * var12;
        chestModel.chestLid.rotateAngleX = -(var12 * (float) Math.PI / 2.0f);
        chestModel.renderAll();
        GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        GL11.glPopMatrix();
    }

    private void setColor(TileEntityStorageUnit storage) {
        int color = storage != null ? storage.color : 0;
        Color col = new Color(DartUtils.getMcColor(color));
        GL11.glColor4f(
            (float) ((float) col.getRed() / 255.0f),
            (float) ((float) col.getGreen() / 255.0f),
            (float) ((float) col.getBlue() / 255.0f),
            (float) 1.0f
        );
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
        TileEntityStorageUnit storage = (TileEntityStorageUnit) te;
        int meta = storage.facing.ordinal();
        this.render(x, y, z, f, meta, storage);
    }

    @Override
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(
        IItemRenderer.ItemRenderType type,
        ItemStack item,
        IItemRenderer.ItemRendererHelper helper
    ) {
        return true;
    }

    @Override
    public void
    renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        int meta = item.getItemDamage();
        if (meta >= 0 && meta < 16) {
            TileEntityStorageUnit storage = new TileEntityStorageUnit();
            storage.color = item.getItemDamage() % 16;
            storage.sturdy
                = item.hasTagCompound() && item.getTagCompound().getBoolean("sturdy");
            if (type == IItemRenderer.ItemRenderType.EQUIPPED
                || type == IItemRenderer.ItemRenderType.INVENTORY) {
                this.render(0.0, 0.0, 0.0, 0.0f, 0, storage);
                return;
            } else {
                this.render(-0.5, -0.5, -0.5, 0.0f, 0, storage);
            }
            return;
        }
        if (meta >= 16 && meta >= 16)
            return;
    }
}
