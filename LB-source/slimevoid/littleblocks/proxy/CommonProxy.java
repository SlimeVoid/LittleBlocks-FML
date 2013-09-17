package slimevoid.littleblocks.proxy;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.network.CommonPacketHandler;
import slimevoid.littleblocks.tickhandlers.LittleWorldServerTickHandler;
import slimevoid.littleblocks.world.LittleWorld;
import slimevoid.littleblocks.world.LittleWorldServer;
import slimevoidlib.IPacketHandling;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements ILBCommonProxy {

	@Override
	public void preInit() {
		CommonPacketHandler.init();
		PacketLib.registerPacketHandlers();
	}

	@Override
	public void registerRenderInformation() {
	}

	@Override
	public void registerTileEntitySpecialRenderer(Class<? extends TileEntity> clazz) {
	}

	@Override
	public String getMinecraftDir() {
		return "./";
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
	}

	@Override
	public void registerTickHandler() {
		TickRegistry.registerTickHandler(new LittleWorldServerTickHandler(), Side.SERVER);
	}

	@Override
	public IPacketHandling getPacketHandler() {
		return null;
	}
	
	@Override
	public ILittleWorld getLittleWorld(IBlockAccess iblockaccess, boolean needsRefresh) {
		if (LBCore.littleWorldServer == null) {
			LBCore.littleWorldServer = new HashMap<Integer, LittleWorld>();
			LBCore.littleDimensionServer = new HashMap<Integer, Integer>();
			LBCore.littleProviderTypeServer = new HashMap<Integer, Integer>();
			LBCore.littleProviderServer = new HashMap<Integer, WorldProvider>();
			World[] worlds = DimensionManager.getWorlds();
			for (World world : worlds) {
				LBCore.littleDimensionServer.put(world.provider.dimensionId, -1);
				LBCore.littleProviderTypeServer.put(world.provider.dimensionId, -1);
			}
		}
		World world = (World) iblockaccess;
		if (world != null) {
			int dimension = world.provider.dimensionId;
			if (LBCore.littleDimensionServer != null && LBCore.littleDimensionServer.containsKey(dimension)) {
				if (LBCore.littleDimensionServer.get(dimension) == -1) {
					this.setLittleDimension(
							world,
							DimensionManager.getNextFreeDimId());
					LBCore.littleProviderTypeServer.put(dimension, DimensionManager
							.getProviderType(dimension));
					DimensionManager.registerDimension(
							LBCore.littleDimensionServer.get(dimension),
							LBCore.littleProviderTypeServer.get(dimension));
					LBCore.littleProviderServer.put(dimension, DimensionManager
							.createProviderFor(LBCore.littleDimensionServer.get(dimension)));
				} else if (LBCore.littleDimensionServer.get(dimension) == -2) {
					this.setLittleDimension(
							world,
							DimensionManager.getNextFreeDimId());
					LBCore.littleProviderTypeServer.put(dimension, DimensionManager
							.getProviderType(world.provider.dimensionId));
					LBCore.littleProviderServer.put(dimension, DimensionManager
							.getProvider(LBCore.littleDimensionServer.get(dimension)));
				}
				if (!LBCore.littleWorldServer.containsKey(dimension) || LBCore.littleWorldServer.get(dimension)
						.isOutdated(world) || needsRefresh) {
					LBCore.littleWorldServer.put(dimension, new LittleWorldServer(
							world,
							LBCore.littleProviderServer.get(dimension)));
				}
				return LBCore.littleWorldServer.get(dimension);
			}
		}
		return null;
	}

	@Override
	public void setLittleDimension(World world, int nextFreeDimId) {
		ConfigurationLib.getConfiguration().load();
		LBCore.littleDimensionServer.put(world.provider.dimensionId, ConfigurationLib.getConfiguration().get(
				Configuration.CATEGORY_GENERAL,
				"littleDimension[" + world.provider.dimensionId + "]",
				nextFreeDimId).getInt());
		ConfigurationLib.getConfiguration().save();
	}

	@Override
	public void resetLittleBlocks() {
		if (LBCore.littleProviderServer != null) {
			for (Integer id : LBCore.littleDimensionServer.values()) {
				DimensionManager.unregisterDimension(id);
			}
			LBCore.littleProviderServer = null;
			LBCore.littleDimensionServer = null;
		}
	}

	@Override
	public void registerConfigurationProperties(File configFile) {
		ConfigurationLib.CommonConfig(configFile);
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
	}

	@Override
	public boolean isClient(World world) {
		return FMLCommonHandler.instance().getSide() == Side.CLIENT || (world != null && world.isRemote);
	}
}
