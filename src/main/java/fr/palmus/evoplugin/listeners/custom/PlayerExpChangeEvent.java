package fr.palmus.evoplugin.listeners.custom;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.api.enumeration.ExpAction;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerExpChangeEvent extends Event implements Cancellable {

    private final Player player;
    private final EvoPlayer evoPlayer;
    private final ExpAction action;
    private final int exp;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public PlayerExpChangeEvent(Player pl, int exp, ExpAction action, EvoPlugin main){
        this.player = pl;
        this.evoPlayer = EvoPlayer.getInstanceOf(pl);
        this.action = action;
        this.exp = exp;
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
        return evoPlayer;
    }

    public int getExp() {
        return exp;
    }

    public ExpAction getAction() {
        return action;
    }

    public static HandlerList getHandlersList() {
        return HANDLERS_LIST;
    }


}
