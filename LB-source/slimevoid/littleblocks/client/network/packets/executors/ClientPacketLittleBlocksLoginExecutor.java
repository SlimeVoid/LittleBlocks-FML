package slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.lib.IPacketExecutor;
import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;

public class ClientPacketLittleBlocksLoginExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocksSettings && packet.getCommand() == CommandLib.SETTINGS) {
			LBCore.littleBlocksClip = ((PacketLittleBlocksSettings)packet).getClipMode();
		}
	}

}
