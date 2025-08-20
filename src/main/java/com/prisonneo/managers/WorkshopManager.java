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

public class WorkshopManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<String, WorkshopRecipe> recipes;
    private final Map<UUID, Integer> playerCraftingLevel;
    
    public WorkshopManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.recipes = new HashMap<>();
        this.playerCraftingLevel = new HashMap<>();
        
        setupRecipes();
    }
    
    private void setupRecipes() {
        // Basic tools
        recipes.put("shiv", new WorkshopRecipe("Заточка", 
            Arrays.asList(new ItemStack(Material.IRON_INGOT, 1), new ItemStack(Material.STICK, 1)),
            new ItemStack(Material.IRON_SWORD), 1, 50.0));
        
        recipes.put("lockpick", new WorkshopRecipe("Отмычка",
            Arrays.asList(new ItemStack(Material.IRON_INGOT, 2)),
            createCustomItem(Material.TRIPWIRE_HOOK, "§cОтмычка", "§7Инструмент для побега"), 2, 100.0));
        
        recipes.put("rope", new WorkshopRecipe("Верёвка",
            Arrays.asList(new ItemStack(Material.STRING, 9)),
            createCustomItem(Material.LEAD, "§eВерёвка", "§7Инструмент для побега"), 1, 75.0));
        
        // Contraband items
        recipes.put("cigarettes", new WorkshopRecipe("Сигареты",
            Arrays.asList(new ItemStack(Material.PAPER, 5), new ItemStack(Material.BROWN_DYE, 2)),
            createCustomItem(Material.STICK, "§6Сигареты", "§7Контрабанда"), 3, 150.0));
        
        recipes.put("alcohol", new WorkshopRecipe("Самогон",
            Arrays.asList(new ItemStack(Material.POTATO, 10), new ItemStack(Material.SUGAR, 5)),
            createCustomItem(Material.POTION, "§cСамогон", "§7Контрабанда"), 4, 200.0));
        
        // Useful items
        recipes.put("bandage", new WorkshopRecipe("Бинт",
            Arrays.asList(new ItemStack(Material.WHITE_WOOL, 2)),
            createCustomItem(Material.PAPER, "§fБинт", "§7Восстанавливает здоровье"), 1, 25.0));
        
        recipes.put("energy_drink", new WorkshopRecipe("Энергетик",
            Arrays.asList(new ItemStack(Material.SUGAR, 3), new ItemStack(Material.REDSTONE, 2)),
            createCustomItem(Material.HONEY_BOTTLE, "§eЭнергетик", "§7Даёт скорость"), 2, 80.0));
    }
    
    private ItemStack createCustomItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    public void openWorkshopMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8Мастерская - Уровень " + getCraftingLevel(player));
        
        int slot = 0;
        for (Map.Entry<String, WorkshopRecipe> entry : recipes.entrySet()) {
            WorkshopRecipe recipe = entry.getValue();
            
            if (getCraftingLevel(player) >= recipe.getRequiredLevel()) {
                ItemStack item = recipe.getResult().clone();
                ItemMeta meta = item.getItemMeta();
                
                List<String> lore = new ArrayList<>();
                lore.add("§7Требуется:");
                for (ItemStack ingredient : recipe.getIngredients()) {
                    lore.add("§e- " + ingredient.getAmount() + "x " + getItemName(ingredient.getType()));
                }
                lore.add("§7Стоимость: §c$" + recipe.getCost());
                lore.add("§aЛКМ - Создать");
                
                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.setItem(slot++, item);
            } else {
                ItemStack locked = new ItemStack(Material.BARRIER);
                ItemMeta meta = locked.getItemMeta();
                meta.setDisplayName("§c" + recipe.getName());
                meta.setLore(Arrays.asList(
                    "§7Требуется уровень: §e" + recipe.getRequiredLevel(),
                    "§7Ваш уровень: §c" + getCraftingLevel(player)
                ));
                locked.setItemMeta(meta);
                inv.setItem(slot++, locked);
            }
            
            if (slot >= 45) break;
        }
        
        // Crafting level info
        ItemStack levelInfo = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta levelMeta = levelInfo.getItemMeta();
        levelMeta.setDisplayName("§6Уровень мастерства: " + getCraftingLevel(player));
        levelMeta.setLore(Arrays.asList(
            "§7Создавайте предметы для повышения уровня",
            "§7Высокий уровень открывает новые рецепты"
        ));
        levelInfo.setItemMeta(levelMeta);
        inv.setItem(49, levelInfo);
        
        player.openInventory(inv);
    }
    
    public boolean craftItem(Player player, String recipeId) {
        WorkshopRecipe recipe = recipes.get(recipeId);
        if (recipe == null) return false;
        
        // Check level requirement
        if (getCraftingLevel(player) < recipe.getRequiredLevel()) {
            player.sendMessage("§cНужен уровень мастерства: " + recipe.getRequiredLevel());
            return false;
        }
        
        // Check cost
        if (!plugin.getEconomyManager().removeMoney(player, recipe.getCost())) {
            player.sendMessage("§cНедостаточно денег! Нужно: $" + recipe.getCost());
            return false;
        }
        
        // Check ingredients
        for (ItemStack ingredient : recipe.getIngredients()) {
            if (!player.getInventory().containsAtLeast(ingredient, ingredient.getAmount())) {
                player.sendMessage("§cНедостаточно материалов: " + getItemName(ingredient.getType()));
                plugin.getEconomyManager().addMoney(player, recipe.getCost()); // Refund
                return false;
            }
        }
        
        // Remove ingredients
        for (ItemStack ingredient : recipe.getIngredients()) {
            player.getInventory().removeItem(ingredient);
        }
        
        // Give result
        player.getInventory().addItem(recipe.getResult());
        player.sendMessage("§aВы создали: " + recipe.getName());
        
        // Add crafting experience
        addCraftingExperience(player, 1);
        
        return true;
    }
    
    private void addCraftingExperience(Player player, int exp) {
        UUID uuid = player.getUniqueId();
        int current = playerCraftingLevel.getOrDefault(uuid, 0);
        int newLevel = current + exp;
        playerCraftingLevel.put(uuid, newLevel);
        
        // Check for level up
        int oldLevel = current / 10;
        int currentLevel = newLevel / 10;
        
        if (currentLevel > oldLevel) {
            player.sendMessage("§6§lПОВЫШЕНИЕ УРОВНЯ МАСТЕРСТВА!");
            player.sendMessage("§eНовый уровень: " + currentLevel);
            plugin.getReputationManager().addReputation(player, 5);
        }
    }
    
    public int getCraftingLevel(Player player) {
        return playerCraftingLevel.getOrDefault(player.getUniqueId(), 0) / 10;
    }
    
    private String getItemName(Material material) {
        return material.name().toLowerCase().replace("_", " ");
    }
    
    @EventHandler
    public void onWorkshopClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith("§8Мастерская")) return;
        
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        
        if (event.getCurrentItem() == null) return;
        
        // Find recipe by result item
        for (Map.Entry<String, WorkshopRecipe> entry : recipes.entrySet()) {
            WorkshopRecipe recipe = entry.getValue();
            if (recipe.getResult().getType() == event.getCurrentItem().getType()) {
                player.closeInventory();
                craftItem(player, entry.getKey());
                break;
            }
        }
    }
    
    // Workshop recipe data class
    private static class WorkshopRecipe {
        private final String name;
        private final List<ItemStack> ingredients;
        private final ItemStack result;
        private final int requiredLevel;
        private final double cost;
        
        public WorkshopRecipe(String name, List<ItemStack> ingredients, ItemStack result, int requiredLevel, double cost) {
            this.name = name;
            this.ingredients = ingredients;
            this.result = result;
            this.requiredLevel = requiredLevel;
            this.cost = cost;
        }
        
        public String getName() { return name; }
        public List<ItemStack> getIngredients() { return ingredients; }
        public ItemStack getResult() { return result; }
        public int getRequiredLevel() { return requiredLevel; }
        public double getCost() { return cost; }
    }
}
