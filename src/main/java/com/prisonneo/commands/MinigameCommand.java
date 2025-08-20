package com.prisonneo.commands;

import com.prisonneo.PrisonNEO;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MinigameCommand implements CommandExecutor {
    
    private final PrisonNEO plugin;
    
    public MinigameCommand(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут играть в мини-игры!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "lockpick":
            case "взлом":
                plugin.getMinigameManager().startLockpickingGame(player);
                break;
            case "cards":
            case "карты":
                plugin.getMinigameManager().startCardGame(player);
                break;
            case "reaction":
            case "реакция":
                plugin.getMinigameManager().startReactionGame(player);
                break;
            case "memory":
            case "память":
                plugin.getMinigameManager().startMemoryGame(player);
                break;
            case "quit":
            case "выход":
                plugin.getMinigameManager().forceEndGame(player);
                player.sendMessage("§eВы покинули мини-игру.");
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§6=== Мини-игры ===");
        player.sendMessage("§e/minigame lockpick §f- Игра на взлом замков");
        player.sendMessage("§e/minigame cards §f- Блэкджек");
        player.sendMessage("§e/minigame reaction §f- Тест на реакцию");
        player.sendMessage("§e/minigame memory §f- Игра на память");
        player.sendMessage("§e/minigame quit §f- Выйти из игры");
    }
}
