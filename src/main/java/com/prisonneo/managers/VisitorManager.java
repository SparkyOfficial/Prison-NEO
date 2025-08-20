package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class VisitorManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<UUID, String> scheduledVisits;
    private final Map<UUID, Long> lastVisitTime;
    private final Set<UUID> activeVisits;
    
    public VisitorManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.scheduledVisits = new HashMap<>();
        this.lastVisitTime = new HashMap<>();
        this.activeVisits = new HashSet<>();
        
        startVisitorScheduler();
    }
    
    public void openVisitorMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§bСвидания");
        
        // Schedule visit options
        createVisitOption(inv, 10, "family", "§aСемья", 0);
        createVisitOption(inv, 12, "lawyer", "§6Адвокат", 100);
        createVisitOption(inv, 14, "friend", "§eДруг", 50);
        createVisitOption(inv, 16, "business", "§9Деловой партнёр", 200);
        
        // Current visit status
        if (hasScheduledVisit(player)) {
            ItemStack status = new ItemStack(Material.CLOCK);
            ItemMeta meta = status.getItemMeta();
            meta.setDisplayName("§fЗапланированное свидание");
            meta.setLore(Arrays.asList(
                "§7Тип: §e" + getVisitTypeDisplay(scheduledVisits.get(player.getUniqueId())),
                "§7Статус: §aОжидание"
            ));
            status.setItemMeta(meta);
            inv.setItem(22, status);
        }
        
        player.openInventory(inv);
    }
    
    private void createVisitOption(Inventory inv, int slot, String type, String name, int cost) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        
        List<String> lore = new ArrayList<>();
        lore.add("§7Стоимость: §a$" + cost);
        lore.add("§7Длительность: §e10 минут");
        lore.add("§7Эффект: §b+" + getVisitBenefit(type));
        meta.setLore(lore);
        
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }
    
    private String getVisitBenefit(String type) {
        switch (type) {
            case "family": return "Настроение, -Стресс";
            case "lawyer": return "Информация о деле";
            case "friend": return "Моральная поддержка";
            case "business": return "Деньги, Связи";
            default: return "Неизвестно";
        }
    }
    
    private String getVisitTypeDisplay(String type) {
        switch (type) {
            case "family": return "Семья";
            case "lawyer": return "Адвокат";
            case "friend": return "Друг";
            case "business": return "Деловой партнёр";
            default: return "Неизвестно";
        }
    }
    
    public boolean scheduleVisit(Player player, String visitType) {
        UUID uuid = player.getUniqueId();
        
        // Check cooldown
        if (lastVisitTime.containsKey(uuid)) {
            long lastVisit = lastVisitTime.get(uuid);
            if (System.currentTimeMillis() - lastVisit < 3600000) { // 1 hour cooldown
                player.sendMessage("§cВы недавно имели свидание! Подождите час.");
                return false;
            }
        }
        
        if (hasScheduledVisit(player)) {
            player.sendMessage("§cУ вас уже запланировано свидание!");
            return false;
        }
        
        // Check cost
        int cost = getVisitCost(visitType);
        if (!plugin.getEconomyManager().removeMoney(player, cost)) {
            player.sendMessage("§cНедостаточно денег! Нужно: $" + cost);
            return false;
        }
        
        scheduledVisits.put(uuid, visitType);
        player.sendMessage("§aСвидание запланировано: " + getVisitTypeDisplay(visitType));
        player.sendMessage("§eОжидайте вызова в комнату свиданий...");
        
        // Schedule the actual visit
        int delay = 1200 + (int)(Math.random() * 2400); // 1-3 minutes
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            startVisit(player, visitType);
        }, delay);
        
        return true;
    }
    
    private int getVisitCost(String type) {
        switch (type) {
            case "family": return 0;
            case "lawyer": return 100;
            case "friend": return 50;
            case "business": return 200;
            default: return 0;
        }
    }
    
    private void startVisit(Player player, String visitType) {
        if (!player.isOnline()) {
            scheduledVisits.remove(player.getUniqueId());
            return;
        }
        
        UUID uuid = player.getUniqueId();
        scheduledVisits.remove(uuid);
        activeVisits.add(uuid);
        lastVisitTime.put(uuid, System.currentTimeMillis());
        
        // Teleport to visiting room
        Location visitRoom = new Location(plugin.getWorldManager().getPrisonWorld(), 90, 63, -10);
        player.teleport(visitRoom);
        
        player.sendMessage("§bДобро пожаловать в комнату свиданий!");
        player.sendMessage("§eВаш посетитель: " + getVisitTypeDisplay(visitType));
        
        // Apply visit benefits
        applyVisitBenefits(player, visitType);
        
        // End visit after 10 minutes
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            endVisit(player);
        }, 12000L); // 10 minutes
    }
    
    private void applyVisitBenefits(Player player, String visitType) {
        switch (visitType) {
            case "family":
                plugin.getReputationManager().addReputation(player, 10);
                player.sendMessage("§aВаша семья поддерживает вас! +10 репутация");
                break;
            case "lawyer":
                if (Math.random() < 0.3) { // 30% chance
                    plugin.getPlayerManager().reduceSentenceTime(player, 12 * 60); // 12 hours
                    player.sendMessage("§aАдвокат добился сокращения срока на 12 часов!");
                }
                player.sendMessage("§eАдвокат даёт юридические советы...");
                break;
            case "friend":
                plugin.getReputationManager().addReputation(player, 5);
                if (Math.random() < 0.5) { // 50% chance
                    plugin.getEconomyManager().addMoney(player, 25.0);
                    player.sendMessage("§aДруг передал вам $25!");
                }
                break;
            case "business":
                double businessMoney = 100.0 + (Math.random() * 200.0);
                plugin.getEconomyManager().addMoney(player, businessMoney);
                plugin.getReputationManager().addReputation(player, 15);
                player.sendMessage("§aДеловая встреча принесла $" + String.format("%.1f", businessMoney));
                break;
        }
    }
    
    private void endVisit(Player player) {
        UUID uuid = player.getUniqueId();
        activeVisits.remove(uuid);
        
        // Teleport back to yard
        Location yard = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 62, 0);
        player.teleport(yard);
        
        player.sendMessage("§eСвидание окончено. Возвращайтесь к обычной деятельности.");
    }
    
    private void startVisitorScheduler() {
        // Random visitor announcements
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (Math.random() < 0.1) { // 10% chance every 15 minutes
                announceVisitorDay();
            }
        }, 0L, 18000L); // Every 15 minutes
    }
    
    private void announceVisitorDay() {
        Bukkit.broadcastMessage("§b§l[ОБЪЯВЛЕНИЕ] День свиданий!");
        Bukkit.broadcastMessage("§eВсе свидания сегодня бесплатны!");
        
        // Make all visits free for 1 hour
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage("§7День свиданий окончен.");
        }, 72000L); // 1 hour
    }
    
    @EventHandler
    public void onVisitorMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§bСвидания")) return;
        
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        
        if (event.getCurrentItem() == null) return;
        
        int slot = event.getSlot();
        String visitType = null;
        
        switch (slot) {
            case 10: visitType = "family"; break;
            case 12: visitType = "lawyer"; break;
            case 14: visitType = "friend"; break;
            case 16: visitType = "business"; break;
        }
        
        if (visitType != null) {
            player.closeInventory();
            scheduleVisit(player, visitType);
        }
    }
    
    public boolean hasScheduledVisit(Player player) {
        return scheduledVisits.containsKey(player.getUniqueId());
    }
    
    public boolean isInVisit(Player player) {
        return activeVisits.contains(player.getUniqueId());
    }
    
    public void cancelVisit(Player player) {
        UUID uuid = player.getUniqueId();
        if (scheduledVisits.containsKey(uuid)) {
            scheduledVisits.remove(uuid);
            player.sendMessage("§eСвидание отменено.");
        }
    }
}
