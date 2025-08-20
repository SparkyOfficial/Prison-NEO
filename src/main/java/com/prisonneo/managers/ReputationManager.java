package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReputationManager {
    
    private final PrisonNEO plugin;
    private final Map<UUID, Integer> playerReputation;
    
    public ReputationManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.playerReputation = new HashMap<>();
    }
    
    public void addReputation(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int current = playerReputation.getOrDefault(uuid, 0);
        playerReputation.put(uuid, Math.max(-100, Math.min(100, current + amount)));
        
        if (amount > 0) {
            player.sendMessage("§a+" + amount + " репутация");
        } else {
            player.sendMessage("§c" + amount + " репутация");
        }
    }
    
    public int getReputation(Player player) {
        return playerReputation.getOrDefault(player.getUniqueId(), 0);
    }
    
    public String getReputationLevel(Player player) {
        int rep = getReputation(player);
        
        if (rep >= 80) return "§aЛегенда";
        if (rep >= 60) return "§2Уважаемый";
        if (rep >= 40) return "§eИзвестный";
        if (rep >= 20) return "§fОбычный";
        if (rep >= 0) return "§7Новичок";
        if (rep >= -20) return "§6Подозрительный";
        if (rep >= -40) return "§cПроблемный";
        if (rep >= -60) return "§4Опасный";
        return "§4§lВраг";
    }
    
    public double getReputationMultiplier(Player player) {
        int rep = getReputation(player);
        return 1.0 + (rep * 0.01); // 1% per reputation point
    }
}
