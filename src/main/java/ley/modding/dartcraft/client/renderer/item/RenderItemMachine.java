package ley.modding.dartcraft.client.renderer.item;

import ley.modding.dartcraft.client.IconDirectory;
import ley.modding.dartcraft.tile.TileEntityMachine;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

public class RenderItemMachine implements IItemRenderer {
    private static RenderItem renderer = new RenderItem();

    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return type == IItemRenderer.ItemRenderType.INVENTORY;
    }

    public boolean shouldUseRenderHelper(
        IItemRenderer.ItemRenderType type,
        ItemStack item,
        IItemRenderer.ItemRendererHelper helper
    ) {
        return false;
    }

    public void
    renderItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object... data) {
        IIcon icon = stack.getIconIndex();
        //if (stack.getItemDamage() >= 128) {
        //    DartCraftClient.renderCamo.renderItem(type, stack, data);
        //    return;
        //}
        if (stack.getItemDamage() >= 0 && stack.getItemDamage() < 16) {
            icon = IconDirectory.furnaces[stack.getItemDamage()];
        } else {
            TileEntityMachine machine = (TileEntityMachine
            ) TileEntity.createAndLoadEntity((NBTTagCompound) stack.getTagCompound());
            icon = IconDirectory.machines[machine.color];
        }

        if (icon != null) {
            renderer.renderIcon(3, 3, icon, 10, 10);
        }
    }
}
