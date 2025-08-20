package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EventManager {
    
    private final PrisonNEO plugin;
    private final Random random;
    private boolean lockdownActive;
    private boolean riotActive;
    
    public EventManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.lockdownActive = false;
        this.riotActive = false;
        startEventScheduler();
    }
    
    private void startEventScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getWorldManager().getPrisonWorld() == null) return;
                
                double eventChance = 0.05; // 5% chance every 10 minutes
                
                if (random.nextDouble() < eventChance) {
                    triggerRandomEvent();
                }
            }
        }.runTaskTimer(plugin, 0L, 12000L); // Every 10 minutes
    }
    
    private void triggerRandomEvent() {
        PrisonEvent[] events = {
            PrisonEvent.LOCKDOWN,
            PrisonEvent.RIOT,
            PrisonEvent.INSPECTION,
            PrisonEvent.VISITOR_DAY,
            PrisonEvent.FOOD_SHORTAGE,
            PrisonEvent.POWER_OUTAGE
        };
        
        PrisonEvent randomEvent = events[random.nextInt(events.length)];
        executeEvent(randomEvent);
    }
    
    public void triggerEvent(String eventType) {
        switch (eventType.toLowerCase()) {
            case "lockdown":
                startLockdown();
                break;
            case "riot":
                plugin.getRiotManager().startRiot();
                break;
            case "inspection":
                startInspection();
                break;
            case "visitor":
                startVisitorDay();
                break;
            case "food":
                startFoodShortage();
                break;
            case "power":
                startPowerOutage();
                break;
        }
    }
    
    private void executeEvent(PrisonEvent event) {
        switch (event) {
            case LOCKDOWN:
                startLockdown();
                break;
            case RIOT:
                plugin.getRiotManager().startRiot();
                break;
            case INSPECTION:
                startInspection();
                break;
            case VISITOR_DAY:
                startVisitorDay();
                break;
            case FOOD_SHORTAGE:
                startFoodShortage();
                break;
            case POWER_OUTAGE:
                startPowerOutage();
                break;
        }
    }
    
    private void startLockdown() {
        if (lockdownActive) return;
        
        lockdownActive = true;
        Bukkit.broadcastMessage("§4§l🚨 РЕЖИМ ИЗОЛЯЦИИ! 🚨");
        Bukkit.broadcastMessage("§cВсе заключённые должны вернуться в камеры!");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            plugin.getCellManager().teleportToCell(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 6000, 1));
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                endLockdown();
            }
        }.runTaskLater(plugin, 6000L);
    }
    
    private void endLockdown() {
        lockdownActive = false;
        Bukkit.broadcastMessage("§a§lРежим изоляции отменён!");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            player.removePotionEffect(PotionEffectType.SLOWNESS);
        }
    }
    
    private void startRiot() {
        if (riotActive) return;
        
        riotActive = true;
        Bukkit.broadcastMessage("§4§l⚔️ БУНТ В ТЮРЬМЕ! ⚔️");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
            
            if (!prisonPlayer.getRank().equals("GUARD")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 3600, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 0));
            }
        }
        
        plugin.getWorldManager().getPrisonWorld().setPVP(true);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                endRiot();
            }
        }.runTaskLater(plugin, 3600L);
    }
    
    private void endRiot() {
        riotActive = false;
        Bukkit.broadcastMessage("§a§lБунт подавлен!");
        
        plugin.getWorldManager().getPrisonWorld().setPVP(false);
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            player.removePotionEffect(PotionEffectType.STRENGTH);
            player.removePotionEffect(PotionEffectType.SPEED);
            
            PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
            if (!prisonPlayer.getRank().equals("GUARD")) {
                prisonPlayer.setSentence(prisonPlayer.getSentence() + 6);
            }
        }
    }
    
    private void startInspection() {
        Bukkit.broadcastMessage("§6§l🔍 ВНЕЗАПНАЯ ПРОВЕРКА! 🔍");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getContrabandManager().performSearch(player);
                }
            }.runTaskLater(plugin, random.nextInt(100) + 20L);
        }
    }
    
    private void startVisitorDay() {
        Bukkit.broadcastMessage("§b§l👥 ДЕНЬ ПОСЕЩЕНИЙ! 👥");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
            double bonus = 50 + random.nextDouble() * 100;
            prisonPlayer.addMoney(bonus);
            player.sendMessage("§aВы получили $" + String.format("%.2f", bonus) + " от посетителей!");
        }
    }
    
    private void startFoodShortage() {
        Bukkit.broadcastMessage("§c§l🍞 НЕХВАТКА ЕДЫ! 🍞");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            player.setFoodLevel(Math.max(1, player.getFoodLevel() - 10));
            player.sendMessage("§cВы голодны! Найдите еду или купите в магазине!");
        }
    }
    
    private void startPowerOutage() {
        Bukkit.broadcastMessage("§8§l⚡ ОТКЛЮЧЕНИЕ ЭЛЕКТРИЧЕСТВА! ⚡");
        
        // Remove all light sources temporarily
        Location center = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 65, 0);
        for (int x = -200; x <= 200; x += 10) {
            for (int z = -200; z <= 200; z += 10) {
                for (int y = 60; y <= 80; y++) {
                    Location loc = new Location(plugin.getWorldManager().getPrisonWorld(), x, y, z);
                    if (loc.getBlock().getType() == Material.TORCH || 
                        loc.getBlock().getType() == Material.GLOWSTONE) {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
        
        // Give all players night vision temporarily
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 2400, 0));
            player.sendMessage("§8Электричество отключено! Временное ночное зрение активировано.");
        }
        
        // Restore power after 2 minutes
        new BukkitRunnable() {
            @Override
            public void run() {
                restorePower();
            }
        }.runTaskLater(plugin, 2400L);
    }
    
    private void restorePower() {
        Bukkit.broadcastMessage("§a§lЭлектричество восстановлено!");
        
        // Restore lighting (simplified)
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }
    
    public boolean isLockdownActive() { return lockdownActive; }
    public boolean isRiotActive() { return riotActive; }
    
    private enum PrisonEvent {
        LOCKDOWN, RIOT, INSPECTION, VISITOR_DAY, FOOD_SHORTAGE, POWER_OUTAGE
    }
}
