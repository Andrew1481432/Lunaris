package org.lunaris.network.protocol.packet;

import org.lunaris.entity.Entity;
import org.lunaris.entity.data.Attribute;
import org.lunaris.entity.data.EntityMetadata;
import org.lunaris.entity.misc.EntityType;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

import java.util.Collection;

/**
 * Created by RINES on 04.10.17.
 */
public class Packet0DAddEntity extends MinePacket {

    private long entityID;
    private EntityType type;
    private float x, y, z;
    private float motionX, motionY, motionZ;
    private float yaw, pitch;
    private Attribute[] attributes;
    private EntityMetadata metadata;

    public Packet0DAddEntity() {}

    public Packet0DAddEntity(Entity entity) {
        this.entityID = entity.getEntityID();
        this.type = entity.getEntityType();
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.motionX = entity.getMotionX();
        this.motionY = entity.getMotionY();
        this.motionZ = entity.getMotionZ();
        this.yaw = entity.getYaw();
        this.pitch = entity.getPitch();
        Collection<Attribute> attributes = entity.getAttributes();
        this.attributes = new Attribute[attributes.size()];
        int i = 0;
        for(Attribute attribute : attributes)
            this.attributes[i] = attribute;
        this.metadata = entity.getDataProperties();
    }

    @Override
    public int getId() {
        return 0x0d;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeEntityUniqueId(this.entityID);
        buffer.writeEntityRuntimeId(this.entityID);
        buffer.writeUnsignedVarInt(this.type.getId());
        buffer.writeVector3f(this.x, this.y, this.z);
        buffer.writeVector3f(this.motionX, this.motionY, this.motionZ);
        buffer.writeFloat(this.pitch);
        buffer.writeFloat(this.yaw);
        buffer.writeUnsignedVarInt(this.attributes.length);
        for(Attribute attribute : this.attributes) {
            buffer.writeString(attribute.getName());
            buffer.writeFloat(attribute.getMinValue());
            buffer.writeFloat(attribute.getValue());
            buffer.writeFloat(attribute.getMaxValue());
        }
        buffer.writeMetadata(this.metadata);
        buffer.writeUnsignedVarInt(0); //entity links
    }

}
