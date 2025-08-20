package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.Bukkit;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EscapeManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<UUID, EscapeAttempt> activeEscapes;
    private final Set<Location> escapePoints;
    private final Random random;
    
    public EscapeManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.activeEscapes = new HashMap<>();
        this.escapePoints = new HashSet<>();
        this.random = new Random();
        setupEscapePoints();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    private void setupEscapePoints() {
        if (plugin.getWorldManager().getPrisonWorld() == null) return;
        
        // Tunnel entrances (hidden)
        escapePoints.add(new Location(plugin.getWorldManager().getPrisonWorld(), -180, 50, 0));
        escapePoints.add(new Location(plugin.getWorldManager().getPrisonWorld(), 180, 50, 0));
        escapePoints.add(new Location(plugin.getWorldManager().getPrisonWorld(), 0, 50, -180));
        escapePoints.add(new Location(plugin.getWorldManager().getPrisonWorld(), 0, 50, 180));
        
        // Roof access points
        escapePoints.add(new Location(plugin.getWorldManager().getPrisonWorld(), -90, 76, -90));
        escapePoints.add(new Location(plugin.getWorldManager().getPrisonWorld(), 90, 76, 90));
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        
        Location loc = event.getClickedBlock().getLocation();
        
        // Check if player clicked near escape point
        for (Location escapePoint : escapePoints) {
            if (loc.distance(escapePoint) < 3) {
                startEscapeMinigame(event.getPlayer(), escapePoint);
                break;
            }
        }
    }
    
    public void startEscapeMinigame(Player player, Location escapePoint) {
        if (activeEscapes.containsKey(player.getUniqueId())) {
            player.sendMessage("§cВы уже пытаетесь сбежать!");
            return;
        }
        
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        // Check if player has escape plan (contraband)
        boolean hasEscapePlan = hasEscapePlan(player);
        double baseChance = hasEscapePlan ? 0.3 : 0.1; // 30% with plan, 10% without
        
        // Rank affects escape chance
        double rankBonus = getRankEscapeBonus(prisonPlayer.getRank());
        double totalChance = Math.min(baseChance + rankBonus, 0.8); // Max 80%
        
        EscapeAttempt attempt = new EscapeAttempt(player.getUniqueId(), escapePoint, totalChance);
        activeEscapes.put(player.getUniqueId(), attempt);
        
        openEscapeMinigame(player, attempt);
    }
    
    private void openEscapeMinigame(Player player, EscapeAttempt attempt) {
        Inventory inv = Bukkit.createInventory(null, 27, "§4§lПопытка Побега");
        
        // Escape sequence (player must click in order)
        int[] sequence = {10, 12, 14, 16};
        attempt.setSequence(sequence);
        
        for (int i = 0; i < sequence.length; i++) {
            ItemStack step = new ItemStack(Material.RED_WOOL);
            ItemMeta meta = step.getItemMeta();
            meta.setDisplayName("§cШаг " + (i + 1));
            meta.setLore(Arrays.asList(
                "§7Нажмите в правильном порядке",
                "§7Шанс успеха: §e" + String.format("%.1f%%", attempt.getSuccessChance() * 100)
            ));
            step.setItemMeta(meta);
            inv.setItem(sequence[i], step);
        }
        
        // Cancel button
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§cОтменить Побег");
        cancel.setItemMeta(cancelMeta);
        inv.setItem(26, cancel);
        
        player.openInventory(inv);
        player.sendMessage("§c§lВНИМАНИЕ: Попытка побега!");
        player.sendMessage("§eНажимайте кнопки в правильном порядке...");
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.equals("§4§lПопытка Побега")) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        
        EscapeAttempt attempt = activeEscapes.get(player.getUniqueId());
        if (attempt == null) return;
        
        if (clicked.getType() == Material.BARRIER) {
            // Cancel escape
            activeEscapes.remove(player.getUniqueId());
            player.closeInventory();
            player.sendMessage("§eПобег отменён.");
            return;
        }
        
        if (clicked.getType() == Material.RED_WOOL) {
            int clickedSlot = event.getSlot();
            
            if (attempt.isCorrectStep(clickedSlot)) {
                attempt.nextStep();
                
                // Change clicked item to green
                ItemStack success = new ItemStack(Material.GREEN_WOOL);
                ItemMeta meta = success.getItemMeta();
                meta.setDisplayName("§a✓ Выполнено");
                success.setItemMeta(meta);
                event.getInventory().setItem(clickedSlot, success);
                
                if (attempt.isComplete()) {
                    // Attempt complete, check success
                    player.closeInventory();
                    processEscapeResult(player, attempt);
                }
            } else {
                // Wrong step - escape failed
                player.closeInventory();
                escapeFailure(player);
            }
        }
    }
    
    private void processEscapeResult(Player player, EscapeAttempt attempt) {
        if (random.nextDouble() < attempt.getSuccessChance()) {
            // Escape successful!
            escapeSuccess(player);
        } else {
            // Escape failed
            escapeFailure(player);
        }
        
        activeEscapes.remove(player.getUniqueId());
    }
    
    private void escapeSuccess(Player player) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        player.sendMessage("§a§l🎉 ПОБЕГ УДАЛСЯ! 🎉");
        player.sendMessage("§eВы сбежали из тюрьмы!");
        
        // Teleport to spawn world
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        
        // Clear sentence
        prisonPlayer.setSentence(0);
        
        // Broadcast escape
        Bukkit.broadcastMessage("§4§lВНИМАНИЕ: " + player.getName() + " сбежал из тюрьмы!");
        
        // Release cell
        plugin.getCellManager().releaseCell(player.getUniqueId());
    }
    
    private void escapeFailure(Player player) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        player.sendMessage("§4§lПОБЕГ ПРОВАЛЕН!");
        player.sendMessage("§cВас поймали охранники!");
        
        // Punishment
        prisonPlayer.setSentence(prisonPlayer.getSentence() + 48); // Add 48 hours
        prisonPlayer.removeMoney(prisonPlayer.getMoney() * 0.5); // Lose 50% money
        
        player.sendMessage("§cНаказание: +48 часов к сроку");
        player.sendMessage("§cШтраф: 50% от ваших денег");
        
        // Teleport to solitary confinement
        Location solitary = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 55, 0);
        player.teleport(solitary);
        
        // Broadcast failed escape
        Bukkit.broadcastMessage("§6" + player.getName() + " попытался сбежать, но был пойман!");
    }
    
    private boolean hasEscapePlan(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.COMPASS && 
                item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().contains("План Побега")) {
                return true;
            }
        }
        return false;
    }
    
    private double getRankEscapeBonus(String rank) {
        switch (rank) {
            case "S": return 0.2;
            case "A": return 0.15;
            case "B": return 0.1;
            case "C": return 0.05;
            case "TRUSTEE": return 0.3;
            default: return 0.0;
        }
    }
    
    public static class EscapeAttempt {
        private final UUID playerUuid;
        private final Location escapePoint;
        private final double successChance;
        private int[] sequence;
        private int currentStep;
        
        public EscapeAttempt(UUID playerUuid, Location escapePoint, double successChance) {
            this.playerUuid = playerUuid;
            this.escapePoint = escapePoint;
            this.successChance = successChance;
            this.currentStep = 0;
        }
        
        public boolean isCorrectStep(int slot) {
            return currentStep < sequence.length && sequence[currentStep] == slot;
        }
        
        public void nextStep() {
            currentStep++;
        }
        
        public boolean isComplete() {
            return currentStep >= sequence.length;
        }
        
        public void setSequence(int[] sequence) { this.sequence = sequence; }
        public double getSuccessChance() { return successChance; }
        public UUID getPlayerUuid() { return playerUuid; }
        public Location getEscapePoint() { return escapePoint; }
    }
}
