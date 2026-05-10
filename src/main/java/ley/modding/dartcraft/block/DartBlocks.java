package ley.modding.dartcraft.block;

import java.util.stream.IntStream;

import ley.modding.tileralib.api.IRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class DartBlocks {
    public static Block engine;
    public static Block[] forcebrick;
    public static Block forceleaves;
    public static Block forcelog;
    public static Block forceplanks;
    public static Block forcesapling;
    public static BlockSlab[] forceslab;
    public static BlockSlab[] forceslabDouble;
    public static Block[] forcestairs;
    public static Block forcetorch;
    public static Block infuser;
    public static Block liquidforce;
    public static Block powerore;
    public static Block storageunit;

    public static void register(IRegistry reg) {
        FluidRegistry.registerFluid(new FluidLiquidForce());
        if (!FluidRegistry.isFluidRegistered("milk")) {
            Fluid milk = new Fluid("milk");
            FluidRegistry.registerFluid(milk);
        }

        engine = reg.registerBlock(new BlockForceEngine());
        forcebrick = IntStream.range(0, 16)
                         .mapToObj(BlockForceBrick::new)
                         .map(reg::registerBlock)
                         .toArray(Block[] ::new);
        forceleaves = reg.registerBlock(new BlockForceLeaves());
        forcelog = reg.registerBlock(new BlockForceLog());
        forceplanks = reg.registerBlock(new BlockForcePlanks());
        forcesapling = reg.registerBlock(new BlockForceSapling());
        forceslab = IntStream.range(0, 17)
                        .mapToObj(i -> new BlockForceSlab(false, i))
                        .map(reg::registerBlock)
                        .map(b -> (BlockSlab) b)
                        .toArray(BlockSlab[] ::new);
        forceslabDouble = IntStream.range(0, 17)
                              .mapToObj(i -> new BlockForceSlab(true, i))
                              .map(reg::registerBlock)
                              .map(b -> (BlockSlab) b)
                              .toArray(BlockSlab[] ::new);
        forcestairs = IntStream.range(0, 17)
                          .mapToObj(BlockForceStairs::new)
                          .map(reg::registerBlock)
                          .toArray(Block[] ::new);
        forcetorch = reg.registerBlock(new BlockForceTorch());
        infuser = reg.registerBlock(new BlockInfuser());
        liquidforce = reg.registerBlock(new BlockLiquidForce());
        powerore = reg.registerBlock(new BlockPowerOre());
        storageunit = reg.registerBlock(new BlockStorageUnit());
    }
}
