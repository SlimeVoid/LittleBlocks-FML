package net.slimevoid.littleblocks.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.library.network.executor.PacketExecutor;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlock;

public class PacketLittleBlockClickedExecutor extends PacketExecutor {

    @Override
    public PacketUpdate execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlock
            && packet.getCommand().equals(CommandLib.BLOCK_CLICKED)) {
            ((BlockLittleChunk) ConfigurationLib.littleChunk).onServerBlockClicked(
                    (World) LittleBlocks.proxy.getLittleWorld(
                            world,
                            false),
                    packet.getPosition(),
                    EnumFacing.getFront(packet.side),
                    entityplayer);
        }
        return null;
    }
}
