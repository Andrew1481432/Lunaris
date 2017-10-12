package org.lunaris.event.chunk;

import org.lunaris.api.world.Chunk;
import org.lunaris.event.Event;

/**
 * Created by RINES on 15.09.17.
 */
public class ChunkLoadedEvent extends Event {

    private final Chunk chunk;

    public ChunkLoadedEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return this.chunk;
    }

}
