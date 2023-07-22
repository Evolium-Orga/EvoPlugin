package fr.palmus.evoplugin.persistance.mysql;

import fr.palmus.evoplugin.EvoPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class DBCredentials {

    public File file;
    public FileConfiguration cfg;

    private final String host;

    private final String user;

    private final String pass;

    private final String dbName;

    private final String dbType;

    private final int port;

    EvoPlugin main = EvoPlugin.getInstance();

    public DBCredentials() {
        main.getCustomLogger().debug(ChatColor.GREEN + "Starting MYSQL module...");
        if (file == null) {
            file = new File("plugins/EvoPlugin", "mysql.yml");
        }
        if (!file.exists()) {
            main.saveResource("mysql.yml", false);
        }
        cfg = YamlConfiguration.loadConfiguration(file);
        main.getCustomLogger().debug(ChatColor.GREEN + "MYSQL config files generated");

        host = cfg.getString("host");
        user = cfg.getString("user");
        pass = cfg.getString("password");
        dbName = cfg.getString("name");
        dbType = cfg.getString("dbType");
        port = cfg.getInt("port");
    }

    public String toURI() {
        final StringBuilder sb = new StringBuilder();
        sb.append("jdbc:" + dbType + "://")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(dbName);

        return sb.toString();
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
