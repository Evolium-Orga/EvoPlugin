package fr.palmus.evoplugin.economy;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.listeners.custom.PlayerMoneyChangeEvent;
import fr.palmus.evoplugin.persistance.config.EvoConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;


/**
 * EvoEconomy class handles the economy system for players.
 * It manages player money and bank balance.
 */
public class EvoEconomy {

    Player player;

    int money;

    int bank;

    FileConfiguration economyConfig;

    private final EvoPlugin main = EvoPlugin.getInstance();

    /**
     * Constructor for the EvoEconomy class.
     * Initializes the player's economy data by loading it from the configuration file.
     *
     * @param pl The player associated with this economy instance.
     */
    public EvoEconomy(Player pl) {
        this.player = pl;
        economyConfig = EvoConfig.getEconomyConfiguration();

        this.bank = economyConfig.getInt(player.getUniqueId() + ".bank");
        this.money = economyConfig.getInt(player.getUniqueId() + ".money");
    }

    /**
     * Initializes the economy data for the player.
     * If the player's data does not exist in the configuration file, it sets their money and bank balance to 0.
     * If there's an error saving the data, it logs an error message and shuts down the server.
     */
    public void initPlayerEcon() {

        if (!economyConfig.contains(player.getDisplayName())) {
            economyConfig.set(player.getUniqueId() + ".money", 0);
            economyConfig.set(player.getUniqueId() + ".bank", 0);

            try {
                economyConfig.save(EvoConfig.getEconomyFile());
            } catch (IOException e) {
                e.printStackTrace();
                main.getCustomLogger().log(ChatColor.RED + "Failed to save money of player '" + player.getDisplayName() + "' shutting down the server");
                Bukkit.shutdown();
            }
        }
    }

    /**
     * Adds money to the player's balance.
     * Triggers a PlayerMoneyChangeEvent with the updated money amount and transfer type.
     *
     * @param money The amount of money to add.
     */
    public void addMoney(int money) {
        this.money += money;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.MONEY, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Subtracts money from the player's balance.
     * Triggers a PlayerMoneyChangeEvent with the updated money amount and transfer type.
     *
     * @param money The amount of money to subtract.
     */
    public void subtractMoney(int money) {
        this.money -= money;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.MONEY, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Sets the player's money to a specific amount.
     * Triggers a PlayerMoneyChangeEvent with the updated money amount and transfer type.
     *
     * @param money The new money amount.
     */
    public void setMoney(int money) {
        this.money = money;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.MONEY, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Adds money to the player's bank balance.
     * Triggers a PlayerMoneyChangeEvent with the updated money amount and transfer type.
     *
     * @param money The amount of money to add to the bank balance.
     */
    public void addBank(int money) {
        this.money += money;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.BANK, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Subtracts money from the player's bank balance.
     * Triggers a PlayerMoneyChangeEvent with the updated money amount and transfer type.
     *
     * @param money The amount of money to subtract from the bank balance.
     */
    public void subtractBank(int money) {
        this.money -= money;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.BANK, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Sets the player's bank balance to a specific amount.
     * Triggers a PlayerMoneyChangeEvent with the updated bank balance and transfer type.
     *
     * @param bank The new bank balance amount.
     */
    public void setBank(int bank) {
        this.bank = bank;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.BANK, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Saves the player's money to the configuration file.
     */
    public void saveMoney() {
        economyConfig.set(player.getDisplayName() + ".money", money);
    }

    /**
     * Returns the player associated with this economy instance.
     *
     * @return The player object.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the amount of money the player has.
     *
     * @return The money amount.
     */
    public int getMoney() {
        return money;
    }

    /**
     * Returns the amount of money in the player's bank.
     *
     * @return The bank balance amount.
     */
    public int getBank() {
        return bank;
    }

    /**
     * Enumeration representing the transfer type for the PlayerMoneyChangeEvent.
     * It can be either BANK or MONEY.
     */
    public enum TransferType {
        BANK, MONEY
    }
}
