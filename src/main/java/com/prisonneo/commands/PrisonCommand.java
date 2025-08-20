package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrisonCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public PrisonCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "info":
                handleInfo(sender);
                break;
            case "stats":
                handleStats(sender);
                break;
            case "shop":
                handleShop(sender);
                break;
            case "help":
                sendHelp(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void handleInfo(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can view info!");
            return;
        }
        
        Player player = (Player) sender;
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        player.sendMessage(ChatColor.GOLD + "=== Prison Info ===");
        player.sendMessage(ChatColor.YELLOW + "Name: " + ChatColor.WHITE + prisonPlayer.getName());
        player.sendMessage(ChatColor.YELLOW + "Rank: " + plugin.getRankManager().getRankData(prisonPlayer.getRank()).getDisplayName());
        player.sendMessage(ChatColor.YELLOW + "Money: " + ChatColor.GREEN + "$" + String.format("%.2f", prisonPlayer.getMoney()));
        player.sendMessage(ChatColor.YELLOW + "Sentence: " + ChatColor.RED + prisonPlayer.getSentence() + " hours");
        player.sendMessage(ChatColor.YELLOW + "Cell: " + ChatColor.AQUA + (prisonPlayer.getCellId() != null ? prisonPlayer.getCellId() : "None"));
        player.sendMessage(ChatColor.YELLOW + "Play Time: " + ChatColor.WHITE + prisonPlayer.getFormattedPlayTime());
    }
    
    private void handleStats(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Prison NEO Statistics ===");
        sender.sendMessage(ChatColor.YELLOW + "Total Players: " + ChatColor.WHITE + plugin.getPlayerManager().getAllPlayers().size());
        sender.sendMessage(ChatColor.YELLOW + "Prison World: " + ChatColor.WHITE + 
                          (plugin.getWorldManager().isPrisonWorldLoaded() ? "Loaded" : "Not Loaded"));
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
    }
    
    private void handleShop(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use the shop!");
            return;
        }
        
        Player player = (Player) sender;
        player.sendMessage(ChatColor.GOLD + "=== Prison Shop ===");
        player.sendMessage(ChatColor.GREEN + "Use /prison buy <item> <amount> to purchase items");
        player.sendMessage(ChatColor.GREEN + "Use /prison sell <item> <amount> to sell items");
        player.sendMessage(ChatColor.YELLOW + "Available items:");
        player.sendMessage(ChatColor.WHITE + "- Food: bread, cooked_beef, apple");
        player.sendMessage(ChatColor.WHITE + "- Tools: wooden_pickaxe, stone_pickaxe, iron_pickaxe");
        player.sendMessage(ChatColor.WHITE + "- Armor: leather armor sets");
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Prison NEO Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/prison info" + ChatColor.WHITE + " - View your prison information");
        sender.sendMessage(ChatColor.YELLOW + "/prison stats" + ChatColor.WHITE + " - View server statistics");
        sender.sendMessage(ChatColor.YELLOW + "/prison shop" + ChatColor.WHITE + " - View shop information");
        sender.sendMessage(ChatColor.YELLOW + "/prisonworld create" + ChatColor.WHITE + " - Create prison world (admin)");
        sender.sendMessage(ChatColor.YELLOW + "/cell tp" + ChatColor.WHITE + " - Teleport to your cell");
        sender.sendMessage(ChatColor.YELLOW + "/mine tp <mine>" + ChatColor.WHITE + " - Teleport to mine");
        sender.sendMessage(ChatColor.YELLOW + "/rank up" + ChatColor.WHITE + " - Rank up if you have enough money");
    }
}
