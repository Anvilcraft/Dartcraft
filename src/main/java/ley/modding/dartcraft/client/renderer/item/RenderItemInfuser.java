package ley.modding.dartcraft.client.renderer.item;

import ley.modding.dartcraft.proxy.ClientProxy;
import ley.modding.dartcraft.tile.TileEntityInfuser;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

public class RenderItemInfuser implements IItemRenderer {
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return true;
    }

    public boolean shouldUseRenderHelper(
        IItemRenderer.ItemRenderType type,
        ItemStack item,
        IItemRenderer.ItemRendererHelper helper
    ) {
        return true;
    }

    public void
    renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        TileEntityInfuser infuser = null;
        if (item != null && item.hasTagCompound()) {
            infuser = (TileEntityInfuser
            ) TileEntity.createAndLoadEntity((NBTTagCompound) item.getTagCompound());
            infuser.bookSpread = 0.5f;
            infuser.pageFlip = 0.5f;
        }
        if (type == IItemRenderer.ItemRenderType.EQUIPPED
            || type == IItemRenderer.ItemRenderType.INVENTORY) {
            ClientProxy.infuserRender.render(infuser, 0.0, 0.0, 0.0, 0.0f);
        } else {
            ClientProxy.infuserRender.render(infuser, -0.5, -0.5, -0.5, 0.0f);
        }
    }
}
