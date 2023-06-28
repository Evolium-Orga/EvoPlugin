package fr.palmus.evoplugin.api.messages;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Utility class for formatting strings with the evoplugin's messages and applying color codes
 * Provides methods for formatting normal messages, good messages, player-specific messages, and error messages
 * @see PrefixLevel
 */
public class Formator {

    /**
     * Format a string by adding the evoplugin's normal version of the messages and applying color codes
     * @param str String you want to be formatted
     * @return Return the formatted String
     */
    public String formatNormal(String str) {

        return Prefix.getPrefix(PrefixLevel.NORMAL) + ChatColor.translateAlternateColorCodes('&', str);
    }

    /**
     * Format a string by adding the evoplugin's normal version of the messages, applying color codes, and replacing "{player}" with the player's display name
     * @param str String containing placeholder
     * @param pl Placeholder found in the string will be replaced by the provided player
     * @return Return the formatted String
     */
    public String formatNormal(String str, Player pl) {
        String finalStr = Prefix.getPrefix(PrefixLevel.NORMAL) + ChatColor.translateAlternateColorCodes('&', str);

        PlaceholderAPI.setPlaceholders(pl, finalStr);

        return finalStr;
    }

    /**
     * Format a string by adding the evoplugin's good version of the messages and applying color codes
     * @param str String you want to be formatted
     * @return Return the formatted String
     */
    public String formatGood(String str) {

        return Prefix.getPrefix(PrefixLevel.GOOD) + ChatColor.translateAlternateColorCodes('&', str);
    }

    /**
     * Format a string by adding the evoplugin's good version of the messages, applying color codes, and replacing "{player}" with the player's display name
     * @param str String containing placeholder
     * @param pl Placeholder found in the string will be replaced by the provided player
     * @return Return the formatted String
     */
    public String formatGood(String str, Player pl) {
        String finalStr = Prefix.getPrefix(PrefixLevel.GOOD) + ChatColor.translateAlternateColorCodes('&', str);

        PlaceholderAPI.setPlaceholders(pl, finalStr);

        return finalStr;
    }

    /**
     * Format an error message by adding the evoplugin's error version of the messages, applying red color, and applying color codes
     * @param str String you want to be formatted with the error format
     * @return Return the formatted String
     */
    public String formatError(String str) {

        return Prefix.getPrefix(PrefixLevel.ERROR) + ChatColor.RED + ChatColor.translateAlternateColorCodes('&', str);
    }

    /**
     * @param str String containing placeholder
     * @param pl Placeholder found in the string will be replaced by the provided player
     * @return Return the formatted String using error format
     */
    public String formatError(String str, Player pl) {
        String finalStr = Prefix.getPrefix(PrefixLevel.ERROR) + ChatColor.RED + ChatColor.translateAlternateColorCodes('&', str);

        PlaceholderAPI.setPlaceholders(pl, finalStr);

        return finalStr;
    }
}
