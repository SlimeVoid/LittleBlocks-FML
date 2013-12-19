package slimevoid.littleblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import slimevoid.littleblocks.core.lib.CoreLib;
import slimevoidlib.data.Logger;
import slimevoidlib.network.handlers.SubPacketHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class CommonPacketHandler implements IPacketHandler {

	private static Map<Integer, SubPacketHandler>	commonHandlers;

	/**
	 * Initializes the commonHandler Map
	 */
	public static void init() {
		commonHandlers = new HashMap<Integer, SubPacketHandler>();
	}

	/**
	 * Register a sub-handler with the server-side packet handler.
	 * 
	 * @param packetID
	 *            Packet ID for the sub-handler to handle.
	 * @param handler
	 *            The sub-handler.
	 */
	public static void registerPacketHandler(int packetID, SubPacketHandler handler) {
		if (commonHandlers.containsKey(packetID)) {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(CommonPacketHandler.class.toString())).write(	false,
																												"PacketID ["
																														+ packetID
																														+ "] already registered.",
																												Logger.LogLevel.ERROR);
			throw new RuntimeException("PacketID [" + packetID
										+ "] already registered.");
		}
		commonHandlers.put(	packetID,
							handler);
	}

	/**
	 * Retrieves the registered sub-handler from the server side list
	 * 
	 * @param packetID
	 * @return the sub-handler
	 */
	public static SubPacketHandler getPacketHandler(int packetID) {
		if (!commonHandlers.containsKey(packetID)) {
			LoggerLittleBlocks.getInstance(Logger.filterClassName(CommonPacketHandler.class.toString())).write(	false,
																												"Tried to get a Packet Handler for ID: "
																														+ packetID
																														+ " that has not been registered.",
																												Logger.LogLevel.WARNING);
			throw new RuntimeException("Tried to get a Packet Handler for ID: "
										+ packetID
										+ " that has not been registered.");
		}
		return commonHandlers.get(packetID);
	}

	/**
	 * The server-side packet handler receives a packet.<br>
	 * Fetches the packet ID and routes it on to sub-handlers.
	 */
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			if (packet.channel.equals(CoreLib.MOD_CHANNEL)) {
				int packetID = data.read();
				getPacketHandler(packetID).onPacketData(manager,
														packet,
														player);
			} else if (packet.channel.equals("MC|AdvCdm")) {
				if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCommandBlockEnabled()) {
					((EntityPlayer) player).sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.notEnabled"));
				} else if (((EntityPlayer) player).canCommandSenderUseCommand(	2,
																				"")
							&& ((EntityPlayer) player).capabilities.isCreativeMode) {
					try {
						data = new DataInputStream(new ByteArrayInputStream(packet.data));
						int i = data.readInt();
						int j = data.readInt();
						int k = data.readInt();
						String s = Packet.readString(	data,
														256);
						TileEntity tileentity = ((EntityPlayer) player).worldObj.getBlockTileEntity(i,
																									j,
																									k);
						// attempt to get littleworld
						if (tileentity == null || s.startsWith("LW:")) {
							World littleWorld = (World) LittleBlocks.proxy.getLittleWorld(	((EntityPlayer) player).worldObj,
																							false);
							tileentity = littleWorld.getBlockTileEntity(i,
																		j,
																		k);
							if (s.startsWith("LW:")) {
								s = s.substring(3);
							}
						}

						if (tileentity != null
							&& tileentity instanceof TileEntityCommandBlock) {
							((TileEntityCommandBlock) tileentity).setCommand(s);
							((EntityPlayer) player).worldObj.markBlockForUpdate(i,
																				j,
																				k);
							((EntityPlayer) player).sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(	"advMode.setCommand.success",
																																	new Object[] { s }));
						}
					} catch (Exception exception3) {
						exception3.printStackTrace();
					}
				} else {
					((EntityPlayer) player).sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("advMode.notAllowed"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
