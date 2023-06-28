package fr.palmus.evoplugin.persistance.mysql;

import fr.palmus.evoplugin.EvoPlugin;
import org.bukkit.ChatColor;

import java.sql.SQLException;

public class DatabaseManager {

    private final DBConnection periodConnection;

    EvoPlugin main = EvoPlugin.getInstance();

    public DatabaseManager() {
        this.periodConnection = new DBConnection(new DBCredentials());
    }

    public void close() {
        try {
            this.periodConnection.close();
        } catch (SQLException e) {
            main.getCustomLogger().log(ChatColor.RED + "Failed to bind to mysql database, maybe the credentials are incorrect. FATAL");
            main.getPluginLoader().disablePlugin(main);
        }
    }

    public DBConnection getDatabase() {
        return periodConnection;
    }

}
