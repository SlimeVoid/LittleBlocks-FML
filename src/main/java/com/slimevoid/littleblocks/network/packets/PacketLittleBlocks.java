package com.slimevoid.littleblocks.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.slimevoid.library.network.PacketIds;
import com.slimevoid.library.network.PacketPayload;
import com.slimevoid.library.network.PacketUpdate;
import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;
import com.slimevoid.littleblocks.core.lib.CoreLib;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class PacketLittleBlocks extends PacketUpdate {

    @Override
    public void writeData(DataOutputStream data) throws IOException {
        super.writeData(data);
    }

    @Override
    public void readData(DataInputStream data) throws IOException {
        super.readData(data);
    }

    public PacketLittleBlocks() {
        super(PacketIds.UPDATE);
        this.setChannel(CoreLib.MOD_CHANNEL);
    }

    public PacketLittleBlocks(int x, int y, int z, World world) {
        this();
        this.setPosition(x,
                         y,
                         z,
                         Block.getIdFromBlock(world.getBlock(x,
                                          y,
                                          z)));
        this.payload = new PacketPayload(1, 0, 0, 1);
        this.setCommand(CommandLib.UPDATE_CLIENT);
        this.setMetadata(world.getBlockMetadata(x,
                                                y,
                                                z));
    }

    @Override
    public boolean targetExists(World world) {
        if (world.getBlock(this.xPosition >> 3,
                             this.yPosition >> 3,
                             this.zPosition >> 3) == ConfigurationLib.littleChunk) {
            return true;
        }
        return false;
    }

    public void setMetadata(int metadata) {
        this.payload.setIntPayload(0,
                                   metadata);
    }

    public int getBlockID() {
        return this.side;
    }
    
    public Block getBlock() {
        return Block.getBlockById(this.getBlockID());
    }

    public int getMetadata() {
        return this.payload.getIntPayload(0);
    }
}
