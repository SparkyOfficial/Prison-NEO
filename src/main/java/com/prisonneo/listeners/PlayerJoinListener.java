package com.prisonneo.listeners;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    
    private final PrisonNEO plugin;
    
    public PlayerJoinListener(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(event.getPlayer());
        
        // Always teleport to prison world on join
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().isOnline()) {
                plugin.getWorldManager().teleportToPrison(event.getPlayer());
            }
        }, 20L); // Delay by 1 second to ensure world is loaded
        
        // Set join time for new players
        if (prisonPlayer.getName() == null) {
            plugin.getPlayerManager().addPlayer(event.getPlayer());
            
            event.getPlayer().sendMessage(ChatColor.RED + "=== ДОБРО ПОЖАЛОВАТЬ В PRISON NEO ===");
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Вы были приговорены к тюремному заключению!");
            event.getPlayer().sendMessage(ChatColor.WHITE + "Используйте /prison info для просмотра статуса");
            event.getPlayer().sendMessage(ChatColor.WHITE + "Используйте /cell assign для получения камеры");
            event.getPlayer().sendMessage(ChatColor.WHITE + "Используйте /mine list для просмотра шахт");
            event.getPlayer().sendMessage(ChatColor.AQUA + "Работайте усердно, чтобы заработать деньги и повысить ранг!");
            
            // Welcome message with prison atmosphere
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (event.getPlayer().isOnline()) {
                    event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "Железные ворота захлопнулись за вами...");
                    event.getPlayer().sendMessage(ChatColor.GRAY + "Добро пожаловать в вашу новую реальность.");
                }
            }, 60L);
        } else {
            // Existing player
            String displayName = plugin.getRankManager().getPlayerDisplayName(prisonPlayer);
            event.setJoinMessage(ChatColor.GRAY + "[ТЮРЬМА] " + displayName + ChatColor.GRAY + " вернулся в тюрьму");
            
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Добро пожаловать обратно в Prison NEO!");
            event.getPlayer().sendMessage(ChatColor.GRAY + "Ваш ранг: " + plugin.getRankManager().getPlayerRank(prisonPlayer).getDisplayName());
        }
    }
}
