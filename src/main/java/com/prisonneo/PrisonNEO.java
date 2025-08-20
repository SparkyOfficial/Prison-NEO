package com.prisonneo;

import com.prisonneo.commands.*;
import com.prisonneo.listeners.*;
import com.prisonneo.managers.*;
import com.prisonneo.ui.UIManager;
import com.prisonneo.world.WorldGenerator;
import com.prisonneo.world.NPCSpawner;
import org.bukkit.plugin.java.JavaPlugin;

public final class PrisonNEO extends JavaPlugin {

    private static PrisonNEO instance;
    private WorldManager worldManager;
    private PlayerManager playerManager;
    private RankManager rankManager;
    private CellManager cellManager;
    private MineManager mineManager;
    private EconomyManager economyManager;
    private NPCManager npcManager;
    private ShopManager shopManager;
    private JobManager jobManager;
    private GangManager gangManager;
    private ContrabandManager contrabandManager;
    private WardenManager wardenManager;
    private LawyerManager lawyerManager;
    private DoctorManager doctorManager;
    private InformantManager informantManager;
    private EscapeManager escapeManager;
    private EventManager eventManager;
    private ReputationManager reputationManager;
    private MinigameManager minigameManager;
    private LoanManager loanManager;
    private AdvancedEscapeManager advancedEscapeManager;
    private RiotManager riotManager;
    private VisitorManager visitorManager;
    private MailManager mailManager;
    private com.prisonneo.managers.SecurityManager securityManager;
    private PunishmentManager punishmentManager;
    private ScheduleManager scheduleManager;
    private AchievementManager achievementManager;
    private WorkshopManager workshopManager;
    private LibraryManager libraryManager;
    private UIManager uiManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        initializeManagers();
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        getLogger().info("Prison NEO has been enabled!");
        getLogger().info("Use /prisonworld create to generate the prison world");
    }

    @Override
    public void onDisable() {
        if (worldManager != null) {
            worldManager.saveData();
        }
        if (playerManager != null) {
            playerManager.saveData();
        }
        getLogger().info("Prison NEO has been disabled!");
    }

    private void initializeManagers() {
        worldManager = new WorldManager(this);
        playerManager = new PlayerManager(this);
        rankManager = new RankManager(this);
        cellManager = new CellManager(this);
        mineManager = new MineManager(this);
        economyManager = new EconomyManager(this);
        npcManager = new NPCManager(this);
        shopManager = new ShopManager(this);
        jobManager = new JobManager(this);
        gangManager = new GangManager(this);
        contrabandManager = new ContrabandManager(this);
        wardenManager = new WardenManager(this);
        lawyerManager = new LawyerManager(this);
        doctorManager = new DoctorManager(this);
        informantManager = new InformantManager(this);
        escapeManager = new EscapeManager(this);
        eventManager = new EventManager(this);
        reputationManager = new ReputationManager(this);
        minigameManager = new MinigameManager(this);
        loanManager = new LoanManager(this);
        advancedEscapeManager = new AdvancedEscapeManager(this);
        riotManager = new RiotManager(this);
        visitorManager = new VisitorManager(this);
        mailManager = new MailManager(this);
        securityManager = new com.prisonneo.managers.SecurityManager(this);
        punishmentManager = new PunishmentManager(this);
        scheduleManager = new ScheduleManager(this);
        achievementManager = new AchievementManager(this);
        workshopManager = new WorkshopManager(this);
        libraryManager = new LibraryManager(this);
        uiManager = new UIManager(this);
    }

    private void registerCommands() {
        getCommand("prison").setExecutor(new PrisonCommand(this));
        getCommand("prisonworld").setExecutor(new PrisonWorldCommand(this));
        getCommand("cell").setExecutor(new CellCommand(this));
        getCommand("rank").setExecutor(new RankCommand(this));
        getCommand("mine").setExecutor(new MineCommand(this));
        getCommand("gang").setExecutor(new GangCommand(this));
        getCommand("contraband").setExecutor(new ContrabandCommand(this));
        getCommand("job").setExecutor(new JobCommand(this));
        getCommand("minigame").setExecutor(new MinigameCommand(this));
        getCommand("loan").setExecutor(new LoanCommand(this));
        getCommand("escape").setExecutor(new EscapeCommand(this));
        getCommand("visitor").setExecutor(new VisitorCommand(this));
        getCommand("mail").setExecutor(new MailCommand(this));
        getCommand("prisonadmin").setExecutor(new AdminCommand(this));
        getCommand("workshop").setExecutor(new WorkshopCommand(this));
        getCommand("library").setExecutor(new LibraryCommand(this));
        getCommand("achievement").setExecutor(new AchievementCommand(this));
        getCommand("schedule").setExecutor(new ScheduleCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new NPCClickListener(this), this);
        getServer().getPluginManager().registerEvents(minigameManager, this);
        getServer().getPluginManager().registerEvents(loanManager, this);
        getServer().getPluginManager().registerEvents(advancedEscapeManager, this);
        getServer().getPluginManager().registerEvents(visitorManager, this);
        getServer().getPluginManager().registerEvents(mailManager, this);
        getServer().getPluginManager().registerEvents(achievementManager, this);
        getServer().getPluginManager().registerEvents(workshopManager, this);
        getServer().getPluginManager().registerEvents(libraryManager, this);
    }

    public static PrisonNEO getInstance() {
        return instance;
    }

    // Getters for managers
    public WorldManager getWorldManager() { return worldManager; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public RankManager getRankManager() { return rankManager; }
    public CellManager getCellManager() { return cellManager; }
    public MineManager getMineManager() { return mineManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public NPCManager getNPCManager() { return npcManager; }
    public ShopManager getShopManager() { return shopManager; }
    public JobManager getJobManager() { return jobManager; }
    public GangManager getGangManager() { return gangManager; }
    public ContrabandManager getContrabandManager() { return contrabandManager; }
    public WardenManager getWardenManager() { return wardenManager; }
    public LawyerManager getLawyerManager() { return lawyerManager; }
    public DoctorManager getDoctorManager() { return doctorManager; }
    public InformantManager getInformantManager() { return informantManager; }
    public EscapeManager getEscapeManager() { return escapeManager; }
    public EventManager getEventManager() { return eventManager; }
    public ReputationManager getReputationManager() { return reputationManager; }
    public MinigameManager getMinigameManager() { return minigameManager; }
    public LoanManager getLoanManager() { return loanManager; }
    public AdvancedEscapeManager getAdvancedEscapeManager() { return advancedEscapeManager; }
    public RiotManager getRiotManager() { return riotManager; }
    public VisitorManager getVisitorManager() { return visitorManager; }
    public MailManager getMailManager() { return mailManager; }
    public com.prisonneo.managers.SecurityManager getSecurityManager() { return securityManager; }
    public PunishmentManager getPunishmentManager() { return punishmentManager; }
    public ScheduleManager getScheduleManager() { return scheduleManager; }
    public AchievementManager getAchievementManager() { return achievementManager; }
    public WorkshopManager getWorkshopManager() { return workshopManager; }
    public LibraryManager getLibraryManager() { return libraryManager; }
    public UIManager getUIManager() { return uiManager; }
}
