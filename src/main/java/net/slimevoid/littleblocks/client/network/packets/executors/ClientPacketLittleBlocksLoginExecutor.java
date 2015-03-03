package net.slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.slimevoid.library.IPacketExecutor;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.library.network.executor.PacketExecutor;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;

public class ClientPacketLittleBlocksLoginExecutor extends PacketExecutor {

    @Override
    public PacketUpdate execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet.getCommand() == CommandLib.SETTINGS) {
            ConfigurationLib.littleBlocksClip = ((PacketLittleBlocksSettings) packet).getClipMode();
        }
        return null;
    }

}
