package fr.palmus.evoplugin.listeners;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.fastboard.EvoScoreboard;
import fr.palmus.evoplugin.listeners.custom.PlayerExpChangeEvent;
import fr.palmus.evoplugin.listeners.custom.PlayerMoneyChangeEvent;
import fr.palmus.evoplugin.listeners.custom.PlayerPeriodChangeEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ScoreboardUpdater implements Listener {

    EvoPlugin main;
    LuckPerms luckPerms;

    public ScoreboardUpdater(EvoPlugin plugin, LuckPerms luckPerms) {
        this.main = plugin;
        this.luckPerms = luckPerms;
        register();
    }

    public void register() {
        EventBus eventBus = this.luckPerms.getEventBus();
        eventBus.subscribe(this.main, NodeAddEvent.class, this::onNodeAdd);
        eventBus.subscribe(this.main, NodeRemoveEvent.class, this::onNodeRemove);
    }

    @EventHandler
    public void onPeriodChange(PlayerPeriodChangeEvent e) {
        EvoScoreboard.updateScoreboard(e.getPlayer());
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent e) {
        EvoScoreboard.updateScoreboard(e.getPlayer());
    }

    @EventHandler
    public void onMoneyChange(PlayerMoneyChangeEvent e) {
        EvoScoreboard.updateScoreboard(e.getPlayer());
    }

    private void onNodeAdd(NodeAddEvent e) {
        if (!e.isUser()) {
            return;
        }
        User target = (User) e.getTarget();
        EvoScoreboard.updateScoreboard(Bukkit.getPlayer(target.getUniqueId()));
    }

    private void onNodeRemove(NodeRemoveEvent e) {
        if (!e.isUser()) {
            return;
        }
        User target = (User) e.getTarget();
        EvoScoreboard.updateScoreboard(Bukkit.getPlayer(target.getUniqueId()));
    }
}
