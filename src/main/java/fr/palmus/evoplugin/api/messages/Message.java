package fr.palmus.evoplugin.api.messages;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.persistance.config.StringConfig;
import org.bukkit.entity.Player;

/**
 * This class simplifies the process of sending messages to players by providing a centralized method that handles message formatting based on their level.
 * It promotes code reusability and flexibility by allowing different message levels and customized formatting methods.
 * The message strings themselves are obtained from a configuration file, making it easier to modify them without altering the code directly.
 */
public class Message {

    public static final String NO_PERMISSION = StringConfig.getString("commands.no_perm");
    public static final String UNKNOWN_PLAYER = StringConfig.getString("commands.unknown_player");

    /**
     * The function can send a formatted message to the player based on the provided level
     * @param player  Represents the player to whom the message will be sent. It should be an instance of the {@link Player} class.
     * @param level Specifies the level of the message. It should be an instance of the {@link PrefixLevel} enum.
     * @param msg Represents the content of the message that will be sent to the player. It should be a string value.
     */
    public static void sendPlayerMessage(Player player, PrefixLevel level, String msg) {

        switch (level){
            case GOOD -> player.sendMessage(Formator.formatGood(msg, player));
            case NORMAL -> player.sendMessage(Formator.formatNormal(msg, player));
            case ERROR -> player.sendMessage(Formator.formatError(msg, player));
        }
    }
}
