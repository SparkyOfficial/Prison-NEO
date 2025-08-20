package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ShopManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<String, ShopData> shops;
    
    public ShopManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.shops = new HashMap<>();
        setupShops();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    private void setupShops() {
        // General shop
        ShopData generalShop = new ShopData("general", "§aTюремный Магазин");
        generalShop.addItem(Material.BREAD, 5.0, 2.0, "Хлеб");
        generalShop.addItem(Material.WATER_BUCKET, 8.0, 0.0, "Вода");
        generalShop.addItem(Material.APPLE, 3.0, 1.5, "Яблоко");
        generalShop.addItem(Material.COOKED_BEEF, 10.0, 4.0, "Говядина");
        shops.put("general", generalShop);
        
        // Tool shop
        ShopData toolShop = new ShopData("tools", "§6Инструменты");
        toolShop.addItem(Material.WOODEN_PICKAXE, 10.0, 0.0, "Деревянная кирка");
        toolShop.addItem(Material.STONE_PICKAXE, 25.0, 0.0, "Каменная кирка");
        toolShop.addItem(Material.IRON_PICKAXE, 75.0, 0.0, "Железная кирка");
        toolShop.addItem(Material.DIAMOND_PICKAXE, 500.0, 0.0, "Алмазная кирка");
        toolShop.addItem(Material.WOODEN_SHOVEL, 8.0, 0.0, "Деревянная лопата");
        shops.put("tools", toolShop);
        
        // Food shop
        ShopData foodShop = new ShopData("food", "§eЕда и Напитки");
        foodShop.addItem(Material.BREAD, 4.0, 2.0, "Хлеб");
        foodShop.addItem(Material.BAKED_POTATO, 3.0, 1.5, "Печёная картошка");
        foodShop.addItem(Material.COOKED_CHICKEN, 8.0, 3.0, "Курица");
        foodShop.addItem(Material.MILK_BUCKET, 6.0, 0.0, "Молоко");
        foodShop.addItem(Material.COOKIE, 2.0, 1.0, "Печенье");
        shops.put("food", foodShop);
    }
    
    public void openShop(Player player, String shopType) {
        ShopData shop = shops.get(shopType);
        if (shop == null) {
            player.sendMessage("§cМагазин не найден!");
            return;
        }
        
        Inventory inv = Bukkit.createInventory(null, 54, shop.getTitle());
        
        int slot = 0;
        for (ShopItem item : shop.getItems()) {
            ItemStack displayItem = new ItemStack(item.getMaterial());
            ItemMeta meta = displayItem.getItemMeta();
            
            meta.setDisplayName("§f" + item.getName());
            meta.setLore(Arrays.asList(
                "§7Цена покупки: §a$" + String.format("%.2f", item.getBuyPrice()),
                item.getSellPrice() > 0 ? "§7Цена продажи: §e$" + String.format("%.2f", item.getSellPrice()) : "§cНельзя продать",
                "",
                "§eЛКМ - Купить 1",
                "§eПКМ - Купить 10",
                item.getSellPrice() > 0 ? "§eShift+ЛКМ - Продать всё" : ""
            ));
            
            displayItem.setItemMeta(meta);
            inv.setItem(slot++, displayItem);
            
            if (slot >= 45) break; // Leave space for navigation
        }
        
        // Close button
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName("§cЗакрыть");
        closeButton.setItemMeta(closeMeta);
        inv.setItem(53, closeButton);
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        // Check if it's a shop inventory
        boolean isShop = false;
        for (ShopData shop : shops.values()) {
            if (title.equals(shop.getTitle())) {
                isShop = true;
                break;
            }
        }
        
        if (!isShop) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        // Close button
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }
        
        // Find shop item
        ShopItem shopItem = findShopItem(clicked.getType());
        if (shopItem == null) return;
        
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        if (event.isLeftClick() && !event.isShiftClick()) {
            // Buy 1
            buyItem(player, prisonPlayer, shopItem, 1);
        } else if (event.isRightClick()) {
            // Buy 10
            buyItem(player, prisonPlayer, shopItem, 10);
        } else if (event.isShiftClick() && event.isLeftClick()) {
            // Sell all
            sellAllItems(player, prisonPlayer, shopItem);
        }
    }
    
    private void buyItem(Player player, PrisonPlayer prisonPlayer, ShopItem item, int amount) {
        double totalCost = item.getBuyPrice() * amount;
        
        if (!prisonPlayer.removeMoney(totalCost)) {
            player.sendMessage(String.format("§cНедостаточно денег! Нужно $%.2f, у вас $%.2f", 
                              totalCost, prisonPlayer.getMoney()));
            return;
        }
        
        ItemStack itemStack = new ItemStack(item.getMaterial(), amount);
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), itemStack);
            player.sendMessage("§eИнвентарь полон! Предметы выброшены на землю.");
        } else {
            player.getInventory().addItem(itemStack);
        }
        
        player.sendMessage(String.format("§aКуплено %dx %s за $%.2f", amount, item.getName(), totalCost));
    }
    
    private void sellAllItems(Player player, PrisonPlayer prisonPlayer, ShopItem item) {
        if (item.getSellPrice() <= 0) {
            player.sendMessage("§cЭтот предмет нельзя продать!");
            return;
        }
        
        int count = 0;
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && invItem.getType() == item.getMaterial()) {
                count += invItem.getAmount();
            }
        }
        
        if (count == 0) {
            player.sendMessage("§cУ вас нет " + item.getName() + " для продажи!");
            return;
        }
        
        // Remove items
        plugin.getEconomyManager().sellItem(player, item.getMaterial(), count);
    }
    
    private ShopItem findShopItem(Material material) {
        for (ShopData shop : shops.values()) {
            for (ShopItem item : shop.getItems()) {
                if (item.getMaterial() == material) {
                    return item;
                }
            }
        }
        return null;
    }
    
    public static class ShopData {
        private final String id;
        private final String title;
        private final Map<Material, ShopItem> items;
        
        public ShopData(String id, String title) {
            this.id = id;
            this.title = title;
            this.items = new HashMap<>();
        }
        
        public void addItem(Material material, double buyPrice, double sellPrice, String name) {
            items.put(material, new ShopItem(material, buyPrice, sellPrice, name));
        }
        
        public String getId() { return id; }
        public String getTitle() { return title; }
        public Iterable<ShopItem> getItems() { return items.values(); }
    }
    
    public static class ShopItem {
        private final Material material;
        private final double buyPrice;
        private final double sellPrice;
        private final String name;
        
        public ShopItem(Material material, double buyPrice, double sellPrice, String name) {
            this.material = material;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.name = name;
        }
        
        public Material getMaterial() { return material; }
        public double getBuyPrice() { return buyPrice; }
        public double getSellPrice() { return sellPrice; }
        public String getName() { return name; }
    }
}
