package com.slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.slimevoid.library.IPacketExecutor;
import com.slimevoid.library.network.PacketUpdate;
import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;

public class ClientPacketLittleBlocksLoginExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocksSettings
				&& packet.getCommand() == CommandLib.SETTINGS) {
			ConfigurationLib.littleBlocksClip = ((PacketLittleBlocksSettings) packet)
					.getClipMode();
		}
	}

}
