package ley.modding.dartcraft.block;

import java.util.stream.IntStream;

import ley.modding.tileralib.api.IRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class DartBlocks {
    public static Block engine;
    public static Block[] forcebrick;
    public static Block forceleaves;
    public static Block forcelog;
    public static Block forceplanks;
    public static Block forcesapling;
    public static Block[] forceslab;
    public static Block[] forcestairs;
    public static Block forcetorch;
    public static Block liquidforce;
    public static Block powerore;

    public static void register(IRegistry reg) {
        FluidRegistry.registerFluid(new FluidLiquidForce());
        if (!FluidRegistry.isFluidRegistered("milk")) {
            Fluid milk = new Fluid("milk");
            FluidRegistry.registerFluid(milk);
        }

        DartBlocks.engine = reg.registerBlock(new BlockForceEngine());
        DartBlocks.forcebrick = IntStream.range(0, 16)
                                    .mapToObj(BlockForceBrick::new)
                                    .map(reg::registerBlock)
                                    .toArray(Block[] ::new);
        DartBlocks.forceleaves = reg.registerBlock(new BlockForceLeaves());
        DartBlocks.forcelog = reg.registerBlock(new BlockForceLog());
        DartBlocks.forceplanks = reg.registerBlock(new BlockForcePlanks());
        DartBlocks.forcesapling = reg.registerBlock(new BlockForceSapling());
        DartBlocks.forceslab = IntStream.range(0, 17)
                                   .mapToObj(BlockForceSlab::new)
                                   .map(reg::registerBlock)
                                   .toArray(Block[] ::new);
        DartBlocks.forcestairs = IntStream.range(0, 17)
                                     .mapToObj(BlockForceStairs::new)
                                     .map(reg::registerBlock)
                                     .toArray(Block[] ::new);
        DartBlocks.forcetorch = reg.registerBlock(new BlockForceTorch());
        DartBlocks.liquidforce = reg.registerBlock(new BlockLiquidForce());
        DartBlocks.powerore = reg.registerBlock(new BlockPowerOre());
    }
}
