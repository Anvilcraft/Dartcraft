package ley.modding.dartcraft.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemCraftingInventory extends InventoryCrafting implements IInventory {
    public boolean isItemInventory = false;

    public ItemStack parent;

    protected ItemStack[] contents;

    private Container eventHandler;

    public ItemCraftingInventory(int size) {
        super(null, 3, 3);
        this.contents = new ItemStack[size];
    }

    public ItemCraftingInventory(int size, ItemStack stack) {
        this(size);
        this.parent = stack;
        this.isItemInventory = true;
        if (stack != null && stack.hasTagCompound())
            readFromNBT(stack.getTagCompound());
    }

    public void onGuiSaved(EntityPlayer player) {
        this.parent = findParent(player);
        if (this.parent != null)
            save();
    }

    public ItemStack findParent(EntityPlayer player) {
        if (this.parent == null)
            return null;
        NBTTagCompound comp = this.parent.getTagCompound();
        if (comp == null)
            return null;
        int id = comp.getInteger("ID");
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i) != null) {
                NBTTagCompound playerComp
                    = player.inventory.getStackInSlot(i).getTagCompound();
                if (playerComp != null)
                    if (id == playerComp.getInteger("ID"))
                        return player.inventory.getStackInSlot(i);
            }
        }
        if (player.inventory.getItemStack() != null) {
            NBTTagCompound playerComp = player.inventory.getItemStack().getTagCompound();
            if (playerComp != null && id == playerComp.getInteger("ID"))
                return player.inventory.getItemStack();
        }
        return null;
    }

    public boolean matchesID(int secondID) {
        if (this.parent == null)
            return false;
        NBTTagCompound comp = this.parent.getTagCompound();
        if (comp == null)
            return false;
        int id = comp.getInteger("ID");
        return (id == secondID);
    }

    public void save() {
        NBTTagCompound comp = this.parent.getTagCompound();
        if (comp == null)
            comp = new NBTTagCompound();
        writeToNBT(comp);
        this.parent.setTagCompound(comp);
    }

    public void readFromNBT(NBTTagCompound comp) {
        if (comp == null)
            return;
        if (comp.hasKey("contents")) {
            NBTTagList contentList = (NBTTagList) comp.getTag("contents");
            this.contents = new ItemStack[getSizeInventory()];
            for (int i = 0; i < contentList.tagCount(); i++) {
                NBTTagCompound tempComp = contentList.getCompoundTagAt(i);
                byte slotByte = tempComp.getByte("Slot");
                if (slotByte >= 0 && slotByte < this.contents.length)
                    this.contents[slotByte] = ItemStack.loadItemStackFromNBT(tempComp);
            }
        }
    }

    public void writeToNBT(NBTTagCompound comp) {
        NBTTagList contentList = new NBTTagList();
        for (int i = 0; i < this.contents.length; i++) {
            if (this.contents[i] != null) {
                NBTTagCompound tempComp = new NBTTagCompound();
                tempComp.setByte("Slot", (byte) i);
                this.contents[i].writeToNBT(tempComp);
                contentList.appendTag((NBTBase) tempComp);
            }
            comp.setTag("contents", (NBTBase) contentList);
        }
    }

    public void setCraftingListener(Container container) {
        this.eventHandler = container;
    }

    public ItemStack decrStackSize(int i, int j) {
        if (this.contents == null || this.contents.length <= i || i < 0
            || this.contents[i] == null)
            return null;
        if ((this.contents[i]).stackSize <= j) {
            ItemStack itemStack = this.contents[i];
            this.contents[i] = null;
            onInventoryChanged();
            return itemStack;
        }
        ItemStack product = this.contents[i].splitStack(j);
        if ((this.contents[i]).stackSize == 0)
            this.contents[i] = null;
        onInventoryChanged();
        return product;
    }

    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.contents == null || this.contents.length <= slot || slot < 0
            || this.contents[slot] == null)
            return null;
        ItemStack returnVal = this.contents[slot];
        this.contents[slot] = null;
        onInventoryChanged();
        return returnVal;
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        if (this.contents == null || this.contents.length <= index || index < 0)
            return;
        this.contents[index] = stack;
        onInventoryChanged();
    }

    public ItemStack getStackInSlot(int index) {
        if (this.contents != null && this.contents.length > index)
            return this.contents[index];
        return null;
    }

    public int getSizeInventory() {
        return (this.contents != null) ? this.contents.length : 0;
    }

    public String getInventoryName() {
        return "inventory.clipboard";
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public void onInventoryChanged() {
        if (this.eventHandler != null)
            this.eventHandler.onCraftMatrixChanged(this);
    }

    public void openInventory() {}

    public void closeInventory() {}

    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }
}
