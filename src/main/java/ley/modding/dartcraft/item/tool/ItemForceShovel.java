package ley.modding.dartcraft.item.tool;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IBreakable;
import ley.modding.dartcraft.api.IForceConsumer;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.util.ForceConsumerUtils;
import ley.modding.dartcraft.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

public class ItemForceShovel extends ItemSpade implements IBreakable, IForceConsumer {
    private static int damage = 1;
    private static float efficiency = 5.0F;
    private static int toolLevel = 10;
    public static ToolMaterial material = EnumHelper.addToolMaterial(
        "FORCE", toolLevel, 512, efficiency, (float) damage, 0
    );

    public ItemForceShovel() {
        super(material);
        Util.configureItem(this, "forceshovel");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setBoolean(
            "active", !stack.getTagCompound().getBoolean("active")
        );
        if (!Dartcraft.proxy.isSimulating(world)) {
            if (stack.getTagCompound().getBoolean("active")) {
                Dartcraft.proxy.sendChatToPlayer(player, "Area mode activated.");
            } else {
                Dartcraft.proxy.sendChatToPlayer(player, "Area mode deactivated");
            }
        }

        return stack;
    }

    @Override
    public int getStored(ItemStack stack) {
        return ForceConsumerUtils.getStoredForce(stack);
    }

    @Override
    public int getMaxStored(ItemStack var1) {
        return 10000;
    }

    @Override
    public int amountUsedBase(ItemStack var1) {
        return 0;
    }

    @Override
    public boolean useForce(ItemStack var1, int var2, boolean var3) {
        return false;
    }

    @Override
    public boolean attemptRepair(ItemStack stack) {
        return ForceConsumerUtils.attemptRepair(stack);
    }

    @Override
    public ItemStack itemReturned() {
        return new ItemStack(DartItems.forceshard);
    }

    @Override
    public boolean canHarvestBlock(Block block, ItemStack itemStack) {
        return block.getMaterial() == Material.clay
            || block.getMaterial() == Material.craftedSnow
            || block.getMaterial() == Material.ground
            || block.getMaterial() == Material.sand
            || block.getMaterial() == Material.snow
            || block.getMaterial() == Material.grass;
    }

    @Override
    public boolean
    onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        World world = player.worldObj;
        Block tempBlock = world.getBlock(x, y, z);
        boolean force = false;

        if (!this.canHarvestBlock(tempBlock, stack)) {
            return false;
        } else {
            if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("active")) {
                force = true;
            }

            if (force) {
                for (int i = -1; i < 2; ++i) {
                    for (int j = -1; j < 2; ++j) {
                        for (int k = -1; k < 2; ++k) {
                            if (i != 0 || j != 0 || k != 0) {
                                if (stack == null
                                    || stack.getItemDamage() >= stack.getMaxDamage()) {
                                    return false;
                                }

                                this.tryBlock(stack, x + i, y + j, z + k, player);
                            }
                        }
                    }
                }
            }

            if (stack != null && stack.getItemDamage() < stack.getMaxDamage())
                this.tryBlock(stack, x, y, z, player);

            return false;
        }
    }

    public void tryBlock(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        World world = player.worldObj;
        Block block = world.getBlock(x, y, z);

        if (this.canHarvestBlock(block, stack)
            && block.getBlockHardness(world, x, y, z) > 0F
                & world.getTileEntity(x, y, z) == null) {
            world.getBlock(x, y, z).harvestBlock(
                world, player, x, y, z, world.getBlockMetadata(x, y, z)
            );
            world.setBlockToAir(x, y, z);
            stack.damageItem(1, player);
        }
    }
}
