package fr.palmus.evoplugin.economy;

import fr.palmus.evoplugin.EvoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

public class Economy {

    EvoPlugin main;

    public File file;
    public FileConfiguration cfg;

    public HashMap<Player, PlayerEconomy> econManagement;

    public Economy(EvoPlugin main){
        this.main = main;
        setup();
        main.getCustomLogger().debug(ChatColor.GREEN + "Economy module successfully loadStringConfig !");
    }

    public void setup(){
        main.getCustomLogger().debug(ChatColor.GOLD + "Starting Economy module...");
        if (file == null) {
            file = new File("plugins/EvoPlugin", "economy.yml");
        }
        if (!file.exists()) {
            main.saveResource("economy.yml", false);
        }
        cfg = YamlConfiguration.loadConfiguration(file);
        main.getCustomLogger().debug(ChatColor.GOLD + "Economy config files loaded");
        econManagement = new HashMap<>();
        main.getCustomLogger().debug(ChatColor.GOLD + "Economy storage loaded");
    }

    public PlayerEconomy getPlayerEcon(Player pl){
        return econManagement.get(pl);
    }

    public void initPlayerEcon(Player pl) {

        if(!cfg.contains(pl.getDisplayName())){
            cfg.set(pl.getUniqueId() + ".money", 0);
            cfg.set(pl.getUniqueId() + ".bank", 0);

            try {
                cfg.save(this.file);
            } catch (IOException e) {
                e.printStackTrace();
                main.getCustomLogger().log(ChatColor.RED + "Failed to save money of player '" + pl.getDisplayName() + "' shutting down the server");
                Bukkit.shutdown();
            }
        }
        econManagement.put(pl, new PlayerEconomy(pl, this));
    }
}
