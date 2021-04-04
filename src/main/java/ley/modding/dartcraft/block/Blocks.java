package ley.modding.dartcraft.block;

import ley.modding.tileralib.api.IRegistry;
import net.minecraft.block.Block;

public class Blocks {
    public static Block powerore;
    public static Block forcesapling;
    public static Block forcelog;
    public static Block forceleaves;

    public static void register(IRegistry reg) {
        Blocks.forcesapling = reg.registerBlock(new BlockForceSapling());
        Blocks.powerore = reg.registerBlock(new BlockPowerOre());
        Blocks.forcelog = reg.registerBlock(new BlockForceLog());
        Blocks.forceleaves = reg.registerBlock(new BlockForceLeaves());
    }
}
