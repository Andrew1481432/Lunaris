package org.lunaris.event.player;

import org.lunaris.entity.LPlayer;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 13.09.17.
 */
public class PlayerPreLoginEvent extends Event implements Cancellable {

    private final LPlayer player;
    private boolean cancelled;

    public PlayerPreLoginEvent(LPlayer player) {
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

    public LPlayer getPlayer() {
        return player;
    }

}
