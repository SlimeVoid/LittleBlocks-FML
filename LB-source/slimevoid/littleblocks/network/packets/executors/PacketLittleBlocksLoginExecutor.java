package slimevoid.littleblocks.network.packets.executors;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.items.wand.EnumWandAction;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketLittleBlocksLoginExecutor implements IPacketExecutor {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocksSettings
			&& packet.getCommand() == CommandLib.FETCH) {
			EnumWandAction.getWandActionForPlayer(entityplayer);
			PacketLittleBlocksSettings packetSettings = new PacketLittleBlocksSettings();
			packetSettings.setCommand(CommandLib.SETTINGS);
			packetSettings.setClipMode(ConfigurationLib.littleBlocksClip);
			PacketDispatcher.sendPacketToPlayer(packet.getPacket(),
												(Player) entityplayer);
			List<TileEntity> tileEntities = world.loadedTileEntityList;
			for (TileEntity tileentity : tileEntities) {
				if (tileentity instanceof TileEntityLittleChunk) {
					world.markBlockForUpdate(	tileentity.xCoord,
												tileentity.yCoord + 1,
												tileentity.zCoord);
				}
			}
		}
	}

}
