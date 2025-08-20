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
        
        // Set join time for new players
        if (prisonPlayer.getName() == null) {
            plugin.getPlayerManager().addPlayer(event.getPlayer());
            
            event.getPlayer().sendMessage(ChatColor.RED + "=== WELCOME TO PRISON NEO ===");
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You have been sentenced to prison!");
            event.getPlayer().sendMessage(ChatColor.WHITE + "Use /prison info to view your status");
            event.getPlayer().sendMessage(ChatColor.WHITE + "Use /cell assign to get a cell");
            event.getPlayer().sendMessage(ChatColor.WHITE + "Use /mine list to see available mines");
            event.getPlayer().sendMessage(ChatColor.AQUA + "Work hard to earn money and rank up!");
        } else {
            // Existing player
            String displayName = plugin.getRankManager().getPlayerDisplayName(prisonPlayer);
            event.setJoinMessage(ChatColor.GRAY + "[PRISON] " + displayName + ChatColor.GRAY + " has returned to prison");
            
            // Teleport to prison if not already there
            if (!event.getPlayer().getWorld().equals(plugin.getWorldManager().getPrisonWorld())) {
                plugin.getWorldManager().teleportToPrison(event.getPlayer());
            }
        }
    }
}
