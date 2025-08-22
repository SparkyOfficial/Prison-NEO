package com.prisonneo.gangs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Gang {
    
    private final String name;
    private final String tag;
    private final ChatColor color;
    private final UUID leader;
    private final Set<UUID> members;
    private final Set<Location> territory;
    private final Map<String, Integer> relationships; // Gang name -> relationship level (-100 to 100)
    private int influence;
    private int respect;
    private final long createdTime;
    
    public Gang(String name, String tag, ChatColor color, UUID leader) {
        this.name = name;
        this.tag = tag;
        this.color = color;
        this.leader = leader;
        this.members = new HashSet<>();
        this.territory = new HashSet<>();
        this.relationships = new HashMap<>();
        this.influence = 0;
        this.respect = 0;
        this.createdTime = System.currentTimeMillis();
        
        // Leader is automatically a member
        this.members.add(leader);
    }
    
    public String getName() {
        return name;
    }
    
    public String getTag() {
        return tag;
    }
    
    public ChatColor getColor() {
        return color;
    }
    
    public String getDisplayName() {
        return color + "[" + tag + "] " + name + ChatColor.RESET;
    }
    
    public UUID getLeader() {
        return leader;
    }
    
    public Set<UUID> getMembers() {
        return new HashSet<>(members);
    }
    
    public boolean isMember(UUID playerId) {
        return members.contains(playerId);
    }
    
    public boolean isLeader(UUID playerId) {
        return leader.equals(playerId);
    }
    
    public void addMember(UUID playerId) {
        members.add(playerId);
    }
    
    public void removeMember(UUID playerId) {
        if (!isLeader(playerId)) {
            members.remove(playerId);
        }
    }
    
    public int getMemberCount() {
        return members.size();
    }
    
    public Set<Location> getTerritory() {
        return new HashSet<>(territory);
    }
    
    public void addTerritory(Location location) {
        territory.add(location);
    }
    
    public void removeTerritory(Location location) {
        territory.remove(location);
    }
    
    public boolean controlsTerritory(Location location) {
        return territory.stream().anyMatch(loc -> 
            loc.getWorld().equals(location.getWorld()) &&
            loc.distance(location) <= 10); // 10 block radius
    }
    
    public int getInfluence() {
        return influence;
    }
    
    public void addInfluence(int amount) {
        this.influence = Math.max(0, this.influence + amount);
    }
    
    public int getRespect() {
        return respect;
    }
    
    public void addRespect(int amount) {
        this.respect = Math.max(0, this.respect + amount);
    }
    
    public Map<String, Integer> getRelationships() {
        return new HashMap<>(relationships);
    }
    
    public int getRelationshipWith(String gangName) {
        return relationships.getOrDefault(gangName, 0);
    }
    
    public void setRelationship(String gangName, int level) {
        relationships.put(gangName, Math.max(-100, Math.min(100, level)));
    }
    
    public void changeRelationship(String gangName, int change) {
        int current = getRelationshipWith(gangName);
        setRelationship(gangName, current + change);
    }
    
    public boolean isAllied(String gangName) {
        return getRelationshipWith(gangName) >= 50;
    }
    
    public boolean isEnemy(String gangName) {
        return getRelationshipWith(gangName) <= -50;
    }
    
    public boolean isNeutral(String gangName) {
        int relationship = getRelationshipWith(gangName);
        return relationship > -50 && relationship < 50;
    }
    
    public long getCreatedTime() {
        return createdTime;
    }
    
    public int getPowerLevel() {
        return (influence + respect + (members.size() * 10) + territory.size() * 5);
    }
    
    public String getRelationshipStatus(String gangName) {
        int level = getRelationshipWith(gangName);
        if (level >= 75) return ChatColor.DARK_GREEN + "Союзники";
        if (level >= 50) return ChatColor.GREEN + "Дружественные";
        if (level >= 25) return ChatColor.YELLOW + "Нейтральные+";
        if (level >= -25) return ChatColor.GRAY + "Нейтральные";
        if (level >= -50) return ChatColor.GOLD + "Напряженные";
        if (level >= -75) return ChatColor.RED + "Враждебные";
        return ChatColor.DARK_RED + "Кровные враги";
    }
}
