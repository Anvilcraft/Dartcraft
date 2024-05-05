package ley.modding.dartcraft.tile;

import java.util.UUID;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.AccessLevel;
import ley.modding.dartcraft.api.IOwnedTile;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.Util;
import net.anvilcraft.anvillib.util.AnvilUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityMachine
    extends TileEntity implements IInventory, IOwnedTile /*, IPipeConnection*/ {
    public int socketSlot;
    public InventoryBasic contents;
    public UUID owner;
    public AccessLevel access;
    public ForgeDirection facing;
    public String[] validCores;
    public int color;

    public TileEntityMachine() {
        this.facing = ForgeDirection.UP;
        this.access = AccessLevel.OPEN;
        this.owner = null;
    }

    @Override
    public void writeToNBT(NBTTagCompound comp) {
        super.writeToNBT(comp);

        comp.setByte("facing", (byte) this.facing.ordinal());
        if (this.owner != null) {
            comp.setLong("owner_most", this.owner.getMostSignificantBits());
            comp.setLong("owner_least", this.owner.getLeastSignificantBits());
        }
        comp.setByte("access", (byte) this.access.ordinal());
        comp.setInteger("color", this.color);
    }

    @Override
    public void readFromNBT(NBTTagCompound comp) {
        super.readFromNBT(comp);
        this.facing = ForgeDirection.getOrientation(comp.getByte("facing"));
        owner = comp.hasKey("owner_most") && comp.hasKey("owner_least")
            ? new UUID(comp.getLong("owner_most"), comp.getLong("owner_least"))
            : null;
        this.access = AnvilUtil.enumFromInt(AccessLevel.class, comp.getByte("access"));
        this.color = comp.getInteger("color");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();

        this.writeToNBT(nbt);

        return new S35PacketUpdateTileEntity(
            this.xCoord, this.yCoord, this.zCoord, this.getBlockMetadata(), nbt
        );
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }

    public void desocket() {
        try {
            if (Dartcraft.proxy.isSimulating(this.worldObj)
                && this.getStackInSlot(this.socketSlot) != null) {
                this.worldObj.playSoundEffect(
                    (double) this.xCoord,
                    (double) this.yCoord,
                    (double) this.zCoord,
                    "dartcraft:socket",
                    1.0F,
                    DartUtils.randomPitch()
                );
            }

            this.setInventorySlotContents(this.socketSlot, (ItemStack) null);
        } catch (Exception var2) {}

        this.computeUpgrades();
    }

    public void computeUpgrades() {}

    public boolean isSocketValid(ItemStack stack) {
        // TODO: upgrades
        //if (stack != null && stack.itemID == DartItem.upgradeCore.itemID) {
        //    NBTTagCompound upgrades = UpgradeHelper.getUpgradeCompound(stack);
        //    if (this.validCores != null && this.validCores.length > 0) {
        //        String[] arr$ = this.validCores;
        //        int len$ = arr$.length;

        //        for (int i$ = 0; i$ < len$; ++i$) {
        //            String name = arr$[i$];
        //            if (upgrades.hasKey(name)) {
        //                return true;
        //            }
        //        }
        //    }
        //}

        return false;
    }

    public IInventory getNearbyIInventory() {
        if (this.worldObj != null && this.getAccessLevel() == AccessLevel.OPEN) {
            int size = 0;
            int prefZ = 0;
            int prefY = 0;
            int prefX = 0;
            ForgeDirection[] e = ForgeDirection.VALID_DIRECTIONS;
            int inv = e.length;

            for (int i$ = 0; i$ < inv; ++i$) {
                ForgeDirection dir = e[i$];
                TileEntity tile = this.worldObj.getTileEntity(
                    this.xCoord + dir.offsetX,
                    this.yCoord + dir.offsetY,
                    this.zCoord + dir.offsetZ
                );
                if (tile != null && tile instanceof IInventory
                    /*&& !DartPluginForceWrench.isTileBlacklisted(tile.getClass())*/) {
                    IInventory inv1 = (IInventory) tile;
                    if (inv1.getSizeInventory() > size
                        && !(tile instanceof TileEntityHopper)) {
                        size = inv1.getSizeInventory();
                        prefX = tile.xCoord;
                        prefY = tile.yCoord;
                        prefZ = tile.zCoord;
                    }
                }
            }

            try {
                if (size > 0) {
                    TileEntity var12 = this.worldObj.getTileEntity(prefX, prefY, prefZ);
                    if (var12 != null && var12 instanceof IInventory) {
                        IInventory var13 = (IInventory) var12;
                        return var13;
                    }
                }
            } catch (Exception var11) {
                ;
            }

            return null;
        } else {
            return null;
        }
    }

    @Override
    public int getSizeInventory() {
        return this.contents != null ? this.contents.getSizeInventory() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return this.contents != null && i >= 0 && i < this.contents.getSizeInventory()
            ? this.contents.getStackInSlot(i)
            : null;
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        return this.contents != null && i >= 0 && i < this.contents.getSizeInventory()
            ? this.contents.decrStackSize(i, j)
            : null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return this.contents != null && i >= 0 && i < this.contents.getSizeInventory()
            ? this.contents.getStackInSlotOnClosing(i)
            : null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack) {
        if (this.contents != null && i >= 0 && i < this.contents.getSizeInventory()) {
            this.contents.setInventorySlotContents(i, stack);
        }
    }

    @Override
    public String getInventoryName() {
        return "dartMachine.inventory";
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
        return this.canPlayerAccess(player);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return this.access == AccessLevel.OPEN;
    }

    public boolean canPlayerAccess(EntityPlayer player) {
        switch (this.access) {
            case CLOSED:
                if (player != null && this.owner != null)
                    return player.getUniqueID().equals(this.owner);

            default:
                return true;
        }
    }

    @Override
    public UUID getOwner() {
        return this.owner;
    }

    @Override
    public AccessLevel getAccessLevel() {
        return this.access;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    public int[] getContainerSyncData() {
        return new int[] {};
    }

    public void receiveContainerSyncData(int idx, int val) {}
}
