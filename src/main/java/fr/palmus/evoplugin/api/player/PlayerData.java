package fr.palmus.evoplugin.api.player;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.persistance.config.EvoConfig;
import fr.palmus.evoplugin.persistance.mysql.DBConnection;
import fr.palmus.evoplugin.persistance.mysql.EvoDatabase;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.*;

/**
 * Represents a custom player class that manages player data and interactions.
 * Cache is a reference to the periodFileConfiguration
 */
public class PlayerData {
    private final EvoPlugin main = EvoPlugin.getInstance();

    private final Player player;

    private final Connection connection;
    
    private final FileConfiguration periodConfig;

    /**
     * Constructs a new CustomPlayer object.
     *
     * @param player The player on whom the database action will be executed.
     */
    public PlayerData(Player player) {
        this.player = player;
        this.connection = EvoDatabase.getDatabaseConnection().getRawConnection();
        periodConfig = EvoConfig.getPeriodConfiguration();
    }

    /**
     * Initializes the player by updating the player cache and retrieving data from the database.
     */
    public void registerPlayerOnDatabase() {

        if(!EvoDatabase.tableExist("player_data")) {
            String createTableRequest = "CREATE TABLE player_data (" +
                    "uuid VARCHAR(255)," +
                    "period INT(64)," +
                    "exp VARCHAR(255)," +
                    "multiplier INT(64)," +
                    "updated_at DATE," +
                    "created_at DATE)";

            EvoDatabase.sendSqlRequest(createTableRequest, false);
        }

        // If the player's not in the cache, we put him in
        if (periodConfig.get(player.getUniqueId() + ".period") == null) {
            updatePlayerCache(null);
        }

        final DBConnection database = EvoDatabase.getDatabaseConnection();

        try {
            final Connection connection = database.getRawConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid, period, exp, multiplier FROM player_data WHERE uuid = \"" + player.getUniqueId() + "\"");

            ResultSet resultSet = preparedStatement.executeQuery();

            // resultSet.next() = if player is on the database
            if (resultSet.next()) {
                updatePlayerCache(resultSet);
            } else {
                createUserData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            main.getCustomLogger().log(ChatColor.RED + "ERROR ON REGISTERING " + player.getDisplayName() + " IN THE DATABASE");
            player.kickPlayer(ChatColor.RED + "Failed to load data from the database, please report this message to a staff member");
        }

    }

    private void createUserData() {
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO player_data VALUES (?, ?, ?, ?, ?, ?)");
            final long time = System.currentTimeMillis();
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setInt(2, periodConfig.getInt(player.getUniqueId() + ".period"));
            preparedStatement.setString(3, String.valueOf(periodConfig.getInt(player.getUniqueId() + ".exp")));
            preparedStatement.setInt(4, periodConfig.getInt(player.getUniqueId() + ".multiplier"));
            preparedStatement.setTimestamp(5, new Timestamp(time));
            preparedStatement.setTimestamp(6, new Timestamp(time));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            main.getCustomLogger().log(ChatColor.RED + "ERROR on creating user data");
        }
    }

    /**
     * Updates the player cache with the data from the database result set.
     *
     * @param set The result set containing the player data from the database.
     */
    public void updatePlayerCache(ResultSet set) {

        try {
            if (set == null) {
                periodConfig.set(player.getUniqueId() + ".period", 0);
                periodConfig.set(player.getUniqueId() + ".exp", 0);
                periodConfig.set(player.getUniqueId() + ".multiplier", 0);
            } else {
                periodConfig.set(player.getUniqueId() + ".period", set.getInt("period"));
                periodConfig.set(player.getUniqueId() + ".exp", Integer.parseInt(set.getString("exp")));
                periodConfig.set(player.getUniqueId() + ".multiplier", set.getInt("multiplier"));
            }
            periodConfig.save(EvoConfig.getPeriodFile());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            main.getCustomLogger().log(ChatColor.RED + "ERROR on updating user data to the cache");
        }
    }

    /**
     * Saves the player data to the database.
     */
    public void saveData() {
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE player_data SET period = ?, exp = ?, multiplier = ?, updated_at = ? WHERE uuid = ?");

            final long time = System.currentTimeMillis();
            preparedStatement.setInt(1, periodConfig.getInt(player.getUniqueId() + ".period"));
            preparedStatement.setString(2, String.valueOf(periodConfig.getInt(player.getUniqueId() + ".exp")));
            preparedStatement.setInt(3, periodConfig.getInt(player.getUniqueId() + ".multiplier"));
            preparedStatement.setTimestamp(4, new Timestamp(time));
            preparedStatement.setString(5, player.getUniqueId().toString());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}