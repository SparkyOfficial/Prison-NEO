package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public AdminCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("prison.admin")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав администратора!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "riot":
                handleRiotCommands(sender, args);
                break;
            case "event":
                handleEventCommands(sender, args);
                break;
            case "player":
                handlePlayerCommands(sender, args);
                break;
            case "economy":
                handleEconomyCommands(sender, args);
                break;
            case "reload":
                reloadPlugin(sender);
                break;
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void handleRiotCommands(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /prisonadmin riot <start|end|status>");
            return;
        }
        
        switch (args[1].toLowerCase()) {
            case "start":
                plugin.getRiotManager().startRiot();
                sender.sendMessage("§aBунт запущен!");
                break;
            case "end":
                plugin.getRiotManager().forceEndRiot();
                sender.sendMessage("§aBунт принудительно завершён!");
                break;
            case "status":
                boolean active = plugin.getRiotManager().isRiotActive();
                sender.sendMessage("§eСтатус бунта: " + (active ? "§cАктивен" : "§aНеактивен"));
                break;
        }
    }
    
    private void handleEventCommands(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /prisonadmin event <lockdown|inspection|visitor|food|power>");
            return;
        }
        
        String eventType = args[1].toLowerCase();
        plugin.getEventManager().triggerEvent(eventType);
        sender.sendMessage("§aСобытие '" + eventType + "' запущено!");
    }
    
    private void handlePlayerCommands(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cИспользование: /prisonadmin player <игрок> <action>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден!");
            return;
        }
        
        String action = args[2].toLowerCase();
        
        switch (action) {
            case "free":
                plugin.getPlayerManager().setSentenceTime(target, 0);
                plugin.getCellManager().releaseFromCell(target);
                sender.sendMessage("§aИгрок " + target.getName() + " освобождён!");
                target.sendMessage("§aВы освобождены администратором!");
                break;
            case "solitary":
                // Teleport to solitary
                target.teleport(target.getWorld().getSpawnLocation().add(0, -10, -150));
                target.sendMessage("§cВы отправлены в карцер администратором!");
                sender.sendMessage("§aИгрок отправлен в карцер!");
                break;
            case "pardon":
                plugin.getPlayerManager().setSentenceTime(target, 0);
                plugin.getReputationManager().addReputation(target, 50);
                sender.sendMessage("§aИгрок помилован!");
                target.sendMessage("§aВы получили помилование!");
                break;
        }
    }
    
    private void handleEconomyCommands(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cИспользование: /prisonadmin economy <игрок> <add|remove|set> <сумма>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден!");
            return;
        }
        
        String action = args[2].toLowerCase();
        double amount;
        
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cНеверная сумма!");
            return;
        }
        
        switch (action) {
            case "add":
                plugin.getEconomyManager().addMoney(target, amount);
                sender.sendMessage("§aДобавлено $" + amount + " игроку " + target.getName());
                break;
            case "remove":
                plugin.getEconomyManager().removeMoney(target, amount);
                sender.sendMessage("§cСнято $" + amount + " у игрока " + target.getName());
                break;
            case "set":
                plugin.getEconomyManager().setMoney(target, amount);
                sender.sendMessage("§eУстановлено $" + amount + " игроку " + target.getName());
                break;
        }
    }
    
    private void reloadPlugin(CommandSender sender) {
        // Simple reload - in production you'd want more sophisticated reloading
        sender.sendMessage("§eПерезагрузка плагина...");
        
        // Reload config
        plugin.reloadConfig();
        
        // Restart some managers
        plugin.getEventManager().startRandomEvents();
        
        sender.sendMessage("§aПлагин перезагружен!");
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== Админ команды Prison NEO ===");
        sender.sendMessage("§e/prisonadmin riot <start|end|status> §f- Управление бунтами");
        sender.sendMessage("§e/prisonadmin event <тип> §f- Запуск событий");
        sender.sendMessage("§e/prisonadmin player <игрок> <free|solitary|pardon> §f- Управление игроками");
        sender.sendMessage("§e/prisonadmin economy <игрок> <add|remove|set> <сумма> §f- Управление деньгами");
        sender.sendMessage("§e/prisonadmin reload §f- Перезагрузка плагина");
    }
}
