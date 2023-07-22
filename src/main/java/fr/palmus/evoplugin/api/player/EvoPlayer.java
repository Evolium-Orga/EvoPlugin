package fr.palmus.evoplugin.api.player;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.api.messages.Message;
import fr.palmus.evoplugin.api.messages.PrefixLevel;
import fr.palmus.evoplugin.economy.EvoEconomy;
import fr.palmus.evoplugin.enumeration.Period;
import fr.palmus.evoplugin.listeners.custom.PlayerExpChangeEvent;
import fr.palmus.evoplugin.listeners.custom.PlayerPeriodChangeEvent;
import fr.palmus.evoplugin.persistance.config.EvoConfig;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Represents a player in the EvoPlugin system.
 * A rank refers to the section of the period the player's in, for example,
 * If a player's period is Antic 3 the rank is 3, and the period Antic
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
     * Retrieves the rank value of the player.
     *
     * @return The rank value.
     */
    public int getRank() {
        if (EvoConfig.getPeriodConfiguration().get(player.getUniqueId() + ".rank") == null) {
            return 0;
        }

        return EvoConfig.getPeriodConfiguration().getInt(player.getUniqueId() + ".rank");
    }

    /**
     * Adds experience points to the player.
     *
     * @param exp The number of experience points to add.
     */
    public void addExp(int exp) {
        experience = experience + exp;

        PlayerExpChangeEvent event;

        if (exp > 0) {
            if (main.getPeriodCaster().getPeriodExpLimit(getRank()) <= experience) {
                periodUpgrade();
                int oldExp = experience;
                experience = 0;
                addExp(oldExp - main.getPeriodCaster().getPeriodExpLimit(getRank() - 1));
            }
        }

        if (exp < 0) {
            event = new PlayerExpChangeEvent(player, experience, ExpAction.SUBTRACT, main);

            if (0 >= experience) {
                periodDowngrade();
                int oldExp = experience;
                experience = main.getPeriodCaster().getPeriodExpLimit(getRank() - 1);
                addExp(oldExp);
            }
        }else {
            event = new PlayerExpChangeEvent(player, experience, ExpAction.ADD, main);
        }

        Bukkit.getServer().getPluginManager().callEvent(event);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5§kII§r §d+§5" + exp + " §davancement: §5" + getExp() + "§7/§5" + main.getPeriodCaster().getFormattedPeriodExpLimit(getRank()) + " §r§5§kII"));
    }

    /**
     * Sets the experience points for the player.
     *
     * @param exp The new value for the experience points.
     */
    public void setExp(int exp) {
        int oldExp = experience;
        experience = exp;

        if (exp > oldExp) {
            PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, experience, ExpAction.ADD, main);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
        if (exp < oldExp) {
            PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, experience, ExpAction.SUBTRACT, main);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
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
     * Upgrades the player's period if the rank is 3.
     * Otherwise, triggers a period upgrade event.
     */
    public void periodUpgrade() {
        EvoConfig.savePeriodConfig();

        if (getRank() == 3) {
            playerPeriod = Period.getNextPeriod(playerPeriod);

            EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".period", main.getPeriodCaster().getPeriodIntFromEnum(playerPeriod));
            EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".rank", 1);
        } else {
            EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".rank", getRank() + 1);
        }

        EvoConfig.savePeriodConfig();

        playPlayerUpgradeAnimation();
        PlayerPeriodChangeEvent event = new PlayerPeriodChangeEvent(getPlayer(), EvoConfig.getPeriodConfiguration().getInt(player.getUniqueId() + ".period"), getRank(), PeriodAction.UPGRADE, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Resets the player's period to the initial period.
     */
    public void resetPeriod() {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cRESET §2: §a" + getEntirePeriodStyle() + "§2 >> §a" + main.getPeriodCaster().getPeriodToString(Period.PREHISTOIRE) + " I"));

        EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".rank", 1);
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

        PlayerPeriodChangeEvent event = new PlayerPeriodChangeEvent(getPlayer(), EvoConfig.getPeriodConfiguration().getInt(player.getUniqueId() + ".period"), getRank(), PeriodAction.RESET, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Downgrades the player's period to the previous level.
     */
    public void periodDowngrade() {
        if (getRank() == 1) {
            playerPeriod = Period.getBelowPeriod(playerPeriod);
            EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".period", main.getPeriodCaster().getPeriodIntFromEnum(playerPeriod));
            EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".rank", 3);
        } else {
            EvoConfig.getPeriodConfiguration().set(player.getUniqueId() + ".rank", getRank() - 1);
        }

        EvoConfig.savePeriodConfig();

        PlayerPeriodChangeEvent event = new PlayerPeriodChangeEvent(getPlayer(), EvoConfig.getPeriodConfiguration().getInt(player.getUniqueId() + ".period"), getRank(), PeriodAction.DOWNGRADE, main);
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
     * Retrieves the player's entire period style.
     *
     * @return The entire period style.
     */
    public String getEntirePeriodStyle() {
        return main.getPeriodCaster().getPeriodToString(playerPeriod) + " " + main.getPeriodCaster().getRankToString(getRank());
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
     * Plays the player's upgrade animation and handles period and economy updates.
     *
     * @deprecated This method contains a lot of redundant code and needs to be refactored.
     */
    @Deprecated
    private void playPlayerUpgradeAnimation() {
        if (getRank() == 1) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§2UPGRADE §2: §a" + main.getPeriodCaster().getPeriodToString(Period.getBelowPeriod(playerPeriod)) + " III§2 >> §a" + getEntirePeriodStyle()));
            getEconomy().addMoney(10000);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§2UPGRADE §2: §a" + getEntirePeriodStyle().substring(0, getEntirePeriodStyle().length() - 1) + "§2 >> §a" + getEntirePeriodStyle()));
            getEconomy().addMoney(5000);
        }

        player.sendTitle("§a" + getEntirePeriodStyle(), "§2---------------", 20, 60, 20);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);

        //TODO: add the given amount of money in the message
        Message.sendPlayerMessage(player, PrefixLevel.GOOD, "Vous venez de passer en " + getEntirePeriodStyle() + " !");
    }

    @Deprecated
    public String getProgressBar() {
        int placement = this.getExp() * 20 / main.getPeriodCaster().getPeriodExpLimit(this.getRank());
        StringBuilder str = new StringBuilder("||||||||||||||||||||");
        str.insert(placement, "§e");
        str.insert(0, "§6");
        return str.toString();
    }

    @Deprecated
    public int getProgressPercent() {
        return this.getExp() * 100 / main.getPeriodCaster().getPeriodExpLimit(this.getRank());
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