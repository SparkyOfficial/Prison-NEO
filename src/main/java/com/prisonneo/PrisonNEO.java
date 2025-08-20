package com.prisonneo;

import com.prisonneo.commands.*;
import com.prisonneo.listeners.*;
import com.prisonneo.managers.*;
import com.prisonneo.world.WorldGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public final class PrisonNEO extends JavaPlugin {

    private static PrisonNEO instance;
    private WorldManager worldManager;
    private PlayerManager playerManager;
    private RankManager rankManager;
    private CellManager cellManager;
    private MineManager mineManager;
    private EconomyManager economyManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        initializeManagers();
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        getLogger().info("Prison NEO has been enabled!");
        getLogger().info("Use /prisonworld create to generate the prison world");
    }

    @Override
    public void onDisable() {
        if (worldManager != null) {
            worldManager.saveData();
        }
        if (playerManager != null) {
            playerManager.saveData();
        }
        getLogger().info("Prison NEO has been disabled!");
    }

    private void initializeManagers() {
        worldManager = new WorldManager(this);
        playerManager = new PlayerManager(this);
        rankManager = new RankManager(this);
        cellManager = new CellManager(this);
        mineManager = new MineManager(this);
        economyManager = new EconomyManager(this);
    }

    private void registerCommands() {
        getCommand("prison").setExecutor(new PrisonCommand(this));
        getCommand("prisonworld").setExecutor(new PrisonWorldCommand(this));
        getCommand("cell").setExecutor(new CellCommand(this));
        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("mine").setExecutor(new MineCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
    }

    public static PrisonNEO getInstance() {
        return instance;
    }

    // Getters for managers
    public WorldManager getWorldManager() { return worldManager; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public RankManager getRankManager() { return rankManager; }
    public CellManager getCellManager() { return cellManager; }
    public MineManager getMineManager() { return mineManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
}
