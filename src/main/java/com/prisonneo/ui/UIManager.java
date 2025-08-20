package com.prisonneo.ui;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UIManager {
    
    private final PrisonNEO plugin;
    private final Map<UUID, BossBar> playerBossBars;
    private final Map<UUID, Scoreboard> playerScoreboards;
    
    public UIManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.playerBossBars = new HashMap<>();
        this.playerScoreboards = new HashMap<>();
        
        startUIUpdater();
    }
    
    public void createPlayerUI(Player player) {
        createBossBar(player);
        createScoreboard(player);
    }
    
    public void removePlayerUI(Player player) {
        UUID uuid = player.getUniqueId();
        
        // Remove boss bar
        BossBar bossBar = playerBossBars.remove(uuid);
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
        
        // Remove scoreboard
        playerScoreboards.remove(uuid);
    }
    
    private void createBossBar(Player player) {
        BossBar bossBar = Bukkit.createBossBar(
            "§6Тюрьма NEO §8| §fДобро пожаловать!",
            BarColor.YELLOW,
            BarStyle.SOLID
        );
        
        bossBar.addPlayer(player);
        bossBar.setProgress(1.0);
        playerBossBars.put(player.getUniqueId(), bossBar);
    }
    
    private void createScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        
        Objective objective = scoreboard.registerNewObjective("prison", "dummy", "§6§lТЮРЬМА NEO");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        updateScoreboard(player, scoreboard, objective);
        player.setScoreboard(scoreboard);
        playerScoreboards.put(player.getUniqueId(), scoreboard);
    }
    
    private void updateScoreboard(Player player, Scoreboard scoreboard, Objective objective) {
        // Clear existing scores
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        
        var prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        double money = plugin.getEconomyManager().getMoney(player);
        int reputation = plugin.getReputationManager().getReputation(player);
        
        objective.getScore("§f").setScore(15);
        objective.getScore("§6▶ Информация").setScore(14);
        objective.getScore("§7Ранг: §e" + prisonPlayer.getRank()).setScore(13);
        objective.getScore("§7Деньги: §a$" + String.format("%.0f", money)).setScore(12);
        objective.getScore("§7Репутация: §b" + reputation).setScore(11);
        objective.getScore("§f ").setScore(10);
        
        objective.getScore("§6▶ Срок заключения").setScore(9);
        int sentence = prisonPlayer.getSentenceTime();
        if (sentence > 0) {
            objective.getScore("§7Осталось: §c" + sentence + " мин").setScore(8);
        } else {
            objective.getScore("§7Статус: §aСвободен").setScore(8);
        }
        objective.getScore("§f  ").setScore(7);
        
        objective.getScore("§6▶ Активность").setScore(6);
        if (plugin.getEventManager().isLockdownActive()) {
            objective.getScore("§cБЛОКИРОВКА").setScore(5);
        } else if (plugin.getEventManager().isRiotActive()) {
            objective.getScore("§4БУНТ").setScore(5);
        } else {
            objective.getScore("§aОбычный режим").setScore(5);
        }
        objective.getScore("§f   ").setScore(4);
        
        objective.getScore("§6▶ Сервер").setScore(3);
        objective.getScore("§7Онлайн: §e" + Bukkit.getOnlinePlayers().size()).setScore(2);
        objective.getScore("§f    ").setScore(1);
        objective.getScore("§ewww.prisonneo.ru").setScore(0);
    }
    
    public void updateBossBar(Player player, String message, BarColor color, double progress) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar != null) {
            bossBar.setTitle(message);
            bossBar.setColor(color);
            bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        }
    }
    
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
    
    public void sendActionBar(Player player, String message) {
        player.sendActionBar(message);
    }
    
    // Update UI for specific events
    public void showSentenceUpdate(Player player, int newSentence) {
        sendTitle(player, "§c§lСРОК ИЗМЕНЁН", "§7Новый срок: §e" + newSentence + " минут", 10, 40, 10);
        updateBossBar(player, "§cСрок заключения: " + newSentence + " минут", BarColor.RED, (double) newSentence / 100.0);
    }
    
    public void showMoneyUpdate(Player player, double amount, boolean added) {
        String symbol = added ? "+" : "-";
        String color = added ? "§a" : "§c";
        sendActionBar(player, color + symbol + "$" + String.format("%.0f", Math.abs(amount)));
    }
    
    public void showRankPromotion(Player player, String newRank) {
        sendTitle(player, "§6§lПОВЫШЕНИЕ!", "§7Новый ранг: §e" + newRank, 20, 60, 20);
        updateBossBar(player, "§6Поздравляем с повышением до ранга " + newRank + "!", BarColor.YELLOW, 1.0);
    }
    
    public void showEventAlert(Player player, String eventName, String description) {
        sendTitle(player, "§4§l" + eventName, "§7" + description, 10, 50, 10);
        updateBossBar(player, "§4" + eventName + " §8| §7" + description, BarColor.RED, 1.0);
    }
    
    private void startUIUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Scoreboard scoreboard = playerScoreboards.get(player.getUniqueId());
                    if (scoreboard != null) {
                        Objective objective = scoreboard.getObjective("prison");
                        if (objective != null) {
                            updateScoreboard(player, scoreboard, objective);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Update every second
    }
}
