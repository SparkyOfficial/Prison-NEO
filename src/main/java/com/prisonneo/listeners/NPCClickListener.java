package com.prisonneo.listeners;

import com.prisonneo.PrisonNEO;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCClickListener implements Listener {
    
    private final PrisonNEO plugin;
    
    public NPCClickListener(PrisonNEO plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onNPCClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();
        
        plugin.getNPCManager().handleNPCClick(player, npc);
    }
}
