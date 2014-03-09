package com.slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.slimevoid.library.IPacketExecutor;
import com.slimevoid.library.network.SlimevoidPayload;
import com.slimevoid.littleblocks.core.LittleBlocks;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksEvents;

public class ClientBlockEventExecutor implements IPacketExecutor {

    @Override
    public void execute(SlimevoidPayload packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlocksEvents) {
            PacketLittleBlocksEvents packetEvent = (PacketLittleBlocksEvents) packet;
            ((World) LittleBlocks.proxy.getLittleWorld(world,
                                                       false)).addBlockEvent(packetEvent.xPosition,
                                                                             packetEvent.yPosition,
                                                                             packetEvent.zPosition,
                                                                             packetEvent.getBlock(),
                                                                             packetEvent.getInstrumentType(),
                                                                             packetEvent.getPitch());
        }
    }

}
