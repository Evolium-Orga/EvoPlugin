package fr.palmus.evoplugin.listeners.custom;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.api.enumeration.PeriodAction;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerPeriodChangeEvent extends Event implements Cancellable {

    private final Player player;
    private final EvoPlayer plm;
    private final PeriodAction action;
    private final int period;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public PlayerPeriodChangeEvent(Player pl, int period, PeriodAction action, EvoPlugin main){
        this.player = pl;
        this.plm = EvoPlayer.getInstanceOf(pl);
        this.action = action;
        this.period = period;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public EvoPlayer getPlayerManager() {
        return plm;
    }

    public int getPeriod() {
        return period;
    }

    public PeriodAction getAction() {
        return action;
    }

    public static HandlerList getHandlersList() {
        return HANDLERS_LIST;
    }


}
