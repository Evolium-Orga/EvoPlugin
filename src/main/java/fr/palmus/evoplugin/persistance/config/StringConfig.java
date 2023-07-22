package fr.palmus.evoplugin.persistance.config;

import fr.palmus.evoplugin.EvoPlugin;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class StringConfig {

    private static final EvoPlugin main = EvoPlugin.getInstance();

    private static final Properties config;
    private static final Properties defaults;
    private static File propertiesFile;

    // Load the string configuration file
    public static void load() throws IOException {
        if (StringConfig.propertiesFile == null) {
            StringConfig.propertiesFile = new File(main.getDataFolder(), "strings.properties");
        }

        // If the configuration file doesn't exist, save the default one
        if (!StringConfig.propertiesFile.exists()) {
            main.saveResource("strings.properties", false);
        }

        // Load the configuration file and default properties
        StringConfig.config.load(new InputStreamReader(new FileInputStream(StringConfig.propertiesFile)));
        StringConfig.defaults.load(new InputStreamReader(main.getResource("strings.properties")));
    }

    // Get a string value from the configuration based on the provided key
    public static String getString(final String key) {
        String value = StringConfig.config.getProperty(key);
        if (value != null) {
            return value;
        }
        value = StringConfig.defaults.getProperty(key);
        if (value != null) {
            return value;
        }
        return ChatColor.RED + "No value for: '" + key + "' please report this to an administrator";
    }

    static {
        config = new Properties();
        defaults = new Properties();
    }
}
