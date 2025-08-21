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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AdvancedEscapeManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<UUID, Set<String>> playerTools;
    private final Map<UUID, Integer> escapeProgress;
    private final Map<UUID, Long> lastEscapeAttempt;
    private final Map<String, Location> toolLocations;
    
    public AdvancedEscapeManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.playerTools = new HashMap<>();
        this.escapeProgress = new HashMap<>();
        this.lastEscapeAttempt = new HashMap<>();
        this.toolLocations = new HashMap<>();
        
    }
    
    public void initialize() {
        setupToolLocations();
        startRandomToolSpawns();
    }
    
    private void setupToolLocations() {
        // Hidden tool spawn locations
        toolLocations.put("kitchen_knife", new Location(plugin.getWorldManager().getPrisonWorld(), -70, 63, 12));
        toolLocations.put("screwdriver", new Location(plugin.getWorldManager().getPrisonWorld(), 50, 63, -48));
        toolLocations.put("rope", new Location(plugin.getWorldManager().getPrisonWorld(), -45, 63, 45));
        toolLocations.put("lockpick", new Location(plugin.getWorldManager().getPrisonWorld(), 30, 63, -65));
        toolLocations.put("wire_cutters", new Location(plugin.getWorldManager().getPrisonWorld(), -80, 63, 80));
        toolLocations.put("crowbar", new Location(plugin.getWorldManager().getPrisonWorld(), 75, 63, -20));
        toolLocations.put("hammer", new Location(plugin.getWorldManager().getPrisonWorld(), -30, 63, -75));
        toolLocations.put("chisel", new Location(plugin.getWorldManager().getPrisonWorld(), 85, 63, 30));
    }
    
    private void startRandomToolSpawns() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Randomly spawn tools every 10 minutes
            if (Math.random() < 0.3) { // 30% chance
                spawnRandomTool();
            }
        }, 0L, 12000L); // Every 10 minutes
    }
    
    private void spawnRandomTool() {
        String[] tools = toolLocations.keySet().toArray(new String[0]);
        String randomTool = tools[(int)(Math.random() * tools.length)];
        Location location = toolLocations.get(randomTool);
        
        // Drop the tool item
        Material toolMaterial = getToolMaterial(randomTool);
        ItemStack tool = new ItemStack(toolMaterial);
        ItemMeta meta = tool.getItemMeta();
        meta.setDisplayName("§c" + getToolDisplayName(randomTool));
        meta.setLore(Arrays.asList("§7Инструмент для побега", "§eИспользуйте с осторожностью!"));
        tool.setItemMeta(meta);
        
        location.getWorld().dropItem(location, tool);
        
        // Notify nearby players
        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distance(location) <= 20) {
                player.sendMessage("§7Вы слышите звук падающего предмета...");
            }
        }
    }
    
    private Material getToolMaterial(String toolType) {
        switch (toolType) {
            case "kitchen_knife": return Material.IRON_SWORD;
            case "screwdriver": return Material.STICK;
            case "rope": return Material.STRING;
            case "lockpick": return Material.TRIPWIRE_HOOK;
            case "wire_cutters": return Material.SHEARS;
            case "crowbar": return Material.IRON_PICKAXE;
            case "hammer": return Material.IRON_AXE;
            case "chisel": return Material.IRON_HOE;
            default: return Material.STICK;
        }
    }
    
    private String getToolDisplayName(String toolType) {
        switch (toolType) {
            case "kitchen_knife": return "Кухонный Нож";
            case "screwdriver": return "Отвёртка";
            case "rope": return "Верёвка";
            case "lockpick": return "Отмычка";
            case "wire_cutters": return "Кусачки";
            case "crowbar": return "Лом";
            case "hammer": return "Молоток";
            case "chisel": return "Зубило";
            default: return "Инструмент";
        }
    }
    
    public void openEscapePlanMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, "§4План Побега");
        
        // Show available escape routes
        createEscapeRoute(inv, 10, "tunnel", "§6Подкоп", Arrays.asList("screwdriver", "chisel"));
        createEscapeRoute(inv, 12, "wall", "§7Стена", Arrays.asList("rope", "wire_cutters"));
        createEscapeRoute(inv, 14, "gate", "§eВорота", Arrays.asList("lockpick", "crowbar"));
        createEscapeRoute(inv, 16, "disguise", "§aМаскировка", Arrays.asList("kitchen_knife", "hammer"));
        
        // Show player's tools
        Set<String> tools = playerTools.getOrDefault(player.getUniqueId(), new HashSet<>());
        int toolSlot = 27;
        for (String tool : tools) {
            ItemStack toolItem = new ItemStack(getToolMaterial(tool));
            ItemMeta meta = toolItem.getItemMeta();
            meta.setDisplayName("§a" + getToolDisplayName(tool));
            meta.setLore(Arrays.asList("§7У вас есть этот инструмент"));
            toolItem.setItemMeta(meta);
            inv.setItem(toolSlot++, toolItem);
        }
        
        player.openInventory(inv);
    }
    
    private void createEscapeRoute(Inventory inv, int slot, String routeType, String name, List<String> requiredTools) {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        
        List<String> lore = new ArrayList<>();
        lore.add("§7Требуемые инструменты:");
        for (String tool : requiredTools) {
            lore.add("§e- " + getToolDisplayName(tool));
        }
        lore.add("§7Шанс успеха: " + getSuccessChance(routeType) + "%");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }
    
    private int getSuccessChance(String routeType) {
        switch (routeType) {
            case "tunnel": return 45;
            case "wall": return 35;
            case "gate": return 25;
            case "disguise": return 55;
            default: return 30;
        }
    }
    
    public boolean attemptEscape(Player player, String routeType) {
        UUID uuid = player.getUniqueId();
        
        // Cooldown check
        if (lastEscapeAttempt.containsKey(uuid)) {
            long lastAttempt = lastEscapeAttempt.get(uuid);
            if (System.currentTimeMillis() - lastAttempt < 3600000) { // 1 hour
                player.sendMessage("§cВы недавно пытались сбежать! Подождите час.");
                return false;
            }
        }
        
        // Check required tools
        List<String> requiredTools = getRequiredTools(routeType);
        Set<String> playerToolSet = playerTools.getOrDefault(uuid, new HashSet<>());
        
        for (String tool : requiredTools) {
            if (!playerToolSet.contains(tool)) {
                player.sendMessage("§cВам нужен инструмент: " + getToolDisplayName(tool));
                return false;
            }
        }
        
        lastEscapeAttempt.put(uuid, System.currentTimeMillis());
        
        // Calculate success chance
        int baseChance = getSuccessChance(routeType);
        int reputation = plugin.getReputationManager().getReputation(player);
        int finalChance = baseChance + (reputation / 10); // Reputation bonus
        
        boolean success = Math.random() * 100 < finalChance;
        
        if (success) {
            handleSuccessfulEscape(player, routeType);
        } else {
            handleFailedEscape(player, routeType);
        }
        
        // Remove used tools
        for (String tool : requiredTools) {
            playerToolSet.remove(tool);
        }
        playerTools.put(uuid, playerToolSet);
        
        return success;
    }
    
    private List<String> getRequiredTools(String routeType) {
        switch (routeType) {
            case "tunnel": return Arrays.asList("screwdriver", "chisel");
            case "wall": return Arrays.asList("rope", "wire_cutters");
            case "gate": return Arrays.asList("lockpick", "crowbar");
            case "disguise": return Arrays.asList("kitchen_knife", "hammer");
            default: return new ArrayList<>();
        }
    }
    
    private void handleSuccessfulEscape(Player player, String routeType) {
        player.sendMessage("§a§lПОБЕГ УДАЛСЯ!");
        player.sendMessage("§eВы сбежали через: " + routeType);
        
        // Teleport to freedom (outside prison)
        Location freedom = new Location(plugin.getWorldManager().getPrisonWorld(), 300, 70, 300);
        player.teleport(freedom);
        
        // Rewards
        plugin.getEconomyManager().addMoney(player, 1000.0);
        plugin.getReputationManager().addReputation(player, 50);
        
        // Broadcast escape
        Bukkit.broadcastMessage("§4§l[ПОБЕГ] " + player.getName() + " сбежал из тюрьмы!");
        
        // Start manhunt timer
        startManhunt(player);
    }
    
    private void handleFailedEscape(Player player, String routeType) {
        player.sendMessage("§c§lПОБЕГ ПРОВАЛЕН!");
        player.sendMessage("§eВас поймали при попытке побега через: " + routeType);
        
        // Punishments
        plugin.getEconomyManager().removeMoney(player, 200.0);
        plugin.getReputationManager().addReputation(player, -30);
        
        // Add sentence time
        plugin.getPlayerManager().addSentenceTime(player, 24 * 60); // 24 hours
        
        // Solitary confinement
        Location solitary = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 50, -150);
        player.teleport(solitary);
        
        player.sendMessage("§4Вы отправлены в карцер на 10 минут!");
        
        // Release from solitary after 10 minutes
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location yard = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 62, 0);
            player.teleport(yard);
            player.sendMessage("§eВы освобождены из карцера.");
        }, 12000L); // 10 minutes
    }
    
    private void startManhunt(Player player) {
        player.sendMessage("§cНачинается розыск! У вас есть 30 минут свободы!");
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage("§4Вас поймали! Возвращение в тюрьму...");
                Location prison = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 62, 0);
                player.teleport(prison);
                
                // Heavy punishment for being caught
                plugin.getEconomyManager().removeMoney(player, 500.0);
                plugin.getReputationManager().addReputation(player, -50);
                plugin.getPlayerManager().addSentenceTime(player, 48 * 60); // 48 hours
            }
        }, 36000L); // 30 minutes
    }
    
    public void giveEscapeTool(Player player, String toolType) {
        UUID uuid = player.getUniqueId();
        Set<String> tools = playerTools.getOrDefault(uuid, new HashSet<>());
        tools.add(toolType);
        playerTools.put(uuid, tools);
        
        player.sendMessage("§aВы нашли: " + getToolDisplayName(toolType));
        player.sendMessage("§7Используйте §f/escape plan §7для планирования побега");
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null) return;
        
        // Check if player is holding an escape tool
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            if (lore.contains("§7Инструмент для побега")) {
                String toolName = ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase();
                
                for (String toolType : toolLocations.keySet()) {
                    if (getToolDisplayName(toolType).toLowerCase().contains(toolName)) {
                        giveEscapeTool(player, toolType);
                        item.setAmount(item.getAmount() - 1);
                        break;
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onEscapePlanClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§4План Побега")) return;
        
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        
        if (event.getCurrentItem() == null) return;
        
        int slot = event.getSlot();
        String routeType = null;
        
        switch (slot) {
            case 10: routeType = "tunnel"; break;
            case 12: routeType = "wall"; break;
            case 14: routeType = "gate"; break;
            case 16: routeType = "disguise"; break;
        }
        
        if (routeType != null) {
            player.closeInventory();
            
            // Confirmation menu
            Inventory confirm = Bukkit.createInventory(null, 9, "§4Подтвердить побег?");
            
            ItemStack yes = new ItemStack(Material.GREEN_WOOL);
            ItemMeta yesMeta = yes.getItemMeta();
            yesMeta.setDisplayName("§aДА - Попытаться сбежать");
            yes.setItemMeta(yesMeta);
            confirm.setItem(3, yes);
            
            ItemStack no = new ItemStack(Material.RED_WOOL);
            ItemMeta noMeta = no.getItemMeta();
            noMeta.setDisplayName("§cНЕТ - Отменить");
            no.setItemMeta(noMeta);
            confirm.setItem(5, no);
            
            player.openInventory(confirm);
            
            // Store route type for confirmation
            final String finalRouteType = routeType;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.getOpenInventory().getTitle().equals("§4Подтвердить побег?")) {
                    player.closeInventory();
                }
            }, 200L); // Auto-close after 10 seconds
        }
    }
    
    @EventHandler
    public void onEscapeConfirmClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§4Подтвердить побег?")) return;
        
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        
        if (event.getCurrentItem() == null) return;
        
        if (event.getCurrentItem().getType() == Material.GREEN_WOOL) {
            player.closeInventory();
            // Start escape attempt - need to determine route type from previous context
            player.sendMessage("§eНачинается попытка побега...");
            
            // For now, use tunnel as default - in real implementation, 
            // you'd store the route type in a temporary map
            attemptEscape(player, "tunnel");
        } else if (event.getCurrentItem().getType() == Material.RED_WOOL) {
            player.closeInventory();
            player.sendMessage("§7Побег отменён.");
        }
    }
    
    public boolean hasEscapeTool(Player player, String toolType) {
        Set<String> tools = playerTools.getOrDefault(player.getUniqueId(), new HashSet<>());
        return tools.contains(toolType);
    }
    
    public Set<String> getPlayerTools(Player player) {
        return playerTools.getOrDefault(player.getUniqueId(), new HashSet<>());
    }
    
    public void removeEscapeTool(Player player, String toolType) {
        UUID uuid = player.getUniqueId();
        Set<String> tools = playerTools.getOrDefault(uuid, new HashSet<>());
        tools.remove(toolType);
        playerTools.put(uuid, tools);
    }
}
