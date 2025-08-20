package com.prisonneo.listeners;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    
    private final PrisonNEO plugin;
    
    public PlayerQuitListener(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(event.getPlayer());
        
        // Update play time
        long sessionTime = System.currentTimeMillis() - prisonPlayer.getJoinTime();
        prisonPlayer.setPlayTime(prisonPlayer.getPlayTime() + sessionTime);
        
        // Save player data
        plugin.getPlayerManager().savePlayerData(prisonPlayer);
        
        // Custom quit message
        String displayName = plugin.getRankManager().getPlayerDisplayName(prisonPlayer);
        event.setQuitMessage("§7[PRISON] " + displayName + " §7has left the prison");
    }
}
