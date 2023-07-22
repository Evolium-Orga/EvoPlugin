package fr.palmus.evoplugin.api.messages;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    static String formatNormal(String str) {

        return hex(Prefix.getPrefix(PrefixLevel.NORMAL) + str);
    }

    /**
     * Format a string by adding the evoplugin's normal version of the messages, applying color codes, and replacing "{player}" with the player's display name
     * @param str String containing placeholder
     * @param pl Placeholder found in the string will be replaced by the provided player
     * @return Return the formatted String
     */
    static String formatNormal(String str, Player pl) {
        String finalStr = hex(Prefix.getPrefix(PrefixLevel.NORMAL) + str);

        PlaceholderAPI.setPlaceholders(pl, finalStr);

        return finalStr;
    }

    /**
     * Format a string by adding the evoplugin's good version of the messages and applying color codes
     * @param str String you want to be formatted
     * @return Return the formatted String
     */
    static String formatGood(String str) {

        return hex(Prefix.getPrefix(PrefixLevel.GOOD) + str);
    }

    /**
     * Format a string by adding the evoplugin's good version of the messages, applying color codes, and replacing "{player}" with the player's display name
     * @param str String containing placeholder
     * @param pl Placeholder found in the string will be replaced by the provided player
     * @return Return the formatted String
     */
    static String formatGood(String str, Player pl) {
        String finalStr = hex(Prefix.getPrefix(PrefixLevel.GOOD) + str);

        PlaceholderAPI.setPlaceholders(pl, finalStr);

        return finalStr;
    }

    /**
     * Format an error message by adding the evoplugin's error version of the messages, applying red color, and applying color codes
     * @param str String you want to be formatted with the error format
     * @return Return the formatted String
     */
    static String formatError(String str) {

        return hex(Prefix.getPrefix(PrefixLevel.ERROR) + ChatColor.RED + str);
    }

    /**
     * @param str String containing placeholder
     * @param pl Placeholder found in the string will be replaced by the provided player
     * @return Return the formatted String using error format
     */
    static String formatError(String str, Player pl) {
        String finalStr = hex(Prefix.getPrefix(PrefixLevel.ERROR)  + str);

        PlaceholderAPI.setPlaceholders(pl, finalStr);

        return finalStr;
    }


    /**
     * Replaces hexadecimal color codes in the given message with corresponding Minecraft color codes.
     * @param message The message containing hexadecimal color codes.
     * @return The message with replaced color codes using Minecraft color codes.
     */
    public static String hex(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }

            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Replaces hexadecimal color codes in the given message with corresponding Minecraft color codes.
     * @param message An array of String containing hexadecimal color codes.
     * @return An array of String with replaced color codes using Minecraft color codes.
     */
    public static String[] hex(String... message) {
        String[] messages = new String[message.length - 1];
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

        int i = 0;
        for(String msg : message) {
            Matcher matcher = pattern.matcher(msg);
            while (matcher.find()) {
                String hexCode = msg.substring(matcher.start(), matcher.end());
                String replaceSharp = hexCode.replace('#', 'x');

                char[] ch = replaceSharp.toCharArray();
                StringBuilder builder = new StringBuilder();
                for (char c : ch) {
                    builder.append("&").append(c);
                }

                msg = msg.replace(hexCode, builder.toString());
                matcher = pattern.matcher(msg);
            }
            if(i >= message.length -1 ){
                return messages;
            }

            messages[i] = ChatColor.translateAlternateColorCodes('&', msg);
            i++;
        }
        return messages;
    }
}
