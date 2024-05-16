package ley.modding.dartcraft.storage;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class EnderInventory implements IInventory {
    public static final int SIZE = 72;
    public NBTTagCompound tag = new NBTTagCompound();
    protected ItemStack[] contents;
    public boolean change = false;
    public UUID owner;
    public int color = -1;
    public boolean fake;

    public EnderInventory(ItemStack[] contents, UUID owner, int color) {
        this(contents, owner, color, true);
    }

    public EnderInventory(ItemStack[] contents, UUID owner, int color, boolean fake) {
        this.contents = contents;
        this.owner = owner;
        this.color = color;
        this.fake = fake;
    }

    @Override
    public int getSizeInventory() {
        return SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.contents[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        ItemStack invStack = this.contents[slot];
        ItemStack copyStack = invStack.copy();
        copyStack.stackSize = amt;
        if (!this.fake) {
            invStack.stackSize -= amt;
            if (invStack.stackSize <= 0) {
                this.contents[slot] = null;
            }
        }
        this.markDirty();
        return copyStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return this.getStackInSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (this.fake)
            return;

        this.contents[slot] = stack == null ? null : stack;

        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return "dartcraft.enderContainer";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void markDirty() {}

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int alec0, ItemStack alec1) {
        return true;
    }
}
