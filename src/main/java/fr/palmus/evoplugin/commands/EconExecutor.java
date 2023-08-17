package fr.palmus.evoplugin.commands;

import fr.palmus.evoplugin.api.messages.Message;
import fr.palmus.evoplugin.api.messages.PrefixLevel;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import fr.palmus.evoplugin.persistance.config.StringConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EconExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (!player.hasPermission("evoplugin.econ_mod")) {
            Message.sendPlayerMessage(player, PrefixLevel.NORMAL, Message.NO_PERMISSION);
            return true;
        }

        if (args.length == 0 || args.length == 1) {
            if (args.length == 1) {
                Message.sendPlayerMessage(player, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{action}", args[0]));
            } else {
                Message.sendPlayerMessage(player, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{action}", ""));
            }

            return true;
        }

        if (Bukkit.getPlayer(args[1]) == null) {
            Message.sendPlayerMessage(player, PrefixLevel.ERROR, Message.UNKNOWN_PLAYER);
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        int money = 0;

        switch (args[0]) {
            case "add" -> {
                if (args.length == 2) {
                    Message.sendPlayerMessage(player, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{label}", args[0]) + "money");
                    return true;
                }

                try {
                    money = Integer.parseInt(args[2]);
                }catch (NumberFormatException e) {
                    Message.sendPlayerMessage(player, PrefixLevel.ERROR, "Le nombre que vous avez précisé est beaucoup trop grand ou contient des caractères innatendus");
                }

                EvoPlayer.getInstanceOf(target).addMoney(money);
                Message.sendPlayerMessage(player, PrefixLevel.GOOD, StringConfig.getString("money.added_successfully").replace("%money%", String.valueOf(money)));
            }

            case "subtract" -> {
                if (args.length == 2) {
                    Message.sendPlayerMessage(player, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{label}", args[0]) + "money");
                    return true;
                }

                try {
                    money = Integer.parseInt(args[2]);
                }catch (NumberFormatException e) {
                    Message.sendPlayerMessage(player, PrefixLevel.ERROR, "Le nombre que vous avez précisé est beaucoup trop grand ou contient des caractères innatendus");
                }

                EvoPlayer.getInstanceOf(target).addExp(-money);
                Message.sendPlayerMessage(player, PrefixLevel.GOOD, StringConfig.getString("money.subtracted_successfully").replace("%money%", String.valueOf(money)));
            }

            case "set" -> {
                if (args.length == 2) {
                    Message.sendPlayerMessage(player, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{label}", args[0]) + "money");
                    return true;
                }

                try {
                    money = Integer.parseInt(args[2]);
                }catch (NumberFormatException e) {
                    Message.sendPlayerMessage(player, PrefixLevel.ERROR, "Le nombre que vous avez précisé est beaucoup trop grand ou contient des caractères innatendus");
                }

                EvoPlayer.getInstanceOf(target).setExp(money);
                Message.sendPlayerMessage(player, PrefixLevel.GOOD, StringConfig.getString("money.set_successfully"));
            }
        }

        return false;
    }
}
