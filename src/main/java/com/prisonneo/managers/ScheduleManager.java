package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.LocalTime;
import java.util.*;

public class ScheduleManager {
    
    private final PrisonNEO plugin;
    private final Map<UUID, Boolean> playerCompliance;
    private ScheduleEvent currentEvent;
    
    public enum ScheduleEvent {
        WAKE_UP(6, 0, "Подъём"),
        BREAKFAST(7, 0, "Завтрак"),
        WORK_TIME(8, 0, "Рабочее время"),
        LUNCH(12, 0, "Обед"),
        RECREATION(14, 0, "Отдых"),
        DINNER(18, 0, "Ужин"),
        CELL_TIME(21, 0, "Отбой"),
        LIGHTS_OUT(22, 0, "Тишина");
        
        private final int hour;
        private final int minute;
        private final String name;
        
        ScheduleEvent(int hour, int minute, String name) {
            this.hour = hour;
            this.minute = minute;
            this.name = name;
        }
        
        public int getHour() { return hour; }
        public int getMinute() { return minute; }
        public String getName() { return name; }
    }
    
    public ScheduleManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.playerCompliance = new HashMap<>();
        this.currentEvent = ScheduleEvent.WAKE_UP;
        
        startScheduleTimer();
    }
    
    private void startScheduleTimer() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            checkSchedule();
        }, 0L, 1200L); // Every minute
    }
    
    private void checkSchedule() {
        LocalTime now = LocalTime.now();
        
        for (ScheduleEvent event : ScheduleEvent.values()) {
            if (now.getHour() == event.getHour() && now.getMinute() == event.getMinute()) {
                triggerScheduleEvent(event);
                break;
            }
        }
    }
    
    public void triggerScheduleEvent(ScheduleEvent event) {
        currentEvent = event;
        
        Bukkit.broadcastMessage("§6§l[РАСПИСАНИЕ] " + event.getName().toUpperCase());
        
        switch (event) {
            case WAKE_UP:
                handleWakeUp();
                break;
            case BREAKFAST:
                handleMealTime("завтрак", new Location(plugin.getWorldManager().getPrisonWorld(), -30, 63, 70));
                break;
            case WORK_TIME:
                handleWorkTime();
                break;
            case LUNCH:
                handleMealTime("обед", new Location(plugin.getWorldManager().getPrisonWorld(), -30, 63, 70));
                break;
            case RECREATION:
                handleRecreationTime();
                break;
            case DINNER:
                handleMealTime("ужин", new Location(plugin.getWorldManager().getPrisonWorld(), -30, 63, 70));
                break;
            case CELL_TIME:
                handleCellTime();
                break;
            case LIGHTS_OUT:
                handleLightsOut();
                break;
        }
    }
    
    private void handleWakeUp() {
        Bukkit.broadcastMessage("§eВсе заключённые должны встать и покинуть камеры!");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            player.sendMessage("§aПодъём! Время начинать новый день в тюрьме.");
            
            // Remove sleep effects
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            
            // Teleport to yard if in cell
            Location yard = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 62, 0);
            if (isInCellArea(player.getLocation())) {
                player.teleport(yard);
            }
        }
    }
    
    private void handleMealTime(String mealName, Location diningLocation) {
        Bukkit.broadcastMessage("§eВремя " + mealName + "! Все в столовую!");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            player.sendMessage("§6Идите в столовую для получения " + mealName + "!");
            
            // Give hunger bonus if near dining area
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.getLocation().distance(diningLocation) <= 15) {
                    player.setFoodLevel(20);
                    player.setSaturation(20);
                    player.sendMessage("§aВы поели " + mealName + "!");
                    plugin.getReputationManager().addReputation(player, 1);
                    playerCompliance.put(player.getUniqueId(), true);
                } else {
                    player.sendMessage("§cВы пропустили " + mealName + "!");
                    plugin.getPunishmentManager().addViolation(player, "Пропуск " + mealName);
                }
            }, 600L); // 30 seconds to get to dining
        }
    }
    
    private void handleWorkTime() {
        Bukkit.broadcastMessage("§eРабочее время! Идите на работу или в шахты!");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            if (plugin.getJobManager().hasActiveJob(player)) {
                player.sendMessage("§aВремя работать! Идите к вашему рабочему месту.");
            } else {
                player.sendMessage("§eИдите в шахты или найдите работу!");
            }
        }
        
        // Check compliance after 10 minutes
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            checkWorkCompliance();
        }, 12000L);
    }
    
    private void checkWorkCompliance() {
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            boolean working = plugin.getJobManager().hasActiveJob(player) || isInMineArea(player.getLocation());
            
            if (!working) {
                plugin.getPunishmentManager().addViolation(player, "Отказ от работы");
                player.sendMessage("§cВы должны работать в рабочее время!");
            } else {
                plugin.getReputationManager().addReputation(player, 2);
            }
        }
    }
    
    private void handleRecreationTime() {
        Bukkit.broadcastMessage("§eВремя отдыха! Можете играть в мини-игры или общаться.");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            player.sendMessage("§aСвободное время! Отдыхайте в дворе или играйте в мини-игры.");
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 6000, 0));
        }
    }
    
    private void handleCellTime() {
        Bukkit.broadcastMessage("§eВремя по камерам! Все заключённые должны вернуться в камеры!");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            plugin.getCellManager().teleportToCell(player);
            player.sendMessage("§7Отбой. Время отдыхать в камере.");
        }
    }
    
    private void handleLightsOut() {
        Bukkit.broadcastMessage("§8Тишина! Все должны спать!");
        
        for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
            if (!isInCellArea(player.getLocation())) {
                plugin.getPunishmentManager().addViolation(player, "Нарушение тишины");
                plugin.getCellManager().teleportToCell(player);
            }
            
            // Apply night effects
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 36000, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 36000, 0));
            player.sendMessage("§8Спокойной ночи...");
        }
    }
    
    private boolean isInCellArea(Location location) {
        // Check if player is in any cell block area
        int x = (int) location.getX();
        int z = (int) location.getZ();
        
        return (x >= -80 && x <= -40 && z >= -80 && z <= -40) || // Block A
               (x >= 40 && x <= 80 && z >= -80 && z <= -40) ||   // Block B
               (x >= -80 && x <= -40 && z >= 40 && z <= 80) ||   // Block C
               (x >= 40 && x <= 80 && z >= 40 && z <= 80);       // Block D
    }
    
    private boolean isInMineArea(Location location) {
        int x = (int) location.getX();
        int z = (int) location.getZ();
        
        return (x >= -150 && x <= -110 && z >= -150 && z <= -110) || // Mine A
               (x >= 110 && x <= 150 && z >= -150 && z <= -110) ||   // Mine B
               (x >= -150 && x <= -110 && z >= 110 && z <= 150) ||   // Mine C
               (x >= 110 && x <= 150 && z >= 110 && z <= 150);       // Mine D
    }
    
    public ScheduleEvent getCurrentEvent() {
        return currentEvent;
    }
    
    public String getScheduleInfo() {
        StringBuilder info = new StringBuilder("§6=== Расписание тюрьмы ===\n");
        for (ScheduleEvent event : ScheduleEvent.values()) {
            String time = String.format("%02d:%02d", event.getHour(), event.getMinute());
            info.append("§e").append(time).append(" §f- ").append(event.getName()).append("\n");
        }
        return info.toString();
    }
    
    public boolean isPlayerCompliant(Player player) {
        return playerCompliance.getOrDefault(player.getUniqueId(), false);
    }
}
