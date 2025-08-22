package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.world.PrisonStructureBuilder;
import com.prisonneo.world.WorldGenerator;
import com.prisonneo.world.NPCSpawner;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;

public class WorldManager {
    
    private final PrisonNEO plugin;
    private World prisonWorld;
    private final String WORLD_NAME = "prison_neo";
    
    public WorldManager(PrisonNEO plugin) {
        this.plugin = plugin;
        loadPrisonWorld();
    }
    
    public boolean createPrisonWorld() {
        // Delete existing world if it exists
        if (prisonWorld != null) {
            resetPrisonWorld();
        }
        
        try {
            // Create world with custom generator
            WorldCreator creator = new WorldCreator(WORLD_NAME);
            creator.generator(new WorldGenerator(plugin));
            creator.environment(World.Environment.NORMAL);
            creator.type(WorldType.FLAT);
            
            // Create world on main thread
            prisonWorld = creator.createWorld();
            
            if (prisonWorld != null) {
                // Set world properties
                prisonWorld.setDifficulty(Difficulty.NORMAL);
                prisonWorld.setSpawnFlags(false, false); // No monsters, no animals
                prisonWorld.setPVP(false);
                prisonWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                prisonWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                prisonWorld.setGameRule(GameRule.KEEP_INVENTORY, true);
                prisonWorld.setTime(6000); // Noon
                
                // Set spawn location
                Location spawn = new Location(prisonWorld, 0, 61, 0);
                prisonWorld.setSpawnLocation(spawn);
                
                // Generate prison structures
                generatePrisonStructures();
                
                // Schedule NPC initialization after world generation
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    NPCSpawner npcSpawner = new NPCSpawner(plugin, prisonWorld);
                    npcSpawner.spawnAllNPCs();
                    plugin.getLogger().info("NPCs spawned for prison world!");
                }, 200L); // 10 second delay
                
                return true;
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to create prison world: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public void resetPrisonWorld() {
        try {
            if (prisonWorld != null) {
                // Teleport all players out
                for (Player player : prisonWorld.getPlayers()) {
                    player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
                
                // Unload world
                if (!Bukkit.unloadWorld(prisonWorld, false)) {
                    plugin.getLogger().warning("Failed to unload prison world!");
                    return;
                }
                prisonWorld = null;
            }
            
            // Delete world folder
            deleteWorldFolder();
            
            // Create new world
            createPrisonWorld();
        } catch (Exception e) {
            plugin.getLogger().severe("Error resetting prison world: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void deleteWorldFolder() {
        File worldFolder = new File(Bukkit.getWorldContainer(), WORLD_NAME);
        if (worldFolder.exists()) {
            deleteDirectory(worldFolder);
        }
    }
    
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
    
    private void loadPrisonWorld() {
        prisonWorld = Bukkit.getWorld(WORLD_NAME);
        if (prisonWorld == null) {
            plugin.getLogger().info("Prison world not found. Use /prisonworld create to generate it.");
        } else {
            plugin.getLogger().info("Prison world found and loaded.");
            initializeDependentManagers();
        }
    }
    
    private void generatePrisonStructures() {
        if (prisonWorld == null) return;
        
        // Run all world generation on main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                PrisonStructureBuilder builder = new PrisonStructureBuilder(plugin, prisonWorld);
                
                // Build structures in sequence
                builder.buildMainPrison();
                builder.buildCellBlocks();
                builder.buildMines();
                builder.buildYard();
                builder.buildCafeteria();
                builder.buildLibrary();
                builder.buildWorkshop();
                builder.buildMedicalBlock();
                builder.buildSolitaryConfinement();
                builder.buildWalls();
                
                // Save the world after generation
                prisonWorld.save();
                plugin.getLogger().info("Prison structures generated successfully!");
                
                // Initialize managers that depend on the world
                initializeDependentManagers();

                // Teleport all online players to the prison world
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.teleport(new Location(prisonWorld, 0, 70, 0));
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error generating prison structures: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void initializeDependentManagers() {
        plugin.getLogger().info("Initializing world-dependent managers...");
        plugin.getAdvancedEscapeManager().initialize();
        plugin.getSecurityManager().initialize();
        plugin.getCellManager().initialize();
        plugin.getGangManager().initialize();
        plugin.getLogger().info("World-dependent managers initialized.");
    }

    public World getPrisonWorld() {
        return prisonWorld;
    }
    
    public boolean isPrisonWorldLoaded() {
        return prisonWorld != null;
    }
    
    public void teleportToPrison(Player player) {
        if (prisonWorld != null) {
            // Safe spawn location with better positioning
            Location spawn = new Location(prisonWorld, 0.5, 62, 0.5);
            spawn.setYaw(0); // Face north
            spawn.setPitch(0);
            
            player.teleport(spawn);
            
            // Add some atmospheric effects
            player.sendTitle(ChatColor.DARK_RED + "PRISON NEO", 
                           ChatColor.GRAY + "Добро пожаловать в тюрьму", 
                           10, 70, 20);
            
            // Play prison atmosphere sound
            player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1.0f, 0.8f);
            
            plugin.getLogger().info("Player " + player.getName() + " teleported to prison world");
        } else {
            player.sendMessage(ChatColor.RED + "Тюремный мир не загружен! Обратитесь к администратору.");
            plugin.getLogger().warning("Attempted to teleport " + player.getName() + " to prison world, but world is null!");
        }
    }
    
    public void saveData() {
        // Save any world-related data if needed
    }
}
