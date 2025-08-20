package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AchievementManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<UUID, Set<String>> playerAchievements;
    private final Map<String, Achievement> achievements;
    
    public AchievementManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.playerAchievements = new HashMap<>();
        this.achievements = new HashMap<>();
        
        setupAchievements();
    }
    
    private void setupAchievements() {
        // Mining achievements
        achievements.put("first_mine", new Achievement("Первый раз в шахте", "Добыть первый блок", Material.COAL, 10.0));
        achievements.put("miner", new Achievement("Шахтёр", "Добыть 100 блоков", Material.IRON_ORE, 50.0));
        achievements.put("master_miner", new Achievement("Мастер шахт", "Добыть 1000 блоков", Material.DIAMOND_ORE, 200.0));
        
        // Rank achievements
        achievements.put("promoted", new Achievement("Повышение", "Получить ранг C", Material.IRON_INGOT, 25.0));
        achievements.put("trustee", new Achievement("Доверенный", "Получить ранг Trustee", Material.GOLD_INGOT, 100.0));
        
        // Social achievements
        achievements.put("gang_leader", new Achievement("Лидер банды", "Станьте лидером банды", Material.WHITE_BANNER, 100.0));
        achievements.put("popular", new Achievement("Популярный", "Достичь 50 репутации", Material.EMERALD, 100.0));
        
        // Escape achievements
        achievements.put("escapist", new Achievement("Беглец", "Совершить побег", Material.COMPASS, 500.0));
        achievements.put("tool_collector", new Achievement("Коллекционер", "Собрать 5 инструментов", Material.CHEST, 150.0));
        
        // Economy achievements
        achievements.put("rich", new Achievement("Богач", "Накопить $1000", Material.GOLD_BLOCK, 200.0));
        achievements.put("spender", new Achievement("Транжира", "Потратить $500", Material.EMERALD_BLOCK, 100.0));
        
        // Job achievements
        achievements.put("worker", new Achievement("Работяга", "Выполнить 10 работ", Material.IRON_PICKAXE, 75.0));
        achievements.put("employee_month", new Achievement("Работник месяца", "Работать 24 часа", Material.DIAMOND_PICKAXE, 300.0));
        
        // Special achievements
        achievements.put("survivor", new Achievement("Выживший", "Провести в тюрьме 7 дней", Material.CLOCK, 250.0));
        achievements.put("troublemaker", new Achievement("Нарушитель", "Получить 10 нарушений", Material.TNT, 50.0));
    }
    
    public void checkAchievement(Player player, String achievementId, Object... params) {
        UUID uuid = player.getUniqueId();
        Set<String> playerAchs = playerAchievements.getOrDefault(uuid, new HashSet<>());
        
        if (playerAchs.contains(achievementId)) return; // Already has achievement
        
        boolean earned = false;
        
        switch (achievementId) {
            case "first_mine":
                earned = true; // Called when first block mined
                break;
            case "miner":
                earned = (Integer) params[0] >= 100; // blocks mined
                break;
            case "master_miner":
                earned = (Integer) params[0] >= 1000;
                break;
            case "promoted":
                earned = plugin.getRankManager().getRankLevel(player) >= 2; // Rank C
                break;
            case "trustee":
                earned = plugin.getRankManager().getRankLevel(player) >= 6; // Trustee
                break;
            case "popular":
                earned = plugin.getReputationManager().getReputation(player) >= 50;
                break;
            case "rich":
                earned = plugin.getEconomyManager().getMoney(player) >= 1000.0;
                break;
            case "worker":
                earned = (Integer) params[0] >= 10; // jobs completed
                break;
            case "survivor":
                earned = (Long) params[0] >= 7 * 24 * 60 * 60 * 1000L; // 7 days playtime
                break;
        }
        
        if (earned) {
            unlockAchievement(player, achievementId);
        }
    }
    
    private void unlockAchievement(Player player, String achievementId) {
        UUID uuid = player.getUniqueId();
        Set<String> playerAchs = playerAchievements.getOrDefault(uuid, new HashSet<>());
        playerAchs.add(achievementId);
        playerAchievements.put(uuid, playerAchs);
        
        Achievement achievement = achievements.get(achievementId);
        
        // Broadcast achievement
        Bukkit.broadcastMessage("§6§l[ДОСТИЖЕНИЕ] " + player.getName() + " получил: " + achievement.getName());
        
        // Reward player
        plugin.getEconomyManager().addMoney(player, achievement.getReward());
        plugin.getReputationManager().addReputation(player, 10);
        
        player.sendMessage("§a§l✦ ДОСТИЖЕНИЕ РАЗБЛОКИРОВАНО! ✦");
        player.sendMessage("§e" + achievement.getName());
        player.sendMessage("§7" + achievement.getDescription());
        player.sendMessage("§aНаграда: $" + achievement.getReward());
    }
    
    public void openAchievementMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Достижения");
        
        Set<String> playerAchs = playerAchievements.getOrDefault(player.getUniqueId(), new HashSet<>());
        
        int slot = 0;
        for (Map.Entry<String, Achievement> entry : achievements.entrySet()) {
            String id = entry.getKey();
            Achievement ach = entry.getValue();
            boolean unlocked = playerAchs.contains(id);
            
            ItemStack item = new ItemStack(unlocked ? ach.getIcon() : Material.GRAY_STAINED_GLASS);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName((unlocked ? "§a" : "§7") + ach.getName());
            meta.setLore(Arrays.asList(
                "§7" + ach.getDescription(),
                unlocked ? "§a✓ Разблокировано" : "§c✗ Заблокировано",
                "§7Награда: §e$" + ach.getReward()
            ));
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
            
            if (slot >= 54) break;
        }
        
        player.openInventory(inv);
    }
    
    public int getAchievementCount(Player player) {
        return playerAchievements.getOrDefault(player.getUniqueId(), new HashSet<>()).size();
    }
    
    public int getTotalAchievements() {
        return achievements.size();
    }
    
    // Achievement data class
    private static class Achievement {
        private final String name;
        private final String description;
        private final Material icon;
        private final double reward;
        
        public Achievement(String name, String description, Material icon, double reward) {
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.reward = reward;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
        public Material getIcon() { return icon; }
        public double getReward() { return reward; }
    }
}
