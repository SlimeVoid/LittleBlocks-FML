package com.slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.slimevoid.library.IPacketExecutor;
import com.slimevoid.library.network.PacketUpdate;
import com.slimevoid.littleblocks.core.LittleBlocks;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksEvents;

import cpw.mods.fml.client.FMLClientHandler;

public class ClientPacketLittleBlocksSoundExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlocksEvents) {
            PacketLittleBlocksEvents theRealPacket = (PacketLittleBlocksEvents) packet;
            if (theRealPacket.getRelativeVolumeDisabled()) {
                ((World) LittleBlocks.proxy.getLittleWorld(FMLClientHandler.instance().getClient().theWorld,
                                                           false)).func_82739_e(theRealPacket.getSfxID(),
                                                                                theRealPacket.xPosition,
                                                                                theRealPacket.yPosition,
                                                                                theRealPacket.zPosition,
                                                                                theRealPacket.getAuxData());
            } else {
                ((World) LittleBlocks.proxy.getLittleWorld(FMLClientHandler.instance().getClient().theWorld,
                                                           false)).playAuxSFX(theRealPacket.getSfxID(),
                                                                              theRealPacket.xPosition,
                                                                              theRealPacket.yPosition,
                                                                              theRealPacket.zPosition,
                                                                              theRealPacket.getAuxData());
            }
        }
    }
}
