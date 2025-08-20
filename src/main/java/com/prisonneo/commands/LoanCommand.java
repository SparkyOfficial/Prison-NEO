package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoanCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public LoanCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут брать кредиты!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            plugin.getLoanManager().openLoanMenu(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "menu":
                plugin.getLoanManager().openLoanMenu(player);
                break;
            case "repay":
            case "погасить":
                plugin.getLoanManager().repayLoan(player);
                break;
            case "status":
            case "статус":
                showLoanStatus(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showLoanStatus(Player player) {
        if (!plugin.getLoanManager().hasActiveLoan(player)) {
            player.sendMessage("§aУ вас нет активных кредитов!");
            return;
        }
        
        int daysLeft = plugin.getLoanManager().getDaysLeft(player);
        player.sendMessage("§6=== Статус кредита ===");
        player.sendMessage("§7Дней до погашения: §e" + daysLeft);
        player.sendMessage("§7Используйте §f/loan repay §7для погашения");
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Кредитная система ===");
        player.sendMessage("§e/loan §f- Открыть меню кредитов");
        player.sendMessage("§e/loan repay §f- Погасить кредит");
        player.sendMessage("§e/loan status §f- Статус кредита");
    }
}
