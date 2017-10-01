package org.lunaris.network.protocol.packet;

import org.lunaris.entity.data.Gamemode;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 01.10.17.
 */
public class Packet3ESetPlayerGameType extends MinePacket {

    private Gamemode gamemode;

    public Packet3ESetPlayerGameType() {}

    public Packet3ESetPlayerGameType(Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    @Override
    public int getId() {
        return 0x3e;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.gamemode = Gamemode.values()[buffer.readVarInt()];
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeVarInt(this.gamemode.ordinal());
    }

}