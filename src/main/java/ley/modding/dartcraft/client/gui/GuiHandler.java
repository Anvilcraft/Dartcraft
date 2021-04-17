package ley.modding.dartcraft.client.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import ley.modding.dartcraft.item.ItemClipboard;
import ley.modding.dartcraft.tile.TileEntityForceEngine;
import ley.modding.dartcraft.util.EntityUtils;
import ley.modding.dartcraft.util.ItemCraftingInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack clipStack;
        switch (ID) {
            case 0:
                clipStack = getClipboard(player);
                if (clipStack != null)
                    return new ContainerClipboard(player, new ItemCraftingInventory(9, clipStack));
                break;
            case 7:
                TileEntity te2 = world.getTileEntity(x, y, z);
                if (te2 != null && te2 instanceof TileEntityForceEngine) {
                    TileEntityForceEngine engine = (TileEntityForceEngine)te2;
                    return new ContainerForceEngine(player, engine);
                }
                break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack clipStack;
        switch (ID) {
            case 0:
                clipStack = getClipboard(player);
                if (clipStack != null)
                    return new GuiClipboard(new ContainerClipboard(player, new ItemCraftingInventory(9, clipStack)));
                break;
            case 7:
                TileEntity te2 = world.getTileEntity(x, y, z);
                if (te2 instanceof TileEntityForceEngine) {
                    TileEntityForceEngine engine = (TileEntityForceEngine)te2;
                    return new GuiEngine(new ContainerForceEngine(player, engine));
                }
                break;
        }
        return null;
    }

    private ItemStack getClipboard(EntityPlayer player) {
        NBTTagCompound dartTag = EntityUtils.getModComp((Entity) player);
        int location = -1;
        if (dartTag != null)
            for (int i = 0; i < 9; i++) {
                ItemStack tempStack = player.inventory.mainInventory[i];
                if (tempStack != null && tempStack.getItem() instanceof ItemClipboard && tempStack
                        .getTagCompound().getInteger("ID") == dartTag.getInteger("toOpen"))
                    if (location == -1) {
                        location = i;
                    } else {
                        player.inventory.mainInventory[i].getTagCompound().removeTag("ID");
                        dartTag.removeTag("toOpen");
                        return null;
                    }
            }
        dartTag.removeTag("toOpen");
        if (location > -1)
            return player.inventory.mainInventory[location];
        return null;
    }

}
