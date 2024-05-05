package ley.modding.dartcraft.item;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.client.gui.GuiType;
import ley.modding.dartcraft.proxy.CommonProxy;
import ley.modding.dartcraft.util.EntityUtils;
import ley.modding.dartcraft.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class ItemClipboard extends Item {
    public ItemClipboard() {
        Util.configureItem(this, "clipboard");
        setMaxStackSize(1);
    }

    public void
    onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        if (stack == null)
            return;
        if (!stack.hasTagCompound())
            initCompound(stack);
        if (!stack.getTagCompound().hasKey("ID"))
            stack.getTagCompound().setInteger("ID", CommonProxy.rand.nextInt());
    }

    private void initCompound(ItemStack stack) {
        NBTTagCompound comp = new NBTTagCompound();
        comp.setBoolean("useInventory", false);
        NBTTagList contents = new NBTTagList();
        for (int i = 0; i < 9; i++) {
            NBTTagCompound itemComp = new NBTTagCompound();
            itemComp.setByte("Slot", (byte) i);
            contents.appendTag((NBTBase) itemComp);
        }
        comp.setTag("contents", (NBTBase) contents);
        stack.setTagCompound(comp);
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack.hasTagCompound()) {
            NBTTagCompound dartTag = EntityUtils.getModComp((Entity) player);
            dartTag.setInteger("toOpen", stack.getTagCompound().getInteger("ID"));
            if (Dartcraft.proxy.isSimulating(world))
                player.openGui(
                    Dartcraft.instance,
                    GuiType.CLIPBOARD.ordinal(),
                    world,
                    (int) ((Entity) player).posX,
                    (int) ((Entity) player).posY,
                    (int) ((Entity) player).posZ
                );
        }
        return stack;
    }
}
