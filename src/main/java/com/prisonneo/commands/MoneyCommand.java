package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public MoneyCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать эту команду!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showBalance(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "balance":
            case "bal":
                showBalance(player);
                break;
            case "pay":
                if (args.length < 3) {
                    player.sendMessage("§cИспользование: /money pay <игрок> <количество>");
                    return true;
                }
                handlePayment(player, args[1], args[2]);
                break;
            case "top":
                showTopPlayers(player);
                break;
            case "help":
                sendHelp(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showBalance(Player player) {
        player.sendMessage("§6=== Ваш баланс ===");
        player.sendMessage(plugin.getPrisonCurrency().getBalanceString(player));
    }
    
    private void handlePayment(Player sender, String targetName, String amountStr) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден!");
            return;
        }
        
        if (target.equals(sender)) {
            sender.sendMessage("§cВы не можете передать деньги самому себе!");
            return;
        }
        
        try {
            int amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                sender.sendMessage("§cСумма должна быть больше 0!");
                return;
            }
            
            if (plugin.getPrisonCurrency().transferCigarettes(sender, target, amount)) {
                sender.sendMessage("§aУспешно передано " + amount + " сигарет игроку " + target.getName());
            } else {
                sender.sendMessage("§cУ вас недостаточно сигарет!");
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§cНеверное количество!");
        }
    }
    
    private void showTopPlayers(Player player) {
        player.sendMessage("§6=== Топ богачей тюрьмы ===");
        player.sendMessage("§7Функция в разработке...");
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Команды валюты ===");
        player.sendMessage("§e/money balance §f- Показать баланс");
        player.sendMessage("§e/money pay <игрок> <количество> §f- Передать сигареты");
        player.sendMessage("§e/money top §f- Топ игроков по богатству");
    }
}
