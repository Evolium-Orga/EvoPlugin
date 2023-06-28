package fr.palmus.evoplugin.commands;


import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.api.messages.Message;
import fr.palmus.evoplugin.api.messages.PrefixLevel;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import fr.palmus.evoplugin.enumeration.Period;
import fr.palmus.evoplugin.persistance.config.StringConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExpExecutor implements CommandExecutor {

    EvoPlugin main = EvoPlugin.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player pl)) {
            return true;
        }

        if (!pl.hasPermission("evoplugin.exp_mod")) {
            Message.sendPlayerMessage(pl, PrefixLevel.NORMAL, Message.NO_PERMISSION + ", if you want to get some information about your period you can use &a/period");
            return true;
        }

        if (args.length == 0 || args.length == 1) {
            Message.sendPlayerMessage(pl, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{action}", args[0]));
            return false;
        }

        if (Bukkit.getPlayer(args[1]) == null) {
            Message.sendPlayerMessage(pl, PrefixLevel.ERROR, Message.UNKNOWN_PLAYER);
            return false;
        }

        Player target = Bukkit.getPlayer(args[1]);

        switch (args[0]) {
            case "add" -> {
                if (args.length == 2) {
                    Message.sendPlayerMessage(pl, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{label}", args[0]) + "exp");
                    return true;
                }
                int exp = Integer.parseInt(args[2]);
                EvoPlayer.getInstanceOf(target).addExp(exp);
                Message.sendPlayerMessage(pl, PrefixLevel.GOOD, StringConfig.getString("exp.added_successfully").replace("%exp%", String.valueOf(exp)));
                return true;
            }

            case "set" -> {
                if (args.length == 2) {
                    Message.sendPlayerMessage(pl, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{label}", args[0]) + "exp");
                    return true;
                }
                int exp = Integer.parseInt(args[2]);
                EvoPlayer.getInstanceOf(target).setExp(exp);
                Message.sendPlayerMessage(pl, PrefixLevel.GOOD, "exp.set_successfully");
                return true;
            }

            case "downgrade" -> {
                EvoPlayer.getInstanceOf(target).periodDowngrade();
                Message.sendPlayerMessage(pl, PrefixLevel.GOOD, Bukkit.getPlayer(args[1]).getDisplayName() + "§5 est descendu à la période §d" + EvoPlayer.getInstanceOf(target).getEntirePeriodStyle());
                return true;
            }

            case "upgrade" -> {
                if (EvoPlayer.getInstanceOf(target).getPlayerPeriod() == Period.FUTUR && EvoPlayer.getInstanceOf(target).getRank() == 3) {
                    Message.sendPlayerMessage(pl, PrefixLevel.ERROR, "§5Ce joueur est déjà §d" + EvoPlayer.getInstanceOf(target).getEntirePeriodStyle());
                    return false;
                }
                EvoPlayer.getInstanceOf(target).periodUpgrade();
                Message.sendPlayerMessage(pl, PrefixLevel.GOOD, Bukkit.getPlayer(args[1]).getDisplayName() + "§5 est monter à la période §d" + EvoPlayer.getInstanceOf(target).getEntirePeriodStyle());
                return true;
            }

            case "info" -> {
                Message.sendPlayerMessage(pl, PrefixLevel.NORMAL, Bukkit.getPlayer(args[1]).getDisplayName() + "§5 à actuellement §d" + main.getPeriodCaster().formatIntegerToReadableString(EvoPlayer.getInstanceOf(target).getExp()) + " EXP §5et est à la période §d" + EvoPlayer.getInstanceOf(target).getEntirePeriodStyle());
                return true;
            }

            case "reset" -> {
                EvoPlayer.getInstanceOf(target).resetPeriod();
                Message.sendPlayerMessage(pl, PrefixLevel.GOOD, Bukkit.getPlayer(args[1]).getDisplayName() + "§5 c'est vu réinitialiser ses périodes et est maintenant à la période §d" + main.getPeriodCaster().getPeriodToString(Period.PREHISTOIRE) + " I");
                return true;
            }

            default -> {
                return false;
            }
        }
    }
}
