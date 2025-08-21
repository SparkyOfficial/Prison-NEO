package com.prisonneo.world;

import com.prisonneo.PrisonNEO;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

public class PrisonStructureBuilder {
    
    private final PrisonNEO plugin;
    private final World world;
    private final Queue<Runnable> blockChangeQueue = new ArrayDeque<>();
    private final Random random = new Random();
    
    public PrisonStructureBuilder(PrisonNEO plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }
    
    public void buildMainPrison() {
        try {
            // Main prison building (-100 to 100, y=61-80, z=-100 to 100)
            buildRectangle(-100, 61, -100, 100, 80, 100, Material.STONE_BRICKS, true);
            
            // Prison entrance
            buildEntrance();
            
            // Administrative building
            buildAdminBuilding();
            
            // Process all block changes
            processBlockChanges();
            
            // Save the world
            world.save();
        } catch (Exception e) {
            plugin.getLogger().severe("Error building main prison: " + e.getMessage());
            e.printStackTrace();
        }
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
        // Central yard (-35 to 35, y=61, z=-35 to 35)
        for (int x = -35; x <= 35; x++) {
            for (int z = -35; z <= 35; z++) {
                setBlock(x, 61, z, Material.GRASS_BLOCK);
            }
        }
        
        // Yard perimeter fence
        buildRectangle(-35, 62, -35, 35, 65, -34, Material.IRON_BARS, false);
        buildRectangle(-35, 62, 34, 35, 65, 35, Material.IRON_BARS, false);
        buildRectangle(-35, 62, -34, -34, 65, 34, Material.IRON_BARS, false);
        buildRectangle(34, 62, -34, 35, 65, 34, Material.IRON_BARS, false);
        
        // Yard lighting - tall lamp posts
        for (int x = -30; x <= 30; x += 15) {
            for (int z = -30; z <= 30; z += 15) {
                // Lamp post
                for (int y = 62; y <= 68; y++) {
                    setBlock(x, y, z, Material.IRON_BARS);
                }
                setBlock(x, 69, z, Material.GLOWSTONE);
                setBlock(x - 1, 68, z, Material.GLOWSTONE);
                setBlock(x + 1, 68, z, Material.GLOWSTONE);
                setBlock(x, 68, z - 1, Material.GLOWSTONE);
                setBlock(x, 68, z + 1, Material.GLOWSTONE);
            }
        }
        
        // Basketball court
        buildBasketballCourt();
        
        // Build structures in sequence
        buildMainPrison();
        buildCellBlocks();
        buildMines();
        buildYard();
        buildCafeteria();
        buildLibrary();
        buildWorkshop();
        buildMedicalBlock();
        buildSolitaryConfinement();
        buildWalls();
        
        // Walking path
        buildWalkingPath();
    }
    
