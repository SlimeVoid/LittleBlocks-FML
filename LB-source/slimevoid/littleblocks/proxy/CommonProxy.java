package slimevoid.littleblocks.proxy;

import java.io.File;

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
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.handlers.CommonTickHandler;
import slimevoid.littleblocks.network.CommonPacketHandler;
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
		LBCore.littleDimensionServer = -1;
		LBCore.littleProviderTypeServer = -1;
		TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
	}

	@Override
	public IPacketHandling getPacketHandler() {
		return null;
	}
	
	@Override
	public ILittleWorld getLittleWorld(IBlockAccess iblockaccess, boolean needsRefresh) {
		World world = (World) iblockaccess;
		if (world != null) {
			if (LBCore.littleDimensionServer == -1) {
				this.setLittleDimension(
						world,
						DimensionManager.getNextFreeDimId());
				LBCore.littleProviderTypeServer = DimensionManager
						.getProviderType(world.provider.dimensionId);
				DimensionManager.registerDimension(
						LBCore.littleDimensionServer,
						LBCore.littleProviderTypeServer);
				LBCore.littleProviderServer = DimensionManager
						.createProviderFor(LBCore.littleDimensionServer);
			} else if (LBCore.littleDimensionServer == -2) {
				this.setLittleDimension(
						world,
						DimensionManager.getNextFreeDimId());
				LBCore.littleProviderTypeServer = DimensionManager
						.getProviderType(world.provider.dimensionId);
				LBCore.littleProviderServer = DimensionManager
						.getProvider(LBCore.littleDimensionServer);
			}
			if (LBCore.littleWorldServer == null || LBCore.littleWorldServer
					.isOutdated(world) || needsRefresh) {
				LBCore.littleWorldServer = new LittleWorldServer(
						world,
							LBCore.littleProviderServer);
			}
		}
		return LBCore.littleWorldServer;
	}

	@Override
	public void setLittleDimension(World world, int nextFreeDimId) {
		ConfigurationLib.getConfiguration().load();
		LBCore.littleDimensionServer = ConfigurationLib.getConfiguration().get(
				Configuration.CATEGORY_GENERAL,
				"littleDimension",
				nextFreeDimId).getInt();
		ConfigurationLib.getConfiguration().save();
	}

	@Override
	public int getLittleDimension() {
		return LBCore.littleDimensionServer;
	}

	@Override
	public void resetLittleBlocks() {
		if (LBCore.littleProviderServer != null) {
			DimensionManager.unregisterDimension(LBCore.littleDimensionServer);
			LBCore.littleProviderServer = null;
			LBCore.littleDimensionServer = -2;
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
