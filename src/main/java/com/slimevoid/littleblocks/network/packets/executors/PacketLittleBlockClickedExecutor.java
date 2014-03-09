package com.slimevoid.littleblocks.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.slimevoid.library.IPacketExecutor;
import com.slimevoid.library.network.SlimevoidPayload;
import com.slimevoid.littleblocks.blocks.BlockLittleChunk;
import com.slimevoid.littleblocks.core.LittleBlocks;
import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlock;

public class PacketLittleBlockClickedExecutor implements IPacketExecutor {

    @Override
    public void execute(SlimevoidPayload packet, World world, EntityPlayer entityplayer) {
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
    }
}
