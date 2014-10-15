package net.slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.slimevoid.library.IPacketExecutor;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlockChange;

public class ClientBlockChangeExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlockChange
            && packet.getCommand().equals(CommandLib.UPDATE_CLIENT)
            && packet.targetExists(world)) {
        	Block block = ((PacketLittleBlockChange) packet).getBlock();
        	int metadata = ((PacketLittleBlockChange) packet).getMetadata();
            ((World) LittleBlocks.proxy.getLittleWorld(world,
                                                       false)).setBlock(packet.xPosition,
                                                                        packet.yPosition,
                                                                        packet.zPosition,
                                                                        block,
                                                                        metadata,
                                                                        3);
        }
    }

}
