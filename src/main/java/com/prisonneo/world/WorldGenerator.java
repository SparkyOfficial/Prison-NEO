package com.prisonneo.world;

import com.prisonneo.PrisonNEO;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

public class WorldGenerator extends ChunkGenerator {

    private final PrisonNEO plugin;

    public WorldGenerator(PrisonNEO plugin) {
        this.plugin = plugin;
    }

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(worldInfo.getSeed()), 8);
        generator.setScale(0.005D);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;
                
                // Generate base terrain
                double noise = generator.noise(worldX, worldZ, 0.5D, 0.5D);
                int height = (int) (60 + noise * 10);
                
                // Generate prison structure areas
                if (isPrisonArea(worldX, worldZ)) {
                    generatePrisonTerrain(chunkData, x, z, height);
                } else {
                    generateNormalTerrain(chunkData, x, z, height);
                }
            }
        }
    }

    private boolean isPrisonArea(int x, int z) {
        // Prison main area: -200 to 200 on both axes
        return x >= -200 && x <= 200 && z >= -200 && z <= 200;
    }

    private void generatePrisonTerrain(ChunkData chunkData, int x, int z, int height) {
        // Bedrock layer
        chunkData.setBlock(x, 0, z, Material.BEDROCK);
        
        // Stone foundation
        for (int y = 1; y <= Math.min(height - 10, 50); y++) {
            chunkData.setBlock(x, y, z, Material.STONE);
        }
        
        // Prison floor level (smooth stone)
        for (int y = Math.min(height - 9, 51); y <= Math.min(height, 60); y++) {
            chunkData.setBlock(x, y, z, Material.SMOOTH_STONE);
        }
    }

    private void generateNormalTerrain(ChunkData chunkData, int x, int z, int height) {
        // Bedrock
        chunkData.setBlock(x, 0, z, Material.BEDROCK);
        
        // Stone
        for (int y = 1; y <= height - 3; y++) {
            chunkData.setBlock(x, y, z, Material.STONE);
        }
        
        // Dirt
        for (int y = height - 2; y <= height - 1; y++) {
            chunkData.setBlock(x, y, z, Material.DIRT);
        }
        
        // Grass
        chunkData.setBlock(x, height, z, Material.GRASS_BLOCK);
    }

    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        // Bedrock is handled in generateNoise
    }

    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        return Biome.PLAINS;
    }
}
