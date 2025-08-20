package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NPCManager {
    
    private final PrisonNEO plugin;
    private final List<NPC> prisonNPCs;
    private NPCRegistry registry;
    
    public NPCManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.prisonNPCs = new ArrayList<>();
    }
    
    public void initializeNPCs() {
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Citizens")) {
            plugin.getLogger().warning("Citizens plugin not found! NPCs will not be created.");
            return;
        }
        
        registry = CitizensAPI.getNPCRegistry();
        createAllNPCs();
    }
    
    private void createAllNPCs() {
        if (plugin.getWorldManager().getPrisonWorld() == null) return;
        
        // Create guard NPCs
        createGuardNPCs();
        
        // Create job NPCs
        createJobNPCs();
        
        // Create shop NPCs
        createShopNPCs();
        
        // Create special NPCs
        createSpecialNPCs();
    }
    
    private void createGuardNPCs() {
        // Main entrance guard
        createGuard("Охранник Петров", new Location(plugin.getWorldManager().getPrisonWorld(), 0, 62, -95), 
                   "§cСтой! Предъяви документы!", "guard_entrance");
        
        // Yard patrol guards
        createGuard("Охранник Иванов", new Location(plugin.getWorldManager().getPrisonWorld(), -20, 62, 0), 
                   "§eНе нарушай порядок в дворе!", "guard_yard");
        
        createGuard("Охранник Сидоров", new Location(plugin.getWorldManager().getPrisonWorld(), 20, 62, 0), 
                   "§eВремя прогулки ограничено!", "guard_yard2");
        
        // Cell block guards
        createGuard("Надзиратель Волков", new Location(plugin.getWorldManager().getPrisonWorld(), -60, 62, -60), 
                   "§6Тишина в блоке А!", "guard_block_a");
        
        createGuard("Надзиратель Козлов", new Location(plugin.getWorldManager().getPrisonWorld(), 60, 62, -60), 
                   "§6Соблюдай режим!", "guard_block_b");
        
        // Mine guards
        createGuard("Горный Надзиратель", new Location(plugin.getWorldManager().getPrisonWorld(), -130, 45, -130), 
                   "§aРаботай усердно!", "guard_mine");
    }
    
    private void createJobNPCs() {
        // Kitchen manager
        createJobNPC("Повар Михалыч", new Location(plugin.getWorldManager().getPrisonWorld(), -70, 63, 10), 
                    "§eХочешь работать на кухне? Плачу $5 за смену!", "kitchen", 5.0);
        
        // Laundry manager
        createJobNPC("Прачка Марья", new Location(plugin.getWorldManager().getPrisonWorld(), 70, 63, 10), 
                    "§bПрачечная всегда нуждается в помощи! $4 за смену!", "laundry", 4.0);
        
        // Library manager
        createJobNPC("Библиотекарь Олег", new Location(plugin.getWorldManager().getPrisonWorld(), 0, 63, 70), 
                    "§dПомоги с книгами! $3 за смену, но тихая работа!", "library", 3.0);
        
        // Janitor supervisor
        createJobNPC("Завхоз Николай", new Location(plugin.getWorldManager().getPrisonWorld(), -50, 63, 50), 
                    "§7Уборка территории! $6 за смену!", "janitor", 6.0);
    }
    
    private void createShopNPCs() {
        // General store
        createShopNPC("Торговец Василий", new Location(plugin.getWorldManager().getPrisonWorld(), 30, 63, -70), 
                     "§aДобро пожаловать в тюремный магазин!", "general");
        
        // Tool shop
        createShopNPC("Кузнец Андрей", new Location(plugin.getWorldManager().getPrisonWorld(), -30, 63, -70), 
                     "§6Инструменты для работы в шахтах!", "tools");
        
        // Food vendor
        createShopNPC("Буфетчица Света", new Location(plugin.getWorldManager().getPrisonWorld(), 0, 63, -50), 
                     "§eГорячая еда и напитки!", "food");
    }
    
    private void createSpecialNPCs() {
        // Warden
        createSpecialNPC("Начальник Тюрьмы", new Location(plugin.getWorldManager().getPrisonWorld(), 75, 63, -15), 
                        "§4Я слежу за порядком в этой тюрьме!", "warden");
        
        // Lawyer
        createSpecialNPC("Адвокат Петрова", new Location(plugin.getWorldManager().getPrisonWorld(), 65, 63, -25), 
                        "§9Могу помочь сократить срок... за плату!", "lawyer");
        
        // Doctor
        createSpecialNPC("Доктор Смирнов", new Location(plugin.getWorldManager().getPrisonWorld(), 85, 63, -25), 
                        "§2Медицинская помощь и лечение!", "doctor");
        
        // Informant
        createSpecialNPC("Стукач Вася", new Location(plugin.getWorldManager().getPrisonWorld(), 10, 63, 40), 
                        "§8Псст... у меня есть информация...", "informant");
    }
    
    private void createGuard(String name, Location location, String greeting, String id) {
        NPC npc = registry.createNPC(EntityType.PLAYER, name);
        npc.spawn(location);
        npc.data().set("greeting", greeting);
        npc.data().set("type", "guard");
        npc.data().set("id", id);
        
        // Guard equipment
        npc.getEntity().getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        npc.getEntity().getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        npc.getEntity().getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        
        prisonNPCs.add(npc);
    }
    
    private void createJobNPC(String name, Location location, String greeting, String jobType, double pay) {
        NPC npc = registry.createNPC(EntityType.PLAYER, name);
        npc.spawn(location);
        npc.data().set("greeting", greeting);
        npc.data().set("type", "job");
        npc.data().set("job", jobType);
        npc.data().set("pay", pay);
        
        prisonNPCs.add(npc);
    }
    
    private void createShopNPC(String name, Location location, String greeting, String shopType) {
        NPC npc = registry.createNPC(EntityType.PLAYER, name);
        npc.spawn(location);
        npc.data().set("greeting", greeting);
        npc.data().set("type", "shop");
        npc.data().set("shop", shopType);
        
        prisonNPCs.add(npc);
    }
    
    private void createSpecialNPC(String name, Location location, String greeting, String specialType) {
        NPC npc = registry.createNPC(EntityType.PLAYER, name);
        npc.spawn(location);
        npc.data().set("greeting", greeting);
        npc.data().set("type", "special");
        npc.data().set("special", specialType);
        
        prisonNPCs.add(npc);
    }
    
    public void handleNPCClick(Player player, NPC npc) {
        String type = npc.data().get("type", "");
        
        switch (type) {
            case "guard":
                handleGuardInteraction(player, npc);
                break;
            case "job":
                handleJobInteraction(player, npc);
                break;
            case "shop":
                handleShopInteraction(player, npc);
                break;
            case "special":
                handleSpecialInteraction(player, npc);
                break;
        }
    }
    
    private void handleGuardInteraction(Player player, NPC npc) {
        String greeting = npc.data().get("greeting", "Привет!");
        player.sendMessage(greeting);
        
        // Random guard responses
        String[] responses = {
            "§cСоблюдай правила!",
            "§eВозвращайся в камеру до отбоя!",
            "§6Никаких драк в тюрьме!",
            "§aХорошо работаешь, продолжай!"
        };
        
        String response = responses[(int) (Math.random() * responses.length)];
        player.sendMessage(response);
    }
    
    private void handleJobInteraction(Player player, NPC npc) {
        String jobType = npc.data().get("job", "");
        plugin.getJobManager().openJobMenu(player, jobType);
    }
    
    private void handleShopInteraction(Player player, NPC npc) {
        String shopType = npc.data().get("shop", "");
        plugin.getShopManager().openShop(player, shopType);
    }
    
    private void handleSpecialInteraction(Player player, NPC npc) {
        String specialType = npc.data().get("special", "");
        
        switch (specialType) {
            case "warden":
                plugin.getWardenManager().openWardenMenu(player);
                break;
            case "lawyer":
                plugin.getLawyerManager().openLawyerMenu(player);
                break;
            case "doctor":
                plugin.getDoctorManager().openDoctorMenu(player);
                break;
            case "informant":
                plugin.getInformantManager().openInformantMenu(player);
                break;
        }
    }
    
    public void removeAllNPCs() {
        for (NPC npc : prisonNPCs) {
            if (npc.isSpawned()) {
                npc.despawn();
            }
            npc.destroy();
        }
        prisonNPCs.clear();
    }
    
    public List<NPC> getPrisonNPCs() {
        return prisonNPCs;
    }
}
