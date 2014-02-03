package slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksEvents;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;

public class ClientBlockEventExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlocksEvents) {
            PacketLittleBlocksEvents packetEvent = (PacketLittleBlocksEvents) packet;
            ((World) LittleBlocks.proxy.getLittleWorld(world,
                                                       false)).addBlockEvent(packetEvent.xPosition,
                                                                             packetEvent.yPosition,
                                                                             packetEvent.zPosition,
                                                                             packetEvent.getBlockId(),
                                                                             packetEvent.getInstrumentType(),
                                                                             packetEvent.getPitch());
        }
    }

}
