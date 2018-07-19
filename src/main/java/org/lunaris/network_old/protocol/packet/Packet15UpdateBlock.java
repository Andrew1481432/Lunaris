package org.lunaris.network_old.protocol.packet;

import org.lunaris.block.LBlock;
import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 24.09.17.
 */
public class Packet15UpdateBlock extends MinePacket {

    private int x, y, z;
    private int id, data;

    public Packet15UpdateBlock(LBlock block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.id = block.getTypeId();
        this.data = block.getData();
    }

    public Packet15UpdateBlock(int x, int y, int z, int id, int data) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.data = data;
    }

    @Override
    public int getId() {
        return 0x15;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeBlockVector(this.x, this.y, this.z);
        buffer.writeUnsignedVarInt(this.id);
        buffer.writeUnsignedVarInt((0xb << 4) | this.data & 0xf);
    }

}