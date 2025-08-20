package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GangCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public GangCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать команды банд!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            plugin.getGangManager().openGangMenu(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "menu":
                plugin.getGangManager().openGangMenu(player);
                break;
            case "leave":
                plugin.getGangManager().leaveGang(player);
                break;
            case "info":
                showGangInfo(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showGangInfo(Player player) {
        var gang = plugin.getGangManager().getPlayerGang(player);
        if (gang == null) {
            player.sendMessage("§cВы не состоите в банде!");
            return;
        }
        
        player.sendMessage("§6=== Информация о банде ===");
        player.sendMessage("§eНазвание: " + gang.getDisplayName());
        player.sendMessage("§eУчастников: §f" + gang.getMembers().size());
        player.sendMessage("§eТерритория: §f" + gang.getTerritoryInfo());
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Команды банд ===");
        player.sendMessage("§e/gang menu §f- Открыть меню банд");
        player.sendMessage("§e/gang info §f- Информация о вашей банде");
        player.sendMessage("§e/gang leave §f- Покинуть банду");
    }
}
