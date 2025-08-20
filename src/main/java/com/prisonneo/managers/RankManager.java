package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RankManager {
    
    private final PrisonNEO plugin;
    private final Map<String, RankData> ranks;
    
    public RankManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.ranks = new HashMap<>();
        setupRanks();
    }
    
    private void setupRanks() {
        // Rank D - Lowest
        ranks.put("D", new RankData("D", ChatColor.GRAY + "[D]", 0, 1000));
        
        // Rank C
        ranks.put("C", new RankData("C", ChatColor.WHITE + "[C]", 1000, 2500));
        
        // Rank B
        ranks.put("B", new RankData("B", ChatColor.YELLOW + "[B]", 2500, 5000));
        
        // Rank A
        ranks.put("A", new RankData("A", ChatColor.GREEN + "[A]", 5000, 10000));
        
        // Rank S - Highest
        ranks.put("S", new RankData("S", ChatColor.GOLD + "[S]", 10000, -1));
        
        // Trustee - Special rank
        ranks.put("TRUSTEE", new RankData("TRUSTEE", ChatColor.BLUE + "[TRUSTEE]", 15000, -1));
        
        // Guard - Staff rank
        ranks.put("GUARD", new RankData("GUARD", ChatColor.RED + "[GUARD]", -1, -1));
    }
    
    public boolean canRankUp(PrisonPlayer prisonPlayer) {
        RankData currentRank = ranks.get(prisonPlayer.getRank());
        return currentRank != null && currentRank.getNextRankCost() != -1 && 
               prisonPlayer.getMoney() >= currentRank.getNextRankCost();
    }
    
    public void promotePlayer(Player player) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        String currentRank = prisonPlayer.getRank();
        String nextRank = getNextRank(currentRank);
        
        if (nextRank != null) {
            prisonPlayer.setRank(nextRank);
            player.sendMessage("§aYou have been promoted to rank " + nextRank + "!");
        } else {
            player.sendMessage("§cYou are already at the highest rank!");
        }
    }
    
    public boolean rankUp(Player player) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        RankData currentRank = ranks.get(prisonPlayer.getRank());
        
        if (currentRank == null || !canRankUp(prisonPlayer)) {
            return false;
        }
        
        String nextRank = getNextRank(prisonPlayer.getRank());
        if (nextRank == null) {
            return false;
        }
        
        // Deduct money and promote
        prisonPlayer.removeMoney(currentRank.getNextRankCost());
        prisonPlayer.setRank(nextRank);
        
        player.sendMessage(ChatColor.GREEN + "Congratulations! You've been promoted to rank " + 
                          ranks.get(nextRank).getDisplayName());
        
        return true;
    }
    
    private String getNextRank(String currentRank) {
        switch (currentRank) {
            case "D": return "C";
            case "C": return "B";
            case "B": return "A";
            case "A": return "S";
            case "S": return "TRUSTEE";
            default: return null;
        }
    }
    
    public RankData getRankData(String rank) {
        return ranks.get(rank);
    }
    
    public String getPlayerDisplayName(PrisonPlayer prisonPlayer) {
        RankData rankData = ranks.get(prisonPlayer.getRank());
        if (rankData != null) {
            return rankData.getDisplayName() + " " + prisonPlayer.getName();
        }
        return prisonPlayer.getName();
    }
    
    public static class RankData {
        private final String name;
        private final String displayName;
        private final int requiredMoney;
        private final int nextRankCost;
        
        public RankData(String name, String displayName, int requiredMoney, int nextRankCost) {
            this.name = name;
            this.displayName = displayName;
            this.requiredMoney = requiredMoney;
            this.nextRankCost = nextRankCost;
        }
        
        public String getName() { return name; }
        public String getDisplayName() { return displayName; }
        public int getRequiredMoney() { return requiredMoney; }
        public int getNextRankCost() { return nextRankCost; }
    }
    
    // Additional methods needed by other managers
    public int getRankLevel(Player player) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        String rank = prisonPlayer.getRank();
        
        switch (rank.toUpperCase()) {
            case "D": return 1;
            case "C": return 2;
            case "B": return 3;
            case "A": return 4;
            case "S": return 5;
            default: return 1;
        }
    }
}
