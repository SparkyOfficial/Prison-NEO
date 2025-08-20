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
import java.util.Random;

public class InformantManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Random random;
    
    public InformantManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.random = new Random();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void openInformantMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§8§lСтукач");
        
        // Buy information
        ItemStack info = new ItemStack(Material.MAP);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§eКупить Информацию");
        infoMeta.setLore(Arrays.asList(
            "§7Получить полезную информацию",
            "§7о других заключённых",
            "§7Стоимость: §a$150"
        ));
        info.setItemMeta(infoMeta);
        inv.setItem(11, info);
        
        // Sell information
        ItemStack sell = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta sellMeta = sell.getItemMeta();
        sellMeta.setDisplayName("§6Продать Информацию");
        sellMeta.setLore(Arrays.asList(
            "§7Сообщить о нарушениях",
            "§7других заключённых",
            "§7Награда: §a$200-500"
        ));
        sell.setItemMeta(sellMeta);
        inv.setItem(13, sell);
        
        // Gang intel
        ItemStack gangInfo = new ItemStack(Material.SPYGLASS);
        ItemMeta gangMeta = gangInfo.getItemMeta();
        gangMeta.setDisplayName("§5Информация о Бандах");
        gangMeta.setLore(Arrays.asList(
            "§7Узнать о планах банд",
            "§7и их территориях",
            "§7Стоимость: §a$300"
        ));
        gangInfo.setItemMeta(gangMeta);
        inv.setItem(15, gangInfo);
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.equals("§8§lСтукач")) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        switch (clicked.getType()) {
            case MAP:
                handleBuyInformation(player, prisonPlayer);
                break;
            case WRITABLE_BOOK:
                handleSellInformation(player, prisonPlayer);
                break;
            case SPYGLASS:
                handleGangIntel(player, prisonPlayer);
                break;
        }
        
        player.closeInventory();
    }
    
    private void handleBuyInformation(Player player, PrisonPlayer prisonPlayer) {
        if (!prisonPlayer.removeMoney(150)) {
            player.sendMessage("§cНедостаточно денег! Нужно $150");
            return;
        }
        
        String[] information = {
            "§eИнформация: В шахте D прячут контрабанду за алмазной рудой",
            "§eИнформация: Охранник Петров принимает взятки по вечерам",
            "§eИнформация: В библиотеке есть секретный проход",
            "§eИнформация: Банда 'Братва' планирует захват территории",
            "§eИнформация: Новая партия контрабанды прибудет завтра",
            "§eИнформация: В камере A-15 спрятаны деньги",
            "§eИнформация: Доктор продаёт стероиды втридорога"
        };
        
        String randomInfo = information[random.nextInt(information.length)];
        player.sendMessage("§8Стукач шепчет: " + randomInfo);
    }
    
    private void handleSellInformation(Player player, PrisonPlayer prisonPlayer) {
        // Check if player has seen contraband recently
        boolean hasInfo = random.nextBoolean();
        
        if (!hasInfo) {
            player.sendMessage("§cУ вас нет полезной информации для продажи!");
            return;
        }
        
        double reward = 200 + random.nextDouble() * 300; // $200-500
        prisonPlayer.addMoney(reward);
        
        player.sendMessage("§aВы продали информацию за $" + String.format("%.2f", reward));
        player.sendMessage("§8Стукач: Спасибо за сотрудничество...");
        
        // Reduce suspicion as reward for cooperation
        if (plugin.getContrabandManager() != null) {
            plugin.getContrabandManager().reduceSuspicion(player, 5);
        }
    }
    
    private void handleGangIntel(Player player, PrisonPlayer prisonPlayer) {
        if (!prisonPlayer.removeMoney(300)) {
            player.sendMessage("§cНедостаточно денег! Нужно $300");
            return;
        }
        
        String[] gangIntel = {
            "§5Банда 'Братва' контролирует блок A и планирует расширение",
            "§5'Воры в Законе' торгуют контрабандой в блоке B",
            "§5'Авторитеты' имеют связи с охранниками",
            "§5'Новички' ищут защиту у более сильных банд",
            "§5Планируется большая разборка между бандами завтра",
            "§5В блоке C прячут оружие для будущего бунта"
        };
        
        String randomIntel = gangIntel[random.nextInt(gangIntel.length)];
        player.sendMessage("§8Стукач шепчет: " + randomIntel);
    }
}
