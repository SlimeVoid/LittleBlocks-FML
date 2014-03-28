package net.slimevoid.littleblocks.network.packets;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.slimevoid.library.network.PacketPayload;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.slimevoid.littleblocks.core.lib.PacketLib;

public class PacketLittleBlocksEvents extends PacketUpdate {

    /** 1=Double Bass, 2=Snare Drum, 3=Clicks / Sticks, 4=Bass Drum, 5=Harp */
    public int getInstrumentType() {
        return this.payload.getIntPayload(0);
    }

    /**
     * The pitch of the note (between 0-24 inclusive where 0 is the lowest and
     * 24 is the highest).
     */
    public int getPitch() {
        return this.payload.getIntPayload(1);
    }

    /** The block ID this action is set for. */
    public int getBlockId() {
        return this.payload.getIntPayload(2);
    }

    public Block getBlock() {
        return Block.getBlockById(this.getBlockId());
    }

    public PacketLittleBlocksEvents() {
        super(PacketLib.PACKETID_EVENT, new PacketPayload(3, 0, 0, 0));
        this.setChannel(CoreLib.MOD_CHANNEL);
        this.setCommand(CommandLib.BLOCK_EVENT);
    }

    public PacketLittleBlocksEvents(int x, int y, int z, int blockId, int type, int pitch) {
        this();
        this.setPosition(x,
                         y,
                         z,
                         0);
        this.setInstrumentType(type);
        this.setPitch(pitch);
        this.setBlockId(blockId);
    }

    /** 1=Double Bass, 2=Snare Drum, 3=Clicks / Sticks, 4=Bass Drum, 5=Harp */
    public void setInstrumentType(int type) {
        this.payload.setIntPayload(0,
                                   type);
    }

    /**
     * The pitch of the note (between 0-24 inclusive where 0 is the lowest and
     * 24 is the highest).
     */
    public void setPitch(int pitch) {
        this.payload.setIntPayload(1,
                                   pitch);
    }

    /** The block ID this action is set for. */
    public void setBlockId(int blockId) {
        this.payload.setIntPayload(2,
                                   blockId);
    }

    @Override
    public boolean targetExists(World world) {
        return false;
    }

    // .addBlockEvent(xLocation, yLocation, zLocation, blockId, instrumentType,
    // pitch);
}
