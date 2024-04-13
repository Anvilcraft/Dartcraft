package ley.modding.dartcraft.api.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemInventory implements IInventory {
    public ItemStack origin;
    protected ItemStack[] contents;
    private Container handler;
    private String contentName;

    public ItemInventory(int size) {
        this.contents = new ItemStack[size];
        this.contentName = "contents";
    }

    public ItemInventory(int size, ItemStack stack) {
        this(size);
        this.origin = stack;

        readFromNBT(stack.getTagCompound());
    }

    public ItemInventory(int size, ItemStack stack, String name) {
        this(size);
        this.origin = stack;

        this.contentName = name;

        if (this.contentName == null) {
            this.contentName = "contents";
        }
        readFromNBT(stack.getTagCompound());
    }

    public void onGuiSaved(EntityPlayer player) {
        this.origin = findOrigin(player);
        if (this.origin != null) {
            save();
        }
    }

    public ItemStack findOrigin(EntityPlayer player) {
        if (this.origin == null) {
            return null;
        }
        NBTTagCompound comp = this.origin.getTagCompound();
        if (comp == null) {
            return null;
        }
        int id = comp.getInteger("ID");

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i) != null) {
                NBTTagCompound playerComp
                    = player.inventory.getStackInSlot(i).getTagCompound();
                if (playerComp != null) {
                    if (id == playerComp.getInteger("ID")) {
                        return player.inventory.getStackInSlot(i);
                    }
                }
            }
        }

        if (player.inventory.getItemStack() != null) {
            NBTTagCompound playerComp = player.inventory.getItemStack().getTagCompound();
            if ((playerComp != null) && (id == playerComp.getInteger("ID"))) {
                return player.inventory.getItemStack();
            }
        }
        return null;
    }

    public boolean matchesID(int secondID) {
        if (this.origin == null) {
            return false;
        }
        NBTTagCompound comp = this.origin.getTagCompound();
        if (comp == null) {
            return false;
        }
        int id = comp.getInteger("ID");
        return id == secondID;
    }

    public void save() {
        if (this.origin == null) {
            return;
        }
        NBTTagCompound comp = this.origin.getTagCompound();
        if (comp == null)
            comp = new NBTTagCompound();
        writeToNBT(comp);
        this.origin.setTagCompound(comp);
    }

    public void readFromNBT(NBTTagCompound comp) {
        if ((comp == null) || (this.contentName == null))
            return;
        if (comp.hasKey(this.contentName)) {
            NBTTagList contentList = comp.getTagList(this.contentName, 10);
            this.contents = new ItemStack[getSizeInventory()];
            for (int i = 0; i < contentList.tagCount(); i++) {
                NBTTagCompound tempComp = contentList.getCompoundTagAt(i);
                byte slotByte = tempComp.getByte("Slot");

                if ((slotByte >= 0) && (slotByte < this.contents.length)) {
                    this.contents[slotByte] = ItemStack.loadItemStackFromNBT(tempComp);
                }
            }
        }
    }

    public void writeToNBT(NBTTagCompound comp) {
        if (this.contentName == null) {
            this.contentName = "contents";
        }
        NBTTagList contentList = new NBTTagList();
        for (int i = 0; i < this.contents.length; i++) {
            if (this.contents[i] != null) {
                NBTTagCompound tempComp = new NBTTagCompound();
                tempComp.setByte("Slot", (byte) i);

                this.contents[i].writeToNBT(tempComp);
                contentList.appendTag(tempComp);
            }

            comp.setTag(this.contentName, contentList);
        }
    }

    public void setCraftingListener(Container container) {
        this.handler = container;
    }

    public ItemStack decrStackSize(int i, int j) {
        if (this.contents[i] == null) {
            return null;
        }
        if (this.contents[i].stackSize <= j) {
            ItemStack product = this.contents[i];
            this.contents[i] = null;
            markDirty();
            return product;
        }

        ItemStack product = this.contents[i].splitStack(j);

        if (this.contents[i].stackSize == 0) {
            this.contents[i] = null;
        }
        markDirty();
        return product;
    }

    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.contents[slot] == null)
            return null;
        ItemStack returnVal = this.contents[slot];
        this.contents[slot] = null;

        markDirty();
        return returnVal;
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        this.contents[index] = stack;
        markDirty();
    }

    public int getFirstEmptyStack() {
        for (int i = 0; i < this.contents.length; i++) {
            if (this.contents[i] == null)
                return i;
        }
        return -1;
    }

    public int getFreeSlots() {
        int free = 0;

        if ((this.contents != null) && (this.contents.length > 0)) {
            for (ItemStack checkStack : this.contents)
                if (checkStack == null)
                    free++;
        }
        return free;
    }

    private int storePartialItemStack(ItemStack stack) {
        Item i = stack.getItem();
        int j = stack.stackSize;

        if (stack.getMaxStackSize() == 1) {
            int k = getFirstEmptyStack();

            if (k < 0) {
                return j;
            }

            if (this.contents[k] == null) {
                this.contents[k] = ItemStack.copyItemStack(stack);
            }
            return 0;
        }

        int k = storeItemStack(stack);

        if (k < 0) {
            k = getFirstEmptyStack();
        }
        if (k < 0) {
            return j;
        }

        if (this.contents[k] == null) {
            this.contents[k] = new ItemStack(i, 0, stack.getItemDamage());

            if (stack.hasTagCompound()) {
                this.contents[k].setTagCompound(
                    (NBTTagCompound) stack.getTagCompound().copy()
                );
            }
        }
        int l = j;

        if (j > this.contents[k].getMaxStackSize() - this.contents[k].stackSize) {
            l = this.contents[k].getMaxStackSize() - this.contents[k].stackSize;
        }
        if (l > getInventoryStackLimit() - this.contents[k].stackSize) {
            l = getInventoryStackLimit() - this.contents[k].stackSize;
        }
        if (l == 0) {
            return j;
        }

        j -= l;
        this.contents[k].stackSize += l;
        this.contents[k].animationsToGo = 5;
        return j;
    }

    public int storeItemStack(ItemStack stack) {
        for (int i = 0; i < this.contents.length; i++) {
            if ((this.contents[i] != null)
                && (this.contents[i].getItem() == stack.getItem())
                && (this.contents[i].isStackable())
                && (this.contents[i].stackSize < this.contents[i].getMaxStackSize())
                && (this.contents[i].stackSize < getInventoryStackLimit())
                && ((!this.contents[i].getHasSubtypes())
                    || (this.contents[i].getItemDamage() == stack.getItemDamage()))
                && (ItemStack.areItemStackTagsEqual(this.contents[i], stack))) {
                return i;
            }
        }
        return -1;
    }

    public boolean addItemToInventory(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.isItemDamaged()) {
            int i = getFirstEmptyStack();

            if (i >= 0) {
                this.contents[i] = ItemStack.copyItemStack(stack);
                this.contents[i].animationsToGo = 5;
                stack.stackSize = 0;
                markDirty();
                return true;
            }

            return false;
        }

        int i;
        do {
            i = stack.stackSize;
            stack.stackSize = storePartialItemStack(stack);

        } while ((stack.stackSize > 0) && (stack.stackSize < i));

        return stack.stackSize < i;
    }

    public ItemStack getStackInSlot(int index) {
        return this.contents[index];
    }

    public int getSizeInventory() {
        return this.contents.length;
    }

    public String getInventoryName() {
        return "inventory.simulated";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public void markDirty() {
        if (this.handler != null) {
            this.handler.onCraftMatrixChanged(this);
        }
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return false;
    }
}
