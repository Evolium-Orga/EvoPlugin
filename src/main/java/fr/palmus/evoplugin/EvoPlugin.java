package fr.palmus.evoplugin;

import fr.palmus.evoplugin.api.Logger;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import fr.palmus.evoplugin.commands.EconExecutor;
import fr.palmus.evoplugin.commands.ExpExecutor;
import fr.palmus.evoplugin.commands.PeriodExecutor;
import fr.palmus.evoplugin.commands.completer.ExpCompleter;
import fr.palmus.evoplugin.commands.completer.PeriodCompleter;
import fr.palmus.evoplugin.fastboard.EvoScoreboard;
import fr.palmus.evoplugin.fastboard.FastBoard;
import fr.palmus.evoplugin.listeners.*;
import fr.palmus.evoplugin.period.PeriodCaster;
import fr.palmus.evoplugin.persistance.config.EvoConfig;
import fr.palmus.evoplugin.persistance.config.StringConfig;
import fr.palmus.evoplugin.persistance.mysql.EvoDatabase;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class EvoPlugin extends JavaPlugin {

    private static EvoPlugin INSTANCE;

    public boolean DEBUG_MODE;

    private Logger customLogger;

    public boolean safeInitialized = true;

    private PeriodCaster periodCaster;

    private RegisteredServiceProvider<LuckPerms> provider;
    public LuckPerms LPapi;

    @Override
    public void onEnable() {
        INSTANCE = this;

        customLogger = new Logger();

        saveDefaultConfig();

        DEBUG_MODE = getConfig().getBoolean("debug");

        EvoConfig.initializeConfigFile();
        try {
            StringConfig.load();
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while enabling configurations files, FATAL");
            safeInitialized = false;
            return;
        }

        customLogger.debug(ChatColor.GREEN + "Configuration files loaded !");

        customLogger.debug(ChatColor.DARK_GREEN + "Debugger detected, additional messages will be printed in the console");

        customLogger.debug(ChatColor.DARK_GREEN + "-------------------------------------------------------------------");

        EvoDatabase.initializeDatabaseConnection();

        customLogger.debug(ChatColor.DARK_GREEN + "-------------------------------------------------------------------");

        Plugin WE = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (WE != null) {
            customLogger.debug(ChatColor.GREEN + "WorldEdit Hooked !");
        }

        provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LPapi = provider.getProvider();
            customLogger.debug(ChatColor.GREEN + "LuckPerms Hooked !");
        }

        setCommands();
        setListeners();
        setTabCompleter();

        customLogger.debug(ChatColor.DARK_GREEN + "-------------------------------------------------------------------");


        customLogger.debug(ChatColor.YELLOW + "Checking for connected players...");
        for (Player pl : Bukkit.getOnlinePlayers()) {
            EvoPlayer evoPlayer = EvoPlayer.getInstanceOf(pl);

            evoPlayer.getDatabaseConnection().registerPlayerOnDatabase();
            EvoPlayer.getInstanceOf(pl).getEconomy().initPlayerEcon();
            EvoPlayer.recreateInstanceOf(pl);

            FastBoard boards = new FastBoard(pl);
            EvoScoreboard.registerPlayer(pl, boards);
            EvoScoreboard.updateScoreboard(pl);

            customLogger.debug(ChatColor.GREEN + "Found " + pl.getDisplayName() + ", linking done");
        }

        if (EvoScoreboard.getPlayerToScoreboardHashmap().size() == 0) {
            customLogger.debug(ChatColor.GREEN + "No player was found, linking session done");
        }

        customLogger.debug(ChatColor.DARK_GREEN + "-------------------------------------------------------------------");


        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::checkSafeInitialization, 40);
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();

        customLogger.debug(ChatColor.GREEN + "Saving player's data on cache, preparing database pull");
        for (Player pl : Bukkit.getOnlinePlayers()) {
            EvoPlayer evoPlayer = EvoPlayer.getInstanceOf(pl);

            evoPlayer.saveCache();
            evoPlayer.getEconomy().saveMoney();
        }
        customLogger.debug(ChatColor.GREEN + "All player's data successfully saved in cache file");

        customLogger.debug(ChatColor.DARK_GREEN + "-------------------------------------------------------------------");

        customLogger.debug(ChatColor.GREEN + "Pulling player's data in database...");
        for (Player pl : Bukkit.getOnlinePlayers()) {
            EvoPlayer evoPlayer = EvoPlayer.getInstanceOf(pl);

            evoPlayer.getDatabaseConnection().saveData();
        }
        customLogger.debug(ChatColor.GREEN + "Done ! ");

        customLogger.debug(ChatColor.DARK_GREEN + "-------------------------------------------------------------------");

        customLogger.debug(ChatColor.GREEN + "Closing database connection and clearing cache files...");
        EvoDatabase.close();
        EvoConfig.clearCache();
        customLogger.debug(ChatColor.GREEN + "Done ! Plugin disabled successfully ");
    }

    public void setCommands() {
        getCommand("exp").setExecutor(new ExpExecutor());
        getCommand("period").setExecutor(new PeriodExecutor());
        getCommand("money").setExecutor(new EconExecutor());
    }

    private void setTabCompleter() {
        getCommand("exp").setTabCompleter(new ExpCompleter());
        getCommand("period").setTabCompleter(new PeriodCompleter());
    }

    public void setListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinQuitManager(), this);
        pm.registerEvents(new BlockManager(), this);
        pm.registerEvents(new CraftManager(), this);
        pm.registerEvents(new DamageManager(), this);
        pm.registerEvents(new InventoryManager(), this);
        pm.registerEvents(new ScoreboardUpdater(this, LPapi), this);
    }

    public static EvoPlugin getInstance() {
        return INSTANCE;
    }

    public PeriodCaster getPeriodCaster() {
        if (periodCaster == null) {
            periodCaster = new PeriodCaster();
        }
        return periodCaster;
    }

    public Logger getCustomLogger() {
        return customLogger;
    }

    public void checkSafeInitialization() {
        if (!safeInitialized) {
            customLogger.log(ChatColor.RED + "Failed to load Evolium, see above this messaage for error report !");
            getPluginLoader().disablePlugin(this);
            return;
        }
        customLogger.log(ChatColor.GREEN + "Plugin enabled !");
    }
}
