package ley.modding.dartcraft.client.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import ley.modding.dartcraft.item.ItemClipboard;
import ley.modding.dartcraft.item.ItemFortune;
import ley.modding.dartcraft.tile.TileEntityForceEngine;
import ley.modding.dartcraft.tile.TileEntityInfuser;
import ley.modding.dartcraft.util.EntityUtils;
import ley.modding.dartcraft.util.ItemCraftingInventory;
import net.anvilcraft.anvillib.util.AnvilUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object
    getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (AnvilUtil.enumFromInt(GuiType.class, id)) {
            case CLIPBOARD: {
                ItemStack clipStack = getClipboard(player);
                if (clipStack != null)
                    return new ContainerClipboard(
                        player, new ItemCraftingInventory(9, clipStack)
                    );
            } break;

            case ENGINE: {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileEntityForceEngine) {
                    return new ContainerForceEngine(player, (TileEntityForceEngine) te);
                }
            } break;

            case INFUSER: {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileEntityInfuser) {
                    return new ContainerInfuser(player.inventory, (TileEntityInfuser) te);
                }
            } break;

            default:
                break;
        }
        return null;
    }

    @Override
    public Object
    getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (AnvilUtil.enumFromInt(GuiType.class, id)) {
            case CLIPBOARD: {
                ItemStack clipStack = getClipboard(player);
                if (clipStack != null)
                    return new GuiClipboard(new ContainerClipboard(
                        player, new ItemCraftingInventory(9, clipStack)
                    ));
            } break;

            case ENGINE: {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileEntityForceEngine) {
                    TileEntityForceEngine engine = (TileEntityForceEngine) te;
                    return new GuiEngine(new ContainerForceEngine(player, engine));
                }
            } break;

            case INFUSER: {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te instanceof TileEntityInfuser) {
                    return new GuiInfuser(
                        new ContainerInfuser(player.inventory, (TileEntityInfuser) te)
                    );
                }
            } break;

            case FORTUNE: {
                ItemStack stack = player.getHeldItem();
                if (stack.getItem() instanceof ItemFortune)
                    return new GuiFortune(stack);
            } break;
        }
        return null;
    }

    private ItemStack getClipboard(EntityPlayer player) {
        NBTTagCompound dartTag = EntityUtils.getModComp((Entity) player);
        int location = -1;
        if (dartTag != null)
            for (int i = 0; i < 9; i++) {
                ItemStack tempStack = player.inventory.mainInventory[i];
                if (tempStack != null && tempStack.getItem() instanceof ItemClipboard
                    && tempStack.getTagCompound().getInteger("ID")
                        == dartTag.getInteger("toOpen"))
                    if (location == -1) {
                        location = i;
                    } else {
                        player.inventory.mainInventory[i].getTagCompound().removeTag("ID"
                        );
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
