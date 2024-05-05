package ley.modding.dartcraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.api.upgrades.IForceUpgradeMaterial;
import ley.modding.dartcraft.client.IconDirectory;
import ley.modding.dartcraft.item.ItemForceTome;
import ley.modding.dartcraft.tile.TileEntityInfuser;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.ForceConsumerUtils;
import ley.modding.dartcraft.util.ForceUpgradeManager;
import ley.modding.dartcraft.util.TomeUtils;
import net.anvilcraft.anvillib.util.AnvilUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class ContainerInfuser extends ContainerMachine {
    public TileEntityInfuser infuser;
    public IInventory infuserInv;
    public EntityPlayer usingPlayer;
    public int lastEnergyLevel;
    public int lastAmount;

    public ContainerInfuser(InventoryPlayer inv, TileEntityInfuser tileinfuser) {
        super(inv.player, tileinfuser);
        int i;
        this.infuser = tileinfuser;
        this.infuserInv = this.infuser;
        this.usingPlayer = inv.player;
        this.addSlotToContainer(new TomeSlot(this.infuserInv, 0, 10, 10, this.infuser));
        this.addSlotToContainer(new GemSlot(this.infuserInv, 1, 10, 35));
        this.addSlotToContainer(new ItemSlot(this.infuserInv, 2, 80, 57, this.infuser));
        this.addSlotToContainer(
            new UpgradeSlot(this.infuserInv, 3, 80, 20, this.infuser, 0)
        );
        this.addSlotToContainer(
            new UpgradeSlot(this.infuserInv, 4, 104, 32, this.infuser, 1)
        );
        this.addSlotToContainer(
            new UpgradeSlot(this.infuserInv, 5, 116, 57, this.infuser, 2)
        );
        this.addSlotToContainer(
            new UpgradeSlot(this.infuserInv, 6, 104, 81, this.infuser, 3)
        );
        this.addSlotToContainer(
            new UpgradeSlot(this.infuserInv, 7, 80, 93, this.infuser, 4)
        );
        this.addSlotToContainer(
            new UpgradeSlot(this.infuserInv, 8, 56, 81, this.infuser, 5)
        );
        this.addSlotToContainer(
            new UpgradeSlot(this.infuserInv, 9, 44, 57, this.infuser, 6)
        );
        this.addSlotToContainer(
            new UpgradeSlot(this.infuserInv, 10, 56, 32, this.infuser, 7)
        );
        for (i = 0; i < 3; ++i) {
            for (int var4 = 0; var4 < 9; ++var4) {
                this.addSlotToContainer(new Slot(
                    (IInventory) inv, var4 + i * 9 + 9, 8 + var4 * 18, 127 + i * 18
                ));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot((IInventory) inv, i, 8 + i * 18, 185));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack returnStack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            returnStack = stack.copy();
            if (index >= 0 && index < 11 && !this.mergeItemStack(stack, 11, 47, false)) {
                return null;
            }
            if (index > 10 && index < 47) {
                IForceUpgradeMaterial upgrade;
                NBTTagCompound tomeComp;
                if (stack.getItem() instanceof ItemForceTome && stack.hasTagCompound()
                    && ((tomeComp = stack.getTagCompound()).hasKey("type")
                            ? tomeComp.getInteger("type") == 0
                                && !this.mergeItemStack(stack, 0, 1, false)
                            : !this.mergeItemStack(stack, 2, 3, false))) {
                    return null;
                }
                if (ForceConsumerUtils.isForceContainer(stack)
                    && !this.mergeItemStack(stack, 1, 2, false)) {
                    return null;
                }
                if (DartUtils.canUpgrade(stack)) {
                    if (stack.stackSize > 1) {
                        ItemStack copyStack
                            = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
                        if (stack.hasTagCompound()) {
                            copyStack.setTagCompound(stack.getTagCompound());
                        }
                        if (this.getSlot(2).getStack() != null
                            || !this.mergeItemStack(copyStack, 2, 3, false)) {
                            return null;
                        }
                        --stack.stackSize;
                    } else if (!this.mergeItemStack(stack, 2, 3, false)) {
                        return null;
                    }
                }
                if (this.infuser != null && this.infuser.totalProgress == 0
                    && (upgrade = ForceUpgradeManager.getMaterialFromItemStack(stack))
                        != null
                    && ForceUpgradeManager.getFromID(upgrade.getUpgradeID()).getTier()
                        > 0) {
                    int tier = this.infuser != null ? this.infuser.getActiveTier() : 0;
                    if (tier < ForceUpgradeManager.getFromID(upgrade.getUpgradeID())
                                   .getTier()) {
                        return null;
                    }
                    boolean transferred = false;
                    for (int i = 3; i < 4 + tier; ++i) {
                        ItemStack tempStack = stack.copy();
                        tempStack.stackSize = 1;
                        if (this.getSlot(i).getStack() != null
                            || !this.mergeItemStack(tempStack, i, i + 1, false))
                            continue;
                        --stack.stackSize;
                        transferred = true;
                        break;
                    }
                    if (!transferred) {
                        return null;
                    }
                }
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
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);
        if (this.infuser == null) {
            return;
        }
        try {
            crafting.sendProgressBarUpdate(
                (Container) this, 10, (int) this.infuser.storedRF
            );
            crafting.sendProgressBarUpdate(
                (Container) this, 11, this.infuser.liquid.amount
            );
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (this.infuser == null) {
            return;
        }
        try {
            for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
                ICrafting var2 = (ICrafting) this.crafters.get(var1);
                if (this.lastEnergyLevel != (int) this.infuser.storedRF) {
                    var2.sendProgressBarUpdate(
                        (Container) this, 10, (int) this.infuser.storedRF
                    );
                }
                if (this.lastAmount == this.infuser.liquid.amount)
                    continue;
                var2.sendProgressBarUpdate(
                    (Container) this, 11, this.infuser.liquid.amount
                );
            }
            this.lastEnergyLevel = (int) this.infuser.storedRF;
            this.lastAmount = this.infuser.liquid.amount;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public void updateProgressBar(int i, int j) {
        super.updateProgressBar(i, j);
        if (this.infuser == null) {
            return;
        }
        if (j < 0) {
            j = Math.abs(Short.MIN_VALUE - j) + Short.MAX_VALUE + 1;
        }
        switch (i) {
            case 10: {
                this.infuser.storedRF = j;
                break;
            }
            case 11: {
                this.infuser.liquid.amount = j;
            }
        }
    }

    private class TomeSlot extends Slot {
        TileEntityInfuser infuser;

        public TomeSlot(
            IInventory par1iInventory,
            int par2,
            int par3,
            int par4,
            TileEntityInfuser infuser
        ) {
            super(par1iInventory, par2, par3, par4);
            this.infuser = infuser;
        }

        public boolean isItemValid(ItemStack stack) {
            boolean isTome = TomeUtils.getTomeType(stack) == 0;
            try {
                isTome = AnvilUtil
                             .uuidFromNBT(stack.getTagCompound().getTagList("player", 4))
                             .equals(this.infuser.getOwner());
            } catch (Exception exception) {}
            return isTome;
        }

        public boolean canTakeStack(EntityPlayer player) {
            try {
                return player.getUniqueID().equals(this.infuser.getOwner())
                    && this.infuser.totalProgress == 0;
            } catch (Exception exception) {
                return false;
            }
        }

        public int getSlotStackLimit() {
            return 1;
        }
    }

    private class GemSlot extends Slot {
        public GemSlot(IInventory par1iInventory, int par2, int par3, int par4) {
            super(par1iInventory, par2, par3, par4);
        }

        public boolean isItemValid(ItemStack stack) {
            return ForceConsumerUtils.isForceContainer(stack);
        }
    }

    private class ItemSlot extends Slot {
        TileEntityInfuser infuser;

        public ItemSlot(
            IInventory par1iInventory,
            int par2,
            int par3,
            int par4,
            TileEntityInfuser infuser
        ) {
            super(par1iInventory, par2, par3, par4);
            this.infuser = infuser;
        }

        public boolean isItemValid(ItemStack stack) {
            return DartUtils.canUpgrade(stack);
        }

        public boolean canTakeStack(EntityPlayer player) {
            return this.infuser != null ? this.infuser.totalProgress == 0 : false;
        }

        public int getSlotStackLimit() {
            return 1;
        }

        public void onPickupFromSlot(EntityPlayer player, ItemStack product) {
            super.onPickupFromSlot(player, product);
            // TODO: force sword
            //if (product != null && product.itemID == DartItems.forceSword.itemID) {
            //    NBTTagCompound upgrades = UpgradeHelper.getUpgradeCompound(product);
            //    int smite = upgrades.getInteger("Light");
            //    int damage = upgrades.getInteger("Damage");
            //    int heal = upgrades.getInteger("Healing");
            //    if (heal >= 2 && damage >= 5 && smite >= 1) {
            //        DartPluginAchievements.addToPlayer("undeadBane", player);
            //    }
            //}
        }
    }

    private class UpgradeSlot extends Slot {
        private TileEntityInfuser infuser;
        private int tier;

        public UpgradeSlot(
            IInventory par1iInventory,
            int par2,
            int par3,
            int par4,
            TileEntityInfuser infuser,
            int tier
        ) {
            super(par1iInventory, par2, par3, par4);
            this.infuser = infuser;
            this.tier = tier;
        }

        public ResourceLocation getBackgroundIconTexture() {
            if (this.infuser != null && this.infuser.getActiveTier() < this.tier) {
                return new ResourceLocation("items.png");
            }
            return super.getBackgroundIconTexture();
        }

        public IIcon getBackgroundIconIndex() {
            if (this.infuser != null && this.infuser.getActiveTier() < this.tier) {
                return IconDirectory.noSlot;
            }
            return super.getBackgroundIconIndex();
        }

        public boolean isItemValid(ItemStack stack) {
            if (this.infuser == null) {
                return false;
            }
            if (this.infuser.getActiveTier() < this.tier
                || this.infuser.totalProgress > 0) {
                return false;
            }
            IForceUpgradeMaterial upgrade
                = ForceUpgradeManager.getMaterialFromItemStack(stack);
            if (upgrade == null
                || ForceUpgradeManager.getFromID(upgrade.getUpgradeID()).getTier() <= 0) {
                return false;
            }
            for (IForceUpgradeMaterial check : ForceUpgradeManager.materials) {
                if (check == null || !check.equals(upgrade)
                    || (this.infuser != null ? this.infuser.getActiveTier() : 0)
                        < ForceUpgradeManager.getFromID(check.getUpgradeID()).getTier())
                    continue;
                return true;
            }
            return false;
        }

        public boolean canTakeStack(EntityPlayer player) {
            return this.infuser != null ? this.infuser.totalProgress == 0 : false;
        }

        public int getSlotStackLimit() {
            return 1;
        }
    }
}
