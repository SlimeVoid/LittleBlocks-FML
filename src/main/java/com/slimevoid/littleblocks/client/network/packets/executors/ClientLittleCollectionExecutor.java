package com.slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.slimevoid.library.IPacketExecutor;
import com.slimevoid.library.network.PacketUpdate;
import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksCollection;

public class ClientLittleCollectionExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocksCollection
				&& packet.getCommand().equals(CommandLib.ENTITY_COLLECTION)) {
			Entity entity = world
					.getEntityByID(((PacketLittleBlocksCollection) packet)
							.getEntityId());
			if (entity instanceof EntityItemLittleBlocksCollection) {
				((EntityItemLittleBlocksCollection) entity)
						.setCollection(((PacketLittleBlocksCollection) packet).itemstackCollection);
			}
		}
	}

}
