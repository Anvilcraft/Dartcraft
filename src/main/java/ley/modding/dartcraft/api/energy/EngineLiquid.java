package ley.modding.dartcraft.api.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class EngineLiquid {
    public static final int TYPE_FUEL = 0;

    public static final int TYPE_THROTTLE = 1;

    protected Fluid fluid;

    protected int burnTime;

    protected int type;

    protected NBTTagCompound comp;

    protected float modifier = 1.0F;

    public EngineLiquid(FluidStack liquid, int type, int burnTime, float modifier) {
        this.fluid = liquid.getFluid();
        this.comp = liquid.tag;
        this.type = type;
        this.burnTime = burnTime;
        this.modifier = modifier;
    }

    public int getType() {
        return this.type;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public float getModifier() {
        return this.modifier;
    }

    public FluidStack getLiquid() {
        return new FluidStack(this.fluid, 1000);
    }
}
