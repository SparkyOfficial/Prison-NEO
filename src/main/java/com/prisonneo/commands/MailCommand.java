package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MailCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public MailCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать почту!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            plugin.getMailManager().openMailbox(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "send":
            case "отправить":
                if (args.length < 4) {
                    player.sendMessage("§cИспользование: /mail send <игрок> <тема> <сообщение>");
                    return true;
                }
                
                String recipient = args[1];
                String subject = args[2];
                StringBuilder content = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    content.append(args[i]).append(" ");
                }
                
                plugin.getMailManager().sendMail(player, recipient, subject, content.toString().trim());
                break;
                
            case "box":
            case "ящик":
                plugin.getMailManager().openMailbox(player);
                break;
                
            case "count":
            case "количество":
                int total = plugin.getMailManager().getMailCount(player);
                int unread = plugin.getMailManager().getUnreadMailCount(player);
                player.sendMessage("§eВсего писем: §f" + total);
                player.sendMessage("§eНепрочитанных: §a" + unread);
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Почтовая система ===");
        player.sendMessage("§e/mail §f- Открыть почтовый ящик");
        player.sendMessage("§e/mail send <игрок> <тема> <сообщение> §f- Отправить письмо");
        player.sendMessage("§e/mail count §f- Количество писем");
    }
}
