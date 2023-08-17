package fr.palmus.evoplugin.persistance.config;

import fr.palmus.evoplugin.EvoPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class StringConfig {

    private static final EvoPlugin main = EvoPlugin.getInstance();

    private static final File LANG_FILE = new File(main.getDataFolder(), "lang.yml");

    private static YamlConfiguration langConfig;

    // Load the string configuration file
    public static void load() throws IOException {
        // If the configuration file doesn't exist, save the default one
        if (!StringConfig.LANG_FILE.exists()) {
            main.saveResource("lang.yml", false);
        }

        // Load the configuration file and default properties
        langConfig = YamlConfiguration.loadConfiguration(LANG_FILE);
    }

    // Get a string value from the configuration based on the provided key
    public static String getString(final String key) {
        String value = StringConfig.langConfig.getString(key);
        if (value != null) {
            return value;
        }

        return ChatColor.RED + "No value for: '" + key + "' please report this to an administrator";
    }
}
