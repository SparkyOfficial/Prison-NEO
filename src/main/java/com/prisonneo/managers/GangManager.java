package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.gangs.Gang;
import com.prisonneo.data.PrisonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GangManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<String, Gang> gangs;
    private final Map<UUID, String> playerGangs;
    private final Map<UUID, Long> lastTerritoryWarning;
    
    public GangManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.gangs = new HashMap<>();
        this.playerGangs = new HashMap<>();
        this.lastTerritoryWarning = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void initialize() {
        setupDefaultGangs();
        startGangEvents();
    }
    
    private void setupDefaultGangs() {
        // Братва
        Gang bratva = new Gang("bratva", "§4Братва", "§4[БРАТВА]", 
                              new Location(plugin.getWorldManager().getPrisonWorld(), -60, 62, -60));
        bratva.setTerritory(-80, -40, -80, -40);
        gangs.put("bratva", bratva);
        
        // Воры в Законе
        Gang thieves = new Gang("thieves", "§6Воры в Законе", "§6[ВОРЫ]", 
                               new Location(plugin.getWorldManager().getPrisonWorld(), 60, 62, -60));
        thieves.setTerritory(40, 80, -80, -40);
        gangs.put("thieves", thieves);
        
        // Авторитеты
        Gang authorities = new Gang("authorities", "§5Авторитеты", "§5[АВТОРИТЕТ]", 
                                   new Location(plugin.getWorldManager().getPrisonWorld(), -60, 62, 60));
        authorities.setTerritory(-80, -40, 40, 80);
        gangs.put("authorities", authorities);
        
        // Новички
        Gang rookies = new Gang("rookies", "§7Новички", "§7[НОВИЧОК]", 
                               new Location(plugin.getWorldManager().getPrisonWorld(), 60, 62, 60));
        rookies.setTerritory(40, 80, 40, 80);
        gangs.put("rookies", rookies);
        
        plugin.getLogger().info("Созданы банды: " + gangs.size());
    }
    
    private void startGangEvents() {
        // Запуск случайных событий банд каждые 5 минут
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            triggerRandomGangEvent();
        }, 6000L, 6000L); // 5 минут
    }
    
    private void triggerRandomGangEvent() {
        Random random = new Random();
        int eventType = random.nextInt(4);
        
        switch (eventType) {
            case 0:
                triggerTerritoryConflict();
                break;
            case 1:
                triggerGangMeeting();
                break;
            case 2:
                triggerReputationEvent();
                break;
            case 3:
                triggerTradeEvent();
                break;
        }
    }
    
    private void triggerTerritoryConflict() {
        List<Gang> gangList = new ArrayList<>(gangs.values());
        if (gangList.size() < 2) return;
        
        Gang gang1 = gangList.get(new Random().nextInt(gangList.size()));
        Gang gang2 = gangList.get(new Random().nextInt(gangList.size()));
        
        if (gang1.equals(gang2)) return;
        
        // Уведомить участников
        broadcastToGang(gang1, "§c⚔ Конфликт с бандой " + gang2.getDisplayName() + "!");
        broadcastToGang(gang2, "§c⚔ Конфликт с бандой " + gang1.getDisplayName() + "!");
    }
    
    private void triggerGangMeeting() {
        for (Gang gang : gangs.values()) {
            if (gang.getMembers().size() > 0) {
                broadcastToGang(gang, "§e📢 Собрание банды! Встречаемся на территории.");
            }
        }
    }
    
    private void triggerReputationEvent() {
        Gang randomGang = gangs.values().stream()
            .skip(new Random().nextInt(gangs.size()))
            .findFirst().orElse(null);
        
        if (randomGang != null) {
            broadcastToGang(randomGang, "§a✨ Репутация банды выросла!");
        }
    }
    
    private void triggerTradeEvent() {
        broadcastToAllGangs("§6💰 На черном рынке появились новые товары!");
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        
        if (to == null) return;
        
        String playerGangId = playerGangs.get(player.getUniqueId());
        Gang playerGang = playerGangId != null ? gangs.get(playerGangId) : null;
        
        // Проверка территории
        for (Gang gang : gangs.values()) {
            if (gang.isInTerritory(to)) {
                if (playerGang == null || !gang.equals(playerGang)) {
                    // Игрок на чужой территории
                    long lastWarning = lastTerritoryWarning.getOrDefault(player.getUniqueId(), 0L);
                    if (System.currentTimeMillis() - lastWarning > 30000) { // 30 секунд
                        player.sendMessage("§c⚠ Вы на территории банды " + gang.getDisplayName() + "!");
                        lastTerritoryWarning.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                } else {
                    // Своя территория
                    player.sendActionBar("§aВаша территория: " + gang.getDisplayName());
                }
                break;
            }
        }
    }
    
    public void openGangMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8Банды Тюрьмы");
        
        int slot = 0;
        for (Gang gang : gangs.values()) {
            ItemStack gangItem = new ItemStack(Material.WHITE_BANNER);
            ItemMeta meta = gangItem.getItemMeta();
            meta.setDisplayName(gang.getDisplayName());
            meta.setLore(Arrays.asList(
                "§7Участников: §f" + gang.getMembers().size(),
                "§7Территория: §f" + gang.getTerritoryInfo(),
                "§7Лидер: §f" + (gang.getLeader() != null ? gang.getLeader() : "Нет"),
                "",
                "§eНажмите, чтобы присоединиться"
            ));
            gangItem.setItemMeta(meta);
            inv.setItem(slot++, gangItem);
        }
        
        // Информация о текущей банде
        String currentGangId = playerGangs.get(player.getUniqueId());
        if (currentGangId != null) {
            Gang currentGang = gangs.get(currentGangId);
            ItemStack currentInfo = new ItemStack(Material.EMERALD);
            ItemMeta meta = currentInfo.getItemMeta();
            meta.setDisplayName("§aВаша банда: " + currentGang.getDisplayName());
            meta.setLore(Arrays.asList("§eНажмите, чтобы покинуть банду"));
            currentInfo.setItemMeta(meta);
            inv.setItem(49, currentInfo);
        }
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.equals("§8Банды Тюрьмы")) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        
        if (clicked.getType() == Material.WHITE_BANNER) {
            // Join gang
            String gangName = findGangByDisplayName(clicked.getItemMeta().getDisplayName());
            if (gangName != null) {
                joinGang(player, gangName);
            }
        } else if (clicked.getType() == Material.EMERALD) {
            // Leave gang
            leaveGang(player);
        }
        
        player.closeInventory();
    }
    
    public void joinGang(Player player, String gangId) {
        Gang gang = gangs.get(gangId);
        if (gang == null) return;
        
        String currentGangId = playerGangs.get(player.getUniqueId());
        if (currentGangId != null) {
            player.sendMessage("§cВы уже состоите в банде! Сначала покиньте текущую.");
            return;
        }
        
        gang.addMember(player.getUniqueId());
        playerGangs.put(player.getUniqueId(), gangId);
        
        player.sendMessage("§aВы присоединились к банде: " + gang.getDisplayName());
        
        // Notify other gang members
        for (UUID memberUuid : gang.getMembers()) {
            Player member = Bukkit.getPlayer(memberUuid);
            if (member != null && !member.equals(player)) {
                member.sendMessage(gang.getPrefix() + " §f" + player.getName() + " §aприсоединился к банде!");
            }
        }
    }
    
    public void leaveGang(Player player) {
        String gangId = playerGangs.remove(player.getUniqueId());
        if (gangId == null) {
            player.sendMessage("§cВы не состоите в банде!");
            return;
        }
        
        Gang gang = gangs.get(gangId);
        if (gang != null) {
            gang.removeMember(player.getUniqueId());
            player.sendMessage("§eВы покинули банду: " + gang.getDisplayName());
            
            // Notify other gang members
            for (UUID memberUuid : gang.getMembers()) {
                Player member = Bukkit.getPlayer(memberUuid);
                if (member != null) {
                    member.sendMessage(gang.getPrefix() + " §f" + player.getName() + " §cпокинул банду!");
                }
            }
        }
    }
    
    // Utility methods
    private void broadcastToGang(Gang gang, String message) {
        for (UUID memberUuid : gang.getMembers()) {
            Player member = Bukkit.getPlayer(memberUuid);
            if (member != null) {
                member.sendMessage(gang.getPrefix() + " " + message);
            }
        }
    }
    
    private void broadcastToAllGangs(String message) {
        for (Gang gang : gangs.values()) {
            broadcastToGang(gang, message);
        }
    }
    
    private String findGangByDisplayName(String displayName) {
        for (Map.Entry<String, Gang> entry : gangs.entrySet()) {
            if (entry.getValue().getDisplayName().equals(displayName)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    // Public getters
    public Gang getPlayerGang(Player player) {
        String gangId = playerGangs.get(player.getUniqueId());
        return gangId != null ? gangs.get(gangId) : null;
    }
    
    public Collection<Gang> getAllGangs() {
        return gangs.values();
    }
    
    public boolean isInGangTerritory(Player player, Location location) {
        String gangId = playerGangs.get(player.getUniqueId());
        if (gangId == null) return false;
        
        Gang gang = gangs.get(gangId);
        return gang != null && gang.isInTerritory(location);
    }
    
    public static class Gang {
        private final String id;
        private final String name;
        private final String displayName;
        private final String prefix;
        private final Location headquarters;
        private final Set<UUID> members;
        private UUID leader;
        private int x1, x2, z1, z2; // Territory bounds
        
        public Gang(String id, String name, String prefix, Location headquarters) {
            this.id = id;
            this.name = name;
            this.displayName = name;
            this.prefix = prefix;
            this.headquarters = headquarters;
            this.members = new HashSet<>();
        }
        
        public void setTerritory(int x1, int x2, int z1, int z2) {
            this.x1 = x1; this.x2 = x2; this.z1 = z1; this.z2 = z2;
        }
        
        public boolean isInTerritory(Location location) {
            return location.getX() >= x1 && location.getX() <= x2 && 
                   location.getZ() >= z1 && location.getZ() <= z2;
        }
        
        public String getTerritoryInfo() {
            return String.format("(%d,%d) to (%d,%d)", x1, z1, x2, z2);
        }
        
        public void addMember(UUID uuid) { members.add(uuid); }
        public void removeMember(UUID uuid) { members.remove(uuid); }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDisplayName() { return displayName; }
        public String getPrefix() { return prefix; }
        public Location getHeadquarters() { return headquarters; }
        public Set<UUID> getMembers() { return members; }
        public UUID getLeader() { return leader; }
        public void setLeader(UUID leader) { this.leader = leader; }
    }
}
