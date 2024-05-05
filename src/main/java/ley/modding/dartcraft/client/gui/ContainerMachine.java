package ley.modding.dartcraft.client.gui;

import java.lang.reflect.Field;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.AccessLevel;
import ley.modding.dartcraft.tile.TileEntityMachine;
import ley.modding.dartcraft.util.DartUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMachine extends Container {
    public TileEntityMachine machine;
    public EntityPlayer player;
    protected Field[] synced;
    protected int[] prev = new int[]{};

    public ContainerMachine(EntityPlayer player, TileEntityMachine machine) {
        this.machine = machine;
        this.player = player;
    }

    protected void addPlayerInventory(int posX, int posY) {
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(
                    (IInventory) this.player.inventory,
                    9 + i * 9 + j,
                    posX + 18 * j,
                    posY + 18 * i
                ));
            }
        }
        posY += 58;
        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(
                new Slot((IInventory) this.player.inventory, i, posX + 18 * i, posY)
            );
        }
    }

    public void desocket() {
        if (this.machine != null) {
            this.machine.desocket();
        }
    }

    public void setAccessLevel(AccessLevel level) {
        if (this.machine != null) {
            this.machine.access = level;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.machine != null && player != null
            ? this.machine.canPlayerAccess(player)
            : false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        try {
            ItemStack returnStack = null;
            Slot slot = (Slot) this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                returnStack = stack.copy();
                if (index < this.machine.getSizeInventory()) {
                    if (!this.mergeItemStack(
                            stack,
                            this.machine.getSizeInventory(),
                            this.machine.getSizeInventory() + 36,
                            true
                        )) {
                        return null;
                    }
                } else {
                    try {
                        for (int i = 0; i < this.machine.getSizeInventory(); ++i) {
                            Slot tempSlot = (Slot) this.inventorySlots.get(i);
                            if (!(tempSlot instanceof SocketSlot)
                                && tempSlot.isItemValid(stack)
                                && !this.mergeItemStack(stack, i, i + 1, false)) {
                                return null;
                            }
                            if (stack.stackSize == 0) {
                                slot.putStack((ItemStack) null);
                                continue;
                            }
                            slot.onSlotChanged();
                        }
                    } catch (Exception e) {
                        // empty catch block
                    }
                }
                if (stack != null && stack.stackSize == 0) {
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
    protected void
    retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer) {}

    @Override
    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);
        int[] syncdata = this.machine.getContainerSyncData();
        for (int i = 0; i < syncdata.length; i++)
            crafting.sendProgressBarUpdate(this, i, syncdata[i]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int[] syncdata = this.machine.getContainerSyncData();

        for (ICrafting cr : (List<ICrafting>) this.crafters) {
            for (int i = 0; i < syncdata.length; i++) {
                if (i >= this.prev.length || syncdata[i] == this.prev[i])
                    continue;

                cr.sendProgressBarUpdate(this, i, syncdata[i]);
            }
        }

        this.prev = syncdata;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int slot, int value) {
        this.machine.receiveContainerSyncData(slot, value);
    }

    protected class SocketSlot extends Slot {
        public SocketSlot(IInventory inv, int slot, int x, int y) {
            super(inv, slot, x, y);
        }

        public boolean isItemValid(ItemStack stack) {
            return ContainerMachine.this.machine != null
                ? ContainerMachine.this.machine.isSocketValid(stack)
                    && !this.getHasStack() && stack.stackSize == 1
                : false;
        }

        public boolean canTakeStack(EntityPlayer player) {
            return false;
        }

        public void putStack(ItemStack stack) {
            super.putStack(stack);
            if (ContainerMachine.this.machine != null
                && Dartcraft.proxy.isSimulating(ContainerMachine.this.machine.getWorldObj(
                ))) {
                ContainerMachine.this.machine.getWorldObj().playSoundEffect(
                    (double) ContainerMachine.this.machine.xCoord,
                    (double) ContainerMachine.this.machine.yCoord,
                    (double) ContainerMachine.this.machine.zCoord,
                    "dartcraft:socket",
                    1.0f,
                    DartUtils.randomPitch()
                );
            }
        }

        public void onSlotChanged() {
            super.onSlotChanged();
            if (ContainerMachine.this.machine != null) {
                ContainerMachine.this.machine.computeUpgrades();
            }
        }
    }
}
