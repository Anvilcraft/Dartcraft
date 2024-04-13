package ley.modding.dartcraft.client.renderer.item;

import ley.modding.dartcraft.proxy.ClientProxy;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class RenderItemEngine implements IItemRenderer {
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
        if (type == IItemRenderer.ItemRenderType.EQUIPPED
            || type == IItemRenderer.ItemRenderType.INVENTORY) {
            ClientProxy.engineRender.render(false, 0.25F, 1, 0.0D, 0.0D, 0.0D);
        } else {
            ClientProxy.engineRender.render(false, 0.25F, 1, -0.5D, -0.5D, -0.5D);
        }
    }
}
