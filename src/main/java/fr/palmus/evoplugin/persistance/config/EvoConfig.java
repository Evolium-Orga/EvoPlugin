package fr.palmus.evoplugin.persistance.config;

import fr.palmus.evoplugin.EvoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class EvoConfig {

    private static final File PERIOD_FILE = new File("plugins/EvoPlugin", "period.yml");
    private static final File ECONOMY_FILE = new File("plugins/EvoPlugin", "economy.yml");

    private static FileConfiguration periodConfiguration;
    private static FileConfiguration economyConfiguration;

    public static void initializeConfigFile() {
        periodConfiguration = YamlConfiguration.loadConfiguration(PERIOD_FILE);
        economyConfiguration = YamlConfiguration.loadConfiguration(ECONOMY_FILE);
    }

    public static File getPeriodFile() {
        return PERIOD_FILE;
    }

    public static File getEconomyFile() {
        return ECONOMY_FILE;
    }

    public static FileConfiguration getPeriodConfiguration() {
        return periodConfiguration;
    }

    public static FileConfiguration getEconomyConfiguration() {
        return economyConfiguration;
    }

    public static void savePeriodConfig() {
        try {
            EvoConfig.getPeriodConfiguration().save(EvoConfig.getPeriodFile());
        } catch (IOException e) {
            e.printStackTrace();
            EvoPlugin.getInstance().getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to save period's config, shutting down the server");
            Bukkit.shutdown();
        }
    }

    public static void clearCache() {
        PERIOD_FILE.delete();
        ECONOMY_FILE.delete();
    }
}
