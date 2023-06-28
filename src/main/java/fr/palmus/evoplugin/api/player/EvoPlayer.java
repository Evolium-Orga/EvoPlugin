package fr.palmus.evoplugin.api.player;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.api.messages.Message;
import fr.palmus.evoplugin.api.messages.PrefixLevel;
import fr.palmus.evoplugin.economy.PlayerEconomy;
import fr.palmus.evoplugin.enumeration.Period;
import fr.palmus.evoplugin.listeners.custom.PlayerExpChangeEvent;
import fr.palmus.evoplugin.listeners.custom.PlayerPeriodChangeEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Represents a player in the EvoPlugin system.
 * A rank refer to the section of the period the player's in for example:
 * If a player's period is Antic 3 the rank is 3 and the period Antic
 */
public class EvoPlayer {

    private static final HashMap<Player, EvoPlayer> playerToEvoplayerHashmap = new HashMap<>();

    private final EvoPlugin main = EvoPlugin.getInstance();

    private final Player player;

    private int experience;

    private Period playerPeriod;

    private final PlayerEconomy playerEconomy;

    /**
     * Constructor for the EvoPlayer class.
     * @param pl The player associated with this EvoPlayer.
     */
    private EvoPlayer(Player pl) {
        this.player = pl;
        this.experience = main.getPeriodConfigurationFile().getInt(player.getUniqueId() + ".exp");

        playerEconomy = main.getEconomyModule().getPlayerEcon(player);
        playerPeriod = main.getPeriodCaster().getEnumPeriodFromInt(main.getPeriodConfigurationFile().getInt(player.getUniqueId() + ".period"));

        playerToEvoplayerHashmap.put(pl, this);
    }

    /**
     * Retrieves the instance of EvoPlayer associated with the specified player.
     * If the instance does not exist, it creates a new one.
     * @param pl The player to get the EvoPlayer instance for.
     * @return The EvoPlayer instance associated with the player.
     */
    public static EvoPlayer getInstanceOf(Player pl) {
        if(playerToEvoplayerHashmap.containsKey(pl)){
            return playerToEvoplayerHashmap.get(pl);
        }else {
            return new EvoPlayer(pl);
        }
    }

    /**
     * Retrieves the Bukkit Player object associated with this EvoPlayer.
     * @return The Bukkit Player object.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Adds experience points to the player.
     * @param exp The amount of experience points to add.
     */
    public void addExp(int exp) {
        experience = experience + exp;

        PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, experience, ExpAction.ADD, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§5§kII§r §d+§5" + exp + " §davancement: §5" + getExp() + "§7/§5" + main.getPeriodCaster().getFormattedPeriodExpLimit(getRank()) + " §r§5§kII"));
    }

