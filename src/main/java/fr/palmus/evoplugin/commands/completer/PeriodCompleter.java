package fr.palmus.evoplugin.commands.completer;

import fr.palmus.evoplugin.commands.PeriodExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PeriodCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return null;
        }

        final List<String> completions = new ArrayList<>();

        if(args.length == 1) {
            if(!PeriodExecutor.checkPerms(player)) {
                StringUtil.copyPartialMatches(args[0], Arrays.asList("downgrade", "upgrade", "info", "reset", "resetall"), completions);
            }else{
                StringUtil.copyPartialMatches(args[0], List.of("info"), completions);
            }
        }

        if(args.length == 2) {
            return null;
        }

        return completions;
    }

}
