package ley.modding.dartcraft.item;

import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.client.gui.GuiType;
import ley.modding.dartcraft.plugin.DartPluginFortunes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemFortune extends BaseItem {
    public ItemFortune() {
        super("fortune");
    }

    public ItemStack createNewStack() {
        ItemStack stack = new ItemStack(this);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("fortune", DartPluginFortunes.getFortune());
        stack.setTagCompound(nbt);
        return stack;
    }

    @SideOnly(value = Side.CLIENT)
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked", "ALEC" })
    public void
    addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add("Read me.");
        if (Keyboard.isKeyDown((int) 42) || Keyboard.isKeyDown((int) 54)) {
            list.add("Right-click, genius");
        } else if (list.size() > 2) {
            list.remove(2);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            int size = stack.stackSize;
            player.destroyCurrentEquippedItem();
            player.inventory.addItemStackToInventory(new ItemStack(Items.paper, size));
            return stack;
        }
        if (!Dartcraft.proxy.isSimulating(world)) {
            player.openGui(
                (Object) Dartcraft.instance,
                GuiType.FORTUNE.ordinal(),
                world,
                (int) player.posX,
                (int) player.posY,
                (int) player.posZ
            );
        }
        return stack;
    }
}