    // ... rest of the code remains the same ...
    public void buildWalls() {
        // Build perimeter walls
        buildRectangle(-200, 60, -200, 200, 100, -190, Material.STONE_BRICKS, false);
        buildRectangle(-200, 60, 190, 200, 100, 200, Material.STONE_BRICKS, false);
        buildRectangle(-200, 60, -189, -190, 100, 189, Material.STONE_BRICKS, false);
        buildRectangle(190, 60, -189, 200, 100, 189, Material.STONE_BRICKS, false);
        
        // Add guard towers at corners
        buildGuardTower(-190, 60, -190);
        buildGuardTower(-190, 60, 190);
        buildGuardTower(190, 60, -190);
        buildGuardTower(190, 60, 190);
        
        // Process any remaining block changes
        processBlockChanges();
    }
    
    
    private void buildCellBlock(int x1, int x2, int z1, int z2, String blockName) {
        // Outer walls with windows
        buildRectangle(x1, 62, z1, x2, 75, z2, Material.STONE_BRICKS, true);
        
        // Add windows to outer walls
        for (int x = x1 + 5; x < x2; x += 8) {
            setBlock(x, 68, z1, Material.GLASS_PANE);
            setBlock(x, 69, z1, Material.GLASS_PANE);
            setBlock(x, 68, z2, Material.GLASS_PANE);
            setBlock(x, 69, z2, Material.GLASS_PANE);
        }
        for (int z = z1 + 5; z < z2; z += 8) {
            setBlock(x1, 68, z, Material.GLASS_PANE);
            setBlock(x1, 69, z, Material.GLASS_PANE);
            setBlock(x2, 68, z, Material.GLASS_PANE);
            setBlock(x2, 69, z, Material.GLASS_PANE);
        }
        
        // Main corridor floor
        for (int x = x1 + 1; x < x2; x++) {
            for (int z = z1 + 1; z < z2; z++) {
                setBlock(x, 62, z, Material.POLISHED_ANDESITE);
            }
        }
        
        // Interior cells (6x6 each)
        int cellSize = 6;
        for (int x = x1 + 3; x < x2 - cellSize; x += cellSize + 2) {
            for (int z = z1 + 3; z < z2 - cellSize; z += cellSize + 2) {
                buildCell(x, z, cellSize);
            }
        }
        
        // Enhanced corridor lighting
        for (int x = x1 + 3; x < x2; x += 6) {
            for (int z = z1 + 3; z < z2; z += 6) {
                setBlock(x, 74, z, Material.GLOWSTONE);
                // Add hanging lamps
                setBlock(x, 73, z, Material.CHAIN);
                setBlock(x, 72, z, Material.LANTERN);
            }
        }
        
        // Security cameras (decorative)
        setBlock(x1 + 2, 73, z1 + 2, Material.OBSERVER);
        setBlock(x2 - 2, 73, z1 + 2, Material.OBSERVER);
        setBlock(x1 + 2, 73, z2 - 2, Material.OBSERVER);
        setBlock(x2 - 2, 73, z2 - 2, Material.OBSERVER);
        
        // Guard station in center
        int centerX = (x1 + x2) / 2;
        int centerZ = (z1 + z2) / 2;
        buildRectangle(centerX - 2, 62, centerZ - 2, centerX + 2, 68, centerZ + 2, Material.QUARTZ_BLOCK, true);
        setBlock(centerX, 63, centerZ, Material.OAK_STAIRS); // Guard chair
        setBlock(centerX + 1, 63, centerZ, Material.LECTERN); // Control panel
        
        // Block identification sign
        setBlock(x1, 70, z1 + 5, Material.OAK_SIGN);
    }
    
    private void buildCell(int x, int z, int size) {
        // Cell walls - mixed materials for realism
        buildRectangle(x, 62, z, x + size, 68, z + size, Material.STONE_BRICKS, true);
        
        // Iron bars for front wall
        for (int y = 63; y <= 66; y++) {
            setBlock(x, y, z + 1, Material.IRON_BARS);
            setBlock(x, y, z + 2, Material.IRON_BARS);
            setBlock(x, y, z + 3, Material.IRON_BARS);
        }
        
        // Cell door
        setBlock(x, 63, z + 2, Material.IRON_DOOR);
        setBlock(x, 64, z + 2, Material.IRON_DOOR);
        
        // Cell floor
        for (int cx = x + 1; cx < x + size; cx++) {
            for (int cz = z + 1; cz < z + size; cz++) {
                setBlock(cx, 62, cz, Material.SMOOTH_STONE);
            }
        }
        
        // Cell ceiling
        for (int cx = x + 1; cx < x + size; cx++) {
            for (int cz = z + 1; cz < z + size; cz++) {
                setBlock(cx, 67, cz, Material.SMOOTH_STONE_SLAB);
            }
        }
        
        // Bed with pillow
        setBlock(x + 1, 63, z + 1, Material.RED_BED);
        setBlock(x + 2, 63, z + 1, Material.WHITE_WOOL); // Pillow
        
        // Toilet and sink
        setBlock(x + size - 2, 63, z + size - 2, Material.CAULDRON);
        setBlock(x + size - 2, 64, z + size - 3, Material.ITEM_FRAME); // Mirror
        
        // Small table
        setBlock(x + 3, 63, z + 3, Material.OAK_PRESSURE_PLATE);
        setBlock(x + 3, 62, z + 3, Material.OAK_FENCE);
        
        // Cell lighting
        setBlock(x + 2, 66, z + 3, Material.REDSTONE_LAMP);
        setBlock(x + 2, 65, z + 3, Material.LEVER); // Light switch
        
        // Personal items
        if (random.nextBoolean()) {
            setBlock(x + 1, 64, z + 3, Material.BOOK); // Reading material
        }
        if (random.nextBoolean()) {
            setBlock(x + 4, 63, z + 2, Material.FLOWER_POT); // Small plant
        }
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
        // Exercise equipment area (18 to 33, y=62, z=18 to 33)
        for (int x = 18; x <= 33; x++) {
            for (int z = 18; z <= 33; z++) {
                setBlock(x, 62, z, Material.GRAY_CONCRETE);
            }
        }
        
        // Weight lifting area
        setBlock(20, 63, 20, Material.ANVIL); // Weight bench
        setBlock(20, 63, 21, Material.IRON_BLOCK); // Weights
        setBlock(19, 63, 20, Material.IRON_BLOCK);
        setBlock(21, 63, 20, Material.IRON_BLOCK);
        
        // Pull-up bars
        for (int i = 0; i < 3; i++) {
            setBlock(25 + i, 63, 25, Material.OAK_FENCE);
            setBlock(25 + i, 66, 25, Material.IRON_BARS);
            for (int y = 64; y <= 65; y++) {
                setBlock(25 + i, y, 25, Material.IRON_BARS);
            }
        }
        
        // Punching bags
        setBlock(30, 63, 22, Material.HAY_BLOCK);
        setBlock(30, 64, 22, Material.HAY_BLOCK);
        setBlock(30, 65, 22, Material.CHAIN);
        
        setBlock(32, 63, 24, Material.HAY_BLOCK);
        setBlock(32, 64, 24, Material.HAY_BLOCK);
        setBlock(32, 65, 24, Material.CHAIN);
        
        // Exercise mats
        for (int x = 22; x <= 24; x++) {
            for (int z = 28; z <= 30; z++) {
                setBlock(x, 63, z, Material.BLUE_CARPET);
            }
        }
        
        // Equipment storage
        setBlock(19, 63, 30, Material.CHEST);
        setBlock(20, 63, 30, Material.BARREL);
    }
    
