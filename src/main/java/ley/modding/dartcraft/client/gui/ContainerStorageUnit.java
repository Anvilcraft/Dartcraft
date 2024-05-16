package ley.modding.dartcraft.client.gui;

import ley.modding.dartcraft.tile.TileEntityStorageUnit;
import net.anvilcraft.alec.jalec.AlecLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

// TODO: inv tweaks
//@ChestContainer(isLargeChest=true)
public class ContainerStorageUnit extends ContainerMachine {
    public TileEntityStorageUnit storage;
    //private int rows = 0;

    public ContainerStorageUnit(EntityPlayer player, TileEntityStorageUnit storage) {
        super(player, storage);
        this.storage = storage;
        int posX = 14;
        int posY = 14;
        int row = 3;
        int col = 9;
        //this.rows = 3;
        if (this.storage != null && this.storage.getSizeInventory() > 24) {
            switch (this.storage.getSizeInventory()) {
                case 54: {
                    row = 6;
                    break;
                }
                case 72: {
                    row = 6;
                    col = 12;
                    break;
                }
                case 108: {
                    posY = 8;
                    row = 9;
                    col = 12;
                }
            }
        }
        //this.rows = col;
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                this.addSlotToContainer(new StorageSlot(
                    this.storage, i * col + j, posX + j * 18, posY + i * 18
                ));
            }
        }
        posX = 14;
        posY = 76;
        if (this.storage != null && this.storage.getSizeInventory() > 24) {
            switch (this.storage.getSizeInventory()) {
                case 54: {
                    posY = 126;
                    break;
                }
                case 72: {
                    posX = 41;
                    posY = 126;
                    break;
                }
                case 108: {
                    posX = 41;
                    posY = 174;
                }
            }
        }
        this.addPlayerInventory(posX, posY);
        if (this.storage != null) {
            this.storage.openInventory();
        }
    }

    // TODO: inv tweaks
    //@ChestContainer.RowSizeCallback
    //public int getRowSize() {
    //    return this.rows;
    //}

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        try {
            ItemStack returnStack = null;
            Slot slot = (Slot) this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                returnStack = stack.copy();
                if (index < this.storage.getSizeInventory()
                        ? !super.mergeItemStack(
                            stack,
                            this.storage.getSizeInventory(),
                            this.storage.getSizeInventory() + 36,
                            true
                        )
                        : !this.mergeItemStack(
                            stack, 0, this.storage.getSizeInventory(), false
                        )) {
                    return null;
                }
                if (stack.stackSize == 0) {
                    slot.putStack((ItemStack) null);
                } else {
                    slot.onSlotChanged();
                }
                if (stack.stackSize == returnStack.stackSize) {
                    return null;
                }
                slot.onPickupFromSlot(player, stack);
            }
            return returnStack;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (this.storage != null) {
            this.storage.closeInventory();
        }
    }

    private class StorageSlot extends Slot {
        public StorageSlot(IInventory inv, int slot, int x, int y) {
            super(inv, slot, x, y);
        }

        public void onSlotChanged() {
            super.onSlotChanged();
            ContainerStorageUnit.this.storage.setCheck();
        }
    }
}
