package ley.modding.dartcraft.client.gui;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.item.ItemClipboard;
import ley.modding.dartcraft.util.ItemCraftingInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

public class ContainerClipboard extends Container {
    public ItemCraftingInventory contents;

    public InventoryCraftResult craftResult = new InventoryCraftResult();

    public ItemStack originStack;

    public IInventory playerInv;

    public EntityPlayer user;

    private boolean hasInitialized;

    private boolean useInventory;

    private ItemStack[] neiBuffer;

    public ContainerClipboard(EntityPlayer player, ItemCraftingInventory inv) {
        if (player == null)
            return;
        if (inv == null || inv.parent == null) {
            player.closeScreen();
            return;
        }
        this.hasInitialized = false;
        this.contents = inv;
        this.user = player;
        this.playerInv = (IInventory) player.inventory;
        this.contents.setCraftingListener(this);
        this.neiBuffer = new ItemStack[9];
        int i;
        for (i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                addSlotToContainer(new ClipSlot(
                    (IInventory) this.contents, i * 3 + j, 25 + j * 18, 12 + i * 18
                ));
        }
        addSlotToContainer((Slot) new ClipSlotCrafting(
            this.user,
            (IInventory) this.contents,
            (IInventory) this.craftResult,
            0,
            113,
            30
        ));
        for (i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++)
                addSlotToContainer(
                    new Slot(this.playerInv, 9 + i * 9 + j, 8 + 18 * j, 75 + 18 * i)
                );
        }
        for (i = 0; i < 9; i++)
            addSlotToContainer(new Slot(this.playerInv, i, 8 + 18 * i, 133));
        this.hasInitialized = true;
        onCraftMatrixChanged((IInventory) this.contents);
        this.originStack = inv.parent;
        if (this.originStack == null
            || !(this.originStack.getItem() instanceof ItemClipboard))
            player.closeScreen();
        this.useInventory = this.originStack.getTagCompound().getBoolean("useInventory");
        canStayOpen(player);
    }

    public void onCraftMatrixChanged(IInventory inv) {
        if (!this.hasInitialized)
            return;
        this.craftResult.setInventorySlotContents(
            0,
            CraftingManager.getInstance().findMatchingRecipe(
                (InventoryCrafting) this.contents, ((Entity) this.user).worldObj
            )
        );
    }

    public ItemStack slotClick(int slot, int button, int par3, EntityPlayer player) {
        save();
        if (slot < getInventory().size())
            return super.slotClick(slot, button, par3, player);
        return null;
    }

    public void save() {
        if (this.user == null)
            return;
        ItemStack parent = this.contents.findParent(this.user);
        if (parent != null) {
            this.contents.onGuiSaved(this.user);
        } else {
            this.user.closeScreen();
        }
    }

    public boolean canStayOpen(EntityPlayer player) {
        return (this.contents.findParent(player) != null);
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack returnStack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            returnStack = stack.copy();
            if (index >= 0 && index < 9)
                if (!mergeItemStack(stack, 10, 46, true))
                    return null;
            if (index == 9) {
                int maxCraftable = this.craftResult.getStackInSlot(0).getMaxStackSize()
                    / (this.craftResult.getStackInSlot(0)).stackSize;
                int i;
                for (i = 0; i < 9; i++) {
                    ItemStack tempStack = this.contents.getStackInSlot(i);
                    if (tempStack != null && tempStack.stackSize < maxCraftable)
                        maxCraftable = tempStack.stackSize;
                }
                for (i = 0; i < maxCraftable; i++) {
                    if (!mergeItemStack(stack.copy(), 10, 46, true))
                        return null;
                    try {
                        slot.onSlotChanged();
                        slot.onPickupFromSlot(player, stack);
                    } catch (Exception e) {}
                }
            }
            if (index > 9 && index < 46) {
                if (!isItemValidForSlot(stack))
                    return null;
                if (!mergeItemStack(stack, 0, 9, false))
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

    private boolean isItemValidForSlot(ItemStack stack) {
        boolean isValid = !(stack.getItem() instanceof ItemClipboard);
        return isValid;
    }

    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!Dartcraft.proxy.isSimulating(((Entity) player).worldObj))
            return;
        this.contents.onGuiSaved(player);
    }

    protected void
    retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer) {}

    public void grabItems(ItemStack[] items) {
        if (items == null || items.length != 9 || this.contents == null)
            return;
        clearMatrix();
        int i;
        for (i = 0; i < this.contents.getSizeInventory(); i++) {
            ItemStack tempStack = this.contents.getStackInSlot(i);
            if (tempStack != null)
                return;
        }
        for (i = 0; i < 9; i++) {
            if (items[i] != null) {
                boolean found = false;
                int j;
                for (j = 0; j < this.user.inventory.mainInventory.length; j++) {
                    if (isItemEquivalent(
                            items[i], this.user.inventory.mainInventory[j]
                        )) {
                        this.contents.setInventorySlotContents(
                            i, this.user.inventory.mainInventory[j].copy()
                        );
                        this.user.inventory.mainInventory[j] = null;
                        found = true;
                        break;
                    }
                }
                if (!found)
                    for (j = 0; j < this.contents.getSizeInventory(); j++) {
                        if (j >= i)
                            break;
                        if (isItemEquivalent(items[i], this.contents.getStackInSlot(j)))
                            if ((this.contents.getStackInSlot(j)).stackSize > 1) {
                                this.contents.setInventorySlotContents(
                                    i, this.contents.getStackInSlot(j).copy()
                                );
                                (this.contents.getStackInSlot(i)).stackSize = 1;
                                (this.contents.getStackInSlot(j)).stackSize--;
                                break;
                            }
                    }
            }
        }
        balanceItems();
    }

    public void balanceItems() {
        ArrayList<ItemStack> checked = new ArrayList<ItemStack>();
        for (int i = 0; i < 9; i++) {
            ItemStack contentAt = this.contents.getStackInSlot(i);
            if (contentAt != null && contentAt.getMaxStackSize() > 1
                && !containsItem(checked, contentAt)) {
                checked.add(contentAt.copy());
                int total = 0;
                int stacks = 0;
                for (int j = 0; j < 9; j++) {
                    ItemStack tempStack = this.contents.getStackInSlot(j);
                    if (tempStack != null && areStacksSameItem(tempStack, contentAt)) {
                        total += (this.contents.getStackInSlot(j)).stackSize;
                        stacks++;
                    }
                }
                int newAmt = total / stacks;
                int remainder = total % stacks;
                for (int k = 0; k < 9; k++) {
                    ItemStack tempStack = this.contents.getStackInSlot(k);
                    if (tempStack != null && areStacksSameItem(tempStack, contentAt)) {
                        tempStack.stackSize = newAmt;
                        if (remainder > 0) {
                            tempStack.stackSize++;
                            remainder--;
                        }
                    }
                }
            }
        }
    }

    public void doDistribute() {
        ItemStack firstStack = null;
        ItemStack secondStack = null;
        int[] iterator = { 0, 1, 2, 5, 8, 7, 6, 3 };
        int freeSlots = 0;
        for (int i : iterator) {
            if (this.contents.getStackInSlot(i) != null
                && (this.contents.getStackInSlot(i)).stackSize > 1
                && this.contents.getStackInSlot(i).getMaxStackSize() > 1)
                if (firstStack == null) {
                    firstStack = this.contents.getStackInSlot(i);
                } else if (secondStack == null) {
                    secondStack = this.contents.getStackInSlot(i);
                }
            if (this.contents.getStackInSlot(i) == null)
                freeSlots++;
        }
        if (freeSlots == 9)
            return;
        boolean turn = true;
        boolean hasChanged = false;
        if (freeSlots > 0 && firstStack != null)
            for (int i : iterator) {
                if (this.contents.getStackInSlot(i) == null)
                    if (turn) {
                        if (firstStack.stackSize > 1) {
                            this.contents.setInventorySlotContents(
                                i,
                                new ItemStack(
                                    firstStack.getItem(), 1, firstStack.getItemDamage()
                                )
                            );
                            if (firstStack.hasTagCompound())
                                this.contents.getStackInSlot(i).setTagCompound(
                                    (NBTTagCompound) firstStack.getTagCompound().copy()
                                );
                            firstStack.stackSize--;
                            hasChanged = true;
                        } else if (secondStack != null && secondStack.stackSize > 1) {
                            this.contents.setInventorySlotContents(
                                i,
                                new ItemStack(
                                    secondStack.getItem(), 1, secondStack.getItemDamage()
                                )
                            );
                            if (secondStack.hasTagCompound())
                                this.contents.getStackInSlot(i).setTagCompound(
                                    (NBTTagCompound) secondStack.getTagCompound().copy()
                                );
                            secondStack.stackSize--;
                            hasChanged = true;
                        }
                        turn = (secondStack == null);
                    } else {
                        if (secondStack.stackSize > 1) {
                            this.contents.setInventorySlotContents(
                                i,
                                new ItemStack(
                                    secondStack.getItem(), 1, secondStack.getItemDamage()
                                )
                            );
                            if (secondStack.hasTagCompound())
                                this.contents.getStackInSlot(i).setTagCompound(
                                    (NBTTagCompound) secondStack.getTagCompound().copy()
                                );
                            secondStack.stackSize--;
                            hasChanged = true;
                        } else if (firstStack != null && firstStack.stackSize > 1) {
                            this.contents.setInventorySlotContents(
                                i,
                                new ItemStack(
                                    firstStack.getItem(), 1, firstStack.getItemDamage()
                                )
                            );
                            if (firstStack.hasTagCompound())
                                this.contents.getStackInSlot(i).setTagCompound(
                                    (NBTTagCompound) firstStack.getTagCompound().copy()
                                );
                            firstStack.stackSize--;
                            hasChanged = true;
                        }
                        turn = true;
                    }
            }
        if (freeSlots == 0 || !hasChanged) {
            ArrayList<ItemStack> buffer = new ArrayList<ItemStack>();
            for (int i : iterator)
                buffer.add(this.contents.getStackInSlot(i));
            int index = 0;
            for (int i : new int[] { 1, 2, 5, 8, 7, 6, 3, 0 }) {
                this.contents.setInventorySlotContents(i, buffer.get(index));
                index++;
            }
        }
        balanceItems();
    }

    private boolean containsItem(ArrayList<ItemStack> list, ItemStack stack) {
        for (ItemStack check : list) {
            if (areStacksSameItem(check, stack))
                return true;
        }
        return false;
    }

    private boolean areStacksSameItem(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null)
            return (stack1 == null && stack2 == null);
        return (
            stack1.getItem() == stack2.getItem()
            && stack1.getItemDamage() == stack2.getItemDamage()
            && ((stack1.hasTagCompound() && stack2.hasTagCompound()
                 && ItemStack.areItemStackTagsEqual(stack1, stack2))
                || (!stack1.hasTagCompound() && !stack2.hasTagCompound()))
        );
    }

    public void clearMatrix() {
        if (!canStayOpen(this.user)) {
            this.user.closeScreen();
            return;
        }
        for (int i = 0; i < 9; i++)
            transferStackInSlot(this.user, i);
    }

    public boolean getUseInventory() {
        return this.useInventory;
    }

    public boolean canInteractWith(EntityPlayer var1) {
        return true;
    }

    private class ClipSlot extends Slot {
        int index = 0;

        public ClipSlot(IInventory inv, int index, int x, int y) {
            super(inv, index, x, y);
            this.index = index;
        }

        public boolean isItemValid(ItemStack stack) {
            if (stack != null)
                return ContainerClipboard.this.isItemValidForSlot(stack);
            return false;
        }

        public boolean canTakeStack(EntityPlayer player) {
            if (!getHasStack())
                return true;
            if (!ContainerClipboard.this.canStayOpen(player))
                return false;
            return true;
        }

        public void onSlotChanged() {
            super.onSlotChanged();
            if (getHasStack() && (getStack()).stackSize <= 0)
                this.inventory.setInventorySlotContents(this.index, (ItemStack) null);
            ContainerClipboard.this.save();
        }
    }

    private class ClipSlotCrafting extends SlotCrafting {
        public ClipSlotCrafting(
            EntityPlayer player, IInventory inv, IInventory inv2, int slot, int x, int y
        ) {
            super(player, inv, inv2, slot, x, y);
        }

        public void onSlotChanged() {
            super.onSlotChanged();
            ContainerClipboard.this.save();
        }
    }

    public static boolean isItemEquivalent(ItemStack first, ItemStack second) {
        if (OreDictionary.itemMatches(first, second, false))
            return true;
        if (first == null || second == null)
            return false;
        int firstID = OreDictionary.getOreID(first);
        if (firstID > 0) {
            ArrayList<ItemStack> firstOres
                = OreDictionary.getOres(Integer.valueOf(firstID));
            if (firstOres != null && firstOres.size() > 0)
                for (ItemStack tempStack : firstOres) {
                    if (OreDictionary.itemMatches(tempStack, second, false))
                        return true;
                }
        }
        return false;
    }
}
