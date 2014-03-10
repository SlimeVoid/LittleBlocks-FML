package com.slimevoid.littleblocks.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.slimevoid.library.IPacketExecutor;
import com.slimevoid.library.network.PacketUpdate;
import com.slimevoid.littleblocks.blocks.BlockLittleChunk;
import com.slimevoid.littleblocks.core.LittleBlocks;
import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlock;

public class PacketLittleBlockActivatedExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlock
            && packet.getCommand().equals(CommandLib.BLOCK_ACTIVATED)) {
            ((BlockLittleChunk) ConfigurationLib.littleChunk).onServerBlockActivated((World) LittleBlocks.proxy.getLittleWorld(world,
                                                                                                                               false),
                                                                                     entityplayer,
                                                                                     // littlePacket.getItemStack(),
                                                                                     packet.xPosition,
                                                                                     packet.yPosition,
                                                                                     packet.zPosition,
                                                                                     packet.side,
                                                                                     packet.hitX,
                                                                                     packet.hitY,
                                                                                     packet.hitZ);
        }
    }
}
