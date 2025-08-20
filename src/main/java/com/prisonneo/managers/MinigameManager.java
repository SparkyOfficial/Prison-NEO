package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class MinigameManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<UUID, BukkitTask> activeTasks;
    private final Map<UUID, String> activeGames;
    private final Map<UUID, Integer> gameScores;
    
    public MinigameManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.activeTasks = new HashMap<>();
        this.activeGames = new HashMap<>();
        this.gameScores = new HashMap<>();
    }
    
    public void startLockpickingGame(Player player) {
        if (isInGame(player)) {
            player.sendMessage("§cВы уже играете в мини-игру!");
            return;
        }
        
        activeGames.put(player.getUniqueId(), "lockpicking");
        gameScores.put(player.getUniqueId(), 0);
        
        Inventory inv = Bukkit.createInventory(null, 27, "§4Взлом Замка - Найдите правильную комбинацию");
        
        // Create lockpicking interface
        for (int i = 0; i < 27; i++) {
            if (i >= 9 && i <= 17) {
                ItemStack item = new ItemStack(Material.IRON_INGOT);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§6Отмычка " + (i - 8));
                item.setItemMeta(meta);
                inv.setItem(i, item);
            } else {
                inv.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            }
        }
        
        player.openInventory(inv);
        
        // Start timer
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            endGame(player, false);
        }, 600L); // 30 seconds
        
        activeTasks.put(player.getUniqueId(), task);
        player.sendMessage("§eВзламывайте замок! У вас есть 30 секунд!");
    }
    
    public void startCardGame(Player player) {
        if (isInGame(player)) {
            player.sendMessage("§cВы уже играете в мини-игру!");
            return;
        }
        
        activeGames.put(player.getUniqueId(), "cards");
        gameScores.put(player.getUniqueId(), 21); // Blackjack starting
        
        Inventory inv = Bukkit.createInventory(null, 27, "§2Блэкджек - Наберите 21!");
        
        // Create card game interface
        List<Material> cards = Arrays.asList(
            Material.PAPER, Material.MAP, Material.BOOK, Material.WRITABLE_BOOK
        );
        
        for (int i = 0; i < 9; i++) {
            ItemStack card = new ItemStack(cards.get(i % cards.size()));
            ItemMeta meta = card.getItemMeta();
            meta.setDisplayName("§fКарта " + (i + 1));
            meta.setLore(Arrays.asList("§7Значение: " + (1 + (int)(Math.random() * 11))));
            card.setItemMeta(meta);
            inv.setItem(i + 9, card);
        }
        
        // Hit and Stand buttons
        ItemStack hit = new ItemStack(Material.GREEN_WOOL);
        ItemMeta hitMeta = hit.getItemMeta();
        hitMeta.setDisplayName("§aВзять карту");
        hit.setItemMeta(hitMeta);
        inv.setItem(22, hit);
        
        ItemStack stand = new ItemStack(Material.RED_WOOL);
        ItemMeta standMeta = stand.getItemMeta();
        standMeta.setDisplayName("§cПас");
        stand.setItemMeta(standMeta);
        inv.setItem(24, stand);
        
        player.openInventory(inv);
        player.sendMessage("§eИграем в блэкджек! Наберите 21!");
    }
    
    public void startReactionGame(Player player) {
        if (isInGame(player)) {
            player.sendMessage("§cВы уже играете в мини-игру!");
            return;
        }
        
        activeGames.put(player.getUniqueId(), "reaction");
        gameScores.put(player.getUniqueId(), 0);
        
        Inventory inv = Bukkit.createInventory(null, 54, "§6Реакция - Нажмите на зелёный блок!");
        
        // Fill with red blocks
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, new ItemStack(Material.RED_WOOL));
        }
        
        // Random green block
        int greenSlot = (int)(Math.random() * 54);
        ItemStack green = new ItemStack(Material.GREEN_WOOL);
        ItemMeta meta = green.getItemMeta();
        meta.setDisplayName("§aНАЖМИ МЕНЯ!");
        green.setItemMeta(meta);
        inv.setItem(greenSlot, green);
        
        player.openInventory(inv);
        
        // Timer for reaction game
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            endGame(player, false);
        }, 100L); // 5 seconds
        
        activeTasks.put(player.getUniqueId(), task);
        player.sendMessage("§eБыстро найдите и нажмите зелёный блок!");
    }
    
    public void startMemoryGame(Player player) {
        if (isInGame(player)) {
            player.sendMessage("§cВы уже играете в мини-игру!");
            return;
        }
        
        activeGames.put(player.getUniqueId(), "memory");
        gameScores.put(player.getUniqueId(), 0);
        
        Inventory inv = Bukkit.createInventory(null, 36, "§5Память - Запомните последовательность");
        
        // Show pattern for 3 seconds
        List<Integer> pattern = generateMemoryPattern();
        showMemoryPattern(inv, pattern);
        
        player.openInventory(inv);
        player.sendMessage("§eЗапомните последовательность цветов!");
        
        // Hide pattern and start input phase
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            clearInventory(inv);
            setupMemoryInput(inv);
            player.sendMessage("§eТеперь повторите последовательность!");
        }, 60L); // 3 seconds to memorize
    }
    
    private List<Integer> generateMemoryPattern() {
        List<Integer> pattern = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            pattern.add((int)(Math.random() * 4)); // 4 colors
        }
        return pattern;
    }
    
    private void showMemoryPattern(Inventory inv, List<Integer> pattern) {
        Material[] colors = {Material.RED_WOOL, Material.BLUE_WOOL, Material.YELLOW_WOOL, Material.GREEN_WOOL};
        
        for (int i = 0; i < pattern.size(); i++) {
            ItemStack item = new ItemStack(colors[pattern.get(i)]);
            inv.setItem(i + 14, item); // Center row
        }
    }
    
    private void clearInventory(Inventory inv) {
        inv.clear();
    }
    
    private void setupMemoryInput(Inventory inv) {
        Material[] colors = {Material.RED_WOOL, Material.BLUE_WOOL, Material.YELLOW_WOOL, Material.GREEN_WOOL};
        String[] names = {"§cКрасный", "§9Синий", "§eЖёлтый", "§aЗелёный"};
        
        for (int i = 0; i < 4; i++) {
            ItemStack item = new ItemStack(colors[i]);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(names[i]);
            item.setItemMeta(meta);
            inv.setItem(i + 13, item);
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        
        if (!isInGame(player)) return;
        
        event.setCancelled(true);
        
        String gameType = activeGames.get(player.getUniqueId());
        
        switch (gameType) {
            case "lockpicking":
                handleLockpickingClick(player, event);
                break;
            case "cards":
                handleCardGameClick(player, event);
                break;
            case "reaction":
                handleReactionClick(player, event);
                break;
            case "memory":
                handleMemoryClick(player, event);
                break;
        }
    }
    
    private void handleLockpickingClick(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        
        if (event.getCurrentItem().getType() == Material.IRON_INGOT) {
            int score = gameScores.get(player.getUniqueId());
            score++;
            gameScores.put(player.getUniqueId(), score);
            
            if (score >= 5) {
                endGame(player, true);
            } else {
                player.sendMessage("§a+" + score + "/5 отмычек!");
            }
        }
    }
    
    private void handleCardGameClick(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        
        if (event.getCurrentItem().getType() == Material.GREEN_WOOL) {
            // Hit
            int value = 1 + (int)(Math.random() * 11);
            int score = gameScores.get(player.getUniqueId()) + value;
            gameScores.put(player.getUniqueId(), score);
            
            if (score > 21) {
                player.sendMessage("§cПеребор! " + score);
                endGame(player, false);
            } else if (score == 21) {
                player.sendMessage("§aБлэкджек! " + score);
                endGame(player, true);
            } else {
                player.sendMessage("§eСчёт: " + score);
            }
        } else if (event.getCurrentItem().getType() == Material.RED_WOOL) {
            // Stand
            int score = gameScores.get(player.getUniqueId());
            boolean won = score >= 17 && score <= 21;
            endGame(player, won);
        }
    }
    
    private void handleReactionClick(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        
        if (event.getCurrentItem().getType() == Material.GREEN_WOOL) {
            endGame(player, true);
        } else {
            endGame(player, false);
        }
    }
    
    private void handleMemoryClick(Player player, InventoryClickEvent event) {
        // Simplified memory game logic
        if (event.getCurrentItem() == null) return;
        
        if (event.getCurrentItem().getType() != Material.BLACK_STAINED_GLASS_PANE) {
            int score = gameScores.get(player.getUniqueId());
            score++;
            gameScores.put(player.getUniqueId(), score);
            
            if (score >= 5) {
                endGame(player, true);
            }
        }
    }
    
    private void endGame(Player player, boolean won) {
        UUID uuid = player.getUniqueId();
        
        // Cancel timer
        if (activeTasks.containsKey(uuid)) {
            activeTasks.get(uuid).cancel();
            activeTasks.remove(uuid);
        }
        
        // Clean up
        activeGames.remove(uuid);
        gameScores.remove(uuid);
        player.closeInventory();
        
        // Rewards
        if (won) {
            double reward = 10.0 + (Math.random() * 20.0);
            plugin.getEconomyManager().addMoney(player, reward);
            plugin.getReputationManager().addReputation(player, 5);
            player.sendMessage("§aПобеда! Награда: $" + String.format("%.1f", reward));
        } else {
            player.sendMessage("§cПоражение! Попробуйте ещё раз позже.");
        }
    }
    
    public boolean isInGame(Player player) {
        return activeGames.containsKey(player.getUniqueId());
    }
    
    public void forceEndGame(Player player) {
        UUID uuid = player.getUniqueId();
        if (activeTasks.containsKey(uuid)) {
            activeTasks.get(uuid).cancel();
            activeTasks.remove(uuid);
        }
        activeGames.remove(uuid);
        gameScores.remove(uuid);
    }
}
