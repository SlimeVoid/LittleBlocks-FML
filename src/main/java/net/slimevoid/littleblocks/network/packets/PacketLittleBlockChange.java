package net.slimevoid.littleblocks.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.slimevoid.library.network.PacketIds;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.CoreLib;

public class PacketLittleBlockChange extends PacketUpdate {

    @Override
    public void writeData(ChannelHandlerContext ctx, ByteBuf data) {
        super.writeData(ctx,
                        data);
    }

    @Override
    public void readData(ChannelHandlerContext ctx, ByteBuf data) {
        super.readData(ctx,
                       data);
    }

    public PacketLittleBlockChange() {
        super(PacketIds.UPDATE);
        this.setChannel(CoreLib.MOD_CHANNEL);
    }

    public PacketLittleBlockChange(int x, int y, int z, World world) {
        this();
        int blockID = Block.getIdFromBlock(world.getBlock(x,
                y,
                z));
        int metadata = world.getBlockMetadata(
        		x,
                y,
                z);
        int bam = (blockID << 4) + metadata;
        this.setPosition(x,
                         y,
                         z,
                         bam);
        this.setCommand(CommandLib.UPDATE_CLIENT);
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

    public int getBlockID() {
        return this.side >> 4;
    }

    public Block getBlock() {
        return Block.getBlockById(this.getBlockID());
    }

    public int getMetadata() {
        return this.side & 15;
    }
}
