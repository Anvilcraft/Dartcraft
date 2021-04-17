package ley.modding.dartcraft.block;

import ley.modding.tileralib.api.IRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class DartBlocks {
    public static Block powerore;
    public static Block forcesapling;
    public static Block forcelog;
    public static Block forceleaves;
    public static Block engine;
    public static Block liquidforce;

    public static void register(IRegistry reg) {
        FluidRegistry.registerFluid(new FluidLiquidForce());
        if (!FluidRegistry.isFluidRegistered("milk")) {
            Fluid milk = new Fluid("milk");
            FluidRegistry.registerFluid(milk);
        }
        DartBlocks.liquidforce = reg.registerBlock(new BlockLiquidForce());
        DartBlocks.forcesapling = reg.registerBlock(new BlockForceSapling());
        DartBlocks.powerore = reg.registerBlock(new BlockPowerOre());
        DartBlocks.forcelog = reg.registerBlock(new BlockForceLog());
        DartBlocks.forceleaves = reg.registerBlock(new BlockForceLeaves());
        DartBlocks.engine = reg.registerBlock(new BlockForceEngine());
    }
}
