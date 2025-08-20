package com.prisonneo.data;

import java.util.UUID;

public class PrisonPlayer {
    
    private final UUID uuid;
    private String name;
    private String rank;
    private double money;
    private int sentence; // in hours
    private String cellId;
    private long playTime; // in milliseconds
    private long joinTime;
    
    public PrisonPlayer(UUID uuid) {
        this.uuid = uuid;
        this.rank = "D";
        this.money = 0.0;
        this.sentence = 72;
        this.playTime = 0;
        this.joinTime = System.currentTimeMillis();
    }
    
    // Getters
    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getRank() { return rank; }
    public double getMoney() { return money; }
    public int getSentence() { return sentence; }
    public String getCellId() { return cellId; }
    public long getPlayTime() { return playTime; }
    public long getJoinTime() { return joinTime; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setRank(String rank) { this.rank = rank; }
    public void setMoney(double money) { this.money = money; }
    public void setSentence(int sentence) { this.sentence = sentence; }
    public void setCellId(String cellId) { this.cellId = cellId; }
    public void setPlayTime(long playTime) { this.playTime = playTime; }
    
    // Utility methods
    public void addMoney(double amount) {
        this.money += amount;
    }
    
    public boolean removeMoney(double amount) {
        if (money >= amount) {
            money -= amount;
            return true;
        }
        return false;
    }
    
    public void reduceSentence(int hours) {
        sentence = Math.max(0, sentence - hours);
    }
    
    public boolean isFreed() {
        return sentence <= 0;
    }
    
    public String getFormattedPlayTime() {
        long hours = playTime / (1000 * 60 * 60);
        long minutes = (playTime % (1000 * 60 * 60)) / (1000 * 60);
        return String.format("%dh %dm", hours, minutes);
    }
}
