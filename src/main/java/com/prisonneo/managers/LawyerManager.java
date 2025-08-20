package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class LawyerManager implements Listener {
    
    private final PrisonNEO plugin;
    
    public LawyerManager(PrisonNEO plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void openLawyerMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§9§lАдвокат");
        
        // Reduce sentence
        ItemStack reduce = new ItemStack(Material.CLOCK);
        ItemMeta reduceMeta = reduce.getItemMeta();
        reduceMeta.setDisplayName("§aSократить Срок");
        reduceMeta.setLore(Arrays.asList(
            "§7Сокращение срока на 24 часа",
            "§7Стоимость: §a$1000",
            "§7Гарантированный результат"
        ));
        reduce.setItemMeta(reduceMeta);
        inv.setItem(11, reduce);
        
        // Legal advice
        ItemStack advice = new ItemStack(Material.BOOK);
        ItemMeta adviceMeta = advice.getItemMeta();
        adviceMeta.setDisplayName("§eЮридическая Консультация");
        adviceMeta.setLore(Arrays.asList(
            "§7Получить совет по тюремным правилам",
            "§7Стоимость: §a$100",
            "§7Снижает подозрение"
        ));
        advice.setItemMeta(adviceMeta);
        inv.setItem(13, advice);
        
        // Parole request
        ItemStack parole = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta paroleMeta = parole.getItemMeta();
        paroleMeta.setDisplayName("§6Условно-Досрочное Освобождение");
        paroleMeta.setLore(Arrays.asList(
            "§7Попытка получить УДО",
            "§7Стоимость: §a$5000",
            "§7Только для ранга A+",
            "§7Шанс успеха: §e20%"
        ));
        parole.setItemMeta(paroleMeta);
        inv.setItem(15, parole);
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.equals("§9§lАдвокат")) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        switch (clicked.getType()) {
            case CLOCK:
                handleSentenceReduction(player, prisonPlayer);
                break;
            case BOOK:
                handleLegalAdvice(player, prisonPlayer);
                break;
            case GOLDEN_APPLE:
                handleParole(player, prisonPlayer);
                break;
        }
        
        player.closeInventory();
    }
    
    private void handleSentenceReduction(Player player, PrisonPlayer prisonPlayer) {
        if (!prisonPlayer.removeMoney(1000)) {
            player.sendMessage("§cНедостаточно денег! Нужно $1000");
            return;
        }
        
        prisonPlayer.reduceSentence(24);
        player.sendMessage("§aСрок сокращён на 24 часа!");
        player.sendMessage("§eОставшийся срок: " + prisonPlayer.getSentence() + " часов");
    }
    
    private void handleLegalAdvice(Player player, PrisonPlayer prisonPlayer) {
        if (!prisonPlayer.removeMoney(100)) {
            player.sendMessage("§cНедостаточно денег! Нужно $100");
            return;
        }
        
        String[] advice = {
            "§eСовет: Работайте в шахтах для заработка денег",
            "§eСовет: Повышайте ранг для доступа к лучшим шахтам",
            "§eСовет: Избегайте контрабанды - это увеличивает срок",
            "§eСовет: Присоединитесь к банде для защиты",
            "§eСовет: Хорошее поведение снижает подозрение"
        };
        
        String randomAdvice = advice[(int)(Math.random() * advice.length)];
        player.sendMessage("§9Адвокат: " + randomAdvice);
        
        // Reduce suspicion
        if (plugin.getContrabandManager() != null) {
            plugin.getContrabandManager().reduceSuspicion(player, 3);
        }
    }
    
    private void handleParole(Player player, PrisonPlayer prisonPlayer) {
        if (!isHighRank(prisonPlayer.getRank())) {
            player.sendMessage("§cУДО доступно только для ранга A и выше!");
            return;
        }
        
        if (!prisonPlayer.removeMoney(5000)) {
            player.sendMessage("§cНедостаточно денег! Нужно $5000");
            return;
        }
        
        if (Math.random() < 0.2) { // 20% success chance
            prisonPlayer.setSentence(0);
            player.sendMessage("§a§lПОЗДРАВЛЯЕМ! УДО одобрено!");
            player.sendMessage("§eВы свободны! Можете покинуть тюрьму.");
            
            // Teleport to spawn world
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        } else {
            player.sendMessage("§cЗапрос на УДО отклонён. Попробуйте позже.");
        }
    }
    
    private boolean isHighRank(String rank) {
        return rank.equals("A") || rank.equals("S") || rank.equals("TRUSTEE") || rank.equals("GUARD");
    }
}
