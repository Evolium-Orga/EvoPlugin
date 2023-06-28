package fr.palmus.evoplugin.persistance.mysql;

import fr.palmus.evoplugin.EvoPlugin;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private final DBCredentials dbCredentials;

    private Connection connection;

    private boolean connected;

    EvoPlugin main = EvoPlugin.getInstance();

    public DBConnection(DBCredentials dbCredentials) {
        this.dbCredentials = dbCredentials;
        this.connect();
    }

    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Class.forName("org.mariadb.jdbc.Driver");
            this.connection = DriverManager.getConnection(this.dbCredentials.toURI(), this.dbCredentials.getUser(), this.dbCredentials.getPass());
            connected = true;
            main.getCustomLogger().debug(ChatColor.GREEN + "MYSQL connected to " + this.dbCredentials.toURI());
            main.getCustomLogger().debug(ChatColor.GREEN + "MYSQL module successfully loaded");
        } catch (SQLException | ClassNotFoundException e) {
            connected = false;
            main.getCustomLogger().debug(ChatColor.RED + "Failed to bind to mysql database (" + this.dbCredentials.toURI() + "), maybe the credentials are incorrect. FATAL");
            e.printStackTrace();
            main.getPluginLoader().disablePlugin(main);
        }
    }

    public void close() throws SQLException {
        if (!this.connection.isClosed()) {
            this.connection.close();
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.connection != null) {
            if (!this.connection.isClosed()) {
                return this.connection;
            }
        }
        connect();
        return this.connection;
    }

    public boolean isConnected() {
        return connected;
    }
}
