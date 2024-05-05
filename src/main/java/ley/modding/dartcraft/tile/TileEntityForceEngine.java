package ley.modding.dartcraft.tile;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.energy.EngineLiquid;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.util.ForceEngineLiquids;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityForceEngine extends TileEntity
    implements IFluidHandler, IInventory, IEnergyProvider { //TODO Fix GUI

    public static final int MAX_STORED = 50000;

    public static final int MAX_LIQUID = 10000;

    public static final int CYCLE_TIME = 20;

    public InventoryBasic liquidInventory;

    public ItemStack liquidSlot;

    public ForgeDirection facing;

    public boolean isActive;

    public boolean isShutdown;

    public boolean canCycle;

    public boolean stageCycle;

    public float fuelRF;

    public float cycleProgress;

    public int fuelLossCycle;

    public int throttleLossCycle;

    public int packetTime;

    public FluidTank fuelTank;

    public FluidTank throttleTank;

    public TileEntityForceEngine() {
        this.fuelTank = new FluidTank(10000);
        this.throttleTank = new FluidTank(10000);
        this.fuelLossCycle = this.throttleLossCycle = 0;
        this.liquidInventory = new InventoryBasic("forceEngine.stacks", false, 2);
        this.liquidInventory.setInventorySlotContents(0, (ItemStack) null);
        this.liquidInventory.setInventorySlotContents(1, (ItemStack) null);
        this.facing = ForgeDirection.UP;
    }

    public ForgeDirection getFacing() {
        return this.facing;
    }

    public float getCycleProgress() {
        return this.cycleProgress;
    }

    public boolean setFacing(ForgeDirection dir) {
        this.facing = dir;
        return true;
    }

    public boolean rotateBlock() {
        for (int i = this.facing.ordinal() + 1; i < this.facing.ordinal() + 6; ++i) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i % 6];
            TileEntity tile = this.worldObj.getTileEntity(
                xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ
            );
            if (tile instanceof IEnergyReceiver
                && ((IEnergyReceiver) tile).canConnectEnergy(dir.getOpposite())) {
                this.facing = dir;
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                this.worldObj.notifyBlocksOfNeighborChange(
                    xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord)
                );
                return true;
            }
        }

        return false;
    }

    public int getLightValue() {
        return this.isActive ? 7 : 0;
    }

    protected boolean canCycle() {
        TileEntity tile = this.worldObj.getTileEntity(
            xCoord + facing.offsetX, yCoord + facing.offsetY, zCoord + facing.offsetZ
        );
        if (tile instanceof IEnergyReceiver) {
            this.canCycle = true;
        } else {
            this.canCycle = false;
        }

        return this.canCycle;
    }

    protected boolean canProcess() {
        return this.fuelTank.getFluid() != null && this.fuelTank.getFluid().amount > 0;
    }

    public float getEnergyPerProcess() {
        if (this.fuelTank.getFluid() == null) {
            return 0.0F;
        } else {
            EngineLiquid fuel
                = ForceEngineLiquids.getEngineLiquid(this.fuelTank.getFluid());
            EngineLiquid throttle = null;
            if (this.throttleTank.getFluid() != null) {
                throttle
                    = ForceEngineLiquids.getEngineLiquid(this.throttleTank.getFluid());
            }

            if (fuel == null) {
                return 0.0F;
            } else {
                float energy = fuel.getModifier();
                if (throttle != null) {
                    energy *= throttle.getModifier();
                }

                return energy;
            }
        }
    }

    private void doLoss() {
        if (this.fuelTank.getFluid() != null) {
            EngineLiquid fuel
                = ForceEngineLiquids.getEngineLiquid(this.fuelTank.getFluid());
            EngineLiquid throttle = null;
            if (this.throttleTank.getFluid() != null) {
                throttle
                    = ForceEngineLiquids.getEngineLiquid(this.throttleTank.getFluid());
            }

            FluidStack var10000;
            if (fuel != null) {
                ++this.fuelLossCycle;
                if (this.fuelLossCycle >= fuel.getBurnTime() / 1000) {
                    var10000 = this.fuelTank.getFluid();
                    var10000.amount
                        -= 1000 / fuel.getBurnTime() > 0 ? 1000 / fuel.getBurnTime() : 1;
                    this.fuelLossCycle = 0;
                }
            }

            if (throttle != null) {
                ++this.throttleLossCycle;
                if (this.throttleLossCycle >= throttle.getBurnTime() / 1000) {
                    var10000 = this.throttleTank.getFluid();
                    var10000.amount -= 1000 / throttle.getBurnTime() > 0
                        ? 1000 / throttle.getBurnTime()
                        : 1;
                    this.throttleLossCycle = 0;
                }
            }

            if (this.fuelTank.getFluid() != null
                && this.fuelTank.getFluid().amount <= 0) {
                this.fuelTank.setFluid(null);
            }

            if (this.throttleTank.getFluid() != null
                && this.throttleTank.getFluid().amount <= 0) {
                this.throttleTank.setFluid(null);
            }
        }
    }

    public void processActive() {
        this.doLoss();
    }

    protected void transferEnergy() { //TODO better energy transfer
        TileEntity tile = this.worldObj.getTileEntity(
            xCoord + facing.offsetX, yCoord + facing.offsetY, zCoord + facing.offsetZ
        );
        if (tile instanceof IEnergyReceiver
            && ((IEnergyReceiver) tile).canConnectEnergy(facing.getOpposite())) {
            int energy = (int) this.getEnergyPerProcess() * 200;
            ((IEnergyReceiver) tile).receiveEnergy(facing.getOpposite(), energy, false);
        }
    }

    public void updateEntity() {
        if (!Dartcraft.proxy.isSimulating(worldObj)) {
            if (this.cycleProgress > 0.0F || this.isActive && this.canCycle) {
                this.cycleProgress += 0.04F;
                if (this.cycleProgress >= 1.0F) {
                    this.cycleProgress = 0.0F;
                }
            }

        } else {
            FluidStack curActive;
            if (this.liquidInventory.getStackInSlot(0) != null) {
                curActive = FluidContainerRegistry.getFluidForFilledItem(
                    this.liquidInventory.getStackInSlot(0)
                );
                Fluid curCycle = FluidRegistry.getFluid("liquidforce");
                if (curActive != null) {
                    EngineLiquid temp = ForceEngineLiquids.getEngineLiquid(curActive);
                    if (temp != null
                        && (this.fuelTank.getFluid() == null
                            || this.fuelTank.getFluid().isFluidEqual(curActive))
                        && (this.fuelTank.getFluid() == null
                            || 10000 >= this.fuelTank.getFluid().amount + curActive.amount
                        )) {
                        this.fuelTank.fill(curActive, true);
                        ItemStack temp1 = this.liquidInventory.getStackInSlot(0);
                        if (temp1.stackSize == 1 && temp1.getItem().hasContainerItem()) {
                            this.liquidInventory.setInventorySlotContents(
                                0, temp1.getItem().getContainerItem(temp1)
                            );
                        } else {
                            this.liquidInventory.decrStackSize(0, 1);
                        }
                    }
                }

                if (this.liquidInventory.getStackInSlot(0) != null
                    && this.liquidInventory.getStackInSlot(0).getItem()
                        == DartItems.forcegem
                    && curCycle != null
                    && (this.fuelTank.getFluid() == null
                        || 10000 >= this.fuelTank.getFluid().amount
                                + (int) (1000.0F * Config.gemValue))) {
                    this.fuelTank.fill(
                        new FluidStack(curCycle, (int) (1000.0F * Config.gemValue)), true
                    );
                    this.liquidInventory.decrStackSize(0, 1);
                }
            }

            if (this.liquidInventory.getStackInSlot(1) != null) {
                curActive = FluidContainerRegistry.getFluidForFilledItem(
                    this.liquidInventory.getStackInSlot(1)
                );
                if (curActive != null) {
                    EngineLiquid curCycle1
                        = ForceEngineLiquids.getEngineLiquid(curActive);
                    if (curCycle1 != null
                        && (this.throttleTank.getFluid() == null
                            || this.throttleTank.getFluid().isFluidEqual(curActive))
                        && (this.throttleTank.getFluid() == null
                            || 10000 >= this.throttleTank.getFluid().amount
                                    + curActive.amount)) {
                        this.throttleTank.fill(curActive, true);
                        ItemStack temp2 = this.liquidInventory.getStackInSlot(1);
                        if (temp2.stackSize == 1 && temp2.getItem().hasContainerItem()) {
                            this.liquidInventory.setInventorySlotContents(
                                1, temp2.getItem().getContainerItem(temp2)
                            );
                        } else {
                            this.liquidInventory.decrStackSize(1, 1);
                        }
                    }
                }
            }

            if (this.cycleProgress > 0.0F || this.isActive && this.canCycle) {
                this.cycleProgress += 0.04F;
                if (this.cycleProgress >= 1.0F) {
                    this.cycleProgress = 0.0F;
                    this.stageCycle = false;
                } else if ((double) this.cycleProgress >= 0.5D && !this.stageCycle) {
                    this.transferEnergy();
                    this.stageCycle = true;
                }
            }

            if (this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)
                && this.canCycle()) {
                if (this.canProcess()) {
                    this.isActive = true;
                    this.processActive();
                } else {
                    this.isActive = false;
                }
            } else {
                this.isActive = false;
            }
            //TODO Fix Cycling update
        }
    }

    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.facing = ForgeDirection.getOrientation(data.getByte("facing"));
        this.isActive = data.getBoolean("active");
        this.fuelRF = data.getFloat("fuelRF");
        this.canCycle = data.getBoolean("cycle");
        if (data.hasKey("fuel")) {
            this.fuelTank.setFluid(
                FluidStack.loadFluidStackFromNBT(data.getCompoundTag("fuel"))
            );
        }

        if (data.hasKey("throttle")) {
            this.throttleTank.setFluid(
                FluidStack.loadFluidStackFromNBT(data.getCompoundTag("throttle"))
            );
        }

        if (data.hasKey("fuelSlot")) {
            this.liquidInventory.setInventorySlotContents(
                0, ItemStack.loadItemStackFromNBT(data.getCompoundTag("fuelSlot"))
            );
        } else {
            this.liquidInventory.setInventorySlotContents(0, (ItemStack) null);
        }

        if (data.hasKey("throttleSlot")) {
            this.liquidInventory.setInventorySlotContents(
                1, ItemStack.loadItemStackFromNBT(data.getCompoundTag("throttleSlot"))
            );
        } else {
            this.liquidInventory.setInventorySlotContents(1, (ItemStack) null);
        }
    }

    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("facing", (byte) this.facing.ordinal());
        data.setBoolean("active", this.isActive);
        data.setFloat("fuelRF", this.fuelRF);
        data.setBoolean("cycle", this.canCycle);
        if (this.fuelTank.getFluid() != null) {
            data.setTag(
                "fuel", this.fuelTank.getFluid().writeToNBT(new NBTTagCompound())
            );
        }

        if (this.throttleTank.getFluid() != null) {
            data.setTag(
                "throttle", this.throttleTank.getFluid().writeToNBT(new NBTTagCompound())
            );
        }

        if (this.liquidInventory.getStackInSlot(0) != null) {
            data.setTag(
                "fuelSlot",
                this.liquidInventory.getStackInSlot(0).writeToNBT(new NBTTagCompound())
            );
        }

        if (this.liquidInventory.getStackInSlot(1) != null) {
            data.setTag(
                "throttleSlot",
                this.liquidInventory.getStackInSlot(1).writeToNBT(new NBTTagCompound())
            );
        }
    }

    @Override
    public int extractEnergy(ForgeDirection var1, int var2, boolean var3) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection var1) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection var1) {
        return 0;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection var1) {
        return var1 == facing;
    }

    @Override
    public int getSizeInventory() {
        return this.liquidInventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return this.liquidInventory.getStackInSlot(i);
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        return this.liquidInventory.decrStackSize(i, j);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return this.liquidInventory.getStackInSlotOnClosing(i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack) {
        this.liquidInventory.setInventorySlotContents(i, stack);
    }

    @Override
    public String getInventoryName() {
        return this.liquidInventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return this.liquidInventory.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return this.liquidInventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.liquidInventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {
        this.liquidInventory.openInventory();
    }

    @Override
    public void closeInventory() {
        this.liquidInventory.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        switch (i) {
            case 0:
                return ForceEngineLiquids.isFuel(
                    FluidContainerRegistry.getFluidForFilledItem(stack)
                );
            case 1:
                return ForceEngineLiquids.isThrottle(
                    FluidContainerRegistry.getFluidForFilledItem(stack)
                );
            default:
                return false;
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        int filled;
        if (ForceEngineLiquids.isFuel(resource)) {
            filled = this.fuelTank.fill(resource, doFill);
            return filled;
        } else if (ForceEngineLiquids.isThrottle(resource)) {
            filled = this.throttleTank.fill(resource, doFill);
            return filled;
        } else {
            return 0;
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return (from != null && fluid != null)
            && this.fill(from, new FluidStack(fluid, 1), false) > 0;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { this.fuelTank.getInfo(),
                                     this.throttleTank.getInfo() };
    }

    public Packet getDescriptionPacket() {
        NBTTagCompound comp = new NBTTagCompound();
        this.writeToNBT(comp);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, comp);
    }

    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        if (pkt != null && pkt.func_148857_g() != null) {
            this.readFromNBT(pkt.func_148857_g());
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord); //TODO Lighting
            //worldObj.updateAllLightTypes(this.field_70329_l, this.field_70330_m,
            //this.field_70327_n);
        }
    }

    public void sendGuiNetworkData(Container container, ICrafting craft) {
        byte throttleMeta = 0;
        int throttleID = 0;
        int throttleAmount = 0;
        byte fuelMeta = 0;
        int fuelID = 0;
        int fuelAmount = 0;
        if (this.fuelTank.getFluid() != null) {
            fuelID = this.fuelTank.getFluid().getFluidID();
            fuelAmount = this.fuelTank.getFluid().amount;
        }

        if (this.throttleTank.getFluid() != null) {
            throttleID = this.throttleTank.getFluid().getFluidID();
            throttleAmount = this.throttleTank.getFluid().amount;
        }

        craft.sendProgressBarUpdate(container, 0, fuelID);
        craft.sendProgressBarUpdate(container, 1, fuelMeta);
        craft.sendProgressBarUpdate(container, 2, fuelAmount);
        craft.sendProgressBarUpdate(container, 3, throttleID);
        craft.sendProgressBarUpdate(container, 4, throttleMeta);
        craft.sendProgressBarUpdate(container, 5, throttleAmount);
        if (craft instanceof EntityPlayerMP && Dartcraft.proxy.isSimulating(worldObj)) {
            ((EntityPlayerMP) craft)
                .playerNetServerHandler.sendPacket(getDescriptionPacket());
        }
    }

    public void receiveGuiNetworkData(int i, int j) {
        FluidStack tempStack = this.fuelTank.getFluid();
        FluidStack tempStack2 = this.throttleTank.getFluid();
        switch (i) {
            case 0:
                if (this.fuelTank.getFluid() != null) {
                    this.fuelTank.setFluid(
                        new FluidStack(j, tempStack.amount, tempStack.tag)
                    );
                } else if (j > 0) {
                    this.fuelTank.setFluid(new FluidStack(j, 0));
                }
                break;
            case 1:
                if (this.fuelTank.getFluid() != null) {
                    this.fuelTank.setFluid(new FluidStack(
                        tempStack.getFluidID(), tempStack.amount, (NBTTagCompound) null
                    ));
                }
                break;
            case 2:
                if (this.fuelTank.getFluid() != null) {
                    this.fuelTank.getFluid().amount = j;
                }
                break;
            case 3:
                if (this.throttleTank.getFluid() != null) {
                    this.throttleTank.setFluid(
                        new FluidStack(j, tempStack2.amount, tempStack2.tag)
                    );
                } else if (j > 0) {
                    this.throttleTank.setFluid(new FluidStack(j, 0));
                }
                break;
            case 4:
                if (this.throttleTank.getFluid() != null) {
                    this.throttleTank.setFluid(new FluidStack(
                        tempStack2.getFluidID(), tempStack2.amount, (NBTTagCompound) null
                    ));
                }
                break;
            case 5:
                if (this.throttleTank.getFluid() != null) {
                    this.throttleTank.getFluid().amount = j;
                }
        }
    }
}
