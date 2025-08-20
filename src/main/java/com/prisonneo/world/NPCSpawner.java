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
        spawnNPC("Охранник Петров", 0, 62, -95, "guard");
        spawnNPC("Охранник Сергеев", 5, 62, -95, "guard");
        
        // Yard patrol
        spawnNPC("Охранник Иванов", -20, 62, 0, "guard");
        spawnNPC("Охранник Сидоров", 20, 62, 0, "guard");
        spawnNPC("Охранник Козлов", 0, 62, 20, "guard");
        
        // Block supervisors
        spawnNPC("Надзиратель Волков", -60, 62, -60, "guard");
        spawnNPC("Надзиратель Медведев", 60, 62, -60, "guard");
        spawnNPC("Надзиратель Лисицын", -60, 62, 60, "guard");
        spawnNPC("Надзиратель Орлов", 60, 62, 60, "guard");
        
        // Mine guards
        spawnNPC("Горный Надзиратель", -130, 45, -130, "guard");
        spawnNPC("Шахтный Охранник", 130, 45, -130, "guard");
        
        // Tower guards
        spawnNPC("Снайпер Алексеев", -190, 77, -190, "guard");
        spawnNPC("Снайпер Николаев", 190, 77, 190, "guard");
    }
    
    private void spawnJobNPCs() {
        // Kitchen
        spawnNPC("Повар Михалыч", -70, 63, 10, "job_kitchen");
        spawnNPC("Помощник Повара", -65, 63, 15, "job_kitchen");
        
        // Laundry
        spawnNPC("Прачка Марья", 70, 63, 10, "job_laundry");
        
        // Library
        spawnNPC("Библиотекарь Олег", 0, 63, 70, "job_library");
        
        // Janitor
        spawnNPC("Завхоз Николай", -50, 63, 50, "job_janitor");
        
        // Maintenance
        spawnNPC("Слесарь Виктор", 50, 63, -50, "job_maintenance");
    }
    
    private void spawnShopNPCs() {
        // Main shops
        spawnNPC("Торговец Василий", 30, 63, -70, "shop_general");
        spawnNPC("Кузнец Андрей", -30, 63, -70, "shop_tools");
        spawnNPC("Буфетчица Света", 0, 63, -50, "shop_food");
        
        // Black market
        spawnNPC("Барыга Толик", -90, 63, 90, "shop_contraband");
    }
    
    private void spawnSpecialNPCs() {
        // Administration
        spawnNPC("Начальник Тюрьмы", 75, 63, -15, "warden");
        spawnNPC("Заместитель Начальника", 70, 63, -20, "deputy_warden");
        
        // Services
        spawnNPC("Адвокат Петрова", 65, 63, -25, "lawyer");
        spawnNPC("Доктор Смирнов", 85, 63, -25, "doctor");
        spawnNPC("Психолог Анна", 80, 63, -20, "psychologist");
        
        // Informants
        spawnNPC("Стукач Вася", 10, 63, 40, "informant");
        spawnNPC("Информатор Гена", -15, 63, 35, "informant");
        
        // Visitors
        spawnNPC("Священник Отец Иоанн", 40, 63, 40, "priest");
        spawnNPC("Социальный Работник", 45, 63, 35, "social_worker");
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
            
            spawnNPC(name, x, 62, z, "prisoner");
        }
    }
    
    private void spawnNPC(String name, double x, double y, double z, String type, String greeting) {
        Location location = new Location(world, x, y, z);
        
        // Schedule NPC creation for next tick to avoid world loading issues
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                plugin.getNPCManager().createGuardNPC(name, location, greeting);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to spawn NPC " + name + ": " + e.getMessage());
            }
        });
    }
}
