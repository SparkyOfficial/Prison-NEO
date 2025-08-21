package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrisonWorldCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public PrisonWorldCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("prison.world")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "create":
                handleCreate(sender);
                break;
            case "reset":
                handleReset(sender);
                break;
            case "tp":
                handleTeleport(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void handleCreate(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Creating prison world... This may take a moment.");
        
        // Run world creation on the main thread
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                plugin.getWorldManager().createPrisonWorld();
                sender.sendMessage(ChatColor.GREEN + "Prison world created successfully!");
                sender.sendMessage(ChatColor.AQUA + "Use /prisonworld tp to teleport to the prison.");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Failed to create prison world. Check console for errors.");
                plugin.getLogger().severe("Error creating prison world: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void handleReset(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Resetting prison world... This may take a moment.");
        
        // Run world reset on the main thread
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                plugin.getWorldManager().resetPrisonWorld();
                sender.sendMessage(ChatColor.GREEN + "Prison world reset successfully!");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Failed to reset prison world. Check console for errors.");
                plugin.getLogger().severe("Error resetting prison world: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void handleTeleport(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can teleport!");
            return;
        }
        
        Player player = (Player) sender;
        
        if (!plugin.getWorldManager().isPrisonWorldLoaded()) {
            player.sendMessage(ChatColor.RED + "Prison world is not loaded! Use /prisonworld create first.");
            return;
        }
        
        plugin.getWorldManager().teleportToPrison(player);
        player.sendMessage(ChatColor.GREEN + "Welcome to Prison NEO!");
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Prison World Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/prisonworld create" + ChatColor.WHITE + " - Create the prison world");
        sender.sendMessage(ChatColor.YELLOW + "/prisonworld reset" + ChatColor.WHITE + " - Reset the prison world");
        sender.sendMessage(ChatColor.YELLOW + "/prisonworld tp" + ChatColor.WHITE + " - Teleport to prison");
    }
}
