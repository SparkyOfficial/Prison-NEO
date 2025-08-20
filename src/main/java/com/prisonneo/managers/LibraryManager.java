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
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LibraryManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<String, PrisonBook> availableBooks;
    private final Map<UUID, Set<String>> playerBooks;
    private final Map<UUID, Long> lastReadTime;
    
    public LibraryManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.availableBooks = new HashMap<>();
        this.playerBooks = new HashMap<>();
        this.lastReadTime = new HashMap<>();
        
        setupBooks();
    }
    
    private void setupBooks() {
        // Educational books
        availableBooks.put("law_basics", new PrisonBook("Основы права", 
            Arrays.asList("Глава 1: Ваши права", "Каждый заключённый имеет право на справедливое обращение...", 
                         "Глава 2: Апелляции", "Процедура подачи апелляции..."),
            BookType.EDUCATIONAL, 25.0));
        
        availableBooks.put("mining_guide", new PrisonBook("Руководство шахтёра",
            Arrays.asList("Техника безопасности", "Всегда проверяйте крепления...",
                         "Эффективная добыча", "Лучшие методы добычи руды..."),
            BookType.EDUCATIONAL, 30.0));
        
        // Entertainment books
        availableBooks.put("adventure_novel", new PrisonBook("Приключения на острове",
            Arrays.asList("Глава 1", "Корабль попал в шторм...",
                         "Глава 2", "На необитаемом острове..."),
            BookType.ENTERTAINMENT, 15.0));
        
        availableBooks.put("mystery_story", new PrisonBook("Тайна старого замка",
            Arrays.asList("Пролог", "Тёмной ночью детектив получил странный вызов...",
                         "Глава 1", "Замок хранил множество секретов..."),
            BookType.ENTERTAINMENT, 20.0));
        
        // Skill books
        availableBooks.put("lockpicking_manual", new PrisonBook("Искусство замков",
            Arrays.asList("Введение", "История замков и ключей...",
                         "Практика", "Основные техники..."),
            BookType.SKILL, 100.0));
        
        availableBooks.put("social_skills", new PrisonBook("Общение в тюрьме",
            Arrays.asList("Выживание", "Как найти союзников...",
                         "Конфликты", "Избегание неприятностей..."),
            BookType.SKILL, 75.0));
        
        // Contraband books
        availableBooks.put("escape_stories", new PrisonBook("Великие побеги",
            Arrays.asList("Алькатрас", "История знаменитого побега...",
                         "Современность", "Новые методы..."),
            BookType.CONTRABAND, 200.0));
    }
    
    public void openLibraryMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§9Тюремная библиотека");
        
        int slot = 0;
        for (Map.Entry<String, PrisonBook> entry : availableBooks.entrySet()) {
            String bookId = entry.getKey();
            PrisonBook book = entry.getValue();
            
            Set<String> ownedBooks = playerBooks.getOrDefault(player.getUniqueId(), new HashSet<>());
            boolean owned = ownedBooks.contains(bookId);
            
            ItemStack item = new ItemStack(owned ? Material.WRITTEN_BOOK : Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§f" + book.getTitle());
            
            List<String> lore = new ArrayList<>();
            lore.add("§7Тип: " + getTypeColor(book.getType()) + getTypeName(book.getType()));
            lore.add("§7Цена: §e$" + book.getPrice());
            
            if (owned) {
                lore.add("§a✓ У вас есть эта книга");
                lore.add("§eЛКМ - Читать");
            } else {
                lore.add("§c✗ Не куплена");
                lore.add("§eЛКМ - Купить");
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
            
            if (slot >= 45) break;
        }
        
        // Player's reading stats
        ItemStack stats = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta statsMeta = stats.getItemMeta();
        statsMeta.setDisplayName("§6Статистика чтения");
        statsMeta.setLore(Arrays.asList(
            "§7Книг в коллекции: §e" + playerBooks.getOrDefault(player.getUniqueId(), new HashSet<>()).size(),
            "§7Всего книг: §e" + availableBooks.size()
        ));
        stats.setItemMeta(statsMeta);
        inv.setItem(49, stats);
        
        player.openInventory(inv);
    }
    
    public boolean buyBook(Player player, String bookId) {
        PrisonBook book = availableBooks.get(bookId);
        if (book == null) return false;
        
        Set<String> ownedBooks = playerBooks.getOrDefault(player.getUniqueId(), new HashSet<>());
        if (ownedBooks.contains(bookId)) {
            player.sendMessage("§cУ вас уже есть эта книга!");
            return false;
        }
        
        if (!plugin.getEconomyManager().removeMoney(player, book.getPrice())) {
            player.sendMessage("§cНедостаточно денег! Нужно: $" + book.getPrice());
            return false;
        }
        
        ownedBooks.add(bookId);
        playerBooks.put(player.getUniqueId(), ownedBooks);
        
        player.sendMessage("§aВы купили книгу: " + book.getTitle());
        plugin.getReputationManager().addReputation(player, 2);
        
        return true;
    }
    
    public void readBook(Player player, String bookId) {
        PrisonBook book = availableBooks.get(bookId);
        if (book == null) return;
        
        Set<String> ownedBooks = playerBooks.getOrDefault(player.getUniqueId(), new HashSet<>());
        if (!ownedBooks.contains(bookId)) {
            player.sendMessage("§cУ вас нет этой книги!");
            return;
        }
        
        // Check reading cooldown
        UUID uuid = player.getUniqueId();
        if (lastReadTime.containsKey(uuid)) {
            long lastRead = lastReadTime.get(uuid);
            if (System.currentTimeMillis() - lastRead < 300000) { // 5 minutes
                player.sendMessage("§cВы недавно читали! Подождите немного.");
                return;
            }
        }
        
        lastReadTime.put(uuid, System.currentTimeMillis());
        
        // Create readable book
        ItemStack readableBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) readableBook.getItemMeta();
        bookMeta.setTitle(book.getTitle());
        bookMeta.setAuthor("Тюремная библиотека");
        bookMeta.setPages(book.getPages());
        readableBook.setItemMeta(bookMeta);
        
        // Give book to player temporarily
        player.getInventory().addItem(readableBook);
        player.sendMessage("§aВы начали читать: " + book.getTitle());
        
        // Apply reading benefits
        applyReadingBenefits(player, book.getType());
        
        // Remove book after reading time
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.getInventory().remove(readableBook);
            player.sendMessage("§7Вы закончили читать книгу.");
        }, 6000L); // 5 minutes reading time
    }
    
    private void applyReadingBenefits(Player player, BookType type) {
        switch (type) {
            case EDUCATIONAL:
                plugin.getReputationManager().addReputation(player, 5);
                player.sendMessage("§aЧтение образовательной литературы повысило вашу репутацию!");
                break;
            case ENTERTAINMENT:
                plugin.getReputationManager().addReputation(player, 2);
                player.sendMessage("§eРазвлекательное чтение подняло настроение!");
                break;
            case SKILL:
                plugin.getReputationManager().addReputation(player, 3);
                if (Math.random() < 0.3) { // 30% chance
                    plugin.getEconomyManager().addMoney(player, 10.0);
                    player.sendMessage("§aПолученные знания принесли вам $10!");
                }
                break;
            case CONTRABAND:
                plugin.getContrabandManager().addSuspicion(player, 5);
                plugin.getReputationManager().addReputation(player, -2);
                player.sendMessage("§cЧтение запрещённой литературы повысило подозрения...");
                break;
        }
    }
    
    private String getTypeColor(BookType type) {
        switch (type) {
            case EDUCATIONAL: return "§a";
            case ENTERTAINMENT: return "§e";
            case SKILL: return "§b";
            case CONTRABAND: return "§c";
            default: return "§7";
        }
    }
    
    private String getTypeName(BookType type) {
        switch (type) {
            case EDUCATIONAL: return "Образовательная";
            case ENTERTAINMENT: return "Развлекательная";
            case SKILL: return "Навыки";
            case CONTRABAND: return "Запрещённая";
            default: return "Неизвестно";
        }
    }
    
    @EventHandler
    public void onLibraryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§9Тюремная библиотека")) return;
        
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        
        if (event.getCurrentItem() == null) return;
        
        String bookTitle = event.getCurrentItem().getItemMeta().getDisplayName().substring(2); // Remove color code
        
        // Find book by title
        for (Map.Entry<String, PrisonBook> entry : availableBooks.entrySet()) {
            if (entry.getValue().getTitle().equals(bookTitle)) {
                String bookId = entry.getKey();
                Set<String> ownedBooks = playerBooks.getOrDefault(player.getUniqueId(), new HashSet<>());
                
                player.closeInventory();
                
                if (ownedBooks.contains(bookId)) {
                    readBook(player, bookId);
                } else {
                    buyBook(player, bookId);
                }
                break;
            }
        }
    }
    
    public int getBookCount(Player player) {
        return playerBooks.getOrDefault(player.getUniqueId(), new HashSet<>()).size();
    }
    
    // Book data classes
    private enum BookType {
        EDUCATIONAL, ENTERTAINMENT, SKILL, CONTRABAND
    }
    
    private static class PrisonBook {
        private final String title;
        private final List<String> pages;
        private final BookType type;
        private final double price;
        
        public PrisonBook(String title, List<String> pages, BookType type, double price) {
            this.title = title;
            this.pages = pages;
            this.type = type;
            this.price = price;
        }
        
        public String getTitle() { return title; }
        public List<String> getPages() { return pages; }
        public BookType getType() { return type; }
        public double getPrice() { return price; }
    }
}
