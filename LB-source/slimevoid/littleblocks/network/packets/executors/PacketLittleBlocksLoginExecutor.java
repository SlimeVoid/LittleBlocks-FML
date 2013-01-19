package slimevoid.littleblocks.network.packets.executors;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.lib.IPacketExecutor;
import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketLittleBlocksLoginExecutor implements IPacketExecutor {

	@Override
	public void execute(PacketUpdate packet, World world,
			EntityPlayer entityplayer) {
		if (packet instanceof PacketLittleBlocksSettings && packet.getCommand() == CommandLib.FETCH) {
			PacketLittleBlocksSettings packetSettings = new PacketLittleBlocksSettings();
			packetSettings.setCommand(CommandLib.SETTINGS);
			packetSettings.setClipMode(LBCore.littleBlocksClip);
			PacketDispatcher.sendPacketToPlayer(packet.getPacket(), (Player) entityplayer);
			List<TileEntity> tileEntities = world.loadedTileEntityList;
			for (TileEntity tileentity : tileEntities) {
				if (tileentity instanceof TileEntityLittleBlocks) {
					world.markBlockForUpdate(tileentity.xCoord, tileentity.yCoord+1, tileentity.zCoord);
				}
			}
		}
	}

}
