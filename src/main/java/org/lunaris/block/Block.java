package org.lunaris.block;

import org.lunaris.material.BlockMaterial;
import org.lunaris.material.Material;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.world.Chunk;
import org.lunaris.world.Location;
import org.lunaris.world.World;

/**
 * Created by RINES on 13.09.17.
 */
public class Block {

    private Material type;
    private int data;

    private Location location;

    private AxisAlignedBB boundingBox, collisionBoundingBox;

    public Block(Location location, Material type) {
        this(location, type, 0);
    }

    public Block(Location location, Material type, int data) {
        this.location = location;
        this.type = type;
        this.data = data;
    }

    public void setType(Material type) {
        setTypeAndData(type, 0);
    }

    public void setTypeId(int id) {
        setTypeAndData(Material.getById(id), 0);
    }

    public void setData(int data) {
        this.data = data;
        getWorld().updateBlock(this);
    }

    public void setTypeIdAndData(int id, int data) {
        setTypeAndData(Material.getById(id), data);
    }

    public void setTypeAndData(Material type, int data) {
        if (type == this.type && this.data == data)
            return;
        boolean typeChanged = false;
        if (type != this.type) {
            getSpecifiedMaterial().onBreak(null, this);
            this.type = type;
            typeChanged = true;
        }
        this.data = data;
        getWorld().updateBlock(this);
        if (typeChanged)
            getSpecifiedMaterial().onBlockAdd(this);
    }

    public Material getType() {
        return this.type;
    }

    public BlockMaterial getSpecifiedMaterial() {
        return (BlockMaterial) this.type.getSpecifiedMaterial();
    }

    public int getTypeId() {
        return this.type.getId();
    }

    public int getData() {
        return this.data;
    }

    public Location getLocation() {
        return this.location;
    }

    public World getWorld() {
        return this.location.getWorld();
    }

    public int getX() {
        return this.location.getBlockX();
    }

    public int getY() {
        return this.location.getBlockY();
    }

    public int getZ() {
        return this.location.getBlockZ();
    }

    public Chunk getChunk() {
        return getWorld().getChunkAt(getX() >> 4, getZ() >> 4);
    }

    public Block getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public Block getSide(BlockFace face, int step) {
        return getWorld().getBlockAt(this.location.getSide(face.getIndex(), step));
    }

    public Block getRelative(int x, int y, int z) {
        return getWorld().getBlockAt(getX() + x, getY() + y, getZ() + z);
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public AxisAlignedBB getCollisionBoundingBox() {
        return collisionBoundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setCollisionBoundingBox(AxisAlignedBB collisionBoundingBox) {
        this.collisionBoundingBox = collisionBoundingBox;
    }

    public boolean collidesWithBB(AxisAlignedBB bb) {
        return collidesWithBB(bb, false);
    }

    public boolean collidesWithBB(AxisAlignedBB bb, boolean collisionBB) {
        AxisAlignedBB bb1 = collisionBB ? this.getCollisionBoundingBox() : this.getBoundingBox();
        return bb1 != null && bb.intersectsWith(bb1);
    }

    @Override
    public String toString() {
        return "Block(world=" + getWorld() + ", x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ", type=" + type + ":" + data + ")";
    }
}
