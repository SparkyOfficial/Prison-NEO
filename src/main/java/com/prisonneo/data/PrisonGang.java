package com.prisonneo.data;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PrisonGang {
    
    private final String id;
    private final String name;
    private final String displayName;
    private final String prefix;
    private final Location headquarters;
    private final Set<UUID> members;
    private UUID leader;
    private int x1, x2, z1, z2; // Territory bounds
    private double gangMoney;
    private int influence;
    
    public PrisonGang(String id, String name, String prefix, Location headquarters) {
        this.id = id;
        this.name = name;
        this.displayName = name;
        this.prefix = prefix;
        this.headquarters = headquarters;
        this.members = new HashSet<>();
        this.gangMoney = 0.0;
        this.influence = 0;
    }
    
    public void setTerritory(int x1, int x2, int z1, int z2) {
        this.x1 = x1; this.x2 = x2; this.z1 = z1; this.z2 = z2;
    }
    
    public boolean isInTerritory(Location location) {
        return location.getX() >= x1 && location.getX() <= x2 && 
               location.getZ() >= z1 && location.getZ() <= z2;
    }
    
    public String getTerritoryInfo() {
        return String.format("(%d,%d) to (%d,%d)", x1, z1, x2, z2);
    }
    
    public void addMember(UUID uuid) { 
        members.add(uuid);
        influence += 10;
    }
    
    public void removeMember(UUID uuid) { 
        members.remove(uuid);
        influence = Math.max(0, influence - 10);
    }
    
    public void addMoney(double amount) {
        gangMoney += amount;
    }
    
    public boolean removeMoney(double amount) {
        if (gangMoney >= amount) {
            gangMoney -= amount;
            return true;
        }
        return false;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public String getPrefix() { return prefix; }
    public Location getHeadquarters() { return headquarters; }
    public Set<UUID> getMembers() { return members; }
    public UUID getLeader() { return leader; }
    public void setLeader(UUID leader) { this.leader = leader; }
    public double getGangMoney() { return gangMoney; }
    public int getInfluence() { return influence; }
    public void setInfluence(int influence) { this.influence = influence; }
}
