package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public JobCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут работать!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "list":
                showJobList(player);
                break;
            case "quit":
                quitJob(player);
                break;
            case "status":
                showJobStatus(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showJobList(Player player) {
        player.sendMessage("§6=== Доступные работы ===");
        player.sendMessage("§e• Кухня §f- $5.0 за 5 минут");
        player.sendMessage("§e• Прачечная §f- $4.0 за 4 минуты");
        player.sendMessage("§e• Библиотека §f- $3.0 за 3 минуты");
        player.sendMessage("§e• Уборка §f- $6.0 за 6 минут");
        player.sendMessage("§e• Помощник Охранника §f- $8.0 за 8 минут (только Trustee)");
        player.sendMessage("§aНайдите NPC для начала работы!");
    }
    
    private void quitJob(Player player) {
        if (!plugin.getJobManager().hasActiveJob(player)) {
            player.sendMessage("§cВы не работаете!");
            return;
        }
        
        String jobType = plugin.getJobManager().getActiveJob(player);
        plugin.getJobManager().completeJob(player);
        player.sendMessage("§eВы бросили работу: " + jobType);
    }
    
    private void showJobStatus(Player player) {
        if (!plugin.getJobManager().hasActiveJob(player)) {
            player.sendMessage("§cВы не работаете!");
            return;
        }
        
        String jobType = plugin.getJobManager().getActiveJob(player);
        player.sendMessage("§aTекущая работа: " + jobType);
        player.sendMessage("§eИспользуйте §f/job quit §eчтобы бросить работу");
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Команды работы ===");
        player.sendMessage("§e/job list §f- Список доступных работ");
        player.sendMessage("§e/job status §f- Статус текущей работы");
        player.sendMessage("§e/job quit §f- Бросить текущую работу");
    }
}