    private void buildPicnicArea() {
        // Picnic tables area (-20 to -5, y=62, z=-20 to -5)
        for (int x = -20; x <= -5; x++) {
            for (int z = -20; z <= -5; z++) {
                setBlock(x, 62, z, Material.COARSE_DIRT);
            }
        }
        
        // Picnic tables
        for (int x = -18; x <= -8; x += 5) {
            for (int z = -18; z <= -8; z += 5) {
                // Table
                setBlock(x, 63, z, Material.OAK_PRESSURE_PLATE);
                setBlock(x, 62, z, Material.OAK_FENCE);
                
                // Benches
                setBlock(x - 1, 63, z, Material.OAK_STAIRS);
                setBlock(x + 1, 63, z, Material.OAK_STAIRS);
                setBlock(x, 63, z - 1, Material.OAK_STAIRS);
                setBlock(x, 63, z + 1, Material.OAK_STAIRS);
            }
        }
    }
    
    private void buildGardenArea() {
        // Garden area (5 to 20, y=62, z=-20 to -5)
        for (int x = 5; x <= 20; x++) {
            for (int z = -20; z <= -5; z++) {
                setBlock(x, 62, z, Material.FARMLAND);
            }
        }
        
        // Plant crops and flowers
        for (int x = 6; x <= 19; x += 2) {
            for (int z = -19; z <= -6; z += 2) {
                if (random.nextBoolean()) {
                    setBlock(x, 63, z, Material.WHEAT);
                } else {
                    setBlock(x, 63, z, Material.CARROTS);
                }
            }
        }
        
        // Flower beds
        for (int x = 7; x <= 18; x += 3) {
            for (int z = -18; z <= -7; z += 3) {
                setBlock(x, 63, z, Material.POPPY);
            }
        }
        
        // Water source
        setBlock(12, 62, -12, Material.WATER);
        
        // Garden tools storage
        setBlock(19, 63, -19, Material.CHEST);
        setBlock(18, 63, -19, Material.COMPOSTER);
    }
    
