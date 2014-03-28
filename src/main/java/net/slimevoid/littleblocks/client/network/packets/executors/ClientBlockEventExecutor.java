package net.slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.slimevoid.library.IPacketExecutor;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlocksEvents;

public class ClientBlockEventExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
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
