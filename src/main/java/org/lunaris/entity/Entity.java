package org.lunaris.entity;

import org.lunaris.Lunaris;
import org.lunaris.entity.data.*;
import org.lunaris.entity.misc.EntityType;
import org.lunaris.entity.misc.Movable;
import org.lunaris.event.entity.EntityDamageEvent;
import org.lunaris.material.block.LiquidBlock;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.packet.Packet27SetEntityData;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.world.Chunk;
import org.lunaris.world.Location;
import org.lunaris.world.World;

import java.util.*;

/**
 * Created by RINES on 13.09.17.
 */
public abstract class Entity extends Metadatable implements Movable {

    private final long entityID;
    private final EntityType entityType;
    private final MovementData movement;

    private World world;

    private final Map<Integer, Attribute> attributes = new HashMap<>();

    private int fireTicks;

    private AxisAlignedBB boundingBox = new AxisAlignedBB( 0, 0, 0, 0, 0, 0 );

    private boolean collidedVertically;
    private boolean collidedHorizontally;
    private boolean onGround;
    private float fallDistance;

    protected Entity(long entityID, EntityType entityType) {
        this.entityID = entityID;
        this.entityType = entityType;
        this.movement = generateEntityMovement();
    }

    @Override
    public long getEntityID() {
        return this.entityID;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public Collection<Attribute> getAttributes() {
        return this.attributes.values();
    }

    public Attribute getAttribute(int id) {
        Attribute a = this.attributes.get(id);
        if(a != null)
            return a;
        a = Attribute.getAttribute(id);
        this.attributes.put(id, a);
        return a;
    }

    public void setAttribute(int id, float value) {
        Attribute a = getAttribute(id);
        a.setValue(value);
    }

    public void teleport(Location location) {
        if(this.world != location.getWorld())
            throw new IllegalArgumentException("Entities can not be teleported between worlds!");
        this.setPositionAndRotation(location);
    }

    public Location getLocation() {
        return this.movement.getLocation(this.world);
    }

    public Chunk getChunk() {
        return this.world.getChunkAt(((int) getX()) >> 4, ((int) getZ()) >> 4);
    }

    @Override
    public float getX() {
        return this.movement.getX();
    }

    @Override
    public float getY() {
        return this.movement.getY();
    }

    @Override
    public float getZ() {
        return this.movement.getZ();
    }

    @Override
    public float getMotionX() {
        return this.movement.getMotionX();
    }

    @Override
    public float getMotionY() {
        return this.movement.getMotionY();
    }

    @Override
    public float getMotionZ() {
        return this.movement.getMotionZ();
    }

    @Override
    public float getYaw() {
        return this.movement.getYaw();
    }

    @Override
    public float getHeadYaw() {
        return this.movement.getHeadYaw();
    }

    @Override
    public float getPitch() {
        return this.movement.getPitch();
    }

    @Override
    public Location getLocation(World world) {
        throw new IllegalStateException("Unsupported method");
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.movement.setPosition(x, y, z);
    }

    @Override
    public void setRotation(float yaw, float headYaw, float pitch) {
        this.movement.setRotation(yaw, headYaw, pitch);
    }

    @Override
    public void setMotion(float x, float y, float z) {
        this.movement.setMotion(x, y, z);
    }

    public World getWorld() {
        return this.world;
    }

    public void setDisplayName(String name) {
        setDataProperty(EntityDataOption.NAMETAG, name);
    }

    public String getDisplayName() {
        return getDataPropertyString(EntityDataOption.NAMETAG);
    }

    public void setDisplayNameVisible(boolean visible, boolean always) {
        setDataFlag(false, EntityDataFlag.CAN_SHOW_NAMETAG, visible, false);
        setDataFlag(false, EntityDataFlag.ALWAYS_SHOW_NAMETAG, always, false);
    }

    public boolean isDisplayNameVisible() {
        return getDataFlag(false, EntityDataFlag.CAN_SHOW_NAMETAG);
    }

    public boolean isDisplayNameAlwaysVisible() {
        return isDisplayNameVisible() && getDataFlag(false, EntityDataFlag.ALWAYS_SHOW_NAMETAG);
    }

    public final void remove() {
        getWorld().removeEntityFromWorld(this);
    }

    public void tick(long current, float dT) {
        this.movement.tickMovement(current, dT);
        if(this.fireTicks > 0) {
            if(this instanceof LivingEntity)
                ((LivingEntity) this).damage(EntityDamageEvent.DamageCause.FIRE, 1);
            setDataFlag(false, EntityDataFlag.ON_FIRE, this.fireTicks --> 1, true);
        }
        if(isDirtyMetadata()) {
            setDirtyMetadata(false);
            Lunaris.getInstance().getNetworkManager().broadcastPacket(new Packet27SetEntityData(this.entityID, getDataProperties()));
        }
        if(getY() <= -16)
            if(this instanceof LivingEntity)
                ((LivingEntity) this).damage(EntityDamageEvent.DamageCause.VOID, 1);
            else
                remove();
    }

    /**
     * Внутренний метод для расчета коллизий сущности после последнего перемещения
     * @param motionX движение по x
     * @param motionY движение по y
     * @param motionZ движение по z
     * @param dx разница перемещения по x
     * @param dy разница перемещения по y
     * @param dz разница перемещения по z
     */
    public void setupCollisionFlags(float motionX, float motionY, float motionZ, float dx, float dy, float dz) {
        this.collidedVertically = motionY != dy;
        this.collidedHorizontally = motionX != dx || motionZ != dz;
        this.onGround = motionY != dy && motionY < 0;
    }

    /**
     * Внутренний метод для расчета дистанции падения сущности после последнего перемещения
     * @param dy разница перемещения по y
     */
    public void setupFallDistance(float dy) {
        if(this.onGround) {
            if(this.fallDistance > 0F)
                fall();
            this.fallDistance = 0;
        }else if(dy < 0F)
            this.fallDistance -= dy;
    }

    /**
     * Внутренний метод для установки мира сущности после ее создания
     * @param world мир
     */
    public void initWorld(World world) {
        this.world = world;
    }

    public void setOnFire(int ticks) {
        this.fireTicks = ticks;
    }

    public void setFallDistance(float fallDistance) {
        this.fallDistance = fallDistance;
    }

    /**
     * Сколько тиков осталось гореть этой сущности
     * @return сколько тиков осталось гореть этой сущности
     */
    public int getFireTicks() {
        return this.fireTicks;
    }

    /**
     * Высота глаз сущности
     * @return высоту глаз сущности
     */
    public float getEyeHeight() {
        return this.getHeight() / 2 + 0.1f;
    }

    /**
     * Высота сущности
     * @return высоту сущности
     */
    public abstract float getHeight();

    /**
     * Ширина сущности
     * @return ширину сущности
     */
    public abstract float getWidth();

    /**
     * Получение максимальной высоты, на которую может подняться сущность за одно перемещение
     * @return максимальную высоту, на которую может подняться сущность за одно перемещение
     */
    public abstract float getStepHeight();

    /**
     * Метод, вызываемый при падении сущности на землю с высоты
     */
    public abstract void fall();

    public abstract MinePacket createSpawnPacket();

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public boolean isCollidedVertically() {
        return this.collidedVertically;
    }

    public boolean isCollidedHorizontally() {
        return this.collidedHorizontally;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean isInsideOfWater() {
        Location location = getLocation().add(0D, getEyeHeight(), 0D);
        return this.world.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()).getHandle() instanceof LiquidBlock;
    }

    public boolean hasJustMoved() {
        return this.movement.isDirty();
    }

    public float getFallDistance() {
        return this.fallDistance;
    }

    private MovementData generateEntityMovement() {
        return new MovementData(this);
    }

    @Override
    public int hashCode() {
        return (int) this.entityID;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof Entity))
            return false;
        return this.entityID == ((Entity) o).entityID;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-ID" + this.entityID;
    }

}
