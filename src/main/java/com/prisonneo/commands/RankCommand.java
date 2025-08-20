package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import com.prisonneo.managers.RankManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public RankCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use rank commands!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            args = new String[]{"info"};
        }
        
        switch (args[0].toLowerCase()) {
            case "up":
            case "rankup":
                handleRankUp(player);
                break;
            case "info":
                handleInfo(player);
                break;
            case "list":
                handleList(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void handleRankUp(Player player) {
        if (plugin.getRankManager().rankUp(player)) {
            PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
            player.sendMessage(ChatColor.GREEN + "Successfully ranked up to " + prisonPlayer.getRank() + "!");
        } else {
            PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
            RankManager.RankData currentRank = plugin.getRankManager().getRankData(prisonPlayer.getRank());
            
            if (currentRank.getNextRankCost() == -1) {
                player.sendMessage(ChatColor.RED + "You are already at the highest rank!");
            } else {
                player.sendMessage(ChatColor.RED + String.format("You need $%.2f to rank up! You have $%.2f", 
                                  (double) currentRank.getNextRankCost(), prisonPlayer.getMoney()));
            }
        }
    }
    
    private void handleInfo(Player player) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        RankManager.RankData rankData = plugin.getRankManager().getRankData(prisonPlayer.getRank());
        
        player.sendMessage(ChatColor.GOLD + "=== Rank Information ===");
        player.sendMessage(ChatColor.YELLOW + "Current Rank: " + rankData.getDisplayName());
        player.sendMessage(ChatColor.YELLOW + "Money: " + ChatColor.GREEN + "$" + String.format("%.2f", prisonPlayer.getMoney()));
        
        if (rankData.getNextRankCost() != -1) {
            player.sendMessage(ChatColor.YELLOW + "Next Rank Cost: " + ChatColor.RED + "$" + rankData.getNextRankCost());
            double needed = rankData.getNextRankCost() - prisonPlayer.getMoney();
            if (needed > 0) {
                player.sendMessage(ChatColor.YELLOW + "Money Needed: " + ChatColor.RED + "$" + String.format("%.2f", needed));
            } else {
                player.sendMessage(ChatColor.GREEN + "You can rank up! Use /rank up");
            }
        } else {
            player.sendMessage(ChatColor.GOLD + "You are at the maximum rank!");
        }
    }
    
    private void handleList(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Prison Ranks ===");
        player.sendMessage(ChatColor.GRAY + "[D] " + ChatColor.WHITE + "- Starting rank (Free)");
        player.sendMessage(ChatColor.WHITE + "[C] " + ChatColor.WHITE + "- Cost: $1,000");
        player.sendMessage(ChatColor.YELLOW + "[B] " + ChatColor.WHITE + "- Cost: $2,500");
        player.sendMessage(ChatColor.GREEN + "[A] " + ChatColor.WHITE + "- Cost: $5,000");
        player.sendMessage(ChatColor.GOLD + "[S] " + ChatColor.WHITE + "- Cost: $10,000");
        player.sendMessage(ChatColor.BLUE + "[TRUSTEE] " + ChatColor.WHITE + "- Cost: $15,000");
        player.sendMessage(ChatColor.RED + "[GUARD] " + ChatColor.WHITE + "- Staff only");
    }
    
    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Rank Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/rank up" + ChatColor.WHITE + " - Rank up if you have enough money");
        player.sendMessage(ChatColor.YELLOW + "/rank info" + ChatColor.WHITE + " - View your rank information");
        player.sendMessage(ChatColor.YELLOW + "/rank list" + ChatColor.WHITE + " - View all available ranks");
    }
}
