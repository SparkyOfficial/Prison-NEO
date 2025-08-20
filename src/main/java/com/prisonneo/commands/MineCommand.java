package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import com.prisonneo.managers.MineManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MineCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public MineCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use mine commands!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "tp":
            case "teleport":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /mine tp <mine>");
                    return true;
                }
                handleTeleport(player, args[1]);
                break;
            case "list":
                handleList(player);
                break;
            case "info":
                handleInfo(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void handleTeleport(Player player, String mineId) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        MineManager.MineData mine = plugin.getMineManager().getMine(mineId.toUpperCase());
        
        if (mine == null) {
            player.sendMessage(ChatColor.RED + "Mine not found! Available mines: A, B, C, D");
            return;
        }
        
        // Check rank requirement
        if (!hasRequiredRank(prisonPlayer.getRank(), mine.getRequiredRank())) {
            player.sendMessage(ChatColor.RED + "You need rank " + mine.getRequiredRank() + " to access " + mine.getName() + "!");
            return;
        }
        
        plugin.getMineManager().teleportToMine(player, mineId.toUpperCase());
        player.sendMessage(ChatColor.GREEN + "Teleported to " + mine.getName() + "!");
    }
    
    private void handleList(Player player) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        player.sendMessage(ChatColor.GOLD + "=== Available Mines ===");
        player.sendMessage(formatMineInfo("A", "Coal Mine", "D", 1.0, prisonPlayer.getRank()));
        player.sendMessage(formatMineInfo("B", "Iron Mine", "C", 2.5, prisonPlayer.getRank()));
        player.sendMessage(formatMineInfo("C", "Gold Mine", "B", 5.0, prisonPlayer.getRank()));
        player.sendMessage(formatMineInfo("D", "Diamond Mine", "A", 10.0, prisonPlayer.getRank()));
    }
    
    private String formatMineInfo(String id, String name, String requiredRank, double payout, String playerRank) {
        boolean canAccess = hasRequiredRank(playerRank, requiredRank);
        ChatColor color = canAccess ? ChatColor.GREEN : ChatColor.RED;
        String status = canAccess ? "✓" : "✗";
        
        return String.format("%s%s Mine %s: %s - Req: %s - Pay: $%.1f", 
                           color, status, id, name, requiredRank, payout);
    }
    
    private void handleInfo(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Mining Information ===");
        player.sendMessage(ChatColor.YELLOW + "Break ore blocks to earn money!");
        player.sendMessage(ChatColor.YELLOW + "Higher rank mines pay more but require progression");
        player.sendMessage(ChatColor.YELLOW + "Mines reset automatically every 10 minutes");
        player.sendMessage(ChatColor.AQUA + "Use /mine list to see all available mines");
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
    
    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Mine Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/mine tp <mine>" + ChatColor.WHITE + " - Teleport to mine (A, B, C, D)");
        player.sendMessage(ChatColor.YELLOW + "/mine list" + ChatColor.WHITE + " - List all available mines");
        player.sendMessage(ChatColor.YELLOW + "/mine info" + ChatColor.WHITE + " - Mining information");
    }
}
