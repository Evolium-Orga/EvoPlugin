package fr.palmus.evoplugin.commands;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.persistance.config.StringConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class PeriodExecutor implements CommandExecutor {

    EvoPlugin main = EvoPlugin.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if(!(sender instanceof Player pl)){
            return false;
        }

        Inventory inv = Bukkit.createInventory(pl, 45, StringConfig.getString("inventory.info_name").replace("&", "§"));

        main.getComponents().initInfoInventory(inv, pl);

        pl.openInventory(inv);

        return false;
    }
}
