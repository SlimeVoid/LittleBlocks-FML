package net.slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.slimevoid.library.IPacketExecutor;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.library.network.executor.PacketExecutor;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlockChange;

public class ClientBlockChangeExecutor extends PacketExecutor {

    @Override
    public PacketUpdate execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlockChange
                && packet.getCommand().equals(CommandLib.UPDATE_CLIENT)
                && packet.targetExists(world)) {
            Block block = ((PacketLittleBlockChange) packet).getBlock();
            int metadata = ((PacketLittleBlockChange) packet).getMetadata();
            IBlockState state = block.getStateFromMeta(metadata);
            ((World) LittleBlocks.proxy.getLittleWorld(
                    world,
                    false)).setBlockState(
                    packet.getPosition(),
                    state);
        }
        return null;
    }

}
