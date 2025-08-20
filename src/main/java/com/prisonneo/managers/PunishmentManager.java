package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PunishmentManager {
    
    private final PrisonNEO plugin;
    private final Map<UUID, List<String>> playerViolations;
    private final Map<UUID, Long> punishmentEndTimes;
    
    public PunishmentManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.playerViolations = new HashMap<>();
        this.punishmentEndTimes = new HashMap<>();
    }
    
    public void addViolation(Player player, String violation) {
        UUID uuid = player.getUniqueId();
        List<String> violations = playerViolations.getOrDefault(uuid, new ArrayList<>());
        violations.add(violation);
        playerViolations.put(uuid, violations);
        
        int violationCount = violations.size();
        applyPunishment(player, violation, violationCount);
    }
    
    private void applyPunishment(Player player, String violation, int count) {
        player.sendMessage("§4Нарушение: " + violation);
        player.sendMessage("§cВсего нарушений: " + count);
        
        switch (count) {
            case 1:
                // Warning
                player.sendMessage("§eПредупреждение! Следующее нарушение будет наказано!");
                plugin.getReputationManager().addReputation(player, -5);
                break;
            case 2:
                // Fine
                plugin.getEconomyManager().removeMoney(player, 25.0);
                player.sendMessage("§cШтраф: $25");
                plugin.getReputationManager().addReputation(player, -10);
                break;
            case 3:
                // Solitary confinement
                sendToSolitary(player, 5);
                plugin.getReputationManager().addReputation(player, -15);
                break;
            case 4:
                // Extended solitary + sentence increase
                sendToSolitary(player, 15);
                plugin.getPlayerManager().addSentenceTime(player, 6 * 60);
                plugin.getReputationManager().addReputation(player, -25);
                break;
            default:
                // Maximum security
                sendToMaxSecurity(player);
                plugin.getPlayerManager().addSentenceTime(player, 24 * 60);
                plugin.getReputationManager().addReputation(player, -50);
                break;
        }
    }
    
    private void sendToSolitary(Player player, int minutes) {
        Location solitary = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 50, -150);
        player.teleport(solitary);
        
        punishmentEndTimes.put(player.getUniqueId(), System.currentTimeMillis() + (minutes * 60 * 1000L));
        
        player.sendMessage("§4Карцер на " + minutes + " минут!");
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            releasePunishment(player);
        }, minutes * 1200L);
    }
    
    private void sendToMaxSecurity(Player player) {
        Location maxSec = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 45, -180);
        player.teleport(maxSec);
        
        punishmentEndTimes.put(player.getUniqueId(), System.currentTimeMillis() + (60 * 60 * 1000L));
        
        player.sendMessage("§4§lМАКСИМАЛЬНАЯ БЕЗОПАСНОСТЬ - 1 час!");
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 72000, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 72000, 1));
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            releasePunishment(player);
        }, 72000L);
    }
    
    private void releasePunishment(Player player) {
        if (!player.isOnline()) return;
        
        punishmentEndTimes.remove(player.getUniqueId());
        
        Location yard = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 62, 0);
        player.teleport(yard);
        
        player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        
        player.sendMessage("§aВы освобождены из наказания.");
    }
    
    public boolean isInPunishment(Player player) {
        return punishmentEndTimes.containsKey(player.getUniqueId());
    }
    
    public int getViolationCount(Player player) {
        return playerViolations.getOrDefault(player.getUniqueId(), new ArrayList<>()).size();
    }
    
    public void clearViolations(Player player) {
        playerViolations.remove(player.getUniqueId());
        player.sendMessage("§aВаши нарушения очищены администратором.");
    }
}
