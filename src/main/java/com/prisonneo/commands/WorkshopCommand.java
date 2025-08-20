package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorkshopCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public WorkshopCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать мастерскую!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            plugin.getWorkshopManager().openWorkshopMenu(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "menu":
                plugin.getWorkshopManager().openWorkshopMenu(player);
                break;
            case "level":
                int level = plugin.getWorkshopManager().getCraftingLevel(player);
                player.sendMessage("§6Ваш уровень мастерства: " + level);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Мастерская ===");
        player.sendMessage("§e/workshop §f- Открыть меню мастерской");
        player.sendMessage("§e/workshop level §f- Показать уровень мастерства");
    }
}
