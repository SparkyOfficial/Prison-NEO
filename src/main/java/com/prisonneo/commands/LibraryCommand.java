package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LibraryCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public LibraryCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут посещать библиотеку!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            plugin.getLibraryManager().openLibraryMenu(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "menu":
                plugin.getLibraryManager().openLibraryMenu(player);
                break;
            case "books":
                int bookCount = plugin.getLibraryManager().getBookCount(player);
                player.sendMessage("§6У вас " + bookCount + " книг в коллекции");
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Библиотека ===");
        player.sendMessage("§e/library §f- Открыть библиотеку");
        player.sendMessage("§e/library books §f- Показать количество книг");
    }
}
