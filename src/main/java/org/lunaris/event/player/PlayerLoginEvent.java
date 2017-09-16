package org.lunaris.event.player;

import org.lunaris.entity.Player;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 14.09.17.
 */
public class PlayerLoginEvent extends Event implements Cancellable {

    private final Player player;
    private boolean cancelled;

    public PlayerLoginEvent(Player player) {
        this.player = player;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public Player getPlayer() {
        return this.player;
    }

}