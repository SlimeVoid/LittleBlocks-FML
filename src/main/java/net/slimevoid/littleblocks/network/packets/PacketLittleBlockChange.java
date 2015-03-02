package net.slimevoid.littleblocks.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.slimevoid.library.network.PacketIds;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.littleblocks.core.lib.BlockUtil;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.CoreLib;

public class PacketLittleBlockChange extends PacketUpdate {

    @Override
    public void writeData(ByteBuf data) {
        super.writeData(data);
    }

    @Override
    public void readData(ByteBuf data) {
        super.readData(data);
    }

    public PacketLittleBlockChange() {
        super(PacketIds.UPDATE);
        this.setChannel(CoreLib.MOD_CHANNEL);
    }

    public PacketLittleBlockChange(BlockPos pos, World world) {
        this();
        IBlockState state = world.getBlockState(pos);
        int blockID = Block.getIdFromBlock(state.getBlock());
        int metadata = state.getBlock().getMetaFromState(state);
        int bam = (blockID << 4) + metadata;
        this.setPosition(pos,
                         bam);
        this.setCommand(CommandLib.UPDATE_CLIENT);
    }

    @Override
    public boolean targetExists(World world) {
        if (world.getBlockState(BlockUtil.getParentPos(this.getPosition())).getBlock() == ConfigurationLib.littleChunk) {
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
