package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ContrabandManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<Material, ContrabandItem> contrabandItems;
    private final Map<UUID, Integer> playerSuspicion;
    private final Set<UUID> playersWithContraband;
    
    public ContrabandManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.contrabandItems = new HashMap<>();
        this.playerSuspicion = new HashMap<>();
        this.playersWithContraband = new HashSet<>();
        setupContrabandItems();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startRandomSearches();
    }
    
    private void setupContrabandItems() {
        contrabandItems.put(Material.IRON_INGOT, new ContrabandItem(Material.IRON_INGOT, "Металлолом", 15.0, 3));
        contrabandItems.put(Material.REDSTONE, new ContrabandItem(Material.REDSTONE, "Взрывчатка", 25.0, 5));
        contrabandItems.put(Material.GLASS_BOTTLE, new ContrabandItem(Material.GLASS_BOTTLE, "Самогон", 20.0, 4));
        contrabandItems.put(Material.PAPER, new ContrabandItem(Material.PAPER, "Поддельные Документы", 50.0, 7));
        contrabandItems.put(Material.COMPASS, new ContrabandItem(Material.COMPASS, "План Побега", 100.0, 10));
        contrabandItems.put(Material.ENDER_PEARL, new ContrabandItem(Material.ENDER_PEARL, "Магический Артефакт", 200.0, 15));
    }
    
    public void openContrabandShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8§lЧёрный Рынок");
        
        int slot = 0;
        for (ContrabandItem item : contrabandItems.values()) {
            ItemStack displayItem = new ItemStack(item.getMaterial());
            ItemMeta meta = displayItem.getItemMeta();
            meta.setDisplayName("§c" + item.getName());
            meta.setLore(Arrays.asList(
                "§7Контрабанда",
                "§7Цена: §a$" + String.format("%.2f", item.getPrice()),
                "§7Подозрение: §c+" + item.getSuspicionLevel(),
                "",
                "§c§lОСТОРОЖНО!",
                "§7Может быть обнаружено при обыске!",
                "",
                "§eНажмите, чтобы купить"
            ));
            displayItem.setItemMeta(meta);
            inv.setItem(slot++, displayItem);
        }
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.equals("§8§lЧёрный Рынок")) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        
        ContrabandItem contraband = contrabandItems.get(clicked.getType());
        if (contraband == null) return;
        
        buyContraband(player, contraband);
        player.closeInventory();
    }
    
    private void buyContraband(Player player, ContrabandItem item) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        if (!prisonPlayer.removeMoney(item.getPrice())) {
            player.sendMessage("§cНедостаточно денег! Нужно $" + String.format("%.2f", item.getPrice()));
            return;
        }
        
        // Create contraband item with special lore
        ItemStack contrabandItem = new ItemStack(item.getMaterial());
        ItemMeta meta = contrabandItem.getItemMeta();
        meta.setDisplayName("§c" + item.getName());
        meta.setLore(Arrays.asList("§4§lКОНТРАБАНДА", "§7Скрывайте от охранников!"));
        contrabandItem.setItemMeta(meta);
        
        player.getInventory().addItem(contrabandItem);
        playersWithContraband.add(player.getUniqueId());
        
        // Increase suspicion
        int currentSuspicion = playerSuspicion.getOrDefault(player.getUniqueId(), 0);
        playerSuspicion.put(player.getUniqueId(), currentSuspicion + item.getSuspicionLevel());
        
        player.sendMessage("§6Контрабанда куплена: " + item.getName());
        player.sendMessage("§c§lОСТОРОЖНО! Ваш уровень подозрения увеличился!");
    }
    
    public void performSearch(Player player) {
        if (!playersWithContraband.contains(player.getUniqueId())) {
            player.sendMessage("§aОбыск завершён. Ничего подозрительного не найдено.");
            return;
        }
        
        List<ItemStack> foundContraband = new ArrayList<>();
        
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && isContraband(item)) {
                foundContraband.add(item.clone());
                item.setAmount(0);
            }
        }
        
        if (!foundContraband.isEmpty()) {
            player.sendMessage("§4§lОБЫСК: Найдена контрабанда!");
            
            double fine = 0;
            for (ItemStack item : foundContraband) {
                ContrabandItem contraband = contrabandItems.get(item.getType());
                if (contraband != null) {
                    fine += contraband.getPrice() * 2; // Double price as fine
                }
            }
            
            PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
            prisonPlayer.removeMoney(fine);
            prisonPlayer.setSentence(prisonPlayer.getSentence() + 12); // Add 12 hours
            
            player.sendMessage("§cШтраф: $" + String.format("%.2f", fine));
            player.sendMessage("§cСрок увеличен на 12 часов!");
            
            playersWithContraband.remove(player.getUniqueId());
            playerSuspicion.put(player.getUniqueId(), 0);
        } else {
            player.sendMessage("§aОбыск завершён. Ничего не найдено.");
        }
    }
    
    private boolean isContraband(ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
        
        List<String> lore = item.getItemMeta().getLore();
        return lore != null && lore.contains("§4§lКОНТРАБАНДА");
    }
    
    private void startRandomSearches() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getWorldManager().getPrisonWorld() == null) return;
                
                for (Player player : plugin.getWorldManager().getPrisonWorld().getPlayers()) {
                    int suspicion = playerSuspicion.getOrDefault(player.getUniqueId(), 0);
                    
                    // Higher suspicion = higher chance of search
                    double searchChance = Math.min(0.1 + (suspicion * 0.01), 0.5);
                    
                    if (Math.random() < searchChance) {
                        player.sendMessage("§c§lВНИМАНИЕ: Охранник проводит обыск!");
                        
                        // Delay the actual search
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            performSearch(player);
                        }, 60L); // 3 seconds delay
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 6000L); // Every 5 minutes
    }
    
    public int getPlayerSuspicion(Player player) {
        return playerSuspicion.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void reduceSuspicion(Player player, int amount) {
        int current = playerSuspicion.getOrDefault(player.getUniqueId(), 0);
        playerSuspicion.put(player.getUniqueId(), Math.max(0, current - amount));
    }
    
    public static class ContrabandItem {
        private final Material material;
        private final String name;
        private final double price;
        private final int suspicionLevel;
        
        public ContrabandItem(Material material, String name, double price, int suspicionLevel) {
            this.material = material;
            this.name = name;
            this.price = price;
            this.suspicionLevel = suspicionLevel;
        }
        
        public Material getMaterial() { return material; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getSuspicionLevel() { return suspicionLevel; }
    }
    
    // Additional methods needed by other managers
    public int getSuspicionLevel(Player player) {
        return playerSuspicion.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void addSuspicion(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int currentSuspicion = playerSuspicion.getOrDefault(uuid, 0);
        playerSuspicion.put(uuid, currentSuspicion + amount);
        
        if (currentSuspicion + amount > 50) {
            player.sendMessage("§cВы привлекли внимание охраны!");
        }
    }
}
