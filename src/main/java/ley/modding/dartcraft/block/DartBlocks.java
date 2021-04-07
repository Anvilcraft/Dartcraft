package ley.modding.dartcraft.block;

import ley.modding.tileralib.api.IRegistry;
import net.minecraft.block.Block;

public class DartBlocks {
    public static Block powerore;
    public static Block forcesapling;
    public static Block forcelog;
    public static Block forceleaves;

    public static void register(IRegistry reg) {
        DartBlocks.forcesapling = reg.registerBlock(new BlockForceSapling());
        DartBlocks.powerore = reg.registerBlock(new BlockPowerOre());
        DartBlocks.forcelog = reg.registerBlock(new BlockForceLog());
        DartBlocks.forceleaves = reg.registerBlock(new BlockForceLeaves());
    }
}
