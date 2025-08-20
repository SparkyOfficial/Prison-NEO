package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScheduleCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public ScheduleCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут просматривать расписание!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showSchedule(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "info":
                showSchedule(player);
                break;
            case "current":
                showCurrentEvent(player);
                break;
            case "compliance":
                showCompliance(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showSchedule(Player player) {
        String scheduleInfo = plugin.getScheduleManager().getScheduleInfo();
        player.sendMessage(scheduleInfo);
    }
    
    private void showCurrentEvent(Player player) {
        var currentEvent = plugin.getScheduleManager().getCurrentEvent();
        player.sendMessage("§6Текущее событие: §e" + currentEvent.getName());
    }
    
    private void showCompliance(Player player) {
        boolean compliant = plugin.getScheduleManager().isPlayerCompliant(player);
        player.sendMessage("§6Соблюдение расписания: " + (compliant ? "§aДа" : "§cНет"));
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Расписание тюрьмы ===");
        player.sendMessage("§e/schedule §f- Показать расписание");
        player.sendMessage("§e/schedule current §f- Текущее событие");
        player.sendMessage("§e/schedule compliance §f- Ваше соблюдение");
    }
}
