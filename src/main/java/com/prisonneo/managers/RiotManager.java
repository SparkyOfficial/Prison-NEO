package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class RiotManager {
    
    private final PrisonNEO plugin;
    private boolean riotActive;
    private final Set<UUID> riotParticipants;
    private final Map<UUID, Integer> riotPoints;
    private long riotStartTime;
    
    public RiotManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.riotActive = false;
        this.riotParticipants = new HashSet<>();
        this.riotPoints = new HashMap<>();
    }
    
    public void startRiot() {
        if (riotActive) return;
        
        riotActive = true;
        riotStartTime = System.currentTimeMillis();
        
        // Broadcast riot start
        Bukkit.broadcastMessage("§4§l[БУНТ] В тюрьме начался бунт!");
        Bukkit.broadcastMessage("§cЗаключённые взбунтовались! Охрана пытается восстановить порядок!");
        
        // Play alarm sound
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            player.sendMessage("§4§lТРЕВОГА! БУНТ В ТЮРЬМЕ!");
        }
        
        // Start riot effects
        startRiotEffects();
        
        // Auto-end riot after 15 minutes
        Bukkit.getScheduler().runTaskLater(plugin, this::endRiot, 18000L);
    }
    
    private void startRiotEffects() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!riotActive) return;
            
            for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
                // Random chaos effects
                if (Math.random() < 0.1) { // 10% chance per tick
                    applyRiotEffect(player);
                }
            }
        }, 0L, 100L); // Every 5 seconds
    }
    
    private void applyRiotEffect(Player player) {
        int effect = (int)(Math.random() * 5);
        
        switch (effect) {
            case 0: // Smoke
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                player.sendMessage("§7Дым от пожаров затрудняет видимость!");
                break;
            case 1: // Panic
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 0));
                player.sendMessage("§6Паника охватывает вас!");
                break;
            case 2: // Debris damage
                player.damage(2.0);
                player.sendMessage("§cВас ударил летящий обломок!");
                break;
            case 3: // Noise
                player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1.0f, 0.8f);
                player.sendMessage("§eВокруг слышны звуки разрушения!");
                break;
            case 4: // Crowd push
                Location loc = player.getLocation();
                loc.add((Math.random() - 0.5) * 4, 0, (Math.random() - 0.5) * 4);
                player.teleport(loc);
                player.sendMessage("§6Толпа сносит вас!");
                break;
        }
    }
    
    public void joinRiot(Player player) {
        if (!riotActive) {
            player.sendMessage("§cСейчас нет активного бунта!");
            return;
        }
        
        UUID uuid = player.getUniqueId();
        if (riotParticipants.contains(uuid)) {
            player.sendMessage("§cВы уже участвуете в бунте!");
            return;
        }
        
        riotParticipants.add(uuid);
        riotPoints.put(uuid, 0);
        
        player.sendMessage("§4Вы присоединились к бунту!");
        player.sendMessage("§eБейте блоки, атакуйте охрану для очков!");
        
        // Give riot effects
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
        
        // Reputation penalty for joining riot
        plugin.getReputationManager().addReputation(player, -15);
    }
    
    public void leaveRiot(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (!riotParticipants.contains(uuid)) {
            player.sendMessage("§cВы не участвуете в бунте!");
            return;
        }
        
        riotParticipants.remove(uuid);
        player.removePotionEffect(PotionEffectType.STRENGTH);
        player.removePotionEffect(PotionEffectType.SPEED);
        
        player.sendMessage("§eВы покинули бунт.");
        
        // Small reputation bonus for leaving
        plugin.getReputationManager().addReputation(player, 5);
    }
    
    public void addRiotPoints(Player player, int points) {
        UUID uuid = player.getUniqueId();
        if (!riotParticipants.contains(uuid)) return;
        
        int current = riotPoints.getOrDefault(uuid, 0);
        riotPoints.put(uuid, current + points);
        
        player.sendMessage("§6+" + points + " очков бунта! Всего: " + (current + points));
    }
    
    public void endRiot() {
        if (!riotActive) return;
        
        riotActive = false;
        
        // Calculate riot duration
        long duration = (System.currentTimeMillis() - riotStartTime) / 1000 / 60; // minutes
        
        Bukkit.broadcastMessage("§a§l[ПОРЯДОК] Бунт подавлен!");
        Bukkit.broadcastMessage("§eБунт длился " + duration + " минут");
        
        // Reward/punish participants
        for (UUID uuid : riotParticipants) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            
            int points = riotPoints.getOrDefault(uuid, 0);
            
            // Remove riot effects
            player.removePotionEffect(PotionEffectType.STRENGTH);
            player.removePotionEffect(PotionEffectType.SPEED);
            
            if (points > 50) {
                // High participation - big punishment
                plugin.getEconomyManager().removeMoney(player, 300.0);
                plugin.getReputationManager().addReputation(player, -40);
                plugin.getPlayerManager().addSentenceTime(player, 36 * 60); // 36 hours
                player.sendMessage("§4Серьёзное участие в бунте! Суровое наказание!");
            } else if (points > 20) {
                // Medium participation
                plugin.getEconomyManager().removeMoney(player, 150.0);
                plugin.getReputationManager().addReputation(player, -25);
                plugin.getPlayerManager().addSentenceTime(player, 18 * 60); // 18 hours
                player.sendMessage("§cУчастие в бунте наказано!");
            } else {
                // Low participation
                plugin.getEconomyManager().removeMoney(player, 50.0);
                plugin.getReputationManager().addReputation(player, -10);
                plugin.getPlayerManager().addSentenceTime(player, 6 * 60); // 6 hours
                player.sendMessage("§eМинимальное наказание за участие в бунте.");
            }
        }
        
        // Clear riot data
        riotParticipants.clear();
        riotPoints.clear();
        
        // Start lockdown period
        startPostRiotLockdown();
    }
    
    private void startPostRiotLockdown() {
        Bukkit.broadcastMessage("§4§lЛОКДАУН! Все заключённые по камерам!");
        
        // Teleport all players to their cells
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            plugin.getCellManager().teleportToCell(player);
            player.sendMessage("§cЛокдаун на 5 минут после бунта!");
        }
        
        // End lockdown after 5 minutes
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage("§aЛокдаун окончен. Заключённые могут покинуть камеры.");
        }, 6000L); // 5 minutes
    }
    
    public boolean isRiotActive() {
        return riotActive;
    }
    
    public boolean isParticipating(Player player) {
        return riotParticipants.contains(player.getUniqueId());
    }
    
    public int getRiotPoints(Player player) {
        return riotPoints.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void forceEndRiot() {
        if (riotActive) {
            endRiot();
        }
    }
}
