package com.slimevoid.littleblocks.client.network.packets.executors;

import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;

public class ClientPacketLittleBlocksLoginExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlocksSettings
            && packet.getCommand() == CommandLib.SETTINGS) {
            ConfigurationLib.littleBlocksClip = ((PacketLittleBlocksSettings) packet).getClipMode();
        }
    }

}
