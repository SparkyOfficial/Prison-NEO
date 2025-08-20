package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    
    private final PrisonNEO plugin;
    private final Map<UUID, PrisonPlayer> players;
    private File dataFile;
    private FileConfiguration dataConfig;
    
    public PlayerManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.players = new HashMap<>();
        setupDataFile();
        loadPlayerData();
    }
    
    private void setupDataFile() {
        dataFile = new File(plugin.getDataFolder(), "players.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            plugin.saveResource("players.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    public PrisonPlayer getPrisonPlayer(Player player) {
        return getPrisonPlayer(player.getUniqueId());
    }
    
    public PrisonPlayer getPrisonPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, k -> new PrisonPlayer(uuid));
    }
    
    public void addPlayer(Player player) {
        PrisonPlayer prisonPlayer = new PrisonPlayer(player.getUniqueId());
        prisonPlayer.setName(player.getName());
        prisonPlayer.setRank("D"); // Starting rank
        prisonPlayer.setMoney(0);
        prisonPlayer.setSentence(72); // 72 hours default sentence
        players.put(player.getUniqueId(), prisonPlayer);
        
        // Teleport to prison
        plugin.getWorldManager().teleportToPrison(player);
    }
    
    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }
    
    public void savePlayerData(PrisonPlayer prisonPlayer) {
        String path = "players." + prisonPlayer.getUuid().toString();
        dataConfig.set(path + ".name", prisonPlayer.getName());
        dataConfig.set(path + ".rank", prisonPlayer.getRank());
        dataConfig.set(path + ".money", prisonPlayer.getMoney());
        dataConfig.set(path + ".sentence", prisonPlayer.getSentence());
        dataConfig.set(path + ".cellId", prisonPlayer.getCellId());
        dataConfig.set(path + ".playTime", prisonPlayer.getPlayTime());
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data: " + e.getMessage());
        }
    }
    
    private void loadPlayerData() {
        if (dataConfig.getConfigurationSection("players") == null) return;
        
        for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            String path = "players." + uuidString;
            
            PrisonPlayer prisonPlayer = new PrisonPlayer(uuid);
            prisonPlayer.setName(dataConfig.getString(path + ".name", "Unknown"));
            prisonPlayer.setRank(dataConfig.getString(path + ".rank", "D"));
            prisonPlayer.setMoney(dataConfig.getDouble(path + ".money", 0));
            prisonPlayer.setSentence(dataConfig.getInt(path + ".sentence", 72));
            prisonPlayer.setCellId(dataConfig.getString(path + ".cellId", null));
            prisonPlayer.setPlayTime(dataConfig.getLong(path + ".playTime", 0));
            
            players.put(uuid, prisonPlayer);
        }
    }
    
    public void saveData() {
        for (PrisonPlayer prisonPlayer : players.values()) {
            savePlayerData(prisonPlayer);
        }
    }
    
    public Map<UUID, PrisonPlayer> getAllPlayers() {
        return players;
    }
    
    // Additional methods needed by other managers
    public void addSentenceTime(Player player, int minutes) {
        PrisonPlayer prisonPlayer = getPrisonPlayer(player);
        prisonPlayer.setSentenceTime(prisonPlayer.getSentenceTime() + minutes);
        player.sendMessage("§c+" + minutes + " минут к сроку заключения!");
    }
    
    public void reduceSentenceTime(Player player, int minutes) {
        PrisonPlayer prisonPlayer = getPrisonPlayer(player);
        int currentTime = prisonPlayer.getSentenceTime();
        prisonPlayer.setSentenceTime(Math.max(0, currentTime - minutes));
        player.sendMessage("§a-" + minutes + " минут от срока заключения!");
    }
    
    public void setSentenceTime(Player player, int minutes) {
        PrisonPlayer prisonPlayer = getPrisonPlayer(player);
        prisonPlayer.setSentenceTime(minutes);
        player.sendMessage("§eСрок заключения установлен: " + minutes + " минут");
    }
}
