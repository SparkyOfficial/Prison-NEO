package com.prisonneo.managers;

import com.prisonneo.PrisonNEO;
import com.prisonneo.data.PrisonPlayer;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobManager implements Listener {
    
    private final PrisonNEO plugin;
    private final Map<String, JobData> jobs;
    private final Map<UUID, String> activeJobs;
    private final Map<UUID, Long> jobStartTimes;
    
    public JobManager(PrisonNEO plugin) {
        this.plugin = plugin;
        this.jobs = new HashMap<>();
        this.activeJobs = new HashMap<>();
        this.jobStartTimes = new HashMap<>();
        setupJobs();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startJobTimer();
    }
    
    private void setupJobs() {
        jobs.put("kitchen", new JobData("kitchen", "Кухня", 5.0, 300, // 5 minutes
                Material.COOKED_BEEF, "Готовь еду для заключённых"));
        
        jobs.put("laundry", new JobData("laundry", "Прачечная", 4.0, 240, // 4 minutes
                Material.WHITE_WOOL, "Стирай одежду заключённых"));
        
        jobs.put("library", new JobData("library", "Библиотека", 3.0, 180, // 3 minutes
                Material.BOOK, "Организуй книги в библиотеке"));
        
        jobs.put("janitor", new JobData("janitor", "Уборка", 6.0, 360, // 6 minutes
                Material.IRON_SHOVEL, "Убирай территорию тюрьмы"));
        
        jobs.put("guard_assistant", new JobData("guard_assistant", "Помощник Охранника", 8.0, 480, // 8 minutes
                Material.IRON_SWORD, "Помогай охранникам (только для Trustee)"));
    }
    
    public void openJobMenu(Player player, String jobType) {
        JobData job = jobs.get(jobType);
        if (job == null) return;
        
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        // Check if player already has a job
        if (activeJobs.containsKey(player.getUniqueId())) {
            player.sendMessage("§cВы уже работаете! Завершите текущую работу сначала.");
            return;
        }
        
        // Check rank requirement for guard assistant
        if (jobType.equals("guard_assistant") && !prisonPlayer.getRank().equals("TRUSTEE") && !prisonPlayer.getRank().equals("GUARD")) {
            player.sendMessage("§cЭта работа доступна только для Trustee!");
            return;
        }
        
        Inventory inv = Bukkit.createInventory(null, 27, "§8Работа: " + job.getDisplayName());
        
        // Job info
        ItemStack jobInfo = new ItemStack(job.getIcon());
        ItemMeta meta = jobInfo.getItemMeta();
        meta.setDisplayName("§e" + job.getDisplayName());
        meta.setLore(Arrays.asList(
            "§7" + job.getDescription(),
            "",
            "§aОплата: §f$" + String.format("%.2f", job.getPay()),
            "§aВремя: §f" + (job.getDuration() / 60) + " минут",
            "",
            "§eНажмите, чтобы начать работу"
        ));
        jobInfo.setItemMeta(meta);
        inv.setItem(13, jobInfo);
        
        // Accept button
        ItemStack accept = new ItemStack(Material.GREEN_WOOL);
        ItemMeta acceptMeta = accept.getItemMeta();
        acceptMeta.setDisplayName("§aПринять работу");
        accept.setItemMeta(acceptMeta);
        inv.setItem(11, accept);
        
        // Decline button
        ItemStack decline = new ItemStack(Material.RED_WOOL);
        ItemMeta declineMeta = decline.getItemMeta();
        declineMeta.setDisplayName("§cОтказаться");
        decline.setItemMeta(declineMeta);
        inv.setItem(15, decline);
        
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.startsWith("§8Работа:")) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        
        if (clicked.getType() == Material.GREEN_WOOL) {
            // Accept job
            String jobType = getJobTypeFromTitle(title);
            startJob(player, jobType);
            player.closeInventory();
        } else if (clicked.getType() == Material.RED_WOOL) {
            // Decline job
            player.closeInventory();
            player.sendMessage("§eВы отказались от работы.");
        }
    }
    
    private String getJobTypeFromTitle(String title) {
        String jobName = title.replace("§8Работа: ", "");
        for (Map.Entry<String, JobData> entry : jobs.entrySet()) {
            if (entry.getValue().getDisplayName().equals(jobName)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    public void startJob(Player player, String jobType) {
        JobData job = jobs.get(jobType);
        if (job == null) return;
        
        activeJobs.put(player.getUniqueId(), jobType);
        jobStartTimes.put(player.getUniqueId(), System.currentTimeMillis());
        
        player.sendMessage("§aВы начали работать: " + job.getDisplayName());
        player.sendMessage("§eРабота завершится через " + (job.getDuration() / 60) + " минут");
        
        // Give job tool
        ItemStack tool = new ItemStack(job.getIcon());
        ItemMeta meta = tool.getItemMeta();
        meta.setDisplayName("§e" + job.getDisplayName() + " - Инструмент");
        meta.setLore(Arrays.asList("§7Рабочий инструмент", "§cНе выбрасывать!"));
        tool.setItemMeta(meta);
        player.getInventory().addItem(tool);
    }
    
    public void completeJob(Player player) {
        String jobType = activeJobs.remove(player.getUniqueId());
        jobStartTimes.remove(player.getUniqueId());
        
        if (jobType == null) return;
        
        JobData job = jobs.get(jobType);
        PrisonPlayer prisonPlayer = plugin.getPlayerManager().getPrisonPlayer(player);
        
        prisonPlayer.addMoney(job.getPay());
        player.sendMessage("§aРабота завершена! Получено: $" + String.format("%.2f", job.getPay()));
        
        // Remove job tool
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                String displayName = item.getItemMeta().getDisplayName();
                if (displayName.contains("Инструмент")) {
                    player.getInventory().remove(item);
                    break;
                }
            }
        }
    }
    
    private void startJobTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                
                for (Map.Entry<UUID, Long> entry : jobStartTimes.entrySet()) {
                    UUID playerUuid = entry.getKey();
                    long startTime = entry.getValue();
                    String jobType = activeJobs.get(playerUuid);
                    
                    if (jobType != null) {
                        JobData job = jobs.get(jobType);
                        long elapsed = currentTime - startTime;
                        
                        if (elapsed >= job.getDuration() * 1000L) {
                            Player player = Bukkit.getPlayer(playerUuid);
                            if (player != null && player.isOnline()) {
                                completeJob(player);
                            } else {
                                activeJobs.remove(playerUuid);
                                jobStartTimes.remove(playerUuid);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Every second
    }
    
    public boolean hasActiveJob(Player player) {
        return activeJobs.containsKey(player.getUniqueId());
    }
    
    public String getActiveJob(Player player) {
        return activeJobs.get(player.getUniqueId());
    }
    
    public static class JobData {
        private final String id;
        private final String displayName;
        private final double pay;
        private final int duration; // in seconds
        private final Material icon;
        private final String description;
        
        public JobData(String id, String displayName, double pay, int duration, Material icon, String description) {
            this.id = id;
            this.displayName = displayName;
            this.pay = pay;
            this.duration = duration;
            this.icon = icon;
            this.description = description;
        }
        
        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public double getPay() { return pay; }
        public int getDuration() { return duration; }
        public Material getIcon() { return icon; }
        public String getDescription() { return description; }
    }
}
