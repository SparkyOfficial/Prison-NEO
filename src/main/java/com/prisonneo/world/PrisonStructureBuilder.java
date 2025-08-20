package com.prisonneo.world;

import com.prisonneo.PrisonNEO;
import org.bukkit.*;
import org.bukkit.block.Block;

public class PrisonStructureBuilder {
    
    private final PrisonNEO plugin;
    private final World world;
    
    public PrisonStructureBuilder(PrisonNEO plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }
    
    public void buildMainPrison() {
        // Main prison building (-100 to 100, y=61-80, z=-100 to 100)
        buildRectangle(-100, 61, -100, 100, 80, 100, Material.STONE_BRICKS, true);
        
        // Prison entrance
        buildEntrance();
        
        // Administrative building
        buildAdminBuilding();
    }
    
    public void buildCellBlocks() {
        // Cell Block A (-80 to -40, y=62-75, z=-80 to -40)
        buildCellBlock(-80, -40, -80, -40, "A");
        
        // Cell Block B (40 to 80, y=62-75, z=-80 to -40)
        buildCellBlock(40, 80, -80, -40, "B");
        
        // Cell Block C (-80 to -40, y=62-75, z=40 to 80)
        buildCellBlock(-80, -40, 40, 80, "C");
        
        // Cell Block D (40 to 80, y=62-75, z=40 to 80)
        buildCellBlock(40, 80, 40, 80, "D");
    }
    
    public void buildMines() {
        // Mine A (-150 to -120, y=30-60, z=-150 to -120)
        buildMine(-150, -120, -150, -120, Material.COAL_ORE, "A");
        
        // Mine B (120 to 150, y=30-60, z=-150 to -120)
        buildMine(120, 150, -150, -120, Material.IRON_ORE, "B");
        
        // Mine C (-150 to -120, y=30-60, z=120 to 150)
        buildMine(-150, -120, 120, 150, Material.GOLD_ORE, "C");
        
        // Mine D (120 to 150, y=30-60, z=120 to 150)
        buildMine(120, 150, 120, 150, Material.DIAMOND_ORE, "D");
    }
    
    public void buildYard() {
        // Central yard (-30 to 30, y=61, z=-30 to 30)
        for (int x = -30; x <= 30; x++) {
            for (int z = -30; z <= 30; z++) {
                world.getBlockAt(x, 61, z).setType(Material.GRASS_BLOCK);
                
                // Add some decorative elements
                if (x % 10 == 0 && z % 10 == 0) {
                    world.getBlockAt(x, 62, z).setType(Material.TORCH);
                }
            }
        }
        
        // Basketball court
        buildBasketballCourt();
        
        // Exercise area
        buildExerciseArea();
    }
    
    public void buildWalls() {
        // Outer prison walls (-200 to 200, y=61-85)
        buildPerimeter(-200, 200, -200, 200, 61, 85, Material.STONE_BRICK_WALL);
        
        // Guard towers
        buildGuardTower(-190, 61, -190);
        buildGuardTower(190, 61, -190);
        buildGuardTower(-190, 61, 190);
        buildGuardTower(190, 61, 190);
    }
    
    private void buildCellBlock(int x1, int x2, int z1, int z2, String blockName) {
        // Outer walls
        buildRectangle(x1, 62, z1, x2, 75, z2, Material.STONE_BRICKS, true);
        
        // Interior cells (3x3 each)
        int cellSize = 6;
        for (int x = x1 + 2; x < x2 - cellSize; x += cellSize) {
            for (int z = z1 + 2; z < z2 - cellSize; z += cellSize) {
                buildCell(x, z, cellSize);
            }
        }
        
        // Corridor lighting
        for (int x = x1 + 1; x < x2; x += 4) {
            for (int z = z1 + 1; z < z2; z += 4) {
                world.getBlockAt(x, 74, z).setType(Material.GLOWSTONE);
            }
        }
    }
    
    private void buildCell(int x, int z, int size) {
        // Cell walls
        buildRectangle(x, 62, z, x + size, 68, z + size, Material.IRON_BARS, true);
        
        // Cell floor
        for (int cx = x + 1; cx < x + size; cx++) {
            for (int cz = z + 1; cz < z + size; cz++) {
                world.getBlockAt(cx, 62, cz).setType(Material.SMOOTH_STONE);
            }
        }
        
        // Bed
        world.getBlockAt(x + 1, 63, z + 1).setType(Material.RED_BED);
        
        // Toilet
        world.getBlockAt(x + size - 2, 63, z + size - 2).setType(Material.CAULDRON);
    }
    
