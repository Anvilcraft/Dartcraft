package ley.modding.dartcraft.client.gui;

import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.tile.TileEntityForceEngine;
import ley.modding.dartcraft.util.ForceEngineLiquids;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;

public class ContainerForceEngine extends Container {
    public TileEntityForceEngine engine;

    public IInventory playerInv;

    public EntityPlayer user;

    public ContainerForceEngine(EntityPlayer player, TileEntityForceEngine engine) {
        this.user = player;
        this.engine = engine;
        this.playerInv = (IInventory) player.inventory;
        this.addSlotToContainer(
            new FuelSlot((IInventory) engine.liquidInventory, 0, 38, 33)
        );
        this.addSlotToContainer(
            new ThrottleSlot((IInventory) engine.liquidInventory, 1, 122, 33)
        );
        int i;
        for (i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++)
                this.addSlotToContainer(
                    new Slot(this.playerInv, i * 9 + j + 9, 8 + 18 * j, 79 + 18 * i)
                );
        }
        for (i = 0; i < 9; i++)
            this.addSlotToContainer(new Slot(this.playerInv, i, 8 + 18 * i, 137));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack returnStack = null;
        Slot slot = (Slot) inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            returnStack = stack.copy();
            if (index >= 0 && index < 2)
                if (!mergeItemStack(stack, 2, 38, true))
                    return null;
            if (index >= 2)
                if (ForceEngineLiquids.isFuel(
                        FluidContainerRegistry.getFluidForFilledItem(stack)
                    )
                    || stack.getItem() == DartItems.forcegem) {
                    if (!mergeItemStack(stack, 0, 1, false))
                        return null;
                } else if (ForceEngineLiquids.isThrottle(FluidContainerRegistry.getFluidForFilledItem(stack)) && !mergeItemStack(stack, 1, 2, false)) {
                    return null;
                }
            if (stack.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }
            if (stack.stackSize == returnStack.stackSize)
                return null;
            slot.onPickupFromSlot(player, stack);
        }
        return returnStack;
    }

    @Override
    protected void
    retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer) {}

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    private class FuelSlot extends Slot {
        public FuelSlot(IInventory par1iInventory, int par2, int par3, int par4) {
            super(par1iInventory, par2, par3, par4);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (stack.getItem() == DartItems.forcegem)
                return true;
            return ForceEngineLiquids.isFuel(
                FluidContainerRegistry.getFluidForFilledItem(stack)
            );
        }
    }

    private class ThrottleSlot extends Slot {
        public ThrottleSlot(IInventory par1iInventory, int par2, int par3, int par4) {
            super(par1iInventory, par2, par3, par4);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return ForceEngineLiquids.isThrottle(
                FluidContainerRegistry.getFluidForFilledItem(stack)
            );
        }
    }
}
