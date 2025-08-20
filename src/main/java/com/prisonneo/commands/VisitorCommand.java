package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VisitorCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public VisitorCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут назначать свидания!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            plugin.getVisitorManager().openVisitorMenu(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "schedule":
            case "назначить":
                if (args.length < 2) {
                    player.sendMessage("§cУкажите тип: family, lawyer, friend, business");
                    return true;
                }
                plugin.getVisitorManager().scheduleVisit(player, args[1].toLowerCase());
                break;
            case "cancel":
            case "отменить":
                plugin.getVisitorManager().cancelVisit(player);
                break;
            case "status":
            case "статус":
                showVisitStatus(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showVisitStatus(Player player) {
        if (plugin.getVisitorManager().hasScheduledVisit(player)) {
            player.sendMessage("§eУ вас запланировано свидание");
        } else if (plugin.getVisitorManager().isInVisit(player)) {
            player.sendMessage("§aВы сейчас на свидании");
        } else {
            player.sendMessage("§7У вас нет запланированных свиданий");
        }
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Система свиданий ===");
        player.sendMessage("§e/visitor §f- Открыть меню свиданий");
        player.sendMessage("§e/visitor schedule <тип> §f- Назначить свидание");
        player.sendMessage("§e/visitor cancel §f- Отменить свидание");
        player.sendMessage("§e/visitor status §f- Статус свидания");
    }
}
