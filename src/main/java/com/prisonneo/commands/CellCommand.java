package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CellCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public CellCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use cell commands!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            args = new String[]{"tp"};
        }
        
        switch (args[0].toLowerCase()) {
            case "tp":
            case "teleport":
                handleTeleport(player);
                break;
            case "info":
                handleInfo(player);
                break;
            case "assign":
                handleAssign(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void handleTeleport(Player player) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        if (prisonPlayer.getCellId() == null) {
            player.sendMessage(ChatColor.RED + "You don't have a cell assigned! Use /cell assign");
            return;
        }
        
        plugin.getCellManager().teleportToCell(player);
        player.sendMessage(ChatColor.GREEN + "Teleported to your cell: " + prisonPlayer.getCellId());
    }
    
    private void handleInfo(Player player) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        player.sendMessage(ChatColor.GOLD + "=== Cell Information ===");
        if (prisonPlayer.getCellId() != null) {
            player.sendMessage(ChatColor.YELLOW + "Cell ID: " + ChatColor.WHITE + prisonPlayer.getCellId());
            player.sendMessage(ChatColor.YELLOW + "Block: " + ChatColor.WHITE + prisonPlayer.getCellId().split("-")[0]);
        } else {
            player.sendMessage(ChatColor.RED + "No cell assigned!");
        }
    }
    
    private void handleAssign(Player player) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        if (prisonPlayer.getCellId() != null) {
            player.sendMessage(ChatColor.RED + "You already have a cell: " + prisonPlayer.getCellId());
            return;
        }
        
        String cellId = plugin.getCellManager().assignCell(player);
        if (cellId != null) {
            player.sendMessage(ChatColor.GREEN + "Cell assigned: " + cellId);
            player.sendMessage(ChatColor.AQUA + "Use /cell tp to teleport to your cell");
        } else {
            player.sendMessage(ChatColor.RED + "No cells available! Try again later.");
        }
    }
    
    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Cell Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/cell tp" + ChatColor.WHITE + " - Teleport to your cell");
        player.sendMessage(ChatColor.YELLOW + "/cell info" + ChatColor.WHITE + " - View cell information");
        player.sendMessage(ChatColor.YELLOW + "/cell assign" + ChatColor.WHITE + " - Get assigned a cell");
    }
}
