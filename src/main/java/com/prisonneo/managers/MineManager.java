package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MineManager {
    
    private final PrisonNEO plugin;
    private final Map<String, MineData> mines;
    private final Random random;
    
    public MineManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.mines = new HashMap<>();
        this.random = new Random();
        setupMines();
        startMineResetTask();
    }
    
    private void setupMines() {
        mines.put("A", new MineData("A", "Coal Mine", 
            new Location(plugin.getWorldManager().getPrisonWorld(), -135, 45, -135),
            new Location(plugin.getWorldManager().getPrisonWorld(), -135, 45, -135),
            Material.COAL_ORE, 1.0, "D"));
            
        mines.put("B", new MineData("B", "Iron Mine",
            new Location(plugin.getWorldManager().getPrisonWorld(), 135, 45, -135),
            new Location(plugin.getWorldManager().getPrisonWorld(), 135, 45, -135),
            Material.IRON_ORE, 2.5, "C"));
            
        mines.put("C", new MineData("C", "Gold Mine",
            new Location(plugin.getWorldManager().getPrisonWorld(), -135, 45, 135),
            new Location(plugin.getWorldManager().getPrisonWorld(), -135, 45, 135),
            Material.GOLD_ORE, 5.0, "B"));
            
        mines.put("D", new MineData("D", "Diamond Mine",
            new Location(plugin.getWorldManager().getPrisonWorld(), 135, 45, 135),
            new Location(plugin.getWorldManager().getPrisonWorld(), 135, 45, 135),
            Material.DIAMOND_ORE, 10.0, "A"));
    }
    
    public void handleBlockBreak(Player player, Block block) {
        MineData mine = getMineAtLocation(block.getLocation());
        if (mine == null) return;
        
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        // Check if player has required rank
        if (!hasRequiredRank(prisonPlayer.getRank(), mine.getRequiredRank())) {
            player.sendMessage("§cYou need rank " + mine.getRequiredRank() + " to mine here!");
            return;
        }
        
        // Give money for mining
        double earnings = mine.getPayoutPerBlock() + (random.nextDouble() * mine.getPayoutPerBlock() * 0.5);
        prisonPlayer.addMoney(earnings);
        
        player.sendMessage(String.format("§a+$%.2f §7(Total: $%.2f)", earnings, prisonPlayer.getMoney()));
        
        // Schedule block regeneration
        scheduleBlockRegen(block.getLocation(), mine.getOreType());
    }
    
    private MineData getMineAtLocation(Location location) {
        for (MineData mine : mines.values()) {
            if (isInMineArea(location, mine)) {
                return mine;
            }
        }
        return null;
    }
    
    private boolean isInMineArea(Location location, MineData mine) {
        // Check if location is within mine bounds (simplified)
        double distance = location.distance(mine.getCenter());
        return distance <= 30; // 30 block radius
    }
    
    private boolean hasRequiredRank(String playerRank, String requiredRank) {
        int playerRankValue = getRankValue(playerRank);
        int requiredRankValue = getRankValue(requiredRank);
        return playerRankValue >= requiredRankValue;
    }
    
    private int getRankValue(String rank) {
        switch (rank) {
            case "D": return 1;
            case "C": return 2;
            case "B": return 3;
            case "A": return 4;
            case "S": return 5;
            case "TRUSTEE": return 6;
            case "GUARD": return 10;
            default: return 0;
        }
    }
    
    private void scheduleBlockRegen(Location location, Material oreType) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (location.getBlock().getType() == Material.AIR || 
                    location.getBlock().getType() == Material.STONE) {
                    location.getBlock().setType(oreType);
                }
            }
        }.runTaskLater(plugin, 100L + random.nextInt(100)); // 5-10 seconds
    }
    
    private void startMineResetTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                resetAllMines();
            }
        }.runTaskTimer(plugin, 0L, 12000L); // Every 10 minutes
    }
    
    private void resetAllMines() {
        if (plugin.getWorldManager().getPrisonWorld() == null) return;
        
        for (MineData mine : mines.values()) {
            resetMine(mine);
        }
    }
    
    private void resetMine(MineData mine) {
        Location center = mine.getCenter();
        int radius = 15;
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -15; y <= 0; y++) {
                    Location loc = center.clone().add(x, y, z);
                    Block block = loc.getBlock();
                    
                    if (random.nextDouble() < 0.15) {
                        block.setType(mine.getOreType());
                    } else {
                        block.setType(Material.STONE);
                    }
                }
            }
        }
    }
    
    public MineData getMine(String mineId) {
        return mines.get(mineId);
    }
    
    public void teleportToMine(Player player, String mineId) {
        MineData mine = mines.get(mineId);
        if (mine != null) {
            player.teleport(mine.getTeleportLocation());
        }
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
