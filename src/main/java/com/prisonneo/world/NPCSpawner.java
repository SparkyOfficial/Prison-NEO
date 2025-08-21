package com.prisonneo.world;

import com.prisonneo.PrisonNEO;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCSpawner {
    
    private final PrisonNEO plugin;
    private final World world;
    private NPCRegistry registry;
    private static final String PROTECTED_METADATA = "protected";
    
    public NPCSpawner(PrisonNEO plugin, World world) {
        this.plugin = plugin;
        this.world = world;
        
        // Initialize Citizens registry
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            this.registry = CitizensAPI.getNPCRegistry();
            if (this.registry == null) {
                plugin.getLogger().severe("Failed to get Citizens NPC Registry! NPCs will not spawn.");
            }
        } else {
            plugin.getLogger().severe("Citizens plugin not found! NPCs will not spawn.");
        }
    }
    
    public void spawnAllNPCs() {
        // Delay NPC spawning to ensure world is fully loaded
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            spawnGuards();
            spawnJobNPCs();
            spawnShopNPCs();
            spawnSpecialNPCs();
            spawnRandomPrisoners();
            spawnSolitaryGuards(); // Add solitary guards

            plugin.getLogger().info("All NPCs spawned successfully!");
        }, 200L); // 10 second delay
    }
    
    private void spawnGuards() {
        // Main entrance guards
        spawnNPC("Охранник Петров", 0, 62, -195, "guard", "§cСтой! Предъяви документы!");
        spawnNPC("Охранник Сергеев", 5, 62, -195, "guard", "§cПроходи только по пропуску!");

        // Yard patrol
        spawnNPC("Охранник Иванов", -20, 63, 0, "guard", "§eНе нарушай порядок во дворе!");
        spawnNPC("Охранник Сидоров", 20, 63, 0, "guard", "§eВремя прогулки ограничено!");
        spawnNPC("Охранник Козлов", 0, 63, 20, "guard", "§eСоблюдай дисциплину!");

        // Block supervisors
        spawnNPC("Надзиратель Волков", -80, 63, -80, "guard", "§6Тишина в блоке А!");
        spawnNPC("Надзиратель Медведев", 80, 63, -80, "guard", "§6Соблюдай режим!");

        // Tower guards
        spawnNPC("Снайпер Алексеев", -190, 85, -190, "guard", "§cЯ вижу всё с башни!");
        spawnNPC("Снайпер Николаев", 190, 85, 190, "guard", "§cНи один побег не пройдёт!");
    }
    
    private void spawnJobNPCs() {
        // Cafeteria staff (at -45, 63, 58)
        spawnNPC("Повар Михалыч", -45, 63, 58, "kitchen", "§eХочешь работать на кухне? Плачу $5 за смену!");
        spawnNPC("Кухонный Помощник", -42, 63, 58, "kitchen", "§eПомогай на кухне, заработаешь денег!");

        // Library staff (at 30, 63, 58)
        spawnNPC("Библиотекарь Анна", 30, 63, 58, "library", "§dПомощь в библиотеке - $3 за смену!");

        // Workshop staff (at -70, 63, -80)
        spawnNPC("Мастер Цеха", -70, 63, -80, "workshop", "§9Нужны рабочие руки в мастерской. Оплата достойная.");

        // Janitor
        spawnNPC("Уборщик Петя", 30, 63, -30, "janitor", "§7Уборка территории - $3 за смену!");
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

        // Medical staff (at 60, 63, -85)
        spawnNPC("Доктор Смирнов", 60, 63, -85, "doctor", "§2Медицинская помощь и лечение!");
        spawnNPC("Медсестра Елена", 62, 63, -85, "doctor", "§2Помогу с лечением.");

        // Services
        spawnNPC("Адвокат Петрова", 65, 63, -25, "lawyer", "§1Нужна юридическая помощь? Обращайся!");
        spawnNPC("Психолог Анна", 80, 63, -20, "psychologist", "§3Психологическая поддержка и консультации!");
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
            int x = -180 + (int)(Math.random() * 360);
            int z = -180 + (int)(Math.random() * 360);
            
            spawnNPC(name, x, 63, z, "prisoner", "§7Привет, как дела?");
        }
    }

    private void spawnSolitaryGuards() {
        // Solitary confinement guards
        spawnNPC("Охранник Карцера", -130, 56, -12, "guard", "§cЗдесь строгий режим. Нарушители будут наказаны.");
        spawnNPC("Надзиратель Блока Д", -132, 56, -12, "guard", "§cНикаких разговоров. Полная тишина.");
    }
    
    private void spawnNPC(String name, int x, int y, int z, String type, String greeting) {
        if (registry == null) {
            plugin.getLogger().warning("Cannot spawn NPC " + name + " - Citizens registry not available");
            return;
        }
        
        try {
            // Create location for the NPC
            Location location = new Location(world, x + 0.5, y, z + 0.5);
            
            // Create the NPC
            NPC npc = registry.createNPC(EntityType.PLAYER, ChatColor.translateAlternateColorCodes('&', name));
            if (npc == null) {
                plugin.getLogger().warning("Failed to create NPC: " + name);
                return;
            }
            
            // Spawn the NPC
            if (!npc.isSpawned()) {
                npc.spawn(location);
            } else {
                npc.teleport(location, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
            
            // Make NPC look at players
            npc.setProtected(true);
            npc.data().set(PROTECTED_METADATA, true);
            
            // Add custom trait with type and greeting
            NPCTrait trait = new NPCTrait(plugin, type, greeting);
            npc.addTrait(trait);
            
            // Register events if not already registered
            if (!npc.hasTrait(NPCTrait.class)) {
                npc.addTrait(trait);
            }
            
            plugin.getLogger().info("Spawned NPC: " + name + " (" + type + ") at " + location);
        } catch (Exception e) {
            plugin.getLogger().severe("Error spawning NPC " + name + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @TraitName("prisonnpc")
    public static class NPCTrait extends Trait {
        private final PrisonNEO plugin;
        private final String type;
        private final String greeting;
        private boolean registered = false;
        
        // Required no-arg constructor for Citizens
        public NPCTrait() {
            this(null, "", "");
        }
        
        public NPCTrait(PrisonNEO plugin, String type, String greeting) {
            super("prisonnpc");
            this.plugin = plugin != null ? plugin : (PrisonNEO) Bukkit.getPluginManager().getPlugin("PrisonNEO");
            this.type = type != null ? type : "";
            this.greeting = greeting != null ? greeting : "";
        }
        
        @Override
        public void onAttach() {
            plugin.getLogger().info("Attached NPC trait: " + type);
        }
        
        @Override
        public void onSpawn() {
            // Set NPC metadata when spawned
            if (npc != null) {
                npc.data().set("prisonnpc.type", type);
                npc.data().set(PROTECTED_METADATA, true);
            }
        }
        
        @EventHandler
        public void onNPCClick(NPCRightClickEvent event) {
            if (event.getNPC().equals(npc)) {
                Player player = event.getClicker();
                if (greeting != null && !greeting.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', greeting));
                }
                // Add more interaction logic based on NPC type
                switch (type) {
                    case "guard":
                        player.sendMessage(ChatColor.RED + "Нарушитель! В тюрьму!");
                        break;
                    case "shop":
                        player.sendMessage(ChatColor.GREEN + "Что вы хотите купить?");
                        break;
                    // Add more cases for different NPC types
                }
            }
        }
    }
}
