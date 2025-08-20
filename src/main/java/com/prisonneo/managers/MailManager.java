package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MailManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<UUID, List<PrisonMail>> playerMail;
    private final Map<UUID, Boolean> mailNotifications;
    
    public MailManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.playerMail = new HashMap<>();
        this.mailNotifications = new HashMap<>();
        
        startRandomMailDelivery();
    }
    
    public void openMailbox(Player player) {
        List<PrisonMail> mail = playerMail.getOrDefault(player.getUniqueId(), new ArrayList<>());
        
        Inventory inv = Bukkit.createInventory(null, 54, "§eПочтовый Ящик (" + mail.size() + ")");
        
        // Display mail items
        for (int i = 0; i < Math.min(mail.size(), 45); i++) {
            PrisonMail letter = mail.get(i);
            ItemStack item = new ItemStack(letter.isRead() ? Material.PAPER : Material.MAP);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName((letter.isRead() ? "§7" : "§f") + letter.getSubject());
            meta.setLore(Arrays.asList(
                "§7От: §e" + letter.getSender(),
                "§7Дата: §f" + letter.getFormattedDate(),
                letter.isRead() ? "§7Прочитано" : "§aНовое письмо",
                "§7ЛКМ - Прочитать"
            ));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        
        // Mail actions
        ItemStack compose = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta composeMeta = compose.getItemMeta();
        composeMeta.setDisplayName("§aНаписать письмо");
        compose.setItemMeta(composeMeta);
        inv.setItem(49, compose);
        
        ItemStack delete = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName("§cУдалить все письма");
        delete.setItemMeta(deleteMeta);
        inv.setItem(53, delete);
        
        player.openInventory(inv);
        
        // Mark notifications as read
        mailNotifications.put(player.getUniqueId(), false);
    }
    
    public void sendMail(Player sender, String recipient, String subject, String content) {
        Player target = Bukkit.getPlayer(recipient);
        UUID targetUUID = target != null ? target.getUniqueId() : null;
        
        if (targetUUID == null) {
            sender.sendMessage("§cИгрок не найден!");
            return;
        }
        
        PrisonMail mail = new PrisonMail(sender.getName(), subject, content);
        
        List<PrisonMail> targetMail = playerMail.getOrDefault(targetUUID, new ArrayList<>());
        targetMail.add(mail);
        playerMail.put(targetUUID, targetMail);
        
        // Notify recipient
        if (target.isOnline()) {
            target.sendMessage("§e📧 Новое письмо от " + sender.getName());
            mailNotifications.put(targetUUID, true);
        }
        
        sender.sendMessage("§aПисьмо отправлено игроку " + recipient);
        
        // Cost for sending mail
        plugin.getEconomyManager().removeMoney(sender, 5.0);
    }
    
    public void deliverSystemMail(Player player, String subject, String content) {
        UUID uuid = player.getUniqueId();
        PrisonMail mail = new PrisonMail("Администрация", subject, content);
        
        List<PrisonMail> targetMail = playerMail.getOrDefault(uuid, new ArrayList<>());
        targetMail.add(mail);
        playerMail.put(uuid, targetMail);
        
        if (player.isOnline()) {
            player.sendMessage("§e📧 Официальное письмо от администрации");
            mailNotifications.put(uuid, true);
        }
    }
    
    private void startRandomMailDelivery() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Math.random() < 0.05) { // 5% chance per 30 minutes
                    deliverRandomMail(player);
                }
            }
        }, 0L, 36000L); // Every 30 minutes
    }
    
    private void deliverRandomMail(Player player) {
        String[] subjects = {
            "Письмо из дома", "Уведомление о посылке", "Судебная повестка",
            "Письмо от друга", "Банковское уведомление", "Медицинские результаты",
            "Письмо от адвоката", "Семейные новости", "Деловое предложение"
        };
        
        String[] contents = {
            "Дорогой сын, мы скучаем по тебе. Береги себя в тюрьме.",
            "Для вас поступила посылка. Обратитесь к охране.",
            "Вам назначено судебное заседание на следующей неделе.",
            "Привет! Как дела в тюрьме? Жду твоего возвращения.",
            "Ваш счёт заблокирован до освобождения.",
            "Результаты анализов в норме. Берегите здоровье.",
            "Я работаю над вашим делом. Есть хорошие новости.",
            "Бабушка передаёт привет. Она готовит твой любимый пирог.",
            "У меня есть работа для вас после освобождения."
        };
        
        String subject = subjects[(int)(Math.random() * subjects.length)];
        String content = contents[(int)(Math.random() * contents.length)];
        
        deliverSystemMail(player, subject, content);
    }
    
    @EventHandler
    public void onMailboxClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith("§eПочтовый Ящик")) return;
        
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        
        if (event.getCurrentItem() == null) return;
        
        int slot = event.getSlot();
        List<PrisonMail> mail = playerMail.getOrDefault(player.getUniqueId(), new ArrayList<>());
        
        if (slot < mail.size()) {
            // Read mail
            PrisonMail letter = mail.get(slot);
            letter.markAsRead();
            
            player.closeInventory();
            player.sendMessage("§6=== " + letter.getSubject() + " ===");
            player.sendMessage("§7От: " + letter.getSender());
            player.sendMessage("§7Дата: " + letter.getFormattedDate());
            player.sendMessage("§f" + letter.getContent());
            player.sendMessage("§6========================");
            
        } else if (slot == 49) {
            // Compose mail
            player.closeInventory();
            player.sendMessage("§eИспользуйте: §f/mail send <игрок> <тема> <сообщение>");
            
        } else if (slot == 53) {
            // Delete all mail
            playerMail.remove(player.getUniqueId());
            player.closeInventory();
            player.sendMessage("§cВся почта удалена!");
        }
    }
    
    public boolean hasNewMail(Player player) {
        return mailNotifications.getOrDefault(player.getUniqueId(), false);
    }
    
    public int getMailCount(Player player) {
        return playerMail.getOrDefault(player.getUniqueId(), new ArrayList<>()).size();
    }
    
    public int getUnreadMailCount(Player player) {
        List<PrisonMail> mail = playerMail.getOrDefault(player.getUniqueId(), new ArrayList<>());
        return (int) mail.stream().filter(m -> !m.isRead()).count();
    }
    
    // Inner class for mail data
    public static class PrisonMail {
        private final String sender;
        private final String subject;
        private final String content;
        private final long timestamp;
        private boolean read;
        
        public PrisonMail(String sender, String subject, String content) {
            this.sender = sender;
            this.subject = subject;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
            this.read = false;
        }
        
        public String getSender() { return sender; }
        public String getSubject() { return subject; }
        public String getContent() { return content; }
        public long getTimestamp() { return timestamp; }
        public boolean isRead() { return read; }
        public void markAsRead() { this.read = true; }
        
        public String getFormattedDate() {
            Date date = new Date(timestamp);
            return date.toString();
        }
    }
}
