package ley.modding.dartcraft.item.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IBreakable;
import ley.modding.dartcraft.api.IForceConsumer;
import ley.modding.dartcraft.api.upgrades.IForceUpgradable;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.util.ForceConsumerUtils;
import ley.modding.dartcraft.util.ForceUpgradeManager;
import ley.modding.dartcraft.util.Util;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

public class ItemForcePickaxe extends ItemPickaxe implements IBreakable, IForceConsumer, IForceUpgradable {

    private static int damage = 1;
    private static float efficiency = 5.0F;
    private static int toolLevel = 10;
    public static ToolMaterial material = EnumHelper.addToolMaterial("FORCE", toolLevel, 512, efficiency, (float) damage, 0);

    private IIcon heatPick;

    public ItemForcePickaxe() {
        super(material);
        Util.configureItem(this, "forcepickaxe");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setBoolean("active", !stack.getTagCompound().getBoolean("active"));
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
    public ItemStack itemReturned() {
        return new ItemStack(DartItems.forceshard);
    }



    public IIcon getIcon(ItemStack stack, int pass) {
        if(stack.hasTagCompound()) {
            NBTTagCompound upgrades = stack.getTagCompound().getCompoundTag("upgrades");
            if(upgrades != null && upgrades.hasKey("heat")) {
                return heatPick;
            }
        }
        return itemIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        if(stack.hasTagCompound() && stack.getTagCompound().hasKey("upgrades")) {
            NBTTagCompound upgrades = stack.getTagCompound().getCompoundTag("upgrades");
            if(upgrades.hasKey("Heat")) {
                return EnumRarity.uncommon;
            }
        }

        return EnumRarity.common;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        heatPick = reg.registerIcon(Dartcraft.MODID + ":heatpickaxe");
        super.registerIcons(reg);
    }

    @Override
    public int getStored(ItemStack stack) {
        return ForceConsumerUtils.getStoredForce(stack);
    }

    @Override
    public int getMaxStored(ItemStack stack) {
        return 10000;
    }

    @Override
    public int amountUsedBase(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean useForce(ItemStack stack, int var2, boolean var3) {
        return ForceConsumerUtils.useForce(stack, var2, var3);
    }

    @Override
    public boolean attemptRepair(ItemStack stack) {
        return ForceConsumerUtils.attemptRepair(stack);
    }

    public int[] validUpgrades()
    {
        return new int[] { ForceUpgradeManager.HEAT.getID(), ForceUpgradeManager.SPEED.getID(), ForceUpgradeManager.LUCK.getID(), ForceUpgradeManager.TOUCH.getID(), ForceUpgradeManager.STURDY.getID(), ForceUpgradeManager.GRINDING.getID(), ForceUpgradeManager.REPAIR.getID(), ForceUpgradeManager.IMPERVIOUS.getID() };
    }

}
