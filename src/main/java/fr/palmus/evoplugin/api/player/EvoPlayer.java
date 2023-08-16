package fr.palmus.evoplugin.api.player;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.economy.EvoEconomy;
import fr.palmus.evoplugin.enumeration.Period;
import fr.palmus.evoplugin.listeners.custom.PlayerExpChangeEvent;
import fr.palmus.evoplugin.listeners.custom.PlayerPeriodChangeEvent;
import fr.palmus.evoplugin.persistance.config.EvoConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Represents a player in the EvoPlugin system.
 */
public class EvoPlayer {

    private static final HashMap<Player, EvoPlayer> playerToEvoplayerHashmap = new HashMap<>();

    private final EvoPlugin main = EvoPlugin.getInstance();

    private final Player player;

    private int experience;

    private Period playerPeriod;

    private final EvoEconomy playerEconomy;
    private final PlayerData playerDatabaseConn;

    /**
     * Constructor for the EvoPlayer class.
     *
     * @param player The player associated with this EvoPlayer.
     */
    private EvoPlayer(Player player) {
        this.player = player;
        this.experience = EvoConfig.getPeriodConfiguration().getInt(this.player.getUniqueId() + ".exp");

        this.playerEconomy = new EvoEconomy(player);
        this.playerDatabaseConn = new PlayerData(player);
        this.playerPeriod = main.getPeriodCaster().getEnumPeriodFromInt(EvoConfig.getPeriodConfiguration().getInt(this.player.getUniqueId() + ".period"));

        playerToEvoplayerHashmap.put(player, this);
    }

    /**
     * Retrieves the instance of EvoPlayer associated with the specified player.
     * If the instance does not exist, it creates a new one.
     *
     * @param player The player to get the EvoPlayer instance for.
     * @return The EvoPlayer instance associated with the player.
     */
    public static EvoPlayer getInstanceOf(Player player) {
        if (playerToEvoplayerHashmap.containsKey(player)) {
            return playerToEvoplayerHashmap.get(player);
        } else {
            return new EvoPlayer(player);
        }
    }

    /**
     * Recreate the instance of an EvoPlayer associated with the specified player.
     * If the instance does not exist, it creates a new one.
     * This function is used to update player data after a database registration
     *
     * @param player The player to get the EvoPlayer instance for.
     * @return The EvoPlayer instance associated with the player.
     */
    public static EvoPlayer recreateInstanceOf(Player player) {
        if (playerToEvoplayerHashmap.containsKey(player)) {
            playerToEvoplayerHashmap.remove(player);
            return new EvoPlayer(player);
        }
        return getInstanceOf(player);
    }

    /**
     * Retrieves the Bukkit Player object associated with this EvoPlayer.
     *
     * @return The Bukkit Player object.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Adds experience points to the player.
     *
     * @param exp The number of experience points to add.
     */
    public void addExp(int exp) {
        experience = experience + exp;

        PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, experience, ExpAction.ADD, main); // Default to add

        if (exp < 0) {
            event = new PlayerExpChangeEvent(player, experience, ExpAction.SUBTRACT, main); // if exp < 0 event action == Subtract
        }

        if (experience > main.getConfig().getInt("max_player_exp")) {
            experience = main.getConfig().getInt("max_player_exp");
        }

        if (experience < 0) {
            experience = 0;
        }

        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Sets the experience points for the player.
     *
     * @param exp The new value for the experience points.
     */
    public void setExp(int exp) {
        int oldExp = experience;
        experience = exp;

        PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, experience, ExpAction.ADD, main); // Default to add

        if (experience < oldExp) {
            event = new PlayerExpChangeEvent(player, experience, ExpAction.SUBTRACT, main);
        }

        if (experience < 0) {
            experience = 0;
        }

        if (experience > main.getConfig().getInt("max_player_exp")) {
            experience = main.getConfig().getInt("max_player_exp");
        }

        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Resets the experience points for the player to 0.
     */
    public void resetExp() {
        experience = 0;

        PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, experience, ExpAction.RESET, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Upgrades the player's period
     */
    public void periodUpgrade() {
        EvoConfig.savePeriodConfig();

        playerPeriod = Period.getNextPeriod(playerPeriod);

        EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".period", main.getPeriodCaster().getPeriodIntFromEnum(playerPeriod));

        EvoConfig.savePeriodConfig();

        PlayerPeriodChangeEvent event = new PlayerPeriodChangeEvent(getPlayer(), EvoConfig.getPeriodConfiguration().getInt(player.getUniqueId() + ".period"), PeriodAction.UPGRADE, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Resets the player's period to the initial period.
     */
    public void resetPeriod() {
        EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".period", 0);
        EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".multiplier", 0);
        EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".exp", 0);

        experience = 0;
        playerPeriod = Period.PREHISTOIRE;

        try {
            EvoConfig.getPeriodConfiguration().save(EvoConfig.getPeriodFile());
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to save period of player '" + player.getDisplayName() + "' shutting down the server");
            Bukkit.shutdown();
        }

        PlayerPeriodChangeEvent event = new PlayerPeriodChangeEvent(getPlayer(), EvoConfig.getPeriodConfiguration().getInt(player.getUniqueId() + ".period"), PeriodAction.RESET, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Downgrades the player's period to the previous level.
     */
    public void periodDowngrade() {
        playerPeriod = Period.getBelowPeriod(playerPeriod);
        EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".period", main.getPeriodCaster().getPeriodIntFromEnum(playerPeriod));

        EvoConfig.savePeriodConfig();

        PlayerPeriodChangeEvent event = new PlayerPeriodChangeEvent(getPlayer(), EvoConfig.getPeriodConfiguration().getInt(player.getUniqueId() + ".period"), PeriodAction.DOWNGRADE, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Saves the player's experience points to the configuration file.
     */
    public void saveCache() {
        EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".exp", getExp());

        try {
            EvoConfig.getPeriodConfiguration().save(EvoConfig.getPeriodFile());
        } catch (IOException e) {
            e.printStackTrace();
            main.getCustomLogger().log(ChatColor.RED + "Failed to save exp of player '" + player.getDisplayName() + "' shutting down the server");
            Bukkit.shutdown();
        }

        try {
            EvoConfig.getPeriodConfiguration().save(EvoConfig.getPeriodFile());
        } catch (IOException | NullPointerException e) {
            main.getCustomLogger().log(ChatColor.RED + "FAILED TO SAVE CACHE FILE ON DATABASE, EVERY DATA EARN DURING THIS SESSION IS LOST");
        }
    }

    /**
     * Retrieves the player's experience points.
     *
     * @return The experience points.
     */
    public int getExp() {
        return experience;
    }

    /**
     * Retrieves the player's stringed period.
     *
     * @return The period stringed.
     */
    public String getStringedPlayerPeriod() {
        return main.getPeriodCaster().getPeriodToString(playerPeriod);
    }

    /**
     * Enumeration for the period actions (upgrade, downgrade, reset).
     */
    public enum PeriodAction {
        UPGRADE, DOWNGRADE, RESET
    }

    /**
     * Enumeration for the experience actions (add, subtract, reset).
     */
    public enum ExpAction {
        ADD, SUBTRACT, RESET
    }

    /**
     * Retrieves the player's current period.
     *
     * @return The player's period.
     */
    public Period getPlayerPeriod() {
        return playerPeriod;
    }

    /**
     * Retrieves the player's economy instance to manage his money.
     *
     * @return The PlayerEconomy instance of the player.
     */
    public EvoEconomy getEconomy() {
        return playerEconomy;
    }

    public PlayerData getDatabaseConnection() {
        return playerDatabaseConn;
    }
}