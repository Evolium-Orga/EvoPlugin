package fr.palmus.evoplugin.api.player;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.persistance.mysql.DBConnection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.*;

/**
 * Represents a custom player class that manages player data and interactions.
 * Cache is a reference to the periodFileConfiguration
 */
public class PlayerData {
    private final EvoPlugin main;

    /**
     * Constructs a new CustomPlayer object.
     *
     * @param main The main EvoPlugin instance.
     */
    public PlayerData(EvoPlugin main) {
        this.main = main;
    }

    /**
     * Initializes the player by updating the player cache and retrieving data from the database.
     *
     * @param pl The player to initialize.
     */
    public void initPlayer(Player pl) {
        // If the player's not in the cache we put him in
        if (main.getPeriodConfigurationFile().get(pl.getUniqueId() + ".period") == null) {
            updatePlayerCache(pl, null);
        }

        final DBConnection database = main.getDatabaseManager().getDatabase();
        try {
            final Connection connection = database.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid, period, exp, multiplier, rank FROM player_data WHERE uuid = \"" + pl.getUniqueId() + "\"");

            preparedStatement.setString(1, pl.getUniqueId().toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            // resultSet.next() = if player is on the database
            if (resultSet.next()) {
                updatePlayerCache(pl, resultSet);
            } else {
                createUserData(connection, pl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            main.getCustomLogger().log(ChatColor.RED + "ERROR ON INITIALIZING DATA SAVING OF PLAYER " + pl.getDisplayName());
            pl.kickPlayer(ChatColor.RED + "Failed to load data from the database, please report this message to a staff member");
        }

    }

    private void createUserData(Connection connection, Player pl) {
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO player_data VALUES (?, ?, ?, ?, ?, ?, ?)");
            final long time = System.currentTimeMillis();
            preparedStatement.setString(1, pl.getUniqueId().toString());
            preparedStatement.setInt(2, main.getPeriodConfigurationFile().getInt(pl.getUniqueId() + ".period"));
            preparedStatement.setString(3, String.valueOf(main.getPeriodConfigurationFile().getInt(pl.getUniqueId() + ".exp")));
            preparedStatement.setInt(4, main.getPeriodConfigurationFile().getInt(pl.getUniqueId() + ".multiplier"));
            preparedStatement.setInt(6, main.getPeriodConfigurationFile().getInt(pl.getUniqueId() + ".rank"));
            preparedStatement.setTimestamp(7, new Timestamp(time));
            preparedStatement.setTimestamp(8, new Timestamp(time));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            main.getCustomLogger().log(ChatColor.RED + "ERROR on creating user data");
        }
    }

    /**
     * Updates the player cache with the data from the database result set.
     *
     * @param pl  The player to update the cache for.
     * @param set The result set containing the player data from the database.
     */
    public void updatePlayerCache(Player pl, ResultSet set) {

        try {
            if (set == null) {
                main.getPeriodConfigurationFile().set(pl.getUniqueId() + ".period", 0);
                main.getPeriodConfigurationFile().set(pl.getUniqueId() + ".exp", 0);
                main.getPeriodConfigurationFile().set(pl.getUniqueId() + ".multiplier", 0);
                main.getPeriodConfigurationFile().set(pl.getUniqueId() + ".rank", 1);
            } else {
                main.getPeriodConfigurationFile().set(pl.getUniqueId() + ".period", set.getInt("period"));
                main.getPeriodConfigurationFile().set(pl.getUniqueId() + ".exp", Integer.parseInt(set.getString("exp")));
                main.getPeriodConfigurationFile().set(pl.getUniqueId() + ".multiplier", set.getInt("multiplier"));
                main.getPeriodConfigurationFile().set(pl.getUniqueId() + ".rank", set.getInt("rank"));
            }
            main.getPeriodConfigurationFile().save(main.periodConfigFile);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            main.getCustomLogger().log(ChatColor.RED + "ERROR on updating user data to the cache");
        }
    }

    /**
     * Saves the player data to the database.
     *
     * @param pl         The player to save the data for.
     * @param connection The database connection.
     */
    public void saveData(Player pl, Connection connection) {
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE player_data SET period = ?, exp = ?, multiplier = ?, rank = ?, updated_at = ? WHERE uuid = ?");

            final long time = System.currentTimeMillis();
            preparedStatement.setInt(1, main.getPeriodConfigurationFile().getInt(pl.getUniqueId() + ".period"));
            preparedStatement.setString(2, String.valueOf(main.getPeriodConfigurationFile().getInt(pl.getUniqueId() + ".exp")));
            preparedStatement.setInt(3, main.getPeriodConfigurationFile().getInt(pl.getUniqueId() + ".multiplier"));
            preparedStatement.setInt(5, main.getPeriodConfigurationFile().getInt(pl.getUniqueId() + ".rank"));
            preparedStatement.setTimestamp(6, new Timestamp(time));
            preparedStatement.setString(7, pl.getUniqueId().toString());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}