package slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;

public class ClientBlockChangeExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlocks
            && packet.getCommand().equals(CommandLib.UPDATE_CLIENT)
            && packet.targetExists(world)) {
            ((World) LittleBlocks.proxy.getLittleWorld(world,
                                                       false)).setBlock(packet.xPosition,
                                                                        packet.yPosition,
                                                                        packet.zPosition,
                                                                        ((PacketLittleBlocks) packet).getBlockID(),
                                                                        ((PacketLittleBlocks) packet).getMetadata(),
                                                                        3);
        }
    }

}
