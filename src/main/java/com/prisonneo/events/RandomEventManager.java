package com.prisonneo.events;

import com.prisonneo.PrisonNEO;
import com.prisonneo.gangs.Gang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RandomEventManager {
    
    private final PrisonNEO plugin;
    private final Random random = new Random();
    private final List<PrisonEvent> activeEvents = new ArrayList<>();
    
    public RandomEventManager(PrisonNEO plugin) {
        this.plugin = plugin;
        startEventScheduler();
    }
    
    private void startEventScheduler() {
        // Запуск случайных событий каждые 3-7 минут
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (random.nextDouble() < 0.3) { // 30% шанс события
                triggerRandomEvent();
            }
        }, 3600L, 3600L); // 3 минуты
    }
    
    private void triggerRandomEvent() {
        EventType eventType = EventType.values()[random.nextInt(EventType.values().length)];
        
        switch (eventType) {
            case CONTRABAND_RAID:
                triggerContrabandRaid();
                break;
            case GANG_FIGHT:
                triggerGangFight();
                break;
            case BLACKOUT:
                triggerBlackout();
                break;
            case FOOD_POISONING:
                triggerFoodPoisoning();
                break;
            case VISITOR_DAY:
                triggerVisitorDay();
                break;
            case LOCKDOWN:
                triggerLockdown();
                break;
            case CELEBRITY_VISIT:
                triggerCelebrityVisit();
                break;
            case CONTRABAND_DROP:
                triggerContrabandDrop();
                break;
        }
    }
    
    private void triggerContrabandRaid() {
        broadcastEvent("🚨 ОБЫСК! Охранники проводят внеплановый обыск камер!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (random.nextDouble() < 0.2) { // 20% шанс найти контрабанду
                player.sendMessage("§c⚠ У вас нашли контрабанду!");
                plugin.getPrisonCurrency().removeCigarettes(player, 5);
                plugin.getPrisonCurrency().removeReputation(player, 10);
                
                // Возможность карцера
                if (random.nextDouble() < 0.3) {
                    player.sendMessage("§4Вас отправляют в карцер!");
                    // Телепорт в карцер
                }
            }
        }
        
        scheduleEventEnd(5 * 60 * 1000, "Обыск завершен. Заключенные возвращаются к обычному распорядку.");
    }
    
    private void triggerGangFight() {
        Collection<com.prisonneo.managers.GangManager.Gang> gangs = plugin.getGangManager().getAllGangs();
        if (gangs.size() < 2) return;
        
        List<com.prisonneo.managers.GangManager.Gang> gangList = new ArrayList<>(gangs);
        com.prisonneo.managers.GangManager.Gang gang1 = gangList.get(random.nextInt(gangList.size()));
        com.prisonneo.managers.GangManager.Gang gang2 = gangList.get(random.nextInt(gangList.size()));
        
        if (gang1.equals(gang2)) return;
        
        broadcastEvent("⚔ ДРАКА! Банды " + gang1.getName() + " и " + gang2.getName() + " устроили потасовку!");
        
        // Участники получают урон репутации
        for (UUID memberId : gang1.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage("§cВаша банда участвует в драке!");
                plugin.getPrisonCurrency().removeReputation(member, 5);
            }
        }
        
        for (UUID memberId : gang2.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage("§cВаша банда участвует в драке!");
                plugin.getPrisonCurrency().removeReputation(member, 5);
            }
        }
    }
    
    private void triggerBlackout() {
        broadcastEvent("💡 ОТКЛЮЧЕНИЕ СВЕТА! В тюрьме пропало электричество!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("§8Вокруг кромешная тьма...");
            // Эффект слепоты
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.BLINDNESS, 60 * 20, 1));
        }
        
        scheduleEventEnd(2 * 60 * 1000, "💡 Электричество восстановлено!");
    }
    
    private void triggerFoodPoisoning() {
        broadcastEvent("🤢 ПИЩЕВОЕ ОТРАВЛЕНИЕ! Плохая еда в столовой!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (random.nextDouble() < 0.4) { // 40% шанс отравления
                player.sendMessage("§2Вы чувствуете тошноту...");
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.POISON, 30 * 20, 0));
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.HUNGER, 60 * 20, 1));
            }
        }
    }
    
    private void triggerVisitorDay() {
        broadcastEvent("👥 ДЕНЬ ПОСЕЩЕНИЙ! Родственники приехали в тюрьму!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (random.nextDouble() < 0.3) { // 30% шанс получить посещение
                player.sendMessage("§aВас навестили родственники!");
                plugin.getPrisonCurrency().addCigarettes(player, 3);
                plugin.getPrisonCurrency().addReputation(player, 5);
                
                // Возможность получить подарок
                if (random.nextDouble() < 0.5) {
                    ItemStack gift = getRandomGift();
                    player.getInventory().addItem(gift);
                    player.sendMessage("§eВы получили подарок: " + gift.getType().name());
                }
            }
        }
    }
    
    private void triggerLockdown() {
        broadcastEvent("🔒 РЕЖИМ ИЗОЛЯЦИИ! Все заключенные должны оставаться в камерах!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("§cВозвращайтесь в свои камеры немедленно!");
            // Телепорт в камеру если есть
            if (plugin.getCellManager() != null) {
                plugin.getCellManager().teleportToCell(player);
            }
        }
        
        scheduleEventEnd(10 * 60 * 1000, "🔓 Режим изоляции отменен. Можете покидать камеры.");
    }
    
    private void triggerCelebrityVisit() {
        String[] celebrities = {"Владимир Путин", "Алексей Навальный", "Иван Дурак", "Баба Яга"};
        String celebrity = celebrities[random.nextInt(celebrities.length)];
        
        broadcastEvent("⭐ ОСОБЫЙ ГОСТЬ! " + celebrity + " посещает тюрьму!");
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getPrisonCurrency().addReputation(player, 10);
            plugin.getPrisonCurrency().addTokens(player, 1);
        }
    }
    
    private void triggerContrabandDrop() {
        broadcastEvent("📦 ТАЙНАЯ ПОСТАВКА! Где-то в тюрьме появилась контрабанда!");
        
        // Спрятать контрабанду в случайном месте
        Location dropLocation = getRandomLocation();
        ItemStack contraband = new ItemStack(Material.CHEST);
        contraband.getItemMeta().setDisplayName("§6Тайная Поставка");
        
        dropLocation.getWorld().dropItem(dropLocation, contraband);
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            broadcastEvent("📦 Тайная поставка исчезла...");
        }, 5 * 60 * 20L); // 5 минут
    }
    
    private ItemStack getRandomGift() {
        Material[] gifts = {
            Material.BREAD, Material.APPLE, Material.BOOK,
            Material.PAPER, Material.STICK, Material.STRING
        };
        return new ItemStack(gifts[random.nextInt(gifts.length)]);
    }
    
    private Location getRandomLocation() {
        if (plugin.getWorldManager().getPrisonWorld() == null) {
            return new Location(Bukkit.getWorlds().get(0), 0, 70, 0);
        }
        
        int x = random.nextInt(200) - 100;
        int z = random.nextInt(200) - 100;
        return new Location(plugin.getWorldManager().getPrisonWorld(), x, 62, z);
    }
    
    private void broadcastEvent(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("");
            player.sendMessage("§6§l=== СОБЫТИЕ ===");
            player.sendMessage("§e" + message);
            player.sendMessage("§6§l===============");
            player.sendMessage("");
        }
        
        plugin.getLogger().info("Prison Event: " + message);
    }
    
    private void scheduleEventEnd(long delayMs, String endMessage) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("§a" + endMessage);
            }
        }, delayMs / 50); // Convert ms to ticks
    }
    
    public enum EventType {
        CONTRABAND_RAID,
        GANG_FIGHT,
        BLACKOUT,
        FOOD_POISONING,
        VISITOR_DAY,
        LOCKDOWN,
        CELEBRITY_VISIT,
        CONTRABAND_DROP
    }
    
    public static class PrisonEvent {
        private final EventType type;
        private final long startTime;
        private final long duration;
        
        public PrisonEvent(EventType type, long duration) {
            this.type = type;
            this.startTime = System.currentTimeMillis();
            this.duration = duration;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > duration;
        }
        
        public EventType getType() { return type; }
        public long getStartTime() { return startTime; }
        public long getDuration() { return duration; }
    }
}
