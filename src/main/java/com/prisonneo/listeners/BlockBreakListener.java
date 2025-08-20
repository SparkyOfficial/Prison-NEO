package com.prisonneo.listeners;

import com.prisonneo.PrisonNEO;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    
    private final PrisonNEO plugin;
    
    public BlockBreakListener(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Only handle mining in prison world
        if (!event.getBlock().getWorld().equals(plugin.getWorldManager().getPrisonWorld())) {
            return;
        }
        
        Material blockType = event.getBlock().getType();
        
        // Check if it's an ore block
        if (isOreBlock(blockType)) {
            // Handle mining through MineManager
            plugin.getMineManager().handleBlockBreak(event.getPlayer(), event.getBlock());
            
            // Prevent normal drops
            event.setDropItems(false);
            event.setExpToDrop(0);
        } else if (isProtectedBlock(blockType)) {
            // Prevent breaking of prison structure
            if (!event.getPlayer().hasPermission("prison.admin")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cYou cannot break prison structures!");
            }
        }
    }
    
    private boolean isOreBlock(Material material) {
        return material == Material.COAL_ORE ||
               material == Material.IRON_ORE ||
               material == Material.GOLD_ORE ||
               material == Material.DIAMOND_ORE ||
               material == Material.EMERALD_ORE ||
               material == Material.COPPER_ORE ||
               material == Material.REDSTONE_ORE ||
               material == Material.LAPIS_ORE;
    }
    
    private boolean isProtectedBlock(Material material) {
        return material == Material.STONE_BRICKS ||
               material == Material.STONE_BRICK_WALL ||
               material == Material.IRON_BARS ||
               material == Material.IRON_DOOR ||
               material == Material.QUARTZ_BLOCK ||
               material == Material.BEDROCK ||
               material == Material.BARRIER;
    }
}
