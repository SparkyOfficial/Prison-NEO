package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CellManager {
    
    private final PrisonNEO plugin;
    private final Map<String, CellData> cells;
    private final Map<UUID, String> playerCells;
    
    public CellManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.cells = new HashMap<>();
        this.playerCells = new HashMap<>();
        generateCells();
    }
    
    private void generateCells() {
        int cellId = 1;
        
        // Generate cells for each block
        cellId = generateCellsForBlock("A", -80, -40, -80, -40, cellId);
        cellId = generateCellsForBlock("B", 40, 80, -80, -40, cellId);
        cellId = generateCellsForBlock("C", -80, -40, 40, 80, cellId);
        cellId = generateCellsForBlock("D", 40, 80, 40, 80, cellId);
    }
    
    private int generateCellsForBlock(String blockName, int x1, int x2, int z1, int z2, int startId) {
        int cellId = startId;
        int cellSize = 6;
        
        for (int x = x1 + 2; x < x2 - cellSize; x += cellSize) {
            for (int z = z1 + 2; z < z2 - cellSize; z += cellSize) {
                String id = blockName + "-" + cellId;
                Location cellLocation = new Location(plugin.getWorldManager().getPrisonWorld(), 
                                                   x + cellSize/2, 63, z + cellSize/2);
                cells.put(id, new CellData(id, cellLocation, blockName));
                cellId++;
            }
        }
        
        return cellId;
    }
    
    public String assignCell(Player player) {
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        // Find available cell based on rank
        String blockPreference = getRankCellBlock(prisonPlayer.getRank());
        
        for (CellData cell : cells.values()) {
            if (!cell.isOccupied() && cell.getBlock().equals(blockPreference)) {
                cell.setOccupied(true);
                cell.setOccupant(player.getUniqueId());
                playerCells.put(player.getUniqueId(), cell.getId());
                prisonPlayer.setCellId(cell.getId());
                return cell.getId();
            }
        }
        
        // If no preferred block available, assign any available cell
        for (CellData cell : cells.values()) {
            if (!cell.isOccupied()) {
                cell.setOccupied(true);
                cell.setOccupant(player.getUniqueId());
                playerCells.put(player.getUniqueId(), cell.getId());
                prisonPlayer.setCellId(cell.getId());
                return cell.getId();
            }
        }
        
        return null; // No cells available
    }
    
    public void releaseCell(UUID playerUuid) {
        String cellId = playerCells.remove(playerUuid);
        if (cellId != null) {
            CellData cell = cells.get(cellId);
            if (cell != null) {
                cell.setOccupied(false);
                cell.setOccupant(null);
            }
        }
    }
    
    public void teleportToCell(Player player) {
        String cellId = playerCells.get(player.getUniqueId());
        if (cellId != null) {
            CellData cell = cells.get(cellId);
            if (cell != null) {
                player.teleport(cell.getLocation());
            }
        }
    }
    
    private String getRankCellBlock(String rank) {
        switch (rank) {
            case "S":
            case "TRUSTEE":
                return "A"; // Best block
            case "A":
                return "B";
            case "B":
                return "C";
            case "C":
            case "D":
            default:
                return "D"; // Worst block
        }
    }
    
    public CellData getCell(String cellId) {
        return cells.get(cellId);
    }
    
    public static class CellData {
        private final String id;
        private final Location location;
        private final String block;
        private boolean occupied;
        private UUID occupant;
        
        public CellData(String id, Location location, String block) {
            this.id = id;
            this.location = location;
            this.block = block;
            this.occupied = false;
        }
        
        // Getters and setters
        public String getId() { return id; }
        public Location getLocation() { return location; }
        public String getBlock() { return block; }
        public boolean isOccupied() { return occupied; }
        public UUID getOccupant() { return occupant; }
        
        public void setOccupied(boolean occupied) { this.occupied = occupied; }
        public void setOccupant(UUID occupant) { this.occupant = occupant; }
    }
}
