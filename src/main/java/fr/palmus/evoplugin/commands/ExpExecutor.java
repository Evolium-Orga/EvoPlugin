package fr.palmus.evoplugin.commands;


import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.api.messages.Message;
import fr.palmus.evoplugin.api.messages.PrefixLevel;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import fr.palmus.evoplugin.enumeration.Period;
import fr.palmus.evoplugin.fastboard.EvoScoreboard;
import fr.palmus.evoplugin.persistance.config.StringConfig;
import fr.palmus.evoplugin.persistance.mysql.EvoDatabase;
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

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!player.hasPermission("evoplugin.exp_mod")) {
            Message.sendPlayerMessage(player, PrefixLevel.NORMAL, Message.NO_PERMISSION + ", if you want to get some information about your period you can use &a/period");
            return true;
        }

        if (args.length == 0 || args.length == 1) {
            Message.sendPlayerMessage(player, PrefixLevel.ERROR, StringConfig.getString("commands.less_args").replace("{action}", args[0]));
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
                Message.sendPlayerMessage(player, PrefixLevel.GOOD, "exp.set_successfully");
            }

            case "downgrade" -> {
                EvoPlayer.getInstanceOf(target).periodDowngrade();
                Message.sendPlayerMessage(player, PrefixLevel.GOOD, target.getDisplayName() + "est descendu à la période " + EvoPlayer.getInstanceOf(target).getEntirePeriodStyle());
            }

            case "upgrade" -> {
                if (EvoPlayer.getInstanceOf(target).getPlayerPeriod() == Period.FUTUR && EvoPlayer.getInstanceOf(target).getRank() == 3) {
                    Message.sendPlayerMessage(player, PrefixLevel.ERROR, "Ce joueur est déjà §d" + EvoPlayer.getInstanceOf(target).getEntirePeriodStyle());
                    return false;
                }
                EvoPlayer.getInstanceOf(target).periodUpgrade();
                Message.sendPlayerMessage(player, PrefixLevel.GOOD, target.getDisplayName() + "est monter à la période " + EvoPlayer.getInstanceOf(target).getEntirePeriodStyle());
            }

            case "info" -> {
                Message.sendPlayerMessage(player, PrefixLevel.NORMAL, target.getDisplayName() + "à actuellement " + main.getPeriodCaster().formatIntegerToReadableString(EvoPlayer.getInstanceOf(target).getExp()) + " EXP §5et est à la période §d" + EvoPlayer.getInstanceOf(target).getEntirePeriodStyle());
            }

            case "reset" -> {
                EvoPlayer.getInstanceOf(target).resetPeriod();
                Message.sendPlayerMessage(player, PrefixLevel.GOOD, target.getDisplayName() + "c'est vu réinitialiser ses périodes et est maintenant à la période " + main.getPeriodCaster().getPeriodToString(Period.PREHISTOIRE) + " I");
            }

            default -> {
                return false;
            }
        }

        EvoScoreboard.updateScoreboard(target);
        return true;
    }
}
