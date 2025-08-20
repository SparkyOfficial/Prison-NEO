package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SecurityManager {
    
    private final PrisonNEO plugin;
    private final Map<UUID, Integer> securityLevel;
    private final Map<UUID, Long> lastViolation;
    private final Set<Location> securityCameras;
    private final Set<Location> metalDetectors;
    
    public SecurityManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.securityLevel = new HashMap<>();
        this.lastViolation = new HashMap<>();
        this.securityCameras = new HashSet<>();
        this.metalDetectors = new HashSet<>();
        
        setupSecurityDevices();
        startSecurityPatrols();
    }
    
    private void setupSecurityDevices() {
        // Security cameras at key locations
        securityCameras.add(new Location(plugin.getWorldManager().getPrisonWorld(), 0, 65, -95)); // Main entrance
        securityCameras.add(new Location(plugin.getWorldManager().getPrisonWorld(), -60, 65, -60)); // Block A
        securityCameras.add(new Location(plugin.getWorldManager().getPrisonWorld(), 60, 65, -60)); // Block B
        securityCameras.add(new Location(plugin.getWorldManager().getPrisonWorld(), -60, 65, 60)); // Block C
        securityCameras.add(new Location(plugin.getWorldManager().getPrisonWorld(), 60, 65, 60)); // Block D
        securityCameras.add(new Location(plugin.getWorldManager().getPrisonWorld(), 0, 65, 0)); // Yard center
        
        // Metal detectors at entrances
        metalDetectors.add(new Location(plugin.getWorldManager().getPrisonWorld(), 0, 62, -90));
        metalDetectors.add(new Location(plugin.getWorldManager().getPrisonWorld(), -70, 62, 5)); // Kitchen entrance
        metalDetectors.add(new Location(plugin.getWorldManager().getPrisonWorld(), 70, 62, 5)); // Workshop entrance
    }
    
    private void startSecurityPatrols() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            performSecuritySweep();
        }, 0L, 6000L); // Every 5 minutes
    }
    
    private void performSecuritySweep() {
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            checkPlayerSecurity(player);
        }
    }
    
    private void checkPlayerSecurity(Player player) {
        Location loc = player.getLocation();
        
        // Check if near security camera
        for (Location camera : securityCameras) {
            if (camera.distance(loc) <= 10) {
                // Reduce contraband suspicion near cameras
                if (plugin.getContrabandManager().getSuspicionLevel(player) > 0) {
                    player.sendMessage("§7Камера наблюдения фиксирует ваше поведение...");
                }
                break;
            }
        }
        
        // Check metal detectors
        for (Location detector : metalDetectors) {
            if (detector.distance(loc) <= 2) {
                performMetalDetection(player);
                break;
            }
        }
        
        // Random security checks
        if (Math.random() < 0.02) { // 2% chance
            performRandomCheck(player);
        }
    }
    
    private void performMetalDetection(Player player) {
        boolean hasContraband = false;
        
        // Check for contraband items
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isContrabandItem(item.getType())) {
                hasContraband = true;
                break;
            }
        }
        
        if (hasContraband) {
            player.sendMessage("§4§lМЕТАЛЛОДЕТЕКТОР СРАБОТАЛ!");
            triggerSecurityAlert(player, "Металлодетектор");
        } else {
            player.sendMessage("§aМеталлодетектор: чисто");
        }
    }
    
    private boolean isContrabandItem(Material material) {
        return material == Material.IRON_SWORD || material == Material.IRON_AXE || 
               material == Material.SHEARS || material == Material.FLINT_AND_STEEL ||
               material == Material.TNT || material == Material.GUNPOWDER;
    }
    
    private void performRandomCheck(Player player) {
        int currentLevel = getSecurityLevel(player);
        
        if (currentLevel > 3) {
            player.sendMessage("§cОхранник подозрительно смотрит на вас...");
            
            if (Math.random() < 0.3) { // 30% chance for high security players
                triggerSecurityAlert(player, "Подозрительное поведение");
            }
        }
    }
    
    public void triggerSecurityAlert(Player player, String reason) {
        UUID uuid = player.getUniqueId();
        
        // Increase security level
        int current = securityLevel.getOrDefault(uuid, 0);
        securityLevel.put(uuid, Math.min(10, current + 1));
        lastViolation.put(uuid, System.currentTimeMillis());
        
        // Broadcast alert
        Bukkit.broadcastMessage("§4§l[ТРЕВОГА] " + player.getName() + " - " + reason);
        
        // Apply security measures
        applySecurityMeasures(player, current + 1);
        
        // Reputation penalty
        plugin.getReputationManager().addReputation(player, -10);
    }
    
    private void applySecurityMeasures(Player player, int level) {
        switch (level) {
            case 1:
                player.sendMessage("§eПервое предупреждение от охраны");
                break;
            case 2:
                player.sendMessage("§6Второе предупреждение! Будьте осторожны!");
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1200, 0));
                break;
            case 3:
                player.sendMessage("§cТретье предупреждение! Усиленное наблюдение!");
                plugin.getEconomyManager().removeMoney(player, 50.0);
                break;
            case 4:
                player.sendMessage("§4Временный арест!");
                teleportToSolitary(player, 5); // 5 minutes
                break;
            case 5:
                player.sendMessage("§4§lМАКСИМАЛЬНАЯ БЕЗОПАСНОСТЬ!");
                teleportToSolitary(player, 15); // 15 minutes
                plugin.getPlayerManager().addSentenceTime(player, 12 * 60); // 12 hours
                break;
            default:
                if (level > 5) {
                    player.sendMessage("§4§lОПАСНЫЙ ЗАКЛЮЧЁННЫЙ!");
                    teleportToSolitary(player, 30); // 30 minutes
                    plugin.getPlayerManager().addSentenceTime(player, 24 * 60); // 24 hours
                }
                break;
        }
    }
    
    private void teleportToSolitary(Player player, int minutes) {
        Location solitary = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 50, -150);
        player.teleport(solitary);
        
        player.sendMessage("§4Карцер на " + minutes + " минут!");
        
        // Release after time
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                Location yard = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 62, 0);
                player.teleport(yard);
                player.sendMessage("§eВы освобождены из карцера.");
            }
        }, minutes * 1200L); // minutes to ticks
    }
    
    public void reduceSecurityLevel(Player player) {
        UUID uuid = player.getUniqueId();
        int current = securityLevel.getOrDefault(uuid, 0);
        if (current > 0) {
            securityLevel.put(uuid, current - 1);
            player.sendMessage("§aУровень безопасности снижен до " + (current - 1));
        }
    }
    
    public int getSecurityLevel(Player player) {
        return securityLevel.getOrDefault(player.getUniqueId(), 0);
    }
    
    public String getSecurityLevelName(int level) {
        switch (level) {
            case 0: return "§aМинимальная";
            case 1: return "§eНизкая";
            case 2: return "§6Средняя";
            case 3: return "§cВысокая";
            case 4: return "§4Критическая";
            default: return "§4§lМАКСИМАЛЬНАЯ";
        }
    }
    
    public void startLockdown(int minutes) {
        Bukkit.broadcastMessage("§4§l[ЛОКДАУН] Все заключённые по камерам!");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            plugin.getCellManager().teleportToCell(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, minutes * 1200, 1));
        }
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage("§aЛокдаун окончен.");
        }, minutes * 1200L);
    }
    
    public void performMassSearch() {
        Bukkit.broadcastMessage("§4§l[ОБЫСК] Массовый обыск заключённых!");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            plugin.getContrabandManager().performSearch(player);
        }
    }
}
