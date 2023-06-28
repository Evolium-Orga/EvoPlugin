package fr.palmus.evoplugin.listeners;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.fastboard.EvoScoreboard;
import fr.palmus.evoplugin.fastboard.FastBoard;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.sql.SQLException;

public class JoinQuitManager implements Listener {

    EvoPlugin main = EvoPlugin.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player pl = e.getPlayer();
        // TODO: DEBUG
        System.out.println("[EvoPlugin] Scoreboard linked !");
        main.getCustomPlayer().initPlayer(pl);
        if(pl.isOp()){
            pl.setGameMode(GameMode.CREATIVE);
        }
        main.getEconomyModule().initPlayerEcon(pl);
        FastBoard board = new FastBoard(pl);

        board.updateTitle(ChatColor.RED + "Evolium");

        EvoScoreboard.getPlayerToScoreboardHashmap().put(pl.getUniqueId(), board);
        EvoScoreboard.updateScoreboard(pl);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws IOException, SQLException {
        Player pl = e.getPlayer();
        FastBoard board = EvoScoreboard.getPlayerToScoreboardHashmap().remove(e.getPlayer().getUniqueId());

        if (board != null) {
            board.delete();
        }
        EvoPlayer.getInstanceOf(pl).saveExp();
        main.getEconomyModule().getPlayerEcon(pl).saveMoney();
        main.getPeriodConfigurationFile().save(main.periodConfigFile);
        main.getCustomPlayer().saveData(pl, main.getDatabaseManager().getDatabase().getConnection());
    }
}