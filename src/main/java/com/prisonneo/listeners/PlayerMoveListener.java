package com.prisonneo.listeners;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    
    private final PrisonNEO plugin;
    
    public PlayerMoveListener(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only check movement in prison world
        if (!event.getPlayer().getWorld().equals(plugin.getWorldManager().getPrisonWorld())) {
            return;
        }
        
        Location to = event.getTo();
        if (to == null) return;
        
        // Check if player is trying to escape (beyond prison walls)
        if (Math.abs(to.getX()) > 200 || Math.abs(to.getZ()) > 200) {
            PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(event.getPlayer());
            
            // Only allow guards and trustees to go beyond walls
            if (!prisonPlayer.getRank().equals("GUARD") && !prisonPlayer.getRank().equals("TRUSTEE")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot escape the prison walls!");
                
                // Teleport back to safe location
                Location safeLocation = new Location(plugin.getWorldManager().getPrisonWorld(), 0, 61, 0);
                event.getPlayer().teleport(safeLocation);
            }
        }
        
        // Check if player entered a restricted area
        checkRestrictedAreas(event);
    }
    
    private void checkRestrictedAreas(PlayerMoveEvent event) {
        Location loc = event.getTo();
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(event.getPlayer());
        
        // Admin building restriction (only guards and trustees)
        if (isInAdminBuilding(loc) && !prisonPlayer.getRank().equals("GUARD") && !prisonPlayer.getRank().equals("TRUSTEE")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Access denied! Admin area is restricted.");
        }
        
        // Guard tower restriction (only guards)
        if (isInGuardTower(loc) && !prisonPlayer.getRank().equals("GUARD")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Access denied! Guard towers are for guards only.");
        }
    }
    
    private boolean isInAdminBuilding(Location loc) {
        return loc.getX() >= 60 && loc.getX() <= 90 && 
               loc.getZ() >= -30 && loc.getZ() <= 0 && 
               loc.getY() >= 62;
    }
    
    private boolean isInGuardTower(Location loc) {
        // Check all four guard tower locations
        return (isNearLocation(loc, -190, -190, 15) ||
                isNearLocation(loc, 190, -190, 15) ||
                isNearLocation(loc, -190, 190, 15) ||
                isNearLocation(loc, 190, 190, 15)) && loc.getY() > 75;
    }
    
    private boolean isNearLocation(Location loc, double x, double z, double radius) {
        return Math.abs(loc.getX() - x) <= radius && Math.abs(loc.getZ() - z) <= radius;
    }
}
