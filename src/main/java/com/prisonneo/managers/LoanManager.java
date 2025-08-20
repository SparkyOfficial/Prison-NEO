package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LoanManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<UUID, Double> activeLoanAmounts;
    private final Map<UUID, Double> loanInterestRates;
    private final Map<UUID, Long> loanDueDates;
    
    public LoanManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.activeLoanAmounts = new HashMap<>();
        this.loanInterestRates = new HashMap<>();
        this.loanDueDates = new HashMap<>();
        
        // Start loan interest task
        startLoanInterestTask();
    }
    
    public void openLoanMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§6Кредитная Система");
        
        // Loan options
        createLoanOption(inv, 0, 100.0, 5.0, "§eМалый кредит");
        createLoanOption(inv, 1, 500.0, 7.5, "§6Средний кредит");
        createLoanOption(inv, 2, 1000.0, 10.0, "§cБольшой кредит");
        
        // Current loan info
        if (hasActiveLoan(player)) {
            ItemStack info = new ItemStack(Material.PAPER);
            ItemMeta meta = info.getItemMeta();
            meta.setDisplayName("§fТекущий кредит");
            double amount = activeLoanAmounts.get(player.getUniqueId());
            double rate = loanInterestRates.get(player.getUniqueId());
            meta.setLore(Arrays.asList(
                "§7Сумма: §c$" + String.format("%.1f", amount),
                "§7Процент: §e" + String.format("%.1f", rate) + "%",
                "§7Дней до возврата: §a" + getDaysLeft(player)
            ));
            info.setItemMeta(meta);
            inv.setItem(18, info);
            
            // Repay button
            ItemStack repay = new ItemStack(Material.EMERALD);
            ItemMeta repayMeta = repay.getItemMeta();
            repayMeta.setDisplayName("§aПогасить кредит");
            repay.setItemMeta(repayMeta);
            inv.setItem(26, repay);
        }
        
        player.openInventory(inv);
    }
    
    private void createLoanOption(Inventory inv, int slot, double amount, double rate, String name) {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(
            "§7Сумма: §a$" + String.format("%.0f", amount),
            "§7Процент: §e" + String.format("%.1f", rate) + "%",
            "§7Срок: §f7 дней",
            "§7К возврату: §c$" + String.format("%.0f", amount * (1 + rate/100))
        ));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }
    
    public boolean takeLoan(Player player, double amount, double interestRate) {
        if (hasActiveLoan(player)) {
            player.sendMessage("§cУ вас уже есть активный кредит!");
            return false;
        }
        
        UUID uuid = player.getUniqueId();
        activeLoanAmounts.put(uuid, amount * (1 + interestRate/100));
        loanInterestRates.put(uuid, interestRate);
        loanDueDates.put(uuid, System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)); // 7 days
        
        plugin.getEconomyManager().addMoney(player, amount);
        player.sendMessage("§aВы взяли кредит на $" + String.format("%.0f", amount));
        player.sendMessage("§eК возврату: $" + String.format("%.1f", amount * (1 + interestRate/100)));
        
        return true;
    }
    
    public boolean repayLoan(Player player) {
        if (!hasActiveLoan(player)) {
            player.sendMessage("§cУ вас нет активного кредита!");
            return false;
        }
        
        UUID uuid = player.getUniqueId();
        double amount = activeLoanAmounts.get(uuid);
        
        if (plugin.getEconomyManager().getMoney(player) < amount) {
            player.sendMessage("§cНедостаточно денег! Нужно: $" + String.format("%.1f", amount));
            return false;
        }
        
        plugin.getEconomyManager().removeMoney(player, amount);
        activeLoanAmounts.remove(uuid);
        loanInterestRates.remove(uuid);
        loanDueDates.remove(uuid);
        
        player.sendMessage("§aКредит погашен!");
        plugin.getReputationManager().addReputation(player, 10);
        
        return true;
    }
    
    public boolean hasActiveLoan(Player player) {
        return activeLoanAmounts.containsKey(player.getUniqueId());
    }
    
    public int getDaysLeft(Player player) {
        if (!hasActiveLoan(player)) return 0;
        
        long dueDate = loanDueDates.get(player.getUniqueId());
        long timeLeft = dueDate - System.currentTimeMillis();
        return Math.max(0, (int)(timeLeft / (24 * 60 * 60 * 1000L)));
    }
    
    private void startLoanInterestTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (UUID uuid : new HashSet<>(activeLoanAmounts.keySet())) {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) continue;
                
                // Check if loan is overdue
                if (System.currentTimeMillis() > loanDueDates.get(uuid)) {
                    handleOverdueLoan(player);
                }
            }
        }, 0L, 72000L); // Check every hour
    }
    
    private void handleOverdueLoan(Player player) {
        UUID uuid = player.getUniqueId();
        double amount = activeLoanAmounts.get(uuid);
        
        // Increase debt by 20%
        amount *= 1.2;
        activeLoanAmounts.put(uuid, amount);
        
        // Extend due date by 3 days
        loanDueDates.put(uuid, System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000L));
        
        // Reputation penalty
        plugin.getReputationManager().addReputation(player, -20);
        
        player.sendMessage("§4ПРОСРОЧКА КРЕДИТА!");
        player.sendMessage("§cДолг увеличен на 20%: $" + String.format("%.1f", amount));
        player.sendMessage("§eНовый срок: 3 дня");
    }
    
    @EventHandler
    public void onLoanMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§6Кредитная Система")) return;
        
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        
        if (event.getCurrentItem() == null) return;
        
        int slot = event.getSlot();
        
        switch (slot) {
            case 0: // Small loan
                takeLoan(player, 100.0, 5.0);
                player.closeInventory();
                break;
            case 1: // Medium loan
                takeLoan(player, 500.0, 7.5);
                player.closeInventory();
                break;
            case 2: // Large loan
                takeLoan(player, 1000.0, 10.0);
                player.closeInventory();
                break;
            case 26: // Repay
                repayLoan(player);
                player.closeInventory();
                break;
        }
    }
}
