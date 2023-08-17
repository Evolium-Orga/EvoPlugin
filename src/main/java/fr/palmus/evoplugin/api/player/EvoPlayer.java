package fr.palmus.evoplugin.api.player;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.api.enumeration.ExpAction;
import fr.palmus.evoplugin.api.enumeration.PeriodAction;
import fr.palmus.evoplugin.api.enumeration.TransferType;
import fr.palmus.evoplugin.enumeration.Period;
import fr.palmus.evoplugin.listeners.custom.PlayerExpChangeEvent;
import fr.palmus.evoplugin.listeners.custom.PlayerMoneyChangeEvent;
import fr.palmus.evoplugin.listeners.custom.PlayerPeriodChangeEvent;
import fr.palmus.evoplugin.period.PeriodCaster;
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
    private final PlayerData playerDatabaseConn;

    /**
     * Constructor for the EvoPlayer class.
     *
     * @param player The player associated with this EvoPlayer.
     */
    private EvoPlayer(Player player) {
        this.player = player;
        this.experience = EvoConfig.getPeriodConfiguration().getInt(this.player.getUniqueId() + ".exp");

        this.playerDatabaseConn = new PlayerData(player);
        this.playerPeriod = PeriodCaster.getEnumPeriodFromInt(EvoConfig.getPeriodConfiguration().getInt(this.player.getUniqueId() + ".period"));

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

        EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".period", PeriodCaster.getPeriodIntFromEnum(playerPeriod));

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
        EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".period", PeriodCaster.getPeriodIntFromEnum(playerPeriod));

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
        return PeriodCaster.getPeriodToString(playerPeriod);
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
     * Retrieves the database connection.
     *
     * @return The DtabaseConnection of the player.
     */
    public PlayerData getDatabaseConnection() {
        return playerDatabaseConn;
    }

    /**
     * Adds money to the player's balance.
     * Triggers a PlayerMoneyChangeEvent with the updated money amount and transfer type.
     *
     * @param money The amount of money to add.
     */
    public void addMoney(int money) {
        main.getVault().depositPlayer(player, money);
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, (int) main.getVault().getBalance(player), TransferType.MONEY, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Subtracts money from the player's balance.
     * Triggers a PlayerMoneyChangeEvent with the updated money amount and transfer type.
     *
     * @param money The amount of money to subtract.
     */
    public void subtractMoney(int money) {
        main.getVault().withdrawPlayer(player, money);
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, (int) main.getVault().getBalance(player), TransferType.MONEY, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Sets the player's money to a specific amount.
     * Triggers a PlayerMoneyChangeEvent with the updated money amount and transfer type.
     *
     * @param money The new money amount.
     */
    public void setMoney(int money) {
        main.getVault().withdrawPlayer(player, main.getVault().getBalance(player));
        main.getVault().depositPlayer(player, money);

        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, TransferType.MONEY, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Adds money to the player's bank balance.
     * Triggers a PlayerMoneyChangeEvent with the updated money amount and transfer type.
     *
     * @param money The amount of money to add to the bank balance.
     */
    private void addBank(int money) {
        main.getVault().bankDeposit(player.getName(), money);

        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, TransferType.BANK, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Subtracts money from the player's bank balance.
     * Triggers a PlayerMoneyChangeEvent with the updated money amount and transfer type.
     *
     * @param money The amount of money to subtract from the bank balance.
     */
    private void subtractBank(int money) {
        main.getVault().bankWithdraw(player.getName(), money);

        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, TransferType.BANK, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Sets the player's bank balance to a specific amount.
     * Triggers a PlayerMoneyChangeEvent with the updated bank balance and transfer type.
     *
     * @param bank The new bank balance amount.
     */
    private void setBank(int bank) {
        main.getVault().bankWithdraw(player.getName(), main.getVault().getBalance(player));
        main.getVault().bankDeposit(player.getName(), bank);

        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, bank, TransferType.BANK, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Returns the amount of money the player has.
     *
     * @return The money amount.
     */
    public int getMoney() {
        return (int) main.getVault().getBalance(player);
    }

    /**
     * Returns the amount of money in the player's bank.
     *
     * @return The bank balance amount.
     */
    private int getBank() {
        return (int) main.getVault().bankBalance(player.getName()).balance;
    }
}