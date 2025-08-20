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

public class WardenManager implements Listener {
    
    private final PrisonNEO plugin;
    
    public WardenManager(PrisonNEO plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void openWardenMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§4§lНачальник Тюрьмы");
        
        // Appeal sentence
        ItemStack appeal = new ItemStack(Material.PAPER);
        ItemMeta appealMeta = appeal.getItemMeta();
        appealMeta.setDisplayName("§eПодать Апелляцию");
        appealMeta.setLore(Arrays.asList(
            "§7Попросить сокращение срока",
            "§7Стоимость: §a$500",
            "§7Шанс успеха: §e30%"
        ));
        appeal.setItemMeta(appealMeta);
        inv.setItem(11, appeal);
        
        // Request transfer
        ItemStack transfer = new ItemStack(Material.ENDER_PEARL);
        ItemMeta transferMeta = transfer.getItemMeta();
        transferMeta.setDisplayName("§bЗапрос Перевода");
        transferMeta.setLore(Arrays.asList(
            "§7Перевод в другой блок",
            "§7Стоимость: §a$200",
            "§7Только для ранга B+"
        ));
        transfer.setItemMeta(transferMeta);
        inv.setItem(13, transfer);
        
        // Complaint
        ItemStack complaint = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta complaintMeta = complaint.getItemMeta();
        complaintMeta.setDisplayName("§6Подать Жалобу");
        complaintMeta.setLore(Arrays.asList(
            "§7Жалоба на условия содержания",
            "§7Бесплатно",
            "§7Может улучшить репутацию"
        ));
        complaint.setItemMeta(complaintMeta);
        inv.setItem(15, complaint);
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.equals("§4§lНачальник Тюрьмы")) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        switch (clicked.getType()) {
            case PAPER:
                handleAppeal(player, prisonPlayer);
                break;
            case ENDER_PEARL:
                handleTransfer(player, prisonPlayer);
                break;
            case WRITABLE_BOOK:
                handleComplaint(player, prisonPlayer);
                break;
        }
        
        player.closeInventory();
    }
    
    private void handleAppeal(Player player, PrisonPlayer prisonPlayer) {
        if (!prisonPlayer.removeMoney(500)) {
            player.sendMessage("§cНедостаточно денег! Нужно $500");
            return;
        }
        
        if (Math.random() < 0.3) { // 30% success chance
            int reduction = 12 + (int)(Math.random() * 24); // 12-36 hours
            prisonPlayer.reduceSentence(reduction);
            player.sendMessage("§aАпелляция принята! Срок сокращён на " + reduction + " часов!");
        } else {
            player.sendMessage("§cАпелляция отклонена. Деньги потеряны.");
        }
    }
    
    private void handleTransfer(Player player, PrisonPlayer prisonPlayer) {
        if (!isHighRank(prisonPlayer.getRank())) {
            player.sendMessage("§cПеревод доступен только для ранга B и выше!");
            return;
        }
        
        if (!prisonPlayer.removeMoney(200)) {
            player.sendMessage("§cНедостаточно денег! Нужно $200");
            return;
        }
        
        // Release current cell and assign new one
        plugin.getCellManager().releaseCell(player.getUniqueId());
        String newCell = plugin.getCellManager().assignCell(player);
        
        if (newCell != null) {
            player.sendMessage("§aПеревод одобрен! Новая камера: " + newCell);
            plugin.getCellManager().teleportToCell(player);
        } else {
            prisonPlayer.addMoney(200); // Refund
            player.sendMessage("§cНет доступных камер для перевода!");
        }
    }
    
    private void handleComplaint(Player player, PrisonPlayer prisonPlayer) {
        player.sendMessage("§eВаша жалоба принята к рассмотрению.");
        player.sendMessage("§aВаша репутация немного улучшилась.");
        
        // Reduce suspicion if player has any
        if (plugin.getContrabandManager() != null) {
            plugin.getContrabandManager().reduceSuspicion(player, 2);
        }
    }
    
    private boolean isHighRank(String rank) {
        return rank.equals("B") || rank.equals("A") || rank.equals("S") || 
               rank.equals("TRUSTEE") || rank.equals("GUARD");
    }
}
