package ley.modding.dartcraft.block;

import ley.modding.tileralib.api.IRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class DartBlocks {
    public static Block engine;
    public static Block forcebrick;
    public static Block forceleaves;
    public static Block forcelog;
    public static Block forcesapling;
    public static Block liquidforce;
    public static Block powerore;

    public static void register(IRegistry reg) {
        FluidRegistry.registerFluid(new FluidLiquidForce());
        if (!FluidRegistry.isFluidRegistered("milk")) {
            Fluid milk = new Fluid("milk");
            FluidRegistry.registerFluid(milk);
        }

        DartBlocks.engine = reg.registerBlock(new BlockForceEngine());
        DartBlocks.forcebrick = reg.registerBlock(new BlockForceBrick());
        DartBlocks.forceleaves = reg.registerBlock(new BlockForceLeaves());
        DartBlocks.forcelog = reg.registerBlock(new BlockForceLog());
        DartBlocks.forcesapling = reg.registerBlock(new BlockForceSapling());
        DartBlocks.liquidforce = reg.registerBlock(new BlockLiquidForce());
        DartBlocks.powerore = reg.registerBlock(new BlockPowerOre());
    }
}
