package fr.palmus.evoplugin.api;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.api.messages.Prefix;
import fr.palmus.evoplugin.api.messages.PrefixLevel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Utility class for logging messages to the console with proper formatting and debug support
 * Provides methods for general logging and debug logging if debug mode is enabled
 */
public class Logger {

    EvoPlugin main = EvoPlugin.getInstance();

    // Log a formatted message to the console
    public void log(String msg) {
        // Format the message with evoplugin name and color codes
        msg = ChatColor.translateAlternateColorCodes('&', Prefix.getPrefix(PrefixLevel.NORMAL) + msg);
        // Send the formatted message to the console
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    // Log a debug message if debug mode is enabled
    public void debug(String msg) {
        // Check if debug mode is enabled in the StringConfig
        if (main.DEBUG_MODE) {
            // Log the debug message with a specific format if debug mode is enabled
            log("&7||&aDEBUG&7||&r &2" + msg);
        }
    }

}
