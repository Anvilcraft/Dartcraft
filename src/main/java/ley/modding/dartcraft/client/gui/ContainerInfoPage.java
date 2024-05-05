package ley.modding.dartcraft.client.gui;

import java.util.ArrayList;

import ley.modding.dartcraft.api.upgrades.ForceUpgrade;
import ley.modding.dartcraft.api.upgrades.IForceUpgradeMaterial;
import ley.modding.dartcraft.tile.TileEntityInfuser;
import ley.modding.dartcraft.util.ForceUpgradeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerInfoPage extends Container {
    public InventoryBasic contents;
    public TileEntityInfuser infuser;
    public ArrayList<ItemStack> upgrades = new ArrayList<>();
    public int page;
    public int selected;
    public int offsetX;
    public int offsetY;
    public int selectedX;
    public int selectedY;
    public static final int MAX_SLOTS = 8;
    public NBTTagCompound comp = new NBTTagCompound();

    public ContainerInfoPage(TileEntityInfuser infuser, int x, int y) {
        int i;
        this.infuser = infuser;
        this.contents = new InventoryBasic("upgrades", false, 9);
        this.page = 0;
        this.selected = -1;
        if (this.infuser != null && this.infuser.getStackInSlot(0) != null
            && this.infuser.getStackInSlot(0).hasTagCompound()) {
            this.comp = this.infuser.getStackInSlot(0).getTagCompound();
        }
        this.offsetX = x;
        this.offsetY = y;
        int posX = this.offsetX + 14;
        int posY = this.offsetY + 142;
        int rows = 1;
        this.addSlotToContainer(new DummySlot(
            (IInventory) this.contents, this.contents.getSizeInventory() - 1, -1000, -1000
        ));
        for (int i2 = 0; i2 < rows; ++i2) {
            for (int j = 0; j < 8 / rows; ++j) {
                this.addSlotToContainer(new DummySlot(
                    (IInventory) this.contents,
                    i2 * (8 / rows) + j,
                    posX + j * 23,
                    posY + i2 * 23
                ));
            }
        }
        int tier = infuser != null ? infuser.getActiveTier() : 0;
        ArrayList<ArrayList<ItemStack>> buffer = new ArrayList<>();
        for (int i3 = 0; i3 < 8; ++i3) {
            buffer.add(new ArrayList<>());
        }
        for (IForceUpgradeMaterial mat : ForceUpgradeManager.materials) {
            ForceUpgrade upgrade = ForceUpgradeManager.getFromID(mat.getUpgradeID());
            buffer.get(upgrade.getTier())
                .add(new ItemStack(mat.getItem(), 1, mat.getItemMeta()));
        }
        for (i = 0; i <= tier; ++i) {
            for (ItemStack tempStack : buffer.get(i)) {
                this.upgrades.add(tempStack.copy());
            }
        }
        for (i = 0; i < this.contents.getSizeInventory() && i < this.upgrades.size();
             ++i) {
            this.contents.setInventorySlotContents(i, this.upgrades.get(i));
        }
    }

    public void forwardPage() {
        int totalPages
            = this.upgrades.size() / 8 + (this.upgrades.size() % 8 > 0 ? 1 : 0) - 1;
        if (this.page < totalPages) {
            ++this.page;
            this.redoPage();
        }
    }

    public void backPage() {
        if (this.page > 0) {
            --this.page;
            this.redoPage();
        }
    }

    private void redoPage() {
        int start = this.page * 8;
        for (int i = 0; i < this.contents.getSizeInventory(); ++i) {
            if (this.upgrades.size() > start) {
                this.contents.setInventorySlotContents(i, this.upgrades.get(start));
            } else {
                this.contents.setInventorySlotContents(i, (ItemStack) null);
            }
            ++start;
        }
        this.selected = -1;
    }

    public void setSelected(int index) {
        this.selected = index;
    }

    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return null;
    }

    protected void
    retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer) {}

    public boolean canInteractWith(EntityPlayer entityplayer) {
        return false;
    }

    private class DummySlot extends Slot {
        public DummySlot(IInventory inv, int x, int y, int z) {
            super(inv, x, y, z);
        }

        public boolean canTakeStack(EntityPlayer player) {
            return false;
        }
    }
}
