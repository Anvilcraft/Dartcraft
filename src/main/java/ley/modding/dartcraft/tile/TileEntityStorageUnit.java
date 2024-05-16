package ley.modding.dartcraft.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.inventory.ItemInventory;
import ley.modding.dartcraft.block.DartBlocks;
import ley.modding.dartcraft.client.gui.ContainerStorageUnit;
import ley.modding.dartcraft.network.PacketFX;
import ley.modding.dartcraft.storage.EnderInventory;
import ley.modding.dartcraft.storage.EnderStorageHandler;
import ley.modding.dartcraft.util.DartUtils;
import ley.modding.dartcraft.util.UpgradeHelper;
import net.anvilcraft.anvillib.util.AnvilUtil;
import net.anvilcraft.anvillib.vector.WorldVec;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityStorageUnit extends TileEntityMachine {
    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;
    public int tier;
    public boolean sturdy;
    public boolean ender;
    private int ticksSinceSync;
    private int timeout;
    private boolean shouldCheck;
    private Map<String, ArrayList<ItemStack>> cards = new HashMap<>();
    public EnderInventory enderInv;
    private int lastColor = -1;
    private UUID lastOwner;

    public TileEntityStorageUnit() {
        this.setTier(0);
        this.cards.put("None", new ArrayList<>());
        this.cards.put("Force", new ArrayList<>());
        this.cards.put("Craft", new ArrayList<>());
        this.cards.put("Forge", new ArrayList<>());
        this.cards.put("Heat", new ArrayList<>());
    }

    public void setTier(int tier) {
        this.tier = tier;
        ArrayList<ItemStack> previous = new ArrayList<ItemStack>();
        if (this.contents != null) {
            for (int i = 0; i < this.contents.getSizeInventory(); ++i) {
                if (this.contents.getStackInSlot(i) == null)
                    continue;
                previous.add(this.contents.getStackInSlot(i));
            }
        }
        if (previous.size() > 0) {
            for (int i = 0; i < previous.size(); ++i) {
                if (previous.get(i) == null)
                    continue;
                if (i < this.getSizeInventory()) {
                    this.setInventorySlotContents(i, (ItemStack) previous.get(i));
                    continue;
                }
                if (this.worldObj == null)
                    continue;
                DartUtils.dropItem(
                    (ItemStack) previous.get(i),
                    this.worldObj,
                    this.xCoord,
                    this.yCoord,
                    this.zCoord
                );
            }
        }
    }

    public void setCheck() {
        this.shouldCheck = true;
    }

    @Override
    public int getSizeInventory() {
        if (this.ender && this.getEnderInventory() != null) {
            return EnderInventory.SIZE;
        }

        return new int[] { 27, 54, 72, 108 }[this.tier];
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.sturdy = data.getBoolean("sturdy");
        this.ender = data.getBoolean("ender");
        this.tier = data.getInteger("tier");
        this.setTier(this.tier);
        if (data.hasKey("lastColor")) {
            this.lastColor = data.getInteger("lastColor");
        }
        if (data.hasKey("lastOwner")) {
            this.lastOwner = AnvilUtil.uuidFromNBT(data.getTagList("lastOwner", 4));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("sturdy", this.sturdy);
        data.setBoolean("ender", this.ender);
        data.setInteger("tier", this.tier);
        if (this.lastColor >= 0) {
            data.setInteger("lastColor", this.color);
        }
        if (this.lastOwner != null) {
            data.setTag("lastOwner", AnvilUtil.uuidToNBT(this.lastOwner));
        }
    }

    public ItemStack getDropStack() {
        ItemStack stack = new ItemStack(DartBlocks.storageunit, 1, this.color);
        stack.setTagCompound(new NBTTagCompound());
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            ItemStack invStack = this.getStackInSlot(i);
            if (/*Config.wrenchAll ||*/ invStack == null
                || DartUtils.isAllowedInPack(invStack)
                    && (invStack.getItem()
                            != Item.getItemFromBlock(DartBlocks.storageunit)
                        || invStack.getItemDamage() < 0
                        || invStack.getItemDamage() >= 16))
                continue;
            drops.add(invStack.copy());
            this.setInventorySlotContents(i, null);
        }
        if (drops.size() > 0) {
            for (ItemStack dropStack : drops) {
                DartUtils.dropInvincibleItem(
                    dropStack, this.worldObj, this.xCoord, this.yCoord, this.zCoord, 6000
                );
            }
        }
        this.writeToNBT(stack.getTagCompound());
        return stack;
    }

    @Override
    @SuppressWarnings({ "unchecked", "ALEC" })
    public void updateEntity() {
        float modifier;
        if (Dartcraft.proxy.isSimulating(this.worldObj)) {
            ++this.timeout;
            if (this.timeout >= 10) {
                if (this.shouldCheck) {
                    this.shouldCheck = false;
                    this.checkCards();
                    this.runCardLogic();
                }
                this.timeout = 0;
            }
        }
        ++this.ticksSinceSync;
        if (Dartcraft.proxy.isSimulating(this.worldObj) && this.numUsingPlayers != 0
            && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200
                == 0) {
            this.numUsingPlayers = 0;
            modifier = 5.0f;
            List<EntityPlayer> players
                = (List<EntityPlayer>) this.worldObj.getEntitiesWithinAABB(
                    EntityPlayer.class,
                    AxisAlignedBB.getBoundingBox(
                        (double) ((float) this.xCoord - modifier),
                        (double) ((float) this.yCoord - modifier),
                        (double) ((float) this.zCoord - modifier),
                        (double) ((float) (this.xCoord + 1) + modifier),
                        (double) ((float) (this.yCoord + 1) + modifier),
                        (double) ((float) (this.zCoord + 1) + modifier)
                    )
                );
            for (EntityPlayer check : players) {
                TileEntityStorageUnit frame;
                if (!(check.openContainer instanceof ContainerStorageUnit)
                    || (frame = ((ContainerStorageUnit) check.openContainer).storage)
                        != this)
                    continue;
                ++this.numUsingPlayers;
                break;
            }
        }
        modifier = 0.1f;
        this.prevLidAngle = this.lidAngle;
        if (this.numUsingPlayers > 0 && this.lidAngle == 0.0f
            && Dartcraft.proxy.isSimulating(this.worldObj)) {
            this.worldObj.playSoundEffect(
                (double) this.xCoord + 0.5,
                (double) this.yCoord + 0.5,
                (double) this.zCoord + 0.5,
                "dartcraft:openChest",
                0.5f,
                DartUtils.randomPitch()
            );
            new WorldVec(this).markForUpdate();
        }
        if (this.numUsingPlayers == 0 && this.lidAngle > 0.0f
            || this.numUsingPlayers > 0 && this.lidAngle < 1.0f) {
            float var10;
            float var9 = this.lidAngle;
            this.lidAngle = this.numUsingPlayers > 0 ? (this.lidAngle += modifier)
                                                     : (this.lidAngle -= modifier);
            if (this.lidAngle > 1.0f) {
                this.lidAngle = 1.0f;
            }
            if (this.lidAngle < (var10 = 0.5f) && var9 >= var10) {
                this.worldObj.playSoundEffect(
                    (double) this.xCoord + 0.53,
                    (double) this.yCoord + 0.5,
                    (double) this.zCoord + 0.5,
                    "dartcraft:closeChest",
                    0.2f,
                    DartUtils.randomPitch()
                );
            }
            if (Dartcraft.proxy.isSimulating(this.worldObj)) {
                new WorldVec(this).markForUpdate();
            }
            if (this.lidAngle < 0.0f) {
                this.lidAngle = 0.0f;
            }
        }
    }

    public void checkCards() {
        if (!Dartcraft.proxy.isSimulating(this.worldObj)) {
            return;
        }
        this.cards.clear();
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            ItemStack checkStack = this.getStackInSlot(i);
            // TODO: mitgliedskarte
            if (checkStack == null
                || true /*|| checkStack.itemID != DartItem.memberCard.itemID*/)
                continue;
            NBTTagCompound upgrades = UpgradeHelper.getUpgradeCompound(checkStack);
            ItemInventory inv = new ItemInventory(16, checkStack);
            ArrayList<ItemStack> filter = new ArrayList<ItemStack>();
            for (int j = 0; j < inv.getSizeInventory(); ++j) {
                ItemStack invStack = inv.getStackInSlot(j);
                if (invStack == null)
                    continue;
                filter.add(invStack);
            }
            if (upgrades.hasKey("Force")) {
                if (this.cards.get("Force") == null) {
                    this.cards.put("Force", filter);
                    continue;
                }
                ArrayList<ItemStack> temp = this.cards.get("Force");
                temp.addAll(filter);
                continue;
            }
            if (upgrades.hasKey("Craft")) {
                if (this.cards.get("Craft") == null) {
                    this.cards.put("Craft", new ArrayList<>());
                }
                ArrayList<ItemStack> temp = this.cards.get("Craft");
                temp.add(checkStack);
                continue;
            }
            if (upgrades.hasKey("Forge")) {
                if (this.cards.get("Forge") == null) {
                    this.cards.put("Forge", filter);
                    continue;
                }
                ArrayList<ItemStack> temp = this.cards.get("Forge");
                temp.addAll(filter);
                continue;
            }
            if (upgrades.hasKey("Heat")) {
                if (this.cards.get("Heat") == null) {
                    this.cards.put("Heat", filter);
                    continue;
                }
                ArrayList<ItemStack> temp = this.cards.get("Heat");
                temp.addAll(filter);
                continue;
            }
            if (this.cards.get("None") == null) {
                this.cards.put("None", filter);
                continue;
            }
            ArrayList<ItemStack> temp = this.cards.get("None");
            temp.addAll(filter);
        }
    }

    public void runCardLogic() {
        //    boolean burnt;
        //    boolean changed;
        //block32: {
        //    ItemStack invStack;
        //    int j;
        //    ItemStack checkStack;
        //    int i;
        //block31: {
        //    if (!Dartcraft.proxy.isSimulating(this.worldObj)) {
        //        return;
        //    }
        //    if (this.cards == null || this.cards.size() == 0) {
        //        return;
        //    }
        //    changed = false;
        //    burnt = false;
        //    try {
        //        if (this.cards.get("Force") == null)
        //            break block31;
        //        ArrayList<ItemStack> force = this.cards.get("Force");
        //        for (i = 0; i < force.size(); ++i) {
        //            checkStack = force.get(i);
        //            if (checkStack == null)
        //                continue;
        //            for (j = 0; j < this.getSizeInventory(); ++j) {
        //                ItemStack output;
        //                IForceTransmutation trans;
        //                invStack = this.getStackInSlot(j);
        //                if (invStack == null || !CardHandler.itemMatches(checkStack,
        //                invStack)
        //                    || (trans = DartPluginForceTrans.getTransmutable(invStack))
        //                        == null)
        //                    continue;
        //                ItemStack itemStack = output
        //                    = trans.getOutput() != null ? trans.getOutput().copy() :
        //                    null;
        //                if (output == null)
        //                    continue;
        //                for (int size = invStack.stackSize; size > 0; --size) {
        //                    this.decrStackSize(j, 1);
        //                    changed = true;
        //                    ItemStack buffer = output.copy();
        //                    boolean added = DartUtils.addItemStackToInventory(this,
        //                    buffer); if (added || buffer == null || buffer.stackSize <=
        //                    0)
        //                        continue;
        //                    DartUtils.dropItem(
        //                        buffer, this.worldObj, this.xCoord, this.yCoord,
        //                        this.zCoord
        //                    );
        //                    this.makeShiny(false);
        //                    this.shouldCheck = false;
        //                    return;
        //                }
        //            }
        //        }
        //    } catch (Exception e) {
        //        break block32;
        //    }
        //}
        //    if (this.cards.get("Heat") != null) {
        //        ArrayList<ItemStack> heat = this.cards.get("Heat");
        //        for (i = 0; i < heat.size(); ++i) {
        //            checkStack = heat.get(i);
        //            if (checkStack == null)
        //                continue;
        //            for (j = 0; j < this.getSizeInventory(); ++j) {
        //                invStack = this.getStackInSlot(j);
        //                if (invStack == null
        //                    || !OreDictionary.itemMatches(
        //                        (ItemStack) checkStack, (ItemStack) invStack, (boolean)
        //                        true
        //                    ))
        //                    continue;
        //                this.setInventorySlotContents(j, null);
        //                burnt = true;
        //            }
        //        }
        //    }
        //    if (this.cards.get("Forge") != null) {
        //        ArrayList<ItemStack> forge = this.cards.get("Forge");
        //        for (i = 0; i < forge.size(); ++i) {
        //            checkStack = forge.get(i);
        //            if (checkStack == null)
        //                continue;
        //            int id = OreDictionary.getOreID((ItemStack) checkStack);
        //            for (int j2 = 0; j2 < this.getSizeInventory(); ++j2) {
        //                ItemStack invStack2 = this.getStackInSlot(j2);
        //                if (invStack2 == null
        //                    || OreDictionary.getOreID((ItemStack) invStack2) != id
        //                    || OreDictionary.itemMatches(
        //                        (ItemStack) checkStack, (ItemStack) invStack2, (boolean)
        //                        true
        //                    ))
        //                    continue;
        //                invStack2.itemID = checkStack.itemID;
        //                invStack2.setItemDamage(checkStack.getItemDamage());
        //                changed = true;
        //            }
        //        }
        //    }
        //    if (this.cards.get("Craft") == null)
        //        break block32;
        //    ArrayList<ItemStack> craft = this.cards.get("Craft");
        //block9:
        //    for (int k = 0; k < craft.size(); ++k) {
        //        ItemStack cardStack = craft.get(k);
        //        if (cardStack == null)
        //            continue;
        //        ItemInventory cardInv = new ItemInventory(9, cardStack);
        //        ItemStack res = null;
        //        InventoryCrafting inv
        //            = new InventoryCrafting((Container) new CraftingContainer(), 3, 3);
        //        for (int j3 = 0; j3 < 9; ++j3) {
        //            inv.setInventorySlotContents(j3, cardInv.getStackInSlot(j3));
        //        }
        //        res = CraftingManager.getInstance().findMatchingRecipe(inv,
        //        this.worldObj); if (res == null)
        //            break;
        //        InventoryBasic tempInv
        //            = new InventoryBasic("temp", false, this.getSizeInventory());
        //        for (int j4 = 0; j4 < this.getSizeInventory(); ++j4) {
        //            if (this.getStackInSlot(j4) != null) {
        //                tempInv.setInventorySlotContents(j4,
        //                this.getStackInSlot(j4).copy()); continue;
        //            }
        //            tempInv.setInventorySlotContents(j4, (ItemStack) null);
        //        }
        //    block12:
        //        while (true) {
        //            ItemStack tempStack;
        //            int i2 = 0;
        //            while (true) {
        //                if (i2 >= 9)
        //                    break;
        //                tempStack = cardInv.getStackInSlot(i2);
        //                if (tempStack != null) {
        //                    boolean found = false;
        //                    for (int j5 = 0; j5 < tempInv.getSizeInventory(); ++j5) {
        //                        ItemStack checkStack2 = tempInv.getStackInSlot(j5);
        //                        if (checkStack2 == null
        //                            || !ItemInventoryUtils.isItemEquivalent(
        //                                tempStack, checkStack2
        //                            ))
        //                            continue;
        //                        ItemStack contains =
        //                        DartUtils.getContainerItem(checkStack2);
        //                        tempInv.decrStackSize(j5, 1);
        //                        if (tempInv.getStackInSlot(j5) != null
        //                            && tempInv.getStackInSlot((int) j5).stackSize <= 0)
        //                            { tempInv.setInventorySlotContents(j5, (ItemStack)
        //                            null);
        //                        }
        //                        if (tempInv.getStackInSlot(j5) == null && contains !=
        //                        null) {
        //                            tempInv.setInventorySlotContents(j5, contains);
        //                        }
        //                        found = true;
        //                        break;
        //                    }
        //                    if (!found)
        //                        continue block9;
        //                }
        //                ++i2;
        //            }
        //            i2 = 0;
        //            while (true) {
        //            block34: {
        //            block35: {
        //            block33: {
        //                if (i2 >= 9)
        //                    break block33;
        //                tempStack = cardInv.getStackInSlot(i2);
        //                if (tempStack == null)
        //                    break block34;
        //                break block35;
        //            }
        //                changed = true;
        //                ItemStack finalStack = res.copy();
        //                if (DartUtils.addItemStackToInventory(this, finalStack))
        //                    continue block12;
        //                DartUtils.dropItem(
        //                    finalStack, this.worldObj, this.xCoord, this.yCoord,
        //                    this.zCoord
        //                );
        //                continue block12;
        //            }
        //                for (int j6 = 0; j6 < this.getSizeInventory(); ++j6) {
        //                    ItemStack checkStack3 = this.getStackInSlot(j6);
        //                    if (checkStack3 == null
        //                        || !ItemInventoryUtils.isItemEquivalent(
        //                            tempStack, checkStack3
        //                        ))
        //                        continue;
        //                    ItemStack contains =
        //                    DartUtils.getContainerItem(checkStack3);
        //                    this.decrStackSize(j6, 1);
        //                    if (this.getStackInSlot(j6) != null
        //                        && this.getStackInSlot((int) j6).stackSize <= 0) {
        //                        this.setInventorySlotContents(j6, null);
        //                    }
        //                    if (this.getStackInSlot(j6) != null || contains == null)
        //                        break;
        //                    this.setInventorySlotContents(j6, contains);
        //                    break;
        //                }
        //            }
        //                ++i2;
        //            }
        //            break;
        //        }
        //    }
        //}
        //    if (changed) {
        //        this.makeShiny(false);
        //    }
        //    if (burnt) {
        //        this.makeShiny(true);
        //    }
        //    int i = 0;
        //    while (i < this.getSizeInventory()) {
        //        ItemStack tempStack = this.getStackInSlot(i);
        //        if (tempStack != null
        //            && (tempStack.stackSize <= 0
        //                || tempStack.getMaxDamage() > 0 && tempStack.getItemDamage() > 0
        //                    && tempStack.getItemDamage() > tempStack.getMaxDamage())) {
        //            this.setInventorySlotContents(i, null);
        //        }
        //        ++i;
        //    }
        //    return;
    }

    private void makeShiny(boolean burn) {
        if (Dartcraft.proxy.isSimulating(this.worldObj)) {
            if (burn) {
                this.worldObj.playSoundEffect(
                    (double) this.xCoord,
                    (double) this.yCoord,
                    (double) this.zCoord,
                    "dartcraft:ignite",
                    1.0f,
                    DartUtils.randomPitch()
                );
                WorldVec pktPos = new WorldVec(this);
                Dartcraft.channel.sendToAllAround(
                    new PacketFX(pktPos, PacketFX.Type.HEAT, 0, 0, 8),
                    pktPos.targetPoint(80d)
                );
            } else {
                this.worldObj.playSoundEffect(
                    (double) this.xCoord,
                    (double) this.yCoord,
                    (double) this.zCoord,
                    "dartcraft:magic",
                    1.0f,
                    DartUtils.randomPitch()
                );

                WorldVec pktPos = new WorldVec(this);
                Dartcraft.channel.sendToAllAround(
                    new PacketFX(pktPos, PacketFX.Type.MAGIC, 2, 0, 32),
                    pktPos.targetPoint(80d)
                );
            }
        }
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack) {
        if (this.ender) {
            EnderInventory einv = this.getEnderInventory();
            if (einv != null)
                einv.setInventorySlotContents(i, stack);
        }
        super.setInventorySlotContents(i, stack);
        this.setCheck();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.enderInv != null) {
            this.enderInv.markDirty();
        }
        this.setCheck();
    }

    @Override
    public void openInventory() {
        if (this.worldObj != null && Dartcraft.proxy.isSimulating(this.worldObj)) {
            ++this.numUsingPlayers;
        }
    }

    @Override
    public void closeInventory() {
        if (this.worldObj != null && Dartcraft.proxy.isSimulating(this.worldObj)) {
            --this.numUsingPlayers;
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger("numPlayers", this.numUsingPlayers);
        nbt.setInteger("tier", this.tier);
        nbt.setTag("owner", AnvilUtil.uuidToNBT(this.owner));
        nbt.setByte("color", (byte) this.color);
        nbt.setBoolean("ender", this.ender);
        nbt.setByte("facing", (byte) this.facing.ordinal());

        return new S35PacketUpdateTileEntity(
            this.xCoord, this.yCoord, this.zCoord, this.getBlockMetadata(), nbt
        );
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound nbt = pkt.func_148857_g();
        this.numUsingPlayers = nbt.getInteger("numPlayers");
        this.setTier(nbt.getInteger("tier"));
        this.owner = AnvilUtil.uuidFromNBT(nbt.getTagList("owner", 4));
        this.color = nbt.getByte("color");
        this.ender = nbt.getBoolean("ender");
        this.facing = ForgeDirection.getOrientation(nbt.getByte("facing"));
    }

    public EnderInventory getEnderInventory() {
        try {
            this.enderInv = EnderStorageHandler.INSTANCE.getInventory(
                this.getOwner(), this.color, !Dartcraft.proxy.isSimulating(this.worldObj)
            );
            return this.enderInv;
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (this.ender) {
            EnderInventory einv = this.getEnderInventory();
            if (einv != null)
                return einv.getStackInSlot(slot);
        }
        return super.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        this.setCheck();
        if (this.ender) {
            EnderInventory einv = this.getEnderInventory();
            if (einv != null)
                return einv.decrStackSize(slot, amt);
        }
        return super.decrStackSize(slot, amt);
    }
}
