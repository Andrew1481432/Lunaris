package org.lunaris.network.protocol.packet;

import org.lunaris.block.BlockFace;
import org.lunaris.inventory.transaction.ReleaseItemData;
import org.lunaris.inventory.transaction.TransactionData;
import org.lunaris.inventory.transaction.UseItemData;
import org.lunaris.inventory.transaction.UseItemOnEntityData;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 01.10.17.
 */
public class Packet1EInventoryTransaction extends MinePacket {

    private TransactionType type;
    //actions
    private TransactionData data;

    @Override
    public int getId() {
        return 0x1e;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.type = TransactionType.values()[buffer.readUnsignedVarInt()];
        //actions
        switch(this.type) {
            case NORMAL:
            case MISMATCH:
                //Хз че это значит
                //Regular ComplexInventoryTransaction doesn't read any extra data
                break;
            case USE_ITEM: {
                this.data = new UseItemData(
                        UseItemActionType.values()[buffer.readUnsignedVarInt()],
                        buffer.readBlockVector(),
                        BlockFace.fromIndex(buffer.readVarInt()),
                        buffer.readVarInt(),
                        buffer.readItemStack(),
                        buffer.readVector3d(),
                        buffer.readVector3d()
                );
                break;
            }case USE_ITEM_ON_ENTITY: {
                long entityID = buffer.readEntityRuntimeId();
                this.data = new UseItemOnEntityData(
                        UseItemOnEntityActionType.values()[buffer.readUnsignedVarInt()],
                        entityID,
                        buffer.readVarInt(),
                        buffer.readItemStack(),
                        buffer.readVector3d(),
                        buffer.readVector3d()
                );
                break;
            }case RELEASE_ITEM: {
                this.data = new ReleaseItemData(
                        ReleaseItemActionType.values()[buffer.readUnsignedVarInt()],
                        buffer.readVarInt(),
                        buffer.readItemStack(),
                        buffer.readVector3d()
                );
                break;
            }
        }
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeUnsignedVarInt(this.type.ordinal());
        //actions
        switch(this.type) {
            case NORMAL:
            case MISMATCH:
                break;
            case USE_ITEM: {
                UseItemData data = (UseItemData) this.data;
                buffer.writeUnsignedVarInt(data.getType().ordinal());
                buffer.writeBlockVector(data.getBlockPosition());
                buffer.writeVarInt(data.getBlockFace().getIndex());
                buffer.writeVarInt(data.getHotbarSlot());
                buffer.writeItemStack(data.getItemInHand());
                buffer.writeVector3d(data.getPlayerPosition());
                buffer.writeVector3d(data.getClickPosition());
                break;
            }case USE_ITEM_ON_ENTITY: {
                UseItemOnEntityData data = (UseItemOnEntityData) this.data;
                buffer.writeEntityRuntimeId(data.getEntityID());
                buffer.writeUnsignedVarInt(data.getType().ordinal());
                buffer.writeVarInt(data.getHotbarSlot());
                buffer.writeItemStack(data.getItemInHand());
                buffer.writeVector3d(data.getVector1());
                buffer.writeVector3d(data.getVector2());
                break;
            }case RELEASE_ITEM: {
                ReleaseItemData data = (ReleaseItemData) this.data;
                buffer.writeUnsignedVarInt(data.getType().ordinal());
                buffer.writeVarInt(data.getHotbarSlot());
                buffer.writeItemStack(data.getItemInHand());
                buffer.writeVector3d(data.getHeadRotation());
                break;
            }
        }
    }

    public enum TransactionType {
        NORMAL,
        MISMATCH,
        USE_ITEM,
        USE_ITEM_ON_ENTITY,
        RELEASE_ITEM
    }

    public enum UseItemActionType {
        CLICK_BLOCK,
        CLICK_AIR,
        BREAK_BLOCK
    }

    public enum UseItemOnEntityActionType {
        INTERACT,
        ATTACK
    }

    public enum ReleaseItemActionType {
        RELEASE, //bow shoot
        CONSUME //eat food, drink potions, etc
    }

    public enum MagicActionType {
        DROP_ITEM,
        PICKUP_ITEM
    }

    public enum CreativeMagicActionType {
        DELETE_ITEM,
        CREATE_ITEM
    }

}
