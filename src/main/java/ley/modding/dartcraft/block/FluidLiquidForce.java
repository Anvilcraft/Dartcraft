package ley.modding.dartcraft.block;

import net.minecraftforge.fluids.Fluid;

public class FluidLiquidForce extends Fluid {
    public FluidLiquidForce() {
        super("liquidForce");
        this.luminosity = 15;
        this.viscosity = 6000;
        this.density = 200;
    }

    public String getLocalizedName() {
        return "Liquid Force";
    }
}
