package ley.modding.dartcraft.worldgen;

import ley.modding.dartcraft.block.DartBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class GenForceTree extends WorldGenAbstractTree {
    /** The minimum height of a generated tree. */
    private final int minTreeHeight;
    /** The metadata value of the wood to use in tree generation. */
    private final int metaWood;
    /** The metadata value of the leaves to use in tree generation. */
    private final int metaLeaves;

    public GenForceTree(boolean doBlockNotify) {
        this(doBlockNotify, 4, 0, 0);
    }

    public GenForceTree(boolean doBlockNotify, int minTreeHeight, int metaWood, int metaLeaves) {
        super(doBlockNotify);
        this.minTreeHeight = minTreeHeight;
        this.metaWood = metaWood;
        this.metaLeaves = metaLeaves;
    }

    public boolean generate(World world, Random rand, int x, int y, int z) {
        int treeHeight = rand.nextInt(3) + this.minTreeHeight;
        boolean flag = true;

        // check if inside build limit
        if (y >= 1 && y + treeHeight + 1 <= 256) {
            byte b0;
            int k1;
            Block block;

            for (int currentLogY = y; currentLogY <= y + 1 + treeHeight; ++currentLogY) {
                b0 = 1;

                if (currentLogY == y) {
                    b0 = 0;
                }

                if (currentLogY >= y + 1 + treeHeight - 2) {
                    b0 = 2;
                }

                for (int j1 = x - b0; j1 <= x + b0 && flag; ++j1) {
                    for (k1 = z - b0; k1 <= z + b0 && flag; ++k1) {
                        if (currentLogY >= 0 && currentLogY < 256) {
                            block = world.getBlock(j1, currentLogY, k1);

                            if (!this.isReplaceable(world, j1, currentLogY, k1)) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                Block block2 = world.getBlock(x, y - 1, z);

                boolean isSoil =
                    block2.canSustainPlant(

                            world, x, y - 1, z, ForgeDirection.UP, (BlockSapling) DartBlocks.forcesapling);
                if (isSoil && y < 256 - treeHeight - 1) {
                    block2.onPlantGrow(world, x, y - 1, z, x, y, z);
                    b0 = 3;
                    byte b1 = 0;
                    int l1;
                    int i2;
                    int j2;
                    int i3;

                    for (k1 = y - b0 + treeHeight; k1 <= y + treeHeight; ++k1) {
                        i3 = k1 - (y + treeHeight);
                        l1 = b1 + 1 - i3 / 2;

                        for (i2 = x - l1; i2 <= x + l1; ++i2) {
                            j2 = i2 - x;

                            for (int k2 = z - l1; k2 <= z + l1; ++k2) {
                                int l2 = k2 - z;

                                if (Math.abs(j2) != l1 || Math.abs(l2) != l1 || rand.nextInt(2) != 0 && i3 != 0) {
                                    Block block1 = world.getBlock(i2, k1, k2);

                                    if (block1.isAir(world, i2, k1, k2) || block1.isLeaves(world, i2, k1, k2)) {
                                        this.setBlockAndNotifyAdequately(
                                                world, i2, k1, k2, DartBlocks.forceleaves, this.metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (k1 = 0; k1 < treeHeight; ++k1) {
                        block = world.getBlock(x, y + k1, z);

                        if (block.isAir(world, x, y + k1, z) || block.isLeaves(world, x, y + k1, z)) {
                            this.setBlockAndNotifyAdequately(world, x, y + k1, z, DartBlocks.forcelog, this.metaWood);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}

