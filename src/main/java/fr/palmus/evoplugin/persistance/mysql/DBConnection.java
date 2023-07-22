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
            main.safeInitialized = false;
        }
    }

    public void close() {
        try {
            if (!this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException | NullPointerException e) {
            main.getCustomLogger().debug(ChatColor.RED + "Failed to close connection of the mysql database (" + this.dbCredentials.toURI() + "), the initialization of the plugin has probably failed. FATAL");
        }
    }

    public Connection getRawConnection() {
        if (this.connection != null) {
            try {
                if (!this.connection.isClosed()) {
                    return this.connection;
                }
            } catch (SQLException e) {
                main.getCustomLogger().log(ChatColor.RED + "Failed to bind to access mysql database (" + this.dbCredentials.toURI() + ").");
            }
        }
        connect();
        return this.connection;
    }

    public boolean isConnected() {
        return connected;
    }
}
