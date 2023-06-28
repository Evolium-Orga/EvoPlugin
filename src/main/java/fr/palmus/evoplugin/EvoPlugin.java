package fr.palmus.evoplugin;

import fr.palmus.evoplugin.commands.*;
import fr.palmus.evoplugin.commands.completer.ExpCompleter;
import fr.palmus.evoplugin.item.InventoryHelper;
import fr.palmus.evoplugin.economy.Economy;
import fr.palmus.evoplugin.fastboard.EvoScoreboard;
import fr.palmus.evoplugin.listeners.*;
import fr.palmus.evoplugin.period.PeriodCaster;
import fr.palmus.evoplugin.api.messages.Formator;
import fr.palmus.evoplugin.persistance.config.StringConfig;
import fr.palmus.evoplugin.persistance.mysql.DatabaseManager;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import fr.palmus.evoplugin.api.player.PlayerData;
import fr.palmus.evoplugin.api.Logger;
import fr.palmus.evoplugin.fastboard.FastBoard;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class EvoPlugin extends JavaPlugin {

    private static EvoPlugin INSTANCE;

    public boolean DEBUG_MODE;

    private InventoryHelper storage;

    private Logger customLogger;
    private Formator formator;

    private PlayerData customPlayer;

    private Economy economyModule;

    private PeriodCaster periodCaster;

    public File periodConfigFile = new File("plugins/EvoPlugin", "period.yml");
    private FileConfiguration periodConfigurationFile;

    RegisteredServiceProvider<LuckPerms> provider;
    public LuckPerms LPapi;

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        INSTANCE = this;

        customLogger = new Logger();

        saveDefaultConfig();
        DEBUG_MODE = getConfig().getBoolean("debug");

        periodConfigurationFile = YamlConfiguration.loadConfiguration(periodConfigFile);
        try {
            StringConfig.load();
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while enabling configurations files, FATAL");
            getPluginLoader().disablePlugin(this);
            return;
        }

        customLogger.debug(ChatColor.GREEN + "Configuration files loaded !");

        customLogger.debug(ChatColor.DARK_GREEN + "Debugger detected, additional messages will be printed in the console");

        formator = new Formator();
        databaseManager = new DatabaseManager();
        customPlayer = new PlayerData(this);
        storage = new InventoryHelper();
        periodCaster = new PeriodCaster();

        setCommands();
        setListeners();
        setTabCompleter();

        customLogger.debug(ChatColor.GREEN + "Initialisation starting, loading configuration files");


        Plugin WE = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (WE != null) {
            customLogger.debug(ChatColor.GREEN + "WorldEdit Hooked !");
        }

        provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LPapi = provider.getProvider();
            customLogger.debug(ChatColor.GREEN + "LuckPerms Hooked !");
        }


        customLogger.debug(ChatColor.DARK_GREEN + "-------------------------------------------------------------------");

        try {
            economyModule = new Economy(this);
        } catch (Exception e) {
            customLogger.debug(ChatColor.RED + "Failed Load economy module FATAL");
            getPluginLoader().disablePlugin(this);
        }
        customLogger.debug(ChatColor.GREEN + "Economy module enabled !");

        customLogger.debug(ChatColor.DARK_GREEN + "-------------------------------------------------------------------");


        customLogger.debug(ChatColor.YELLOW + "Checking for connected players...");
        for (Player pl : Bukkit.getOnlinePlayers()) {
            getCustomPlayer().initPlayer(pl);
            getEconomyModule().initPlayerEcon(pl);

            FastBoard boards = new FastBoard(pl);
            EvoScoreboard.registerPlayer(pl, boards);
            EvoScoreboard.updateScoreboard(pl);

            customLogger.debug(ChatColor.GREEN + "Found " + pl.getDisplayName() + ", linking done");
        }

        customLogger.debug(ChatColor.DARK_GREEN + "-------------------------------------------------------------------");

        customLogger.debug(ChatColor.GREEN + "Plugin enabled !");
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();

        for (Player pl : Bukkit.getOnlinePlayers()) {
           EvoPlayer.getInstanceOf(pl).saveExp();
            getEconomyModule().getPlayerEcon(pl).saveMoney();
        }

        try {
            periodConfigurationFile.save(periodConfigFile);
        } catch (IOException | NullPointerException e) {
            if (getDatabaseManager() == null) {
                customLogger.debug(ChatColor.RED + "FAILED TO SAVE CONFIG, ALL THE EXP WON THIS SESSION WILL BE LOST.");
            }
        }

        for (Player pl : Bukkit.getOnlinePlayers()) {
            try {
                getCustomPlayer().saveData(pl, getDatabaseManager().getDatabase().getConnection());
            } catch (SQLException e) {
                customLogger.debug(ChatColor.RED + "FAILED TO SAVE DATA ON DATABASE FOR PLAYER: " + pl.getDisplayName());
            }
            getDatabaseManager().close();
        }
    }

    public void setCommands() {
        getCommand("exp").setExecutor(new ExpExecutor());
        getCommand("periode").setExecutor(new PeriodExecutor());
        getCommand("money").setExecutor(new EconExecutor());
        customLogger.debug(ChatColor.GREEN + "Commands modules Enabled");
    }

    private void setTabCompleter() {
        getCommand("exp").setTabCompleter(new ExpCompleter());
    }

    public void setListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuitManager(), this);
        pm.registerEvents(new BlockManager(), this);
        pm.registerEvents(new CraftManager(), this);
        pm.registerEvents(new DamageManager(), this);
        pm.registerEvents(new InventoryManager(), this);
        pm.registerEvents(new ScoreboardUpdater(this, LPapi), this);
        customLogger.debug(ChatColor.GREEN + "Listeners modules Enabled");
    }

    public InventoryHelper getComponents() {
        return storage;
    }

    public static EvoPlugin getInstance() {
        return INSTANCE;
    }

    public PlayerData getCustomPlayer() {
        return customPlayer;
    }

    public PeriodCaster getPeriodCaster() {
        return periodCaster;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public Logger getCustomLogger() {
        return customLogger;
    }

    public Formator getFormator() {
        return formator;
    }

    public FileConfiguration getPeriodConfigurationFile() {
        return periodConfigurationFile;
    }

    public Economy getEconomyModule() {
        return economyModule;
    }
}
