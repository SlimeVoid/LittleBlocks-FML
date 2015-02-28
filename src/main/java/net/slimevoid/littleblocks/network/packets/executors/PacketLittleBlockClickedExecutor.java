package net.slimevoid.littleblocks.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.slimevoid.library.IPacketExecutor;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlock;

public class PacketLittleBlockClickedExecutor implements IPacketExecutor {

    @Override
    public PacketUpdate execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlock
            && packet.getCommand().equals(CommandLib.BLOCK_CLICKED)) {
            ((BlockLittleChunk) ConfigurationLib.littleChunk).onServerBlockClicked((World) LittleBlocks.proxy.getLittleWorld(world,
                                                                                                                             false),
                                                                                   packet.xPosition,
                                                                                   packet.yPosition,
                                                                                   packet.zPosition,
                                                                                   packet.side,
                                                                                   entityplayer);
        }
        return null;
    }
}