    private void buildWalkingPath() {
        // Circular walking path around the yard
        int centerX = 0, centerZ = 0;
        int radius = 32;
        
        for (int angle = 0; angle < 360; angle += 5) {
            double radians = Math.toRadians(angle);
            int x = (int) (centerX + radius * Math.cos(radians));
            int z = (int) (centerZ + radius * Math.sin(radians));
            
            setBlock(x, 62, z, Material.STONE_BRICK_SLAB);
            setBlock(x + 1, 62, z, Material.STONE_BRICK_SLAB);
            setBlock(x, 62, z + 1, Material.STONE_BRICK_SLAB);
        }
        
        // Benches along the path
        for (int angle = 0; angle < 360; angle += 45) {
            double radians = Math.toRadians(angle);
            int x = (int) (centerX + (radius - 3) * Math.cos(radians));
            int z = (int) (centerZ + (radius - 3) * Math.sin(radians));
            
            setBlock(x, 63, z, Material.OAK_STAIRS);
        }
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
    
    private void setBlock(int x, int y, int z, Material material) {
        if (world == null) return;
        
        // Add block change to the queue
        blockChangeQueue.add(() -> {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType() != material) {
                block.setType(material, false);
            }
        });
        
        // Process queue if it gets too large
        if (blockChangeQueue.size() > 1000) {
            processBlockChanges();
        }
    }
    
    private void processBlockChanges() {
        if (world == null || blockChangeQueue.isEmpty()) return;
        
        // Process up to 1000 block changes at once
        int processed = 0;
        while (!blockChangeQueue.isEmpty() && processed < 1000) {
            Runnable task = blockChangeQueue.poll();
            if (task != null) {
                task.run();
                processed++;
            }
        }
        
        // Save the world if we processed any changes
        if (processed > 0) {
            world.save();
        }
    }
    