    /**
     * Sets the experience points for the player.
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
        saveExp();
    }

    /**
     * Resets the experience points for the player to 0.
     */
    public void resetExp() {
        experience = 0;
        saveExp();
        PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, experience, ExpAction.RESET, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Upgrades the player's period if the rank is 3.
     * Otherwise, triggers a period upgrade event.
     */
    public void periodUpgrade() {
        if (getRank() == 3) {
            main.getPeriodConfigurationFile().set(player.getUniqueId() + ".period", main.getPeriodCaster().getIntPeriodFromEnum(playerPeriod) + 1);
            main.getPeriodConfigurationFile().set(player.getUniqueId() + ".rank", 1);
            return;
        }

        playPlayerUpgradeAnimation();
        PlayerPeriodChangeEvent event = new PlayerPeriodChangeEvent(getPlayer(), main.getPeriodConfigurationFile().getInt(player.getUniqueId() + ".period"), getRank(), PeriodAction.UPGRADE, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Resets the player's period to the initial period.
     */
    public void resetPeriod() {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cRESET §2: §a" + getEntirePeriodStyle() + "§2 >> §a" + main.getPeriodCaster().getPeriodToString(Period.PREHISTOIRE) + " I"));

        main.getPeriodConfigurationFile().set(player.getUniqueId() + ".rank", 1);
        main.getPeriodConfigurationFile().set(player.getUniqueId() + ".period", 0);

        try {
            main.getPeriodConfigurationFile().save(main.periodConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to save period of player '" + player.getDisplayName() + "' shutting down the server");
            Bukkit.shutdown();
        }

        experience = 0;
        playerPeriod = Period.PREHISTOIRE;
        PlayerPeriodChangeEvent event = new PlayerPeriodChangeEvent(getPlayer(), main.getPeriodConfigurationFile().getInt(player.getUniqueId() + ".period"), getRank(), PeriodAction.RESET, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Retrieves the rank value of the player.
     * @return The rank value.
     */
    public int getRank() {
        if (EvoPlugin.getInstance().getPeriodConfigurationFile().get(player.getUniqueId() + ".rank") == null) {
            return 0;
        }

        return EvoPlugin.getInstance().getPeriodConfigurationFile().getInt(player.getUniqueId() + ".rank");
    }

    /**
     * Downgrades the player's period to the previous level.
     */
    public void periodDowngrade() {
        if (getRank() == 1) {
            main.getPeriodConfigurationFile().set(player.getUniqueId() + ".period", main.getPeriodCaster().getIntPeriodFromEnum(Period.getBelowPeriod(playerPeriod)));
            main.getPeriodConfigurationFile().set(player.getUniqueId() + ".rank", 3);
        } else {
            main.getPeriodConfigurationFile().set(player.getUniqueId() + ".rank", getRank() - 1);
        }

        try {
            main.getPeriodConfigurationFile().save(main.periodConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to save period of player '" + player.getDisplayName() + "' shutting down the server");
            Bukkit.shutdown();
        }

        PlayerPeriodChangeEvent event = new PlayerPeriodChangeEvent(getPlayer(), main.getPeriodConfigurationFile().getInt(player.getUniqueId() + ".period"), getRank(), PeriodAction.DOWNGRADE, main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    /**
     * Saves the player's experience points to the configuration file.
     */
    public void saveExp() {
        main.getPeriodConfigurationFile().set(player.getUniqueId() + ".exp", getExp());

        try {
            main.getPeriodConfigurationFile().save(main.periodConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to save exp of player '" + player.getDisplayName() + "' shutting down the server");
            Bukkit.shutdown();
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
    public void playPlayerUpgradeAnimation() {
        String stringedPeriod;

        if (getRank() == 3) {
            stringedPeriod = main.getPeriodCaster().getPeriodToString(Period.getNextPeriod(playerPeriod)) + " I";
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§2UPGRADE §2: §a" + getEntirePeriodStyle() + "§2 >> §a" + main.getPeriodCaster().getPeriodToString(Period.getNextPeriod(playerPeriod)) + "I"));
            playerEconomy.addMoney(10000);
        } else {
            stringedPeriod = getEntirePeriodStyle() + "I";
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§2UPGRADE §2: §a" + getEntirePeriodStyle() + "§2 >> §a" + getEntirePeriodStyle() + "I"));
            playerEconomy.addMoney(5000);
        }

        player.sendTitle("§a" + stringedPeriod, "§2---------------", 20, 60, 20);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);

        //TODO: add the given amount of money in the message
        Message.sendPlayerMessage(player, PrefixLevel.GOOD, "Vous venez de passer en " + stringedPeriod + " !");
        main.getPeriodConfigurationFile().set(player.getUniqueId() + ".rank", getRank() + 1);

        try {
            main.getPeriodConfigurationFile().save(main.periodConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
            main.getLogger().log(Level.SEVERE, ChatColor.RED + "Failed to save period of player '" + player.getDisplayName() + "' shutting down the server");
            Bukkit.shutdown();
        }

        playerPeriod = Period.getBelowPeriod(playerPeriod);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6LIMITEUR §2: §a" + main.getPeriodCaster().getRankToString(getRank())));
    }

    public String getProgressBar() {
        int placement = this.getExp() * 20 / main.getPeriodCaster().getPeriodExpLimit(this.getRank());
        StringBuilder str = new StringBuilder("||||||||||||||||||||");
        str.insert(placement, "§e");
        str.insert(0, "§6");
        return str.toString();
    }

    public int getProgressPercent() {
        return this.getExp() * 100 / main.getPeriodCaster().getPeriodExpLimit(this.getRank());
    }
}