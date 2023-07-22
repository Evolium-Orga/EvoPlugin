package fr.palmus.evoplugin.persistance.mysql;

import fr.palmus.evoplugin.EvoPlugin;
import org.bukkit.ChatColor;

import java.sql.*;

public class EvoDatabase {

    private static DBConnection databaseConnection;

    private static final EvoPlugin main = EvoPlugin.getInstance();

    public static void initializeDatabaseConnection() {
        databaseConnection = new DBConnection(new DBCredentials());
    }

    /**
     * Executes an SQL request and returns a result set if requested.
     *
     * @param sqlRequest      The SQL query to be executed.
     * @param returnResultSet Set this to 'true' if you expect the SQL query to return a result set.
     *                        Set this to 'false' if the SQL query does not return a result set.
     * @return If 'returnResultSet' is true and the SQL query returns a result set with at least one row,
     *         the result set containing the data is returned.
     *         If the query returns an empty result set, 'null' is returned.
     *         If 'returnResultSet' is false and the SQL query is executed successfully
     *         (e.g., an UPDATE, DELETE, or INSERT query), 'null' is returned. If an error occurs during
     *         the execution of the SQL query, 'null' is returned.
     *         PS: Some SQL request such as DROP, INSERT or UPDATE does not return a result set, if you execute
     *         those with setting returnResultSet to true an error will be thrown
     */
    public static ResultSet sendSqlRequest(String sqlRequest, boolean returnResultSet) {
        try {
            final Connection connection = getDatabaseConnection().getRawConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest);

            ResultSet resultSet;
            if(returnResultSet) {
                resultSet = preparedStatement.executeQuery();
            }else {
                preparedStatement.executeUpdate();
                return null;
            }

            // resultSet.next() = if player is on the database
            if (resultSet.next()) {
                return resultSet;
            }
            return null;
        } catch (SQLException e) {
            main.getCustomLogger().log(ChatColor.RED + "CANNOT EXECUTE SQL REQUEST: " + sqlRequest);

            e.printStackTrace();
            return null;
        }
    }

    /**
     * Resets the database by dropping the "player_data" database and recreating the table if it doesn't exist.
     * This function does not return any result set.
     */
    public static void resetDatabase() {
        // Drop the "player_data" database (assuming it exists).
        sendSqlRequest("DELETE FROM player_data", false);

        // Check if the "player_data" table exists and create it if it doesn't.
        if (!tableExist("player_data")) {
            sendSqlRequest("CREATE TABLE player_data (" +
                    "uuid VARCHAR(255)," +
                    "period INT(64)," +
                    "exp VARCHAR(255)," +
                    "multiplier INT(64)," +
                    "rank INT(64)," +
                    "updated_at DATE," +
                    "created_at DATE)", true);
        }
    }

    /**
     * Checks if a given table exists in the database.
     *
     * @param tableName The name of the table to check for existence.
     * @return 'true' if the table exists, 'false' otherwise.
     * @throws RuntimeException if an error occurs while checking for the table's existence.
     */
    public static boolean tableExist(String tableName) {
        boolean tExists = false;
        final Connection connection = getDatabaseConnection().getRawConnection();

        try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName, null)) {
            while (rs.next()) {
                String tName = rs.getString("TABLE_NAME");
                if (tName != null && tName.equals(tableName)) {
                    tExists = true;
                    break;
                }
            }
        } catch (SQLException e) {
            main.getCustomLogger().log(ChatColor.RED + "FAILED TO CHECK IF TABLE " + tableName + " EXIST ON DATABASE");
            throw new RuntimeException(e);
        }
        return tExists;
    }

    /**
     * Closes the database connection. This function does not return any result set.
     */
    public static void close() {
        databaseConnection.close();
    }

    /**
     * Retrieves the database connection.
     *
     * @return The DBConnection object representing the database connection.
     */
    public static DBConnection getDatabaseConnection() {
        return databaseConnection;
    }
}