    private void buildRectangle(int x1, int y1, int z1, int x2, int y2, int z2, Material material, boolean hollow) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (hollow) {
                        // Only build walls, floor, and ceiling
                        if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                            setBlock(x, y, z, material);
                        }
                    } else {
                        setBlock(x, y, z, material);
                    }
                }
            }
            
            // Process block changes periodically to prevent memory issues
            if ((x - minX) % 10 == 0) {
                processBlockChanges();
            }
        }
        
        // Process any remaining block changes
        processBlockChanges();
    }
    
    public void buildCafeteria() {
        // Cafeteria building (-50 to -10, y=62-70, z=50 to 90)
        buildRectangle(-50, 62, 50, -10, 70, 90, Material.QUARTZ_BLOCK, true);
        
        // Cafeteria floor
        for (int x = -48; x < -12; x++) {
            for (int z = 52; z < 88; z++) {
                setBlock(x, 62, z, Material.POLISHED_ANDESITE);
            }
        }
        
        // Kitchen area (-48 to -35, z=52 to 65)
        buildRectangle(-48, 62, 52, -35, 68, 65, Material.STONE_BRICKS, true);
        
        // Kitchen equipment
        setBlock(-45, 63, 55, Material.FURNACE);
        setBlock(-44, 63, 55, Material.FURNACE);
        setBlock(-43, 63, 55, Material.FURNACE);
        setBlock(-42, 63, 55, Material.SMOKER);
        setBlock(-41, 63, 55, Material.BLAST_FURNACE);
        
        // Food storage
        setBlock(-45, 63, 62, Material.CHEST);
        setBlock(-44, 63, 62, Material.BARREL);
        setBlock(-43, 63, 62, Material.BARREL);
        
        // Serving counter
        for (int x = -34; x >= -40; x--) {
            setBlock(x, 63, 68, Material.QUARTZ_SLAB);
            setBlock(x, 62, 68, Material.QUARTZ_PILLAR);
        }
        
        // Dining tables (4x4 arrangement)
        for (int x = -30; x >= -45; x -= 8) {
            for (int z = 75; z <= 85; z += 5) {
                // Table
                setBlock(x, 63, z, Material.OAK_PRESSURE_PLATE);
                setBlock(x, 62, z, Material.OAK_FENCE);
                
                // Chairs around table
                setBlock(x-1, 63, z, Material.OAK_STAIRS);
                setBlock(x+1, 63, z, Material.OAK_STAIRS);
                setBlock(x, 63, z-1, Material.OAK_STAIRS);
                setBlock(x, 63, z+1, Material.OAK_STAIRS);
            }
        }
        
        // Cafeteria lighting
        for (int x = -45; x <= -15; x += 10) {
            for (int z = 60; z <= 80; z += 10) {
                setBlock(x, 69, z, Material.GLOWSTONE);
            }
        }
        
        // Entrance
        setBlock(-30, 63, 50, Material.OAK_DOOR);
        setBlock(-30, 64, 50, Material.OAK_DOOR);
    }
    
    public void buildLibrary() {
        // Library building (10 to 50, y=62-70, z=50 to 90)
        buildRectangle(10, 62, 50, 50, 70, 90, Material.OAK_PLANKS, true);
        
        // Library floor
        for (int x = 12; x < 48; x++) {
            for (int z = 52; z < 88; z++) {
                setBlock(x, 62, z, Material.OAK_PLANKS);
            }
        }
        
        // Bookshelves along walls
        for (int x = 13; x < 47; x += 2) {
            setBlock(x, 63, 53, Material.BOOKSHELF);
            setBlock(x, 64, 53, Material.BOOKSHELF);
            setBlock(x, 63, 87, Material.BOOKSHELF);
            setBlock(x, 64, 87, Material.BOOKSHELF);
        }
        
        for (int z = 55; z < 85; z += 2) {
            setBlock(13, 63, z, Material.BOOKSHELF);
            setBlock(13, 64, z, Material.BOOKSHELF);
            setBlock(47, 63, z, Material.BOOKSHELF);
            setBlock(47, 64, z, Material.BOOKSHELF);
        }
        
        // Reading tables
        for (int x = 20; x <= 40; x += 10) {
            for (int z = 60; z <= 80; z += 10) {
                setBlock(x, 63, z, Material.OAK_PRESSURE_PLATE);
                setBlock(x, 62, z, Material.OAK_FENCE);
                
                // Chairs
                setBlock(x-1, 63, z, Material.OAK_STAIRS);
                setBlock(x+1, 63, z, Material.OAK_STAIRS);
                
                // Books on table
                if (random.nextBoolean()) {
                    setBlock(x, 64, z, Material.BOOK);
                }
            }
        }
        
        // Librarian desk
        setBlock(30, 63, 55, Material.LECTERN);
        setBlock(29, 63, 55, Material.OAK_STAIRS);
        setBlock(31, 63, 55, Material.CHEST); // Book storage
        
        // Library lighting
        for (int x = 20; x <= 40; x += 10) {
            for (int z = 60; z <= 80; z += 10) {
                setBlock(x, 68, z, Material.LANTERN);
            }
        }
        
        // Entrance
        setBlock(30, 63, 50, Material.OAK_DOOR);
        setBlock(30, 64, 50, Material.OAK_DOOR);
    }
    
    public void buildWorkshop() {
        // Workshop building (-90 to -50, y=62-70, z=-90 to -50)
        buildRectangle(-90, 62, -90, -50, 70, -50, Material.COBBLESTONE, true);
        
        // Workshop floor
        for (int x = -88; x < -52; x++) {
            for (int z = -88; z < -52; z++) {
                setBlock(x, 62, z, Material.STONE);
            }
        }
        
        // Crafting area
        for (int x = -85; x <= -75; x += 5) {
            for (int z = -85; z <= -75; z += 5) {
                setBlock(x, 63, z, Material.CRAFTING_TABLE);
                setBlock(x+1, 63, z, Material.OAK_STAIRS); // Work stool
            }
        }
        
        // Anvil area
        setBlock(-60, 63, -80, Material.ANVIL);
        setBlock(-59, 63, -80, Material.ANVIL);
        setBlock(-58, 63, -80, Material.ANVIL);
        
        // Tool storage
        setBlock(-55, 63, -85, Material.CHEST);
        setBlock(-55, 63, -84, Material.BARREL);
        setBlock(-55, 63, -83, Material.BARREL);
        
        // Furnace area
        setBlock(-85, 63, -55, Material.FURNACE);
        setBlock(-84, 63, -55, Material.BLAST_FURNACE);
        setBlock(-83, 63, -55, Material.SMOKER);
        
        // Material storage
        for (int x = -75; x <= -65; x += 2) {
            setBlock(x, 63, -55, Material.CHEST);
        }
        
        // Workshop lighting
        for (int x = -80; x <= -60; x += 10) {
            for (int z = -80; z <= -60; z += 10) {
                setBlock(x, 68, z, Material.TORCH);
            }
        }
        
        // Entrance
        setBlock(-70, 63, -50, Material.IRON_DOOR);
        setBlock(-70, 64, -50, Material.IRON_DOOR);
    }
    
    public void buildMedicalBlock() {
        // Medical building (50 to 90, y=62-70, z=-90 to -50)
        buildRectangle(50, 62, -90, 90, 70, -50, Material.WHITE_CONCRETE, true);
        
        // Medical floor
        for (int x = 52; x < 88; x++) {
            for (int z = -88; z < -52; z++) {
                setBlock(x, 62, z, Material.WHITE_CONCRETE);
            }
        }
        
        // Reception area (52-65, z=-85 to -75)
        setBlock(58, 63, -80, Material.LECTERN); // Reception desk
        setBlock(57, 63, -80, Material.OAK_STAIRS); // Chair
        
        // Waiting area
        for (int x = 55; x <= 62; x += 3) {
            setBlock(x, 63, -75, Material.OAK_STAIRS);
        }
        
        // Medical rooms
        for (int x = 70; x <= 85; x += 8) {
            for (int z = -85; z <= -60; z += 12) {
                // Room walls
                buildRectangle(x, 62, z, x+6, 68, z+10, Material.WHITE_CONCRETE, true);
                
                // Medical bed
                setBlock(x+1, 63, z+2, Material.WHITE_BED);
                
                // Medical equipment
                setBlock(x+4, 63, z+2, Material.BREWING_STAND);
                setBlock(x+4, 63, z+4, Material.CAULDRON);
                setBlock(x+1, 63, z+8, Material.CHEST); // Medical supplies
                
                // Room door
                setBlock(x+3, 63, z, Material.OAK_DOOR);
                setBlock(x+3, 64, z, Material.OAK_DOOR);
            }
        }
        
        // Medical lighting
        for (int x = 60; x <= 80; x += 10) {
            for (int z = -80; z <= -60; z += 10) {
                setBlock(x, 68, z, Material.GLOWSTONE);
            }
        }
        
        // Main entrance
        setBlock(70, 63, -50, Material.OAK_DOOR);
        setBlock(70, 64, -50, Material.OAK_DOOR);
    }
    
    public void buildSolitaryConfinement() {
        // Solitary confinement building (-150 to -110, y=55-65, z=-50 to -10)
        buildRectangle(-150, 55, -50, -110, 65, -10, Material.OBSIDIAN, true);
        
        // Solitary floor
        for (int x = -148; x < -112; x++) {
            for (int z = -48; z < -12; z++) {
                setBlock(x, 55, z, Material.OBSIDIAN);
            }
        }
        
        // Individual solitary cells (3x3 each)
        int cellSize = 4;
        for (int x = -145; x <= -115; x += cellSize + 1) {
            for (int z = -45; z <= -15; z += cellSize + 1) {
                // Cell walls
                buildRectangle(x, 55, z, x+cellSize, 62, z+cellSize, Material.OBSIDIAN, true);
                
                // Cell floor
                for (int cx = x+1; cx < x+cellSize; cx++) {
                    for (int cz = z+1; cz < z+cellSize; cz++) {
                        setBlock(cx, 55, cz, Material.OBSIDIAN);
                    }
                }
                
                // Iron door
                setBlock(x, 56, z+2, Material.IRON_DOOR);
                setBlock(x, 57, z+2, Material.IRON_DOOR);
                
                // Minimal furnishing
                setBlock(x+2, 56, z+2, Material.GRAY_BED);
                setBlock(x+3, 56, z+3, Material.CAULDRON); // Toilet
                
                // Dim lighting
                setBlock(x+2, 61, z+2, Material.REDSTONE_TORCH);
            }
        }
        
        // Guard corridor
        for (int x = -148; x < -112; x++) {
            setBlock(x, 55, -30, Material.STONE_BRICKS);
        }
        
        // Security entrance
        setBlock(-130, 56, -10, Material.IRON_DOOR);
        setBlock(-130, 57, -10, Material.IRON_DOOR);
        
        // Warning signs
        setBlock(-130, 58, -12, Material.OAK_SIGN);
    }
}
