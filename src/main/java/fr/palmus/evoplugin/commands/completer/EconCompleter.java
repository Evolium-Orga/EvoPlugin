package fr.palmus.evoplugin.commands.completer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EconCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList("add", "set", "subtract"), completions);
        }

        if (args.length == 2) {
            return null;
        }

        if (args.length == 3) {
            StringUtil.copyPartialMatches(args[2], Arrays.asList("10", "50", "100", "500", "1000", "10000"), completions);
        }

        return completions;
    }
}