    private void buildMine(int x1, int x2, int z1, int z2, Material oreType, String mineName) {
        // Clear area and create mine shaft
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                for (int y = 30; y <= 60; y++) {
                    if (y == 30) {
                        world.getBlockAt(x, y, z).setType(Material.BEDROCK);
                    } else if (y >= 58) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                    } else {
                        // Random ore generation
                        if (Math.random() < 0.15) {
                            world.getBlockAt(x, y, z).setType(oreType);
                        } else {
                            world.getBlockAt(x, y, z).setType(Material.STONE);
                        }
                    }
                }
            }
        }
        
        // Mine entrance
        buildMineEntrance(x1 + (x2 - x1) / 2, z1 + (z2 - z1) / 2);
        
        // Lighting
        for (int x = x1 + 5; x < x2; x += 10) {
            for (int z = z1 + 5; z < z2; z += 10) {
                world.getBlockAt(x, 57, z).setType(Material.TORCH);
            }
        }
    }
    
    private void buildMineEntrance(int x, int z) {
        // Stairs down to mine
        for (int i = 0; i < 30; i++) {
            world.getBlockAt(x, 61 - i, z + i).setType(Material.STONE_STAIRS);
            world.getBlockAt(x - 1, 61 - i, z + i).setType(Material.STONE_BRICKS);
            world.getBlockAt(x + 1, 61 - i, z + i).setType(Material.STONE_BRICKS);
        }
    }
    
    private void buildBasketballCourt() {
        // Court floor (-15 to 15, y=62, z=-15 to 15)
        for (int x = -15; x <= 15; x++) {
            for (int z = -15; z <= 15; z++) {
                world.getBlockAt(x, 62, z).setType(Material.ORANGE_CONCRETE);
            }
        }
        
        // Basketball hoops
        buildBasketballHoop(-12, 62, 0);
        buildBasketballHoop(12, 62, 0);
    }
    
    private void buildBasketballHoop(int x, int y, int z) {
        // Pole
        for (int i = 0; i < 4; i++) {
            world.getBlockAt(x, y + i, z).setType(Material.IRON_BARS);
        }
        
        // Hoop
        world.getBlockAt(x, y + 4, z).setType(Material.ORANGE_WOOL);
        world.getBlockAt(x + 1, y + 4, z).setType(Material.ORANGE_WOOL);
        world.getBlockAt(x - 1, y + 4, z).setType(Material.ORANGE_WOOL);
    }
    
    private void buildExerciseArea() {
        // Exercise equipment area (20 to 35, y=62, z=20 to 35)
        for (int x = 20; x <= 35; x++) {
            for (int z = 20; z <= 35; z++) {
                world.getBlockAt(x, 62, z).setType(Material.GRAY_CONCRETE);
            }
        }
        
        // Exercise equipment (simple representations)
        world.getBlockAt(22, 63, 22).setType(Material.ANVIL); // Weight
        world.getBlockAt(25, 63, 25).setType(Material.IRON_BARS); // Pull-up bar
        world.getBlockAt(28, 63, 28).setType(Material.HAY_BLOCK); // Punching bag
    }
    
    private void buildEntrance() {
        // Main entrance gate (-5 to 5, y=62-67, z=-100)
        for (int x = -5; x <= 5; x++) {
            for (int y = 62; y <= 67; y++) {
                world.getBlockAt(x, y, -100).setType(Material.IRON_DOOR);
            }
        }
        
        // Entrance pathway
        for (int z = -120; z <= -100; z++) {
            for (int x = -10; x <= 10; x++) {
                world.getBlockAt(x, 61, z).setType(Material.STONE_BRICK_SLAB);
            }
        }
    }
    
    private void buildAdminBuilding() {
        // Admin building (60 to 90, y=62-75, z=-30 to 0)
        buildRectangle(60, 62, -30, 90, 75, 0, Material.QUARTZ_BLOCK, true);
        
        // Admin office interior
        for (int x = 62; x < 88; x++) {
            for (int z = -28; z < -2; z++) {
                world.getBlockAt(x, 62, z).setType(Material.QUARTZ_SLAB);
            }
        }
        
        // Furniture
        world.getBlockAt(75, 63, -15).setType(Material.OAK_STAIRS); // Desk
        world.getBlockAt(70, 63, -10).setType(Material.BOOKSHELF);
        world.getBlockAt(80, 63, -20).setType(Material.CHEST); // Storage
    }
    
    private void buildGuardTower(int x, int y, int z) {
        // Tower base (5x5)
        buildRectangle(x - 2, y, z - 2, x + 2, y + 15, z + 2, Material.STONE_BRICKS, true);
        
        // Tower top platform
        for (int tx = x - 3; tx <= x + 3; tx++) {
            for (int tz = z - 3; tz <= z + 3; tz++) {
                world.getBlockAt(tx, y + 15, tz).setType(Material.STONE_BRICK_SLAB);
            }
        }
        
        // Ladder
        for (int ly = y + 1; ly < y + 15; ly++) {
            world.getBlockAt(x, ly, z).setType(Material.LADDER);
        }
    }
    
    private void buildRectangle(int x1, int y1, int z1, int x2, int y2, int z2, Material material, boolean hollow) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                    if (hollow) {
                        // Only build walls, floor, and ceiling
                        if (x == Math.min(x1, x2) || x == Math.max(x1, x2) ||
                            y == Math.min(y1, y2) || y == Math.max(y1, y2) ||
                            z == Math.min(z1, z2) || z == Math.max(z1, z2)) {
                            world.getBlockAt(x, y, z).setType(material);
                        }
                    } else {
                        world.getBlockAt(x, y, z).setType(material);
                    }
                }
            }
        }
    }
    
    private void buildPerimeter(int x1, int x2, int z1, int z2, int y1, int y2, Material material) {
        // North wall
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                world.getBlockAt(x, y, z1).setType(material);
            }
        }
        
        // South wall
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                world.getBlockAt(x, y, z2).setType(material);
            }
        }
        
        // West wall
        for (int z = z1; z <= z2; z++) {
            for (int y = y1; y <= y2; y++) {
                world.getBlockAt(x1, y, z).setType(material);
            }
        }
        
        // East wall
        for (int z = z1; z <= z2; z++) {
            for (int y = y1; y <= y2; y++) {
                world.getBlockAt(x2, y, z).setType(material);
            }
        }
    }
}
