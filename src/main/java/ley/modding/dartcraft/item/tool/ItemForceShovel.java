package ley.modding.dartcraft.item.tool;

import ley.modding.dartcraft.Dartcraft;
import ley.modding.dartcraft.api.IBreakable;
import ley.modding.dartcraft.api.IForceConsumer;
import ley.modding.dartcraft.item.DartItems;
import ley.modding.dartcraft.util.ForceConsumerUtils;
import ley.modding.dartcraft.util.Util;
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
    public static ToolMaterial material = EnumHelper.addToolMaterial("FORCE", toolLevel, 512, efficiency, (float) damage, 0);

	public ItemForceShovel() {
		super(material);
        Util.configureItem(this, "forceshovel");
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
}
