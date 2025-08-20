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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class DoctorManager implements Listener {
    
    private final PrisonNEO plugin;
    
    public DoctorManager(PrisonNEO plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void openDoctorMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§2§lМедпункт");
        
        // Heal
        ItemStack heal = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta healMeta = heal.getItemMeta();
        healMeta.setDisplayName("§aПолное Лечение");
        healMeta.setLore(Arrays.asList(
            "§7Восстановить здоровье и голод",
            "§7Стоимость: §a$50"
        ));
        heal.setItemMeta(healMeta);
        inv.setItem(11, heal);
        
        // Speed boost
        ItemStack speed = new ItemStack(Material.SUGAR);
        ItemMeta speedMeta = speed.getItemMeta();
        speedMeta.setDisplayName("§eВитамины");
        speedMeta.setLore(Arrays.asList(
            "§7Скорость +1 на 10 минут",
            "§7Стоимость: §a$75"
        ));
        speed.setItemMeta(speedMeta);
        inv.setItem(13, speed);
        
        // Strength boost
        ItemStack strength = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta strengthMeta = strength.getItemMeta();
        strengthMeta.setDisplayName("§cСтероиды");
        strengthMeta.setLore(Arrays.asList(
            "§7Сила +1 на 10 минут",
            "§7Стоимость: §a$100",
            "§c§lНЕЗАКОННО!",
            "§7+5 подозрение"
        ));
        strength.setItemMeta(strengthMeta);
        inv.setItem(15, strength);
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.equals("§2§lМедпункт")) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        switch (clicked.getType()) {
            case GOLDEN_APPLE:
                handleHeal(player, prisonPlayer);
                break;
            case SUGAR:
                handleSpeedBoost(player, prisonPlayer);
                break;
            case BLAZE_POWDER:
                handleStrengthBoost(player, prisonPlayer);
                break;
        }
        
        player.closeInventory();
    }
    
    private void handleHeal(Player player, PrisonPlayer prisonPlayer) {
        if (!prisonPlayer.removeMoney(50)) {
            player.sendMessage("§cНедостаточно денег! Нужно $50");
            return;
        }
        
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        player.sendMessage("§aВы полностью вылечены!");
    }
    
    private void handleSpeedBoost(Player player, PrisonPlayer prisonPlayer) {
        if (!prisonPlayer.removeMoney(75)) {
            player.sendMessage("§cНедостаточно денег! Нужно $75");
            return;
        }
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 12000, 0)); // 10 minutes
        player.sendMessage("§eВы получили витамины! Скорость увеличена на 10 минут.");
    }
    
    private void handleStrengthBoost(Player player, PrisonPlayer prisonPlayer) {
        if (!prisonPlayer.removeMoney(100)) {
            player.sendMessage("§cНедостаточно денег! Нужно $100");
            return;
        }
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 12000, 0)); // 10 minutes
        player.sendMessage("§cВы получили стероиды! Сила увеличена на 10 минут.");
        player.sendMessage("§4§lВНИМАНИЕ: Это незаконно! Подозрение увеличено!");
        
        // Increase suspicion
        if (plugin.getContrabandManager() != null) {
            // Add suspicion logic here when ContrabandManager is available
        }
    }
}
