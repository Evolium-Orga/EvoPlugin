package fr.palmus.evoplugin.listeners;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.fastboard.EvoScoreboard;
import fr.palmus.evoplugin.fastboard.FastBoard;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import fr.palmus.evoplugin.persistance.config.EvoConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class JoinQuitManager implements Listener {

    EvoPlugin main = EvoPlugin.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        EvoPlayer evoPlayer = EvoPlayer.getInstanceOf(pl);

        evoPlayer.getDatabaseConnection().registerPlayerOnDatabase();
        EvoPlayer.recreateInstanceOf(pl);

        if(pl.hasPermission("minecraft.command.gamemode")){
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> pl.setGameMode(GameMode.CREATIVE), 20);
        }

        FastBoard board = new FastBoard(pl);

        EvoScoreboard.getPlayerToScoreboardHashmap().put(pl.getUniqueId(), board);
        EvoScoreboard.updateScoreboard(pl);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws IOException {
        Player pl = e.getPlayer();
        EvoPlayer evoPlayer = EvoPlayer.getInstanceOf(pl);

        FastBoard board = EvoScoreboard.getPlayerToScoreboardHashmap().remove(e.getPlayer().getUniqueId());

        if (board != null) {
            board.delete();
        }

        evoPlayer.saveCache();

        EvoConfig.getPeriodConfiguration().save(EvoConfig.getPeriodFile());
        evoPlayer.getDatabaseConnection().saveData();
    }
}
