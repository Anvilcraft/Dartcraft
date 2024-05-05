package ley.modding.dartcraft.api.recipe;

import ley.modding.dartcraft.api.upgrades.ForceUpgrade;
import ley.modding.dartcraft.tile.TileEntityInfuser;
import net.minecraft.item.ItemStack;

public class ForceWildCard {
    private ItemStack input;
    private ItemStack output;
    private boolean ignoreMeta;
    private ForceUpgrade[] validUpgrades;

    public ForceWildCard(ItemStack input, ForceUpgrade[] upgrades) {
        this.input = this.prepareStack(input);
        this.validUpgrades = upgrades;
        this.output = null;
    }

    public ForceWildCard(ItemStack input, ItemStack output, ForceUpgrade upgrade) {
        this.input = this.prepareStack(input);
        this.output = output;
        this.validUpgrades = new ForceUpgrade[] { upgrade };
    }

    private ItemStack prepareStack(ItemStack stack) {
        return stack != null ? new ItemStack(stack.getItem(), 1, stack.getItemDamage())
                             : null;
    }

    public ForceWildCard setIgnoresMeta() {
        this.ignoreMeta = true;
        return this;
    }

    public ItemStack getInput() {
        return this.input;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public ForceUpgrade[] getUpgrades() {
        return this.validUpgrades;
    }

    public boolean hasOutput() {
        return this.output != null;
    }

    public boolean ignoreMeta() {
        return this.ignoreMeta;
    }

    public void onProcess(TileEntityInfuser infuser) {}

    //if (Loader.isModLoaded("IC2")
    //    && IC2Integration.DIAMOND_DRILL_ITEM.getItem()
    //        == wildcard.getInput().getItem()
    //    && Dartcraft.proxy.isSimulating(this.worldObj)) {
    //    DartUtils.dropItem(
    //        new ItemStack(Items.diamond, 3),
    //        this.worldObj,
    //        (double) this.xCoord,
    //        (double) this.yCoord,
    //        (double) this.zCoord
    //    );
    //}
}
