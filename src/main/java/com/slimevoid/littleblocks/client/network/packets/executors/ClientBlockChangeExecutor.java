package com.slimevoid.littleblocks.client.network.packets.executors;

import com.slimevoid.littleblocks.core.LittleBlocks;
import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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
