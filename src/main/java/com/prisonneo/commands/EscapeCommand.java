package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EscapeCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public EscapeCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут планировать побег!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "plan":
            case "план":
                plugin.getAdvancedEscapeManager().openEscapePlanMenu(player);
                break;
            case "tools":
            case "инструменты":
                showPlayerTools(player);
                break;
            case "attempt":
            case "попытка":
                if (args.length < 2) {
                    player.sendMessage("§cУкажите тип побега: tunnel, wall, gate, disguise");
                    return true;
                }
                plugin.getAdvancedEscapeManager().attemptEscape(player, args[1].toLowerCase());
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showPlayerTools(Player player) {
        var tools = plugin.getAdvancedEscapeManager().getPlayerTools(player);
        
        if (tools.isEmpty()) {
            player.sendMessage("§cУ вас нет инструментов для побега!");
            player.sendMessage("§7Ищите инструменты по тюрьме...");
            return;
        }
        
        player.sendMessage("§6=== Ваши инструменты ===");
        for (String tool : tools) {
            player.sendMessage("§a• " + getToolDisplayName(tool));
        }
    }
    
    private String getToolDisplayName(String toolType) {
        switch (toolType) {
            case "kitchen_knife": return "Кухонный Нож";
            case "screwdriver": return "Отвёртка";
            case "rope": return "Верёвка";
            case "lockpick": return "Отмычка";
            case "wire_cutters": return "Кусачки";
            case "crowbar": return "Лом";
            case "hammer": return "Молоток";
            case "chisel": return "Зубило";
            default: return "Инструмент";
        }
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Система побегов ===");
        player.sendMessage("§e/escape plan §f- Открыть план побега");
        player.sendMessage("§e/escape tools §f- Показать ваши инструменты");
        player.sendMessage("§e/escape attempt <тип> §f- Попытка побега");
        player.sendMessage("§7Типы: tunnel, wall, gate, disguise");
    }
}
