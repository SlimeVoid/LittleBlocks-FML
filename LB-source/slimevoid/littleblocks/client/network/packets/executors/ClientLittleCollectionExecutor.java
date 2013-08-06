package slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksCollection;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;

public class ClientLittleCollectionExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocksCollection && packet.getCommand().equals(CommandLib.ENTITY_COLLECTION)) {
			Entity entity = ((PacketLittleBlocksCollection)packet).getEntity(world);
			if (entity instanceof EntityItemLittleBlocksCollection) {
				((EntityItemLittleBlocksCollection)entity).setCollection(((PacketLittleBlocksCollection)packet).itemstackCollection);
			}
		}
	}

}
