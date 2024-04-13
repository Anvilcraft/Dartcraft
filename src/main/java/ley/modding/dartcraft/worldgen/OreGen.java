package ley.modding.dartcraft.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import ley.modding.dartcraft.Config;
import ley.modding.dartcraft.block.DartBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

public class OreGen implements IWorldGenerator {
    WorldGenMinable powerMinable
        = new WorldGenMinable(DartBlocks.powerore, Config.powerOreFreq);

    WorldGenMinable netherMinable = new WorldGenMinable(
        DartBlocks.powerore, 1, Config.powerOreFreq, Blocks.netherrack
    );

    @Override
    public void generate(
        Random random,
        int chunkX,
        int chunkZ,
        World world,
        IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider
    ) {
        doGeneration(random, chunkX, chunkZ, world);
    }

    public void doGeneration(Random rand, int chunkX, int chunkZ, World world) {
        if (world.provider.dimensionId == -1 && Config.generateNetherOre) {
            generateNether(rand, chunkX, chunkZ, world);
            return;
        }
        if (world.provider.dimensionId == 1)
            return;
        if (Config.generateOre)
            normalGen(rand, chunkX, chunkZ, world);
    }

    public void generateNether(Random rand, int chunkX, int chunkZ, World world) {
        for (int i = 0; i < (int) (Config.powerOreRarity * Config.netherFreq); i++) {
            int posx = chunkX * 16 + rand.nextInt(16);
            int posy = rand.nextInt(128);
            int posz = chunkZ * 16 + rand.nextInt(16);
            this.netherMinable.generate(world, rand, posx, posy, posz);
        }
    }

    public void normalGen(Random rand, int chunkX, int chunkZ, World world) {
        for (int i = 0; i < Config.powerOreRarity; i++) {
            int posx = chunkX * 16 + rand.nextInt(16);
            int posy = rand.nextInt(Config.powerOreSpawnHeight);
            int posz = chunkZ * 16 + rand.nextInt(16);
            this.powerMinable.generate(world, rand, posx, posy, posz);
        }
    }
}
