package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ContrabandCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public ContrabandCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут торговать контрабандой!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            plugin.getContrabandManager().openContrabandShop(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "shop":
                plugin.getContrabandManager().openContrabandShop(player);
                break;
            case "suspicion":
                showSuspicion(player);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showSuspicion(Player player) {
        int suspicion = plugin.getContrabandManager().getPlayerSuspicion(player);
        player.sendMessage("§6=== Уровень подозрения ===");
        player.sendMessage("§eТекущий уровень: §c" + suspicion);
        
        if (suspicion == 0) {
            player.sendMessage("§aВы не вызываете подозрений!");
        } else if (suspicion < 5) {
            player.sendMessage("§eНизкий уровень подозрения");
        } else if (suspicion < 10) {
            player.sendMessage("§6Средний уровень подозрения");
        } else {
            player.sendMessage("§cВысокий уровень подозрения!");
            player.sendMessage("§4Будьте осторожны - вас могут обыскать!");
        }
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Команды контрабанды ===");
        player.sendMessage("§e/contraband shop §f- Чёрный рынок");
        player.sendMessage("§e/contraband suspicion §f- Уровень подозрения");
        player.sendMessage("§c§lОСТОРОЖНО: Контрабанда незаконна!");
    }
}
