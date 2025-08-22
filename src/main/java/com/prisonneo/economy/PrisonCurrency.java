package com.prisonneo.economy;

import com.prisonneo.PrisonNEO;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrisonCurrency {
    
    private final PrisonNEO plugin;
    private final Map<UUID, Integer> cigarettes; // Сигареты - основная валюта
    private final Map<UUID, Integer> tokens; // Жетоны - премиум валюта
    private final Map<UUID, Integer> reputation; // Репутация - социальная валюта
    
    public PrisonCurrency(PrisonNEO plugin) {
        this.plugin = plugin;
        this.cigarettes = new HashMap<>();
        this.tokens = new HashMap<>();
        this.reputation = new HashMap<>();
    }
    
    // Cigarettes (основная валюта)
    public int getCigarettes(Player player) {
        return cigarettes.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void addCigarettes(Player player, int amount) {
        int current = getCigarettes(player);
        cigarettes.put(player.getUniqueId(), current + amount);
        
        if (amount > 0) {
            player.sendMessage("§6+" + amount + " сигарет §7(Всего: " + (current + amount) + ")");
        }
    }
    
    public boolean removeCigarettes(Player player, int amount) {
        int current = getCigarettes(player);
        if (current >= amount) {
            cigarettes.put(player.getUniqueId(), current - amount);
            player.sendMessage("§c-" + amount + " сигарет §7(Осталось: " + (current - amount) + ")");
            return true;
        }
        return false;
    }
    
    public boolean hasCigarettes(Player player, int amount) {
        return getCigarettes(player) >= amount;
    }
    
    // Tokens (премиум валюта)
    public int getTokens(Player player) {
        return tokens.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void addTokens(Player player, int amount) {
        int current = getTokens(player);
        tokens.put(player.getUniqueId(), current + amount);
        
        if (amount > 0) {
            player.sendMessage("§e+" + amount + " жетонов §7(Всего: " + (current + amount) + ")");
        }
    }
    
    public boolean removeTokens(Player player, int amount) {
        int current = getTokens(player);
        if (current >= amount) {
            tokens.put(player.getUniqueId(), current - amount);
            player.sendMessage("§c-" + amount + " жетонов §7(Осталось: " + (current - amount) + ")");
            return true;
        }
        return false;
    }
    
    public boolean hasTokens(Player player, int amount) {
        return getTokens(player) >= amount;
    }
    
    // Reputation (социальная валюта)
    public int getReputation(Player player) {
        return reputation.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void addReputation(Player player, int amount) {
        int current = getReputation(player);
        reputation.put(player.getUniqueId(), current + amount);
        
        if (amount > 0) {
            player.sendMessage("§a+" + amount + " репутации §7(Всего: " + (current + amount) + ")");
        }
    }
    
    public boolean removeReputation(Player player, int amount) {
        int current = getReputation(player);
        if (current >= amount) {
            reputation.put(player.getUniqueId(), current - amount);
            player.sendMessage("§c-" + amount + " репутации §7(Осталось: " + (current - amount) + ")");
            return true;
        }
        return false;
    }
    
    public boolean hasReputation(Player player, int amount) {
        return getReputation(player) >= amount;
    }
    
    // Transfer methods
    public boolean transferCigarettes(Player from, Player to, int amount) {
        if (hasCigarettes(from, amount)) {
            removeCigarettes(from, amount);
            addCigarettes(to, amount);
            
            from.sendMessage("§eВы передали " + amount + " сигарет игроку " + to.getName());
            to.sendMessage("§eВы получили " + amount + " сигарет от игрока " + from.getName());
            return true;
        }
        return false;
    }
    
    public boolean transferTokens(Player from, Player to, int amount) {
        if (hasTokens(from, amount)) {
            removeTokens(from, amount);
            addTokens(to, amount);
            
            from.sendMessage("§eВы передали " + amount + " жетонов игроку " + to.getName());
            to.sendMessage("§eВы получили " + amount + " жетонов от игрока " + from.getName());
            return true;
        }
        return false;
    }
    
    // Currency display
    public String getBalanceString(Player player) {
        return String.format("§6Сигареты: §f%d §7| §eЖетоны: §f%d §7| §aРепутация: §f%d",
            getCigarettes(player), getTokens(player), getReputation(player));
    }
    
    // Economy events
    public void giveStarterPack(Player player) {
        addCigarettes(player, 10);
        addTokens(player, 1);
        addReputation(player, 5);
        player.sendMessage("§aВы получили стартовый пакет!");
    }
    
    public void giveWorkReward(Player player, String jobType) {
        switch (jobType.toLowerCase()) {
            case "mine":
                addCigarettes(player, 3);
                addReputation(player, 1);
                break;
            case "kitchen":
                addCigarettes(player, 2);
                addReputation(player, 2);
                break;
            case "library":
                addCigarettes(player, 1);
                addReputation(player, 3);
                break;
            case "workshop":
                addCigarettes(player, 4);
                addReputation(player, 1);
                break;
            default:
                addCigarettes(player, 1);
                break;
        }
    }
    
    public void giveDailyBonus(Player player) {
        addCigarettes(player, 5);
        addTokens(player, 1);
        player.sendMessage("§aВы получили ежедневный бонус!");
    }
    
    public void saveData() {
        // TODO: Implement data saving to files/database
        plugin.getLogger().info("Saving currency data for " + cigarettes.size() + " players");
    }
}
