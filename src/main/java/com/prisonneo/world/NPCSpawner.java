package com.prisonneo.world;

import com.prisonneo.PrisonNEO;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class NPCSpawner {
    
    private final PrisonNEO plugin;
    private final World world;
    
    public NPCSpawner(PrisonNEO plugin, World world) {
        this.plugin = plugin;
        this.world = world;
    }
    
    public void spawnAllNPCs() {
        // Delay NPC spawning to ensure world is fully loaded
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            spawnGuards();
            spawnJobNPCs();
            spawnShopNPCs();
            spawnSpecialNPCs();
            spawnRandomPrisoners();
            
            plugin.getLogger().info("All NPCs spawned successfully!");
        }, 200L); // 10 second delay
    }
    
    private void spawnGuards() {
        // Main entrance guards
        spawnNPC("Охранник Петров", 0, 62, -95, "guard", "§cСтой! Предъяви документы!");
        spawnNPC("Охранник Сергеев", 5, 62, -95, "guard", "§cПроходи только по пропуску!");
        
        // Yard patrol
        spawnNPC("Охранник Иванов", -20, 62, 0, "guard", "§eНе нарушай порядок в дворе!");
        spawnNPC("Охранник Сидоров", 20, 62, 0, "guard", "§eВремя прогулки ограничено!");
        spawnNPC("Охранник Козлов", 0, 62, 20, "guard", "§eСоблюдай дисциплину!");
        
        // Block supervisors
        spawnNPC("Надзиратель Волков", -60, 62, -60, "guard", "§6Тишина в блоке А!");
        spawnNPC("Надзиратель Медведев", 60, 62, -60, "guard", "§6Соблюдай режим!");
        spawnNPC("Надзиратель Лисицын", -60, 62, 60, "guard", "§6Порядок превыше всего!");
        spawnNPC("Надзиратель Орлов", 60, 62, 60, "guard", "§6Никаких нарушений!");
        
        // Mine guards
        spawnNPC("Горный Надзиратель", -130, 45, -130, "guard", "§aРаботай усердно!");
        spawnNPC("Шахтный Охранник", 130, 45, -130, "guard", "§aБез лени в шахте!");
        
        // Tower guards
        spawnNPC("Снайпер Алексеев", -190, 77, -190, "guard", "§cЯ вижу всё с башни!");
        spawnNPC("Снайпер Николаев", 190, 77, 190, "guard", "§cНи один побег не пройдёт!");
    }
    
    private void spawnJobNPCs() {
        // Kitchen staff
        spawnNPC("Повар Михалыч", -70, 63, 10, "kitchen", "§eХочешь работать на кухне? Плачу $5 за смену!");
        spawnNPC("Кухонный Помощник", -65, 63, 15, "kitchen", "§eПомогай на кухне, заработаешь денег!");
        
        // Laundry staff
        spawnNPC("Прачка Мария", -40, 63, -40, "laundry", "§bРабота в прачечной - $4 за смену!");
        spawnNPC("Работник Прачечной", -35, 63, -35, "laundry", "§bСтирка белья - честная работа!");
        
        // Library staff
        spawnNPC("Библиотекарь Анна", 70, 63, 10, "library", "§dПомощь в библиотеке - $3 за смену!");
        
        // Janitor
        spawnNPC("Уборщик Петя", 30, 63, -30, "janitor", "§7Уборка территории - $3 за смену!");
        spawnNPC("Санитар Коля", -30, 63, 30, "janitor", "§7Поддержание чистоты - важная работа!");
        
        // Maintenance
        spawnNPC("Слесарь Виктор", 50, 63, -50, "job_maintenance", "§9Ремонт и техобслуживание - $4 за смену!");
    }
    
    private void spawnShopNPCs() {
        // General store
        spawnNPC("Торговец Семён", 50, 63, -10, "general_store", "§aДобро пожаловать в магазин! Что желаете?");
        
        // Food vendor
        spawnNPC("Продавец Еды", -50, 63, -10, "food_store", "§6Свежая еда каждый день!");
        
        // Tool vendor
        spawnNPC("Торговец Инструментами", 30, 63, 50, "tool_store", "§9Качественные инструменты для работы!");
        
        // Black market (contraband)
        spawnNPC("Тёмный Торговец", 80, 55, 80, "black_market", "§8Псст... у меня есть особые товары...");
    }
    
    private void spawnSpecialNPCs() {
        // Administration
        spawnNPC("Начальник Тюрьмы", 75, 63, -15, "warden", "§4Я здесь главный! Что тебе нужно?");
        spawnNPC("Заместитель Начальника", 70, 63, -20, "deputy_warden", "§4Все вопросы через меня!");
        
        // Services
        spawnNPC("Адвокат Петрова", 65, 63, -25, "lawyer", "§1Нужна юридическая помощь? Обращайся!");
        spawnNPC("Доктор Смирнов", 85, 63, -25, "doctor", "§2Медицинская помощь и лечение!");
        spawnNPC("Психолог Анна", 80, 63, -20, "psychologist", "§3Психологическая поддержка и консультации!");
        
        // Informants
        spawnNPC("Стукач Вася", 10, 63, 40, "informant", "§8Псст... у меня есть информация...");
        spawnNPC("Информатор Гена", -15, 63, 35, "informant", "§8Знаю все секреты тюрьмы...");
        
        // Visitors
        spawnNPC("Священник Отец Иоанн", 40, 63, 40, "priest", "§fБлагословение и духовная поддержка!");
        spawnNPC("Социальный Работник", 45, 63, 35, "social_worker", "§5Помогу с адаптацией в тюрьме!");
    }
    
    private void spawnRandomPrisoners() {
        // Spawn random prisoner NPCs around the prison
        String[] prisonerNames = {
            "Заключённый Андрей", "Заключённый Борис", "Заключённый Владимир",
            "Заключённый Григорий", "Заключённый Дмитрий", "Заключённый Евгений",
            "Заключённый Игорь", "Заключённый Константин", "Заключённый Леонид",
            "Заключённый Максим", "Заключённый Николай", "Заключённый Олег"
        };
        
        for (int i = 0; i < 20; i++) {
            String name = prisonerNames[i % prisonerNames.length] + " " + (i + 1);
            
            // Random locations around prison
            int x = -80 + (int)(Math.random() * 160);
            int z = -80 + (int)(Math.random() * 160);
            
            spawnNPC(name, x, 62, z, "prisoner", "§7Привет, как дела?");
        }
    }
    
    private void spawnNPC(String name, double x, double y, double z, String type, String greeting) {
        Location location = new Location(world, x, y, z);
        
        // Schedule NPC creation for next tick to avoid world loading issues
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                plugin.getNPCManager().createGuard(name, location, greeting, type + "_" + System.currentTimeMillis());
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to spawn NPC " + name + ": " + e.getMessage());
            }
        });
    }
}
