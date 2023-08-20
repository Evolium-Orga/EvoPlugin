package net.evolium.evoshop;

import net.evolium.evoshop.event.PnjManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        registerListeners();

    }

    @Override
    public void onDisable() {
        saveConfig();
    }


    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PnjManager(), this);

    }

    private void registerCommands() {

    }
}
