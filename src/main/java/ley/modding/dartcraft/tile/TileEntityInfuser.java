package ley.modding.dartcraft.tile;

import java.util.stream.IntStream;

import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IForceConsumer;
import ley.modding.dartcraft.api.recipe.ForceWildCard;
import ley.modding.dartcraft.api.upgrades.IForceUpgradable;
import ley.modding.dartcraft.api.upgrades.ForceUpgrade;
import ley.modding.dartcraft.api.upgrades.IForceUpgradeMaterial;
import ley.modding.dartcraft.block.BlockForceTorch;
import ley.modding.dartcraft.infusion.ForceWildCards;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.item.ItemForceTome;
import ley.modding.dartcraft.network.PacketFX;
import ley.modding.dartcraft.network.PacketFX.Type;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.EnchantUtils;
import ley.modding.dartcraft.util.ForceConsumerUtils;
import ley.modding.dartcraft.util.ForceUpgradeManager;
import ley.modding.dartcraft.util.TomeUtils;
import ley.modding.dartcraft.util.UpgradeHelper;
import net.anvilcraft.anvillib.util.AnvilUtil;
import net.anvilcraft.anvillib.util.NBTCollector;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public class TileEntityInfuser extends TileEntityMachine
    implements IFluidTank, IFluidHandler, /*IMachine, IPowerReceptor,*/ ISidedInventory,
               IEnergyReceiver {
    public static final int RF_PER_T = 100;
    public static final int MAX_LEVEL = 50000;
    public int progress;
    public int totalProgress;
    public ErrorType errorType = ErrorType.NONE;
    public boolean isActive;
    public boolean hasTome;
    private boolean tierNeedsUpdate;
    private ItemStack prevBook;
    private int requiredLiquid;
    private int requiredRF;
    public FluidStack liquid;
    public int storedRF;
    private int tankPressure;
    public int canUpgrade;
    private int lastCheck;
    public float bookRotation;
    public float bookRotation2;
    public float bookRotationPrev;
    public float pageFlip;
    public float pageFlipPrev;
    public float bookSpread;
    public float bookSpreadPrev;
    public float tempFloat;
    public int bookInt;
    private EntityPlayer usingPlayer;
    private NBTTagCompound currentUpgrades;
    private int totalCurrentUpgrades;
    private int upgradeWeight;
    private boolean expExempt;

    public TileEntityInfuser() {
        Fluid liquidForce = FluidRegistry.getFluid("liquidforce");
        this.liquid = new FluidStack(liquidForce, 0);

        this.contents = new InventoryBasic("infuser", false, 11);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!Dartcraft.proxy.isSimulating(this.worldObj)) {
            this.updateAnimation();
            return;
        }

        if (this.hasTome != this.hasTome() || this.isActive && this.progress <= 0
            || !this.isActive && this.progress > 0) {
            this.hasTome = this.hasTome();
            this.isActive = this.progress > 0;
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }

        ItemStack gemStack = this.getStackInSlot(1);
        Fluid liquidForce = FluidRegistry.getFluid("liquidforce");
        if (gemStack != null && ForceConsumerUtils.isForceContainer(gemStack)
            && liquidForce != null && this.getFluid() != null
            && this.getFluid().amount <= MAX_LEVEL - 1000) {
            this.fill(new FluidStack(liquidForce, 1000), true);
            if (gemStack.getItem().hasContainerItem() && gemStack.stackSize == 1) {
                this.setInventorySlotContents(
                    1, gemStack.getItem().getContainerItem(gemStack)
                );
            } else {
                if (this.getStackInSlot(1).getItem() == DartItems.forceshard
                    && this.hasTome()) {
                    NBTTagCompound tome = this.getStackInSlot(0).getTagCompound();
                    if (tome != null) {
                        tome.setInteger("bonus", tome.getInteger("bonus") + 10);
                    }
                }

                --this.getStackInSlot(1).stackSize;
                if (this.getStackInSlot(1).stackSize == 0) {
                    this.setInventorySlotContents(1, (ItemStack) null);
                }
            }
        }

        if (this.totalProgress > 0) {
            ++this.progress;
            this.storedRF -= 100;
            if (this.progress >= this.totalProgress) {
                this.totalProgress = this.progress = 0;
                this.doUpgrade();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateAnimation() {
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation2;
        EntityPlayer var1 = this.worldObj.getClosestPlayer(
            (double) ((float) this.xCoord + 0.5F),
            (double) ((float) this.yCoord + 0.5F),
            (double) ((float) this.zCoord + 0.5F),
            3.0D
        );
        if (var1 != null) {
            double var7 = var1.posX - (double) ((float) this.xCoord + 0.5F);
            double var8 = var1.posZ - (double) ((float) this.zCoord + 0.5F);
            this.bookRotation = (float) Math.atan2(var8, var7);
            this.bookSpread += 0.1F;
            if (this.bookSpread < 0.5F || this.worldObj.rand.nextInt(40) == 0) {
                float var6 = (float) this.bookInt;

                do {
                    this.bookInt
                        += this.worldObj.rand.nextInt(4) - this.worldObj.rand.nextInt(4);
                } while (var6 == (float) this.bookInt);
            }
        } else {
            this.bookRotation += 0.02F;
            this.bookSpread -= 0.1F;
        }

        while (this.bookRotation2 >= 3.141593F) {
            this.bookRotation2 -= 6.283186F;
        }

        while (this.bookRotation2 < -3.141593F) {
            this.bookRotation2 += 6.283186F;
        }

        while (this.bookRotation >= 3.141593F) {
            this.bookRotation -= 6.283186F;
        }

        while (this.bookRotation < -3.141593F) {
            this.bookRotation += 6.283186F;
        }

        float var71;
        for (var71 = this.bookRotation - this.bookRotation2; var71 >= 3.141593F;
             var71 -= 6.283186F) {
            ;
        }

        while (var71 < -3.141593F) {
            var71 += 6.283186F;
        }

        this.bookRotation2 += var71 * 0.4F;
        if (this.bookSpread < 0.0F) {
            this.bookSpread = 0.0F;
        }

        if (this.bookSpread > 1.0F) {
            this.bookSpread = 1.0F;
        }

        this.pageFlipPrev = this.pageFlip;
        float var3 = ((float) this.bookInt - this.pageFlip) * 0.4F;
        float var81 = 0.2F;
        if (var3 < -var81) {
            var3 = -var81;
        }

        if (var3 > var81) {
            var3 = var81;
        }

        this.tempFloat += (var3 - this.tempFloat) * 0.9F;
        this.pageFlip += this.tempFloat;
    }

    public boolean hasTome() {
        return this.getStackInSlot(0) != null
            && this.getStackInSlot(0).getItem() instanceof ItemForceTome;
    }

    public int getActiveTier() {
        return !this.hasTome() ? 0 : TomeUtils.getStoredTier(this.getStackInSlot(0));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("items")) {
            NBTTagList items = nbt.getTagList("items", 10);

            int i = 0;
            for (ItemStack stack :
                 (Iterable<ItemStack>) IntStream.range(0, items.tagCount())
                     .mapToObj(items::getCompoundTagAt)
                     .map(ItemStack::loadItemStackFromNBT)::iterator) {
                this.setInventorySlotContents(i++, stack);
            };
        }

        Fluid liquidforce = FluidRegistry.getFluid("liquidforce");
        this.liquid = new FluidStack(liquidforce.getID(), nbt.getInteger("tankLevel"));

        this.storedRF = nbt.getInteger("energyLevel");
        this.progress = nbt.getInteger("progress");
        this.totalProgress = nbt.getInteger("totalProgress");
        this.requiredLiquid = nbt.getInteger("requiredLiquid");
        this.requiredRF = nbt.getInteger("requiredRF");
        this.isActive = nbt.getBoolean("isActive");
        this.hasTome = nbt.getBoolean("hasTome");
        this.prevBook = this.getStackInSlot(0);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setTag(
            "items",
            IntStream.range(0, this.getSizeInventory())
                .mapToObj(this::getStackInSlot)
                .map(
                    s
                    -> s == null ? new NBTTagCompound()
                                 : s.writeToNBT(new NBTTagCompound())
                )
                .collect(new NBTCollector())
        );

        nbt.setInteger("tankLevel", this.getFluid().amount);
        nbt.setInteger("energyLevel", this.storedRF);
        nbt.setInteger("progress", this.progress);
        nbt.setInteger("totalProgress", this.totalProgress);
        nbt.setInteger("requiredLiquid", this.requiredLiquid);
        nbt.setInteger("requiredRF", this.requiredRF);
        nbt.setBoolean("isActive", this.isActive);
        nbt.setBoolean("hasTome", this.hasTome);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (super.contents != null && !this.tierNeedsUpdate) {
            if (this.prevBook != null || this.getStackInSlot(0) != null) {
                if (!(this.prevBook == null & this.getStackInSlot(0) != null)
                    && (this.prevBook == null || this.getStackInSlot(0) != null)) {
                    if (!this.prevBook.equals(this.getStackInSlot(0))) {
                        this.prevBook = this.getStackInSlot(0);
                        this.checkTierContents();
                    }
                } else {
                    this.prevBook = this.getStackInSlot(0);
                    this.checkTierContents();
                }
            }
        }
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

    private void checkTierContents() {
        int tier = this.getActiveTier();

        for (int i = 0; i < 8; ++i) {
            if (this.getStackInSlot(i + 2) != null) {
                boolean shouldDrop = false;
                if (tier + 1 < i) {
                    shouldDrop = true;
                }

                IForceUpgradeMaterial mat = ForceUpgradeManager.getMaterialFromItemStack(
                    this.getStackInSlot(i + 2)
                );
                if (mat != null
                    && tier
                        < ForceUpgradeManager.getFromID(mat.getUpgradeID()).getTier()) {
                    shouldDrop = true;
                }

                if (shouldDrop) {
                    DartUtils.dropItem(
                        this.getStackInSlot(i + 2),
                        this.worldObj,
                        (double) this.xCoord,
                        (double) this.yCoord,
                        (double) this.zCoord
                    );
                    this.setInventorySlotContents(i + 2, (ItemStack) null);
                }
            }
        }

        if (tier < 7 && this.getStackInSlot(10) != null) {
            DartUtils.dropItem(
                this.getStackInSlot(10),
                this.worldObj,
                (double) this.xCoord,
                (double) this.yCoord,
                (double) this.zCoord
            );
            this.setInventorySlotContents(10, (ItemStack) null);
        }
    }

    public boolean canUpgrade(EntityPlayer player) {
        ItemStack toUpgrade = this.getStackInSlot(2);
        boolean creative = player != null && player.capabilities.isCreativeMode;
        boolean change = Dartcraft.proxy.isSimulating(this.worldObj);
        if (this.progress > 0 && this.totalProgress > 0) {
            this.errorType = ErrorType.NONE;
            return false;
        } else if (/*!Config.moochMode && */ (
            player == null || !player.getUniqueID().equals(this.getOwner())
        )) {
            this.errorType = ErrorType.USER;
            return false;
        } else {
            this.expExempt = false;
            if (toUpgrade != null
                && (toUpgrade.getItem() == Items.bucket
                    || toUpgrade.getItem() == DartItems.forceflask
                        && toUpgrade.getItemDamage() == 0)) {
                for (int var19 = 3; var19 < 11; ++var19) {
                    if (this.getStackInSlot(var19) != null) {
                        this.errorType = ErrorType.INVALID;
                        return false;
                    }
                }

                if (this.liquid.amount >= 1000 || creative) {
                    this.requiredLiquid = 1000;
                    this.requiredRF = 100;
                    if (this.storedRF >= this.requiredRF) {
                        this.expExempt = true;
                        return true;
                    }
                }

                this.errorType = ErrorType.FORCE;
                return false;
            } else if (!DartUtils.canUpgrade(toUpgrade)) {
                this.errorType = ErrorType.NONE;
                return false;
            } else {
                float modifier = 0.0F;
                boolean hasUpgrade = false;
                boolean hasCharge = false;
                boolean hasEnder = false;
                boolean hasRepair = false;
                boolean hasSturdy = false;
                int storage = 0;
                int totalTypes = 0;
                int totalUpgrades = 0;
                this.upgradeWeight = 0;
                NBTTagCompound types = new NBTTagCompound();

                int isUpgradable;
                int temp;
                for (isUpgradable = 3; isUpgradable < 11; ++isUpgradable) {
                    if (this.getStackInSlot(isUpgradable) != null) {
                        IForceUpgradeMaterial upgrades
                            = ForceUpgradeManager.getMaterialFromItemStack(
                                this.getStackInSlot(isUpgradable)
                            );
                        ForceUpgrade consumer = null;
                        if (upgrades != null) {
                            consumer
                                = ForceUpgradeManager.getFromID(upgrades.getUpgradeID());
                        }

                        if (consumer == null) {
                            this.errorType = ErrorType.INVALID;
                            return false;
                        }

                        ++totalUpgrades;
                        this.upgradeWeight += consumer.getTier() >= 5
                            ? 3
                            : (consumer.getTier() >= 3 ? 2 : 1);
                        if (!types.hasKey(consumer.getName())) {
                            types.setInteger(consumer.getName(), 1);
                            if (consumer.getID() != ForceUpgradeManager.REPAIR.getID()) {
                                ++totalTypes;
                            } else {
                                hasRepair = true;
                            }

                            if (consumer.getID() == ForceUpgradeManager.CHARGE.getID()
                                || consumer.getID()
                                    == ForceUpgradeManager.CHARGE2.getID()) {
                                hasCharge = true;
                            }
                        } else {
                            temp = types.getInteger(consumer.getName());
                            ++temp;
                            if (temp > consumer.getMaxLevel()) {
                                this.errorType = ErrorType.INVALID;
                                return false;
                            }

                            types.setInteger(consumer.getName(), temp);
                        }

                        if (toUpgrade.getItem() instanceof IForceUpgradable) {
                            if (!ForceUpgradeManager.isUpgradeValid(
                                    consumer, (IForceUpgradable) toUpgrade.getItem()
                                )) {
                                this.errorType = ErrorType.INVALID;
                                return false;
                            }

                            hasUpgrade = true;
                            modifier += upgrades.getEfficiency();
                        } else {
                            // TODO: ForceWildCards?
                            //if (!ForceWildCards.isUpgradeValidForWildCard(
                            //        consumer, toUpgrade
                            //    )) {
                            //    this.errorType = ErrorType.INVALID;
                            //    return false;
                            //}

                            //hasUpgrade = true;
                            //modifier += upgrades.getEfficiency();
                        }

                        if (consumer.getID() == ForceUpgradeManager.STORAGE.getID()) {
                            ++storage;
                        }

                        if (consumer.getID() == ForceUpgradeManager.ENDER.getID()) {
                            hasEnder = true;
                        }

                        if (consumer.getID() == ForceUpgradeManager.STURDY.getID()) {
                            hasSturdy = true;
                        }
                    }
                }

                if (hasUpgrade) {
                    this.currentUpgrades = types;
                    this.totalCurrentUpgrades = totalUpgrades;
                    // TODO: force pack
                    //if (toUpgrade.getItem() instanceof ItemForcePack) {
                    //    if (!toUpgrade.hasTagCompound() || totalTypes > 1
                    //        || storage > 1) {
                    //        this.errorType = ErrorType.INVALID;
                    //        return false;
                    //    }

                    //    if (UpgradeHelper.getUpgradeCompound(toUpgrade).hasKey("Sturdy")
                    //        && hasSturdy) {
                    //        this.errorType = 5;
                    //        return false;
                    //    }

                    //    isUpgradable
                    //        = toUpgrade.getTagCompound().getInteger("size") + 8 *
                    //        storage;
                    //    if (isUpgradable > 40) {
                    //        this.errorType = 5;
                    //        return false;
                    //    }

                    //    if ((isUpgradable - Config.packSize) / 8
                    //        > this.getActiveTier() - 1) {
                    //        this.errorType = 5;
                    //        return false;
                    //    }
                    //} else
                    // TODO: ForceFrame
                    //if (toUpgrade.getItem() == DartBlock.forceFrame.blockID) {
                    //    if (totalTypes > 1 || types.getInteger("Storage") > 1) {
                    //        this.errorType = 5;
                    //        return false;
                    //    }

                    //    isUpgradable = toUpgrade.hasTagCompound()
                    //        ? toUpgrade.getTagCompound().getInteger("tier")
                    //        : 0;
                    //    boolean var22 = toUpgrade.hasTagCompound()
                    //        ? toUpgrade.getTagCompound().getBoolean("sturdy")
                    //        : false;
                    //    boolean var24 = toUpgrade.hasTagCompound()
                    //        ? toUpgrade.getTagCompound().getBoolean("ender")
                    //        : false;
                    //    if (types.getInteger("Storage") > 0
                    //        && (isUpgradable >= 3
                    //            || isUpgradable + 4 > this.getActiveTier()
                    //            || isUpgradable >= 2 && !Config.largeStorageUnits
                    //            || var24)) {
                    //        this.errorType = 5;
                    //        return false;
                    //    }

                    //    if (var22 && hasSturdy) {
                    //        this.errorType = 5;
                    //        return false;
                    //    }

                    //    if (var24 && hasEnder) {
                    //        this.errorType = 5;
                    //        return false;
                    //    }
                    //} else
                    // TODO: ForceRod
                    //if (toUpgrade.getItem() == DartItems.forcerod) {
                    //    if (Config.disableRodSpeed && types.hasKey("Speed")
                    //        || Config.disableRodHeat && types.hasKey("Heat")
                    //        || Config.disableRodHeal && types.hasKey("Healing")
                    //        || Config.disableRodEnder && types.hasKey("Ender")) {
                    //        this.errorType = 5;
                    //        return false;
                    //    }
                    //} else
                    // TODO: ForceArmor
                    //if (toUpgrade.getItem() instanceof ItemForceArmor) {
                    //    if (Config.disableArmorUpgrades
                    //        || Config.disableSturdyArmor && types.hasKey("Sturdy")
                    //        || Config.disableWingArmor && types.hasKey("Wing")) {
                    //        this.errorType = 5;
                    //        return false;
                    //    }
                    //} else
                    // TODO: ForceSword
                    //if (toUpgrade.getItem() == DartItems.forcesword) {
                    //    if (Config.disableEnderSword && types.hasKey("Ender")
                    //        || toUpgrade.hasTagCompound()
                    //            && toUpgrade.getTagCompound().hasKey("upgrades")
                    //            && !toUpgrade.getTagCompound()
                    //                    .getCompoundTag("upgrades")
                    //                    .hasNoTags()) {
                    //        this.errorType = 5;
                    //        return false;
                    //    }
                    //} else
                    if (toUpgrade.getItem() == DartItems.clipboard) {
                        if (!toUpgrade.hasTagCompound() || totalTypes > 1
                            || storage > 1) {
                            this.errorType = ErrorType.INVALID;
                            return false;
                        }

                        if (UpgradeHelper.getUpgradeCompound(toUpgrade).hasKey("Sturdy")
                            && hasSturdy) {
                            this.errorType = ErrorType.FORBIDDEN;
                            return false;
                        }
                    } else if(toUpgrade.hasTagCompound() && toUpgrade.getTagCompound().hasKey("upgrades") && !toUpgrade.getTagCompound().getCompoundTag("upgrades").hasNoTags()) {
                        this.errorType = ErrorType.FORBIDDEN;
                        return false;
                    }

                    if ((/*toUpgrade.getItem() instanceof ItemUpgradeCore
                         ||*/ toUpgrade.getItem() instanceof ItemForceTome
                         /*|| toUpgrade.getItem() instanceof ItemForceRod
                         || toUpgrade.getItem() instanceof ItemMemberCard
                         */
                         || toUpgrade.getItem() instanceof BlockForceTorch.BlockItem)
                        && totalTypes > 1) {
                        this.errorType = ErrorType.INVALID;
                        return false;
                    } else if (toUpgrade.getItem() instanceof BlockForceTorch.BlockItem && toUpgrade.getItemDamage() > 15) {
                        this.errorType = ErrorType.INVALID;
                        return false;
                    } else if (hasCharge && hasRepair) {
                        this.errorType = ErrorType.INVALID;
                        return false;
                    } else {
                        if (change) {
                            this.requiredRF
                                = creative ? 0 : (int) this.energyUsed(modifier);
                            this.requiredLiquid
                                = creative ? 0 : this.liquidUsed(modifier);
                            if (toUpgrade.getItem()
                                    instanceof BlockForceTorch.BlockItem) {
                                this.requiredRF = (int) ((float) this.requiredRF / 16.0F);
                                this.requiredLiquid
                                    = (int) ((float) this.requiredLiquid / 16.0F);
                            }
                        }

                        if (player != null) {
                            isUpgradable = this.getRequiredExp();
                            if (!creative /*&& Config.requireExp*/
                                && player.experienceLevel < isUpgradable) {
                                this.errorType = ErrorType.EXP;
                                return false;
                            }

                            if (change) {
                                this.usingPlayer = player;
                            }
                        }

                        if (!creative && this.storedRF < this.energyUsed(modifier)) {
                            this.errorType = ErrorType.ENERGY;
                            return false;
                        } else if (!creative && this.liquid.amount < this.liquidUsed(modifier)) {
                            this.errorType = ErrorType.FORCE;
                            return false;
                        } else {
                            this.errorType = ErrorType.NONE;
                            return true;
                        }
                    }
                } else {
                    this.upgradeWeight = 0;
                    this.totalCurrentUpgrades = 0;
                    this.currentUpgrades = new NBTTagCompound();
                    boolean var20 = toUpgrade.getItem() instanceof IForceUpgradable;
                    if (toUpgrade.hasTagCompound()
                        && toUpgrade.getItem() instanceof IForceConsumer
                        && ForceConsumerUtils.getStoredForce(toUpgrade)
                            < ((IForceConsumer) toUpgrade.getItem())
                                  .getMaxStored(toUpgrade)
                        && (this.liquid.amount > 0 || creative)
                        && (!var20
                            || var20
                                && (toUpgrade.getTagCompound().hasKey("upgrades")
                                    /*|| toUpgrade.getItem() == DartItems.forcerod*/))) {
                        NBTTagCompound var21
                            = UpgradeHelper.getUpgradeCompound(toUpgrade);
                        if (!var21.hasKey("Charge") && !var21.hasKey("Charge2")) {
                            IForceConsumer var23 = (IForceConsumer) toUpgrade.getItem();
                            temp = (var23.getMaxStored(toUpgrade)
                                    - var23.getStored(toUpgrade))
                                / 2;
                            if (temp < 100) {
                                temp = 100;
                            }

                            if (creative) {
                                temp = 1;
                            }

                            if (!creative && this.storedRF < (float) temp) {
                                this.errorType = ErrorType.ENERGY;
                                return false;
                            } else {
                                if (change) {
                                    this.requiredRF = temp;
                                }

                                this.errorType = ErrorType.NONE;
                                return true;
                            }
                        } else {
                            this.errorType = ErrorType.FORBIDDEN;
                            return false;
                        }
                    } else {
                        this.errorType = ErrorType.NONE;
                        return false;
                    }
                }
            }
        }
    }

    public int getRequiredExp() {
        //return !Config.moochMode && Config.expCost != 0
        //    ? (Config.expCost == 2 ? this.upgradeWeight : this.totalCurrentUpgrades)
        //    : 0;
        return this.upgradeWeight;
    }

    public int energyUsed(float efficiency) {
        return (int) (efficiency * 1000.0F);
    }

    public int liquidUsed(float efficiency) {
        return (int) (efficiency * 200.0F);
    }

    public void go(EntityPlayer player) {
        if (Dartcraft.proxy.isSimulating(this.worldObj)) {
            this.usingPlayer = player;
            if (this.canUpgrade(player)) {
                int var10001 = this.requiredRF;
                this.totalProgress = var10001 / 10 / (this.getActiveTier() >= 7 ? 2 : 1);
                if (player != null) {
                    if (/*Config.requireExp &&*/ !player.capabilities.isCreativeMode
                        && !this.expExempt) {
                        int required = this.getRequiredExp();
                        if (player.experienceLevel >= required) {
                            player.addExperienceLevel(-required);
                        }

                        player.worldObj.playSoundAtEntity(
                            player, "random.exp", 1.0F, DartUtils.randomPitch()
                        );
                    }

                    if (player.capabilities.isCreativeMode) {
                        this.totalProgress = 1;
                    }
                }
            }
        }
    }

    public void doUpgrade() {
        ItemStack product = null;
        if (this.getStackInSlot(2) != null) {
            product = this.getStackInSlot(2).copy();
            NBTTagCompound upgrades = new NBTTagCompound();
            NBTTagCompound tome = this.getStackInSlot(0) != null
                    && this.getStackInSlot(0).hasTagCompound()
                ? this.getStackInSlot(0).getTagCompound()
                : new NBTTagCompound();
            boolean hasUnique = false;
            int numUpgrades = 0;
            boolean creative = this.usingPlayer != null
                && this.usingPlayer.capabilities.isCreativeMode;

            int i;
            for (int realEnchants = 3; realEnchants < 11; ++realEnchants) {
                if (this.getStackInSlot(realEnchants) != null) {
                    IForceUpgradeMaterial hasRealEnchant
                        = ForceUpgradeManager.getMaterialFromItemStack(
                            this.getStackInSlot(realEnchants)
                        );
                    if (hasRealEnchant == null) {
                        return;
                    }

                    ForceUpgrade wildCard
                        = ForceUpgradeManager.getFromID(hasRealEnchant.getUpgradeID());
                    i = wildCard.getMaterialIndex(hasRealEnchant);
                    if (!upgrades.hasKey(wildCard.getName())
                        && !(product.getItem() instanceof BlockForceTorch.BlockItem)) {
                        ++numUpgrades;
                        tome.setInteger(
                            "bonus", tome.getInteger("bonus") + hasRealEnchant.getBonus()
                        );
                        tome.setInteger(
                            wildCard.getName() + i,
                            tome.getInteger(wildCard.getName() + i) + 1
                        );
                    }

                    upgrades.setInteger(
                        wildCard.getName(), upgrades.getInteger(wildCard.getName()) + 1
                    );
                }
            }

            if (this.hasTome() && TomeUtils.canTomeAdvance(this.getStackInSlot(0))) {
                TomeUtils.advanceTier(this.getStackInSlot(0));
            }

            if (!(product.getItem() instanceof IForceConsumer) || numUpgrades != 0) {
                this.liquid.amount -= this.requiredLiquid;
            }

            NBTTagList enchants = EnchantUtils.getRealEnchants(product, upgrades);
            boolean hasEnchants = enchants.tagCount() > 0;
            ForceWildCard wildcard = ForceWildCards.getWildCard(product);
            // TODO: force pack
            //if (product.getItem() instanceof ItemForcePack) {
            //    i = product.getTagCompound().getInteger("size");
            //    if (upgrades.hasKey("Holding")) {
            //        i += upgrades.getInteger("Holding") * 8;
            //        if (i % 8 != 0) {
            //            i -= i % 8;
            //        }

            //        if (i > 40) {
            //            i = 40;
            //        }

            //        product.getTagCompound().setInteger("size", i);
            //    } else if (upgrades.hasKey("Sturdy")) {
            //        if (!product.getTagCompound().hasKey("upgrades")) {
            //            product.getTagCompound().setCompoundTag(
            //                "upgrades", new NBTTagCompound()
            //            );
            //        }

            //        UpgradeHelper.getUpgradeCompound(product).setInteger("Sturdy", 1);
            //    } else if (upgrades.hasKey("Ender")) {
            //        ItemInventory tier = new ItemInventory(
            //            product.getTagCompound().getInteger("size"), product
            //        );
            //        if (tier != null) {
            //            for (int sturdy = 0; sturdy < tier.getSizeInventory(); ++sturdy)
            //            {
            //                ItemStack ender = tier.getStackInSlot(sturdy);
            //                if (ender != null) {
            //                    DartUtils.dropItem(
            //                        ender,
            //                        this.worldObj,
            //                        (double) this.xCoord,
            //                        (double) this.yCoord,
            //                        (double) this.zCoord
            //                    );
            //                }
            //            }
            //        }

            //        product = new ItemStack(DartItem.enderPack, 1);
            //    }
            //} else
            // TODO: ForceBelt
            //if (product.getItem() instanceof ItemForceBelt) {
            //    if (!product.getTagCompound().hasKey("upgrades")) {
            //        product.getTagCompound().setCompoundTag(
            //            "upgrades", new NBTTagCompound()
            //        );
            //    }

            //    if (upgrades.hasKey("Sturdy")) {
            //        UpgradeHelper.getUpgradeCompound(product).setInteger("Sturdy", 1);
            //    }
            //} else
            // TODO: ForceArmor
            //if (product.getItem() instanceof ItemForceArmor) {
            //    if (!product.hasTagCompound()) {
            //        product.setTagCompound(new NBTTagCompound());
            //    }

            //    if (numUpgrades > 0) {
            //        product.getTagCompound().setCompoundTag("upgrades", upgrades);
            //    } else {
            //        this.imbueForce(product, creative);
            //    }
            //} else
            // TODO: MemberCard
            //if (product.getItem() instanceof ItemMemberCard) {
            //    if (!product.hasTagCompound()) {
            //        product.setTagCompound(new NBTTagCompound());
            //    }

            //    product.getTagCompound().setCompoundTag("upgrades", upgrades);
            //} else
            if (product.getItem() instanceof BlockForceTorch.BlockItem) {
                try {
                    i = BlockForceTorch.upgrades.values().iterator().next()[0];
                    product.setItemDamage(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                // TODO: ForceFrame
                //if (product.itemID == DartBlock.forceFrame.blockID) {
                //    try {
                //        TileStorage var26 = new TileStorage();
                //        if (!product.hasTagCompound()) {
                //            product.setTagCompound(new NBTTagCompound());
                //            var26.setColor(product.getItemDamage() % 16);
                //        } else {
                //            try {
                //                var26 = (TileStorage
                //                )
                //                TileEntity.createAndLoadEntity(product.getTagCompound());
                //            } catch (Exception var16) {
                //                ;
                //            }
                //        }

                //        int var23 = var26.tier + (upgrades.hasKey("Storage") ? 1 : 0);
                //        boolean var25 = upgrades.hasKey("Sturdy") || var26.sturdy;
                //        boolean var27 = upgrades.hasKey("Ender") || var26.ender;
                //        if (upgrades.hasKey("Ender") && !var26.ender) {
                //            for (int i1 = 0; i1 < var26.getSizeInventory(); ++i1) {
                //                ItemStack dropStack = var26.getStackInSlot(i1);
                //                if (dropStack != null) {
                //                    DartUtils.dropItem(
                //                        dropStack.copy(),
                //                        this.worldObj,
                //                        (double) this.xCoord,
                //                        (double) this.yCoord,
                //                        (double) this.zCoord
                //                    );
                //                }

                //                var26.setInventorySlotContents(i1, (ItemStack) null);
                //            }
                //        }

                //        var26.tier = var23;
                //        var26.sturdy = var25;
                //        var26.ender = var27;
                //        var26.writeToNBT(product.getTagCompound());
                //    } catch (Exception var18) {
                //        ;
                //    }
                //} else
                if (wildcard != null && wildcard.hasOutput()) {
                    product = wildcard.getOutput();
                    wildcard.onProcess(this);
                } else if (product.getItem() == Items.bucket) {
                    // TODO: bucket
                    //product = new ItemStack(DartItem.forceBucket);
                } else if (product.getItem() == DartItems.forceflask && product.getItemDamage() == 0) {
                    product.setItemDamage(2);
                } else if (product.getItem() instanceof IForceConsumer && numUpgrades <= 0) {
                    this.imbueForce(product, creative);
                } else {
                    if (!product.hasTagCompound()) {
                        product.setTagCompound(new NBTTagCompound());
                    }

                    NBTTagCompound prodNbt = product.getTagCompound();
                    prodNbt.setTag("upgrades", upgrades);
                    // TODO: ItemUpgradeCore
                    //if (hasEnchants && !(product.getItem() instanceof ItemUpgradeCore))
                    //{
                    //    prodNbt.setTag("ench", enchants);
                    //}
                }

            for (i = 3; i < 11; ++i) {
                this.setInventorySlotContents(i, (ItemStack) null);
            }

            this.setInventorySlotContents(2, product);
            this.getStackInSlot(2).stackSize = 1;
            this.worldObj.playSoundEffect(
                (double) this.xCoord,
                (double) this.yCoord,
                (double) this.zCoord,
                "dartcraft:magic",
                1.0F,
                DartUtils.randomPitch()
            );

            WorldVec pktPos = new WorldVec(this);
            Dartcraft.channel.sendToAllAround(
                new PacketFX(pktPos, Type.SPARKLES, 2, 0, 16), pktPos.targetPoint(80d)
            );
        }
    }

    private void imbueForce(ItemStack product, boolean creative) {
        IForceConsumer consumer = (IForceConsumer) product.getItem();
        if (!product.hasTagCompound()) {
            product.setTagCompound(new NBTTagCompound());
        }

        for (NBTTagCompound comp = product.getTagCompound();
             consumer.getMaxStored(product) - consumer.getStored(product) > 0
             && (this.liquid.amount > 0 || creative);
             ForceConsumerUtils.attemptRepair(product)) {
            if (!Dartcraft.proxy.isSimulating(this.worldObj)) {
                break;
            }

            int defecit = consumer.getMaxStored(product) - consumer.getStored(product);
            if (this.liquid.amount < defecit && !creative) {
                defecit = this.liquid.amount;
            }

            comp.setInteger("storedForce", consumer.getStored(product) + defecit);
            if (!creative) {
                this.liquid.amount -= defecit;
            }
        }
    }

    @Override
    public String getInventoryName() {
        return "Force Infuser";
    }

    @Override
    public FluidStack getFluid() {
        return this.liquid;
    }

    @Override
    public int getCapacity() {
        return MAX_LEVEL;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        Fluid liquidForce = FluidRegistry.getFluid("liquidforce");
        if (resource != null && liquidForce != null
            && resource.getFluid() == liquidForce) {
            int space = MAX_LEVEL - this.liquid.amount;
            if (resource.amount <= space) {
                if (doFill) {
                    this.liquid.amount += resource.amount;
                }

                return resource.amount;
            } else {
                if (doFill) {
                    this.liquid.amount = MAX_LEVEL;
                }

                return space;
            }
        } else {
            return 0;
        }
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (this.liquid.amount > 0) {
            int used = maxDrain;
            if (this.liquid.amount < maxDrain) {
                used = this.liquid.amount;
            }

            if (doDrain) {
                this.liquid.amount -= used;
            }

            FluidStack drained
                = new FluidStack(this.liquid.getFluid(), used, this.liquid.tag);
            if (this.liquid.amount < 0) {
                this.liquid.amount = 0;
            }

            return drained;
        }
        return null;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return this.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return this.drain(maxDrain, doDrain);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        if (!super.isItemValidForSlot(i, stack)) {
            return false;
        } else {
            int tier = this.getActiveTier();
            switch (i) {
                case 0:
                    return false;
                case 1:
                    return ForceConsumerUtils.isForceContainer(stack);
                case 2:
                    return DartUtils.canUpgrade(stack);
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                    if (tier + 3 < i) {
                        return false;
                    }

                    return DartUtils.canUpgrade(stack);
                default:
                    return false;
            }
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        Fluid liquidForce = FluidRegistry.getFluid("liquidforce");
        return resource != null && liquidForce != null
                && resource.isFluidEqual(new FluidStack(liquidForce, 1))
            ? this.drain(resource.amount, doDrain)
            : null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        Fluid liquidForce = FluidRegistry.getFluid("liquidforce");
        return fluid != null && liquidForce != null
            && (new FluidStack(fluid, 1)).isFluidEqual(new FluidStack(liquidForce, 1))
            && this.liquid != null && this.liquid.amount < MAX_LEVEL;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        Fluid liquidForce = FluidRegistry.getFluid("liquidforce");
        return fluid != null && liquidForce != null
            && (new FluidStack(fluid, 1)).isFluidEqual(new FluidStack(liquidForce, 1))
            && this.liquid != null && this.liquid.amount > 0;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { this.getInfo() };
    }

    @Override
    public int getFluidAmount() {
        return this.liquid != null ? this.liquid.amount : 0;
    }

    @Override
    public FluidTankInfo getInfo() {
        Fluid liquidForce = FluidRegistry.getFluid("liquidforce");
        return this.liquid != null
            ? new FluidTankInfo(this.liquid, MAX_LEVEL)
            : (liquidForce != null
                   ? new FluidTankInfo(new FluidStack(liquidForce, 0), MAX_LEVEL)
                   : null);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return IntStream.rangeClosed(1, 10).toArray();
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection var1) {
        return true;
    }

    @Override
    public int receiveEnergy(ForgeDirection face, int nrg, boolean simulate) {
        int received = Math.min(nrg, this.getMaxEnergyStored(face) - this.storedRF);
        if (!simulate)
            this.storedRF += received;
        return received;
    }

    @Override
    public int getEnergyStored(ForgeDirection var1) {
        return this.storedRF;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection var1) {
        return 500000;
    }

    public static enum ErrorType {
        NONE,
        FORCE,
        ENERGY,
        INVALID,
        EXP,
        FORBIDDEN,
        USER;
    }

    @Override
    public int[] getContainerSyncData() {
        return new int[] {
            this.errorType.ordinal(),
            this.progress,
            this.totalProgress,
            this.isActive ? 1 : 0,
        };
    }

    @Override
    public void receiveContainerSyncData(int idx, int val) {
        switch (idx) {
            case 0:
                this.errorType = AnvilUtil.enumFromInt(ErrorType.class, val);
                break;
            case 1:
                this.progress = val;
                break;
            case 2:
                this.totalProgress = val;
                break;
            case 3:
                this.isActive = val != 0;
                break;
        }
    }
}
