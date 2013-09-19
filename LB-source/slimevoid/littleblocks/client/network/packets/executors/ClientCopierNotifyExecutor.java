package slimevoid.littleblocks.client.network.packets.executors;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.MessageLib;
import slimevoid.littleblocks.network.packets.PacketLittleNotify;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;

public class ClientCopierNotifyExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleNotify
			&& packet.getCommand().equals(CommandLib.COPIER_MESSAGE)) {
			String message = LanguageRegistry.instance().getStringLocalization(MessageLib.DENY_COPY);
			if (message.equals("")) message = LanguageRegistry.instance().getStringLocalization(MessageLib.DENY_WAND);
			entityplayer.addChatMessage(message);
		}
	}

}
