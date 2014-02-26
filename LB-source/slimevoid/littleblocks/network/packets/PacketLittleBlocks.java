package slimevoid.littleblocks.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.core.lib.CoreLib;
import slimevoidlib.network.PacketIds;
import slimevoidlib.network.PacketPayload;
import slimevoidlib.network.PacketUpdate;

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
                         world.getBlockId(x,
                                          y,
                                          z));
        this.payload = new PacketPayload(1, 0, 0, 1);
        this.setCommand(CommandLib.UPDATE_CLIENT);
        this.setMetadata(world.getBlockMetadata(x,
                                                y,
                                                z));
    }

    @Override
    public boolean targetExists(World world) {
        if (world.getBlockId(this.xPosition >> 3,
                             this.yPosition >> 3,
                             this.zPosition >> 3) == ConfigurationLib.littleChunkID) {
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

    public int getMetadata() {
        return this.payload.getIntPayload(0);
    }
}
