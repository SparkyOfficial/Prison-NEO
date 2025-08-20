package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EconomyManager {
    
    private final PrisonNEO plugin;
    private final Map<Material, Double> sellPrices;
    private final Map<Material, Double> buyPrices;
    
    public EconomyManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.sellPrices = new HashMap<>();
        this.buyPrices = new HashMap<>();
        setupPrices();
    }
    
    private void setupPrices() {
        // Sell prices (what players get for selling items)
        sellPrices.put(Material.COAL, 1.0);
        sellPrices.put(Material.IRON_INGOT, 3.0);
        sellPrices.put(Material.GOLD_INGOT, 8.0);
        sellPrices.put(Material.DIAMOND, 25.0);
        sellPrices.put(Material.EMERALD, 50.0);
        
        // Raw ores
        sellPrices.put(Material.RAW_IRON, 2.0);
        sellPrices.put(Material.RAW_GOLD, 6.0);
        sellPrices.put(Material.RAW_COPPER, 0.5);
        
        // Food
        sellPrices.put(Material.BREAD, 2.0);
        sellPrices.put(Material.COOKED_BEEF, 4.0);
        sellPrices.put(Material.APPLE, 1.5);
        
        // Buy prices (what players pay to buy items)
        buyPrices.put(Material.BREAD, 5.0);
        buyPrices.put(Material.COOKED_BEEF, 10.0);
        buyPrices.put(Material.APPLE, 3.0);
        buyPrices.put(Material.WATER_BUCKET, 8.0);
        
        // Tools
        buyPrices.put(Material.WOODEN_PICKAXE, 10.0);
        buyPrices.put(Material.STONE_PICKAXE, 25.0);
        buyPrices.put(Material.IRON_PICKAXE, 75.0);
        buyPrices.put(Material.DIAMOND_PICKAXE, 500.0);
        
        // Armor
        buyPrices.put(Material.LEATHER_HELMET, 15.0);
        buyPrices.put(Material.LEATHER_CHESTPLATE, 25.0);
        buyPrices.put(Material.LEATHER_LEGGINGS, 20.0);
        buyPrices.put(Material.LEATHER_BOOTS, 10.0);
    }
    
    public boolean sellItem(Player player, Material material, int amount) {
        Double price = sellPrices.get(material);
        if (price == null) {
            player.sendMessage("§cYou cannot sell this item!");
            return false;
        }
        
        // Check if player has the items
        if (!hasItems(player, material, amount)) {
            player.sendMessage("§cYou don't have enough " + material.name() + "!");
            return false;
        }
        
        // Remove items from inventory
        removeItems(player, material, amount);
        
        // Add money
        double totalEarnings = price * amount;
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        prisonPlayer.addMoney(totalEarnings);
        
        player.sendMessage(String.format("§aSold %dx %s for $%.2f! Total: $%.2f", 
                          amount, material.name(), totalEarnings, prisonPlayer.getMoney()));
        
        return true;
    }
    
    public boolean buyItem(Player player, Material material, int amount) {
        Double price = buyPrices.get(material);
        if (price == null) {
            player.sendMessage("§cThis item is not for sale!");
            return false;
        }
        
        double totalCost = price * amount;
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        if (!prisonPlayer.removeMoney(totalCost)) {
            player.sendMessage(String.format("§cYou need $%.2f to buy this! You have $%.2f", 
                              totalCost, prisonPlayer.getMoney()));
            return false;
        }
        
        // Add items to inventory
        ItemStack item = new ItemStack(material, amount);
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendMessage("§eInventory full! Items dropped on ground.");
        } else {
            player.getInventory().addItem(item);
        }
        
        player.sendMessage(String.format("§aBought %dx %s for $%.2f! Remaining: $%.2f", 
                          amount, material.name(), totalCost, prisonPlayer.getMoney()));
        
        return true;
    }
    
    private boolean hasItems(Player player, Material material, int amount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count >= amount;
    }
    
    private void removeItems(Player player, Material material, int amount) {
        int remaining = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material && remaining > 0) {
                int itemAmount = item.getAmount();
                if (itemAmount <= remaining) {
                    remaining -= itemAmount;
                    item.setAmount(0);
                } else {
                    item.setAmount(itemAmount - remaining);
                    remaining = 0;
                }
            }
        }
    }
    
    public Double getSellPrice(Material material) {
        return sellPrices.get(material);
    }
    
    public Double getBuyPrice(Material material) {
        return buyPrices.get(material);
    }
    
    public Map<Material, Double> getAllSellPrices() {
        return new HashMap<>(sellPrices);
    }
    
    public Map<Material, Double> getAllBuyPrices() {
        return new HashMap<>(buyPrices);
    }
    
    public static class MineData {
        private final String id;
        private final String name;
        private final Location center;
        private final Location teleportLocation;
        private final Material oreType;
        private final double payoutPerBlock;
        private final String requiredRank;
        
        public MineData(String id, String name, Location center, Location teleportLocation,
                       Material oreType, double payoutPerBlock, String requiredRank) {
            this.id = id;
            this.name = name;
            this.center = center;
            this.teleportLocation = teleportLocation;
            this.oreType = oreType;
            this.payoutPerBlock = payoutPerBlock;
            this.requiredRank = requiredRank;
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public Location getCenter() { return center; }
        public Location getTeleportLocation() { return teleportLocation; }
        public Material getOreType() { return oreType; }
        public double getPayoutPerBlock() { return payoutPerBlock; }
        public String getRequiredRank() { return requiredRank; }
    }
}
