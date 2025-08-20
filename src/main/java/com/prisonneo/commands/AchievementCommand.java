package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AchievementCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public AchievementCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут просматривать достижения!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            plugin.getAchievementManager().openAchievementMenu(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "menu":
                plugin.getAchievementManager().openAchievementMenu(player);
                break;
            case "stats":
                showAchievementStats(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showAchievementStats(Player player) {
        int unlocked = plugin.getAchievementManager().getAchievementCount(player);
        int total = plugin.getAchievementManager().getTotalAchievements();
        double percentage = (double) unlocked / total * 100;
        
        player.sendMessage("§6=== Статистика достижений ===");
        player.sendMessage("§eРазблокировано: §a" + unlocked + "§f/§e" + total);
        player.sendMessage("§eПрогресс: §a" + String.format("%.1f", percentage) + "%");
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Достижения ===");
        player.sendMessage("§e/achievement §f- Открыть меню достижений");
        player.sendMessage("§e/achievement stats §f- Статистика достижений");
    }
}
