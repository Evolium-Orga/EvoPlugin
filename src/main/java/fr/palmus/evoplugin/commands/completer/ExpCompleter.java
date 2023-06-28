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

public class ExpCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        final List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1:
                StringUtil.copyPartialMatches(args[0], Arrays.asList("add", "set", "downgrade", "upgrade", "infos", "reset"), completions);
            case 2:
                StringUtil.copyPartialMatches(args[1], List.of("<player>"), completions);
            case 3:
                if (args[0] == "add" || args[0] == "set") {
                    StringUtil.copyPartialMatches(args[0], List.of("<number>"), completions);

                }
        }

        return completions;
    }
}
