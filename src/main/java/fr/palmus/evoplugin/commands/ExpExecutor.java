package fr.palmus.evoplugin.commands;


import fr.palmus.evoplugin.api.messages.Message;
import fr.palmus.evoplugin.api.messages.PrefixLevel;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import fr.palmus.evoplugin.fastboard.EvoScoreboard;
import fr.palmus.evoplugin.persistance.config.StringConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExpExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!player.hasPermission("evoplugin.exp_mod")) {
            Message.sendPlayerMessage(player, PrefixLevel.NORMAL, Message.NO_PERMISSION + ", if you want to get some information about your period you can use &a/period");
            return true;
        }

        if (args.length == 0 || args.length == 1) {
            if (args.length == 1) {
                Message.sendPlayerMessage(player, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{action}", args[0]));
            } else {
                Message.sendPlayerMessage(player, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{action}", ""));
            }

            return false;
        }

        if (Bukkit.getPlayer(args[1]) == null) {
            Message.sendPlayerMessage(player, PrefixLevel.ERROR, Message.UNKNOWN_PLAYER);
            return false;
        }

        Player target = Bukkit.getPlayer(args[1]);

        switch (args[0]) {
            case "add" -> {
                if (args.length == 2) {
                    Message.sendPlayerMessage(player, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{label}", args[0]) + "exp");
                }
                int exp = Integer.parseInt(args[2]);
                EvoPlayer.getInstanceOf(target).addExp(exp);
                Message.sendPlayerMessage(player, PrefixLevel.GOOD, StringConfig.getString("exp.added_successfully").replace("%exp%", String.valueOf(exp)));
            }

            case "subtract" -> {
                if (args.length == 2) {
                    Message.sendPlayerMessage(player, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{label}", args[0]) + "exp");
                }
                int exp = Integer.parseInt(args[2]);
                EvoPlayer.getInstanceOf(target).addExp(-exp);
                Message.sendPlayerMessage(player, PrefixLevel.GOOD, StringConfig.getString("exp.subtracted_successfully").replace("%exp%", String.valueOf(exp)));
            }

            case "set" -> {
                if (args.length == 2) {
                    Message.sendPlayerMessage(player, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{label}", args[0]) + "exp");
                }
                int exp = Integer.parseInt(args[2]);
                EvoPlayer.getInstanceOf(target).setExp(exp);
                Message.sendPlayerMessage(player, PrefixLevel.GOOD, StringConfig.getString("exp.set_successfully"));
            }

            default -> {
                return false;
            }
        }

        EvoScoreboard.updateScoreboard(target);
        return true;
    }
}
