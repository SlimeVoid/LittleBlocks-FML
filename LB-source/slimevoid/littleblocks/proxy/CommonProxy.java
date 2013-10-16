package slimevoid.littleblocks.proxy;

import java.io.File;

import net.minecraft.client.multiplayer.NetClientHandler;
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
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.events.WorldServerLoadEvent;
import slimevoid.littleblocks.events.WorldServerUnloadEvent;
import slimevoid.littleblocks.network.CommonPacketHandler;
import slimevoid.littleblocks.tickhandlers.LittleWorldServerTickHandler;
import slimevoid.littleblocks.world.LittlePlayerController;
import slimevoidlib.IPacketHandling;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements ILBCommonProxy {

	@Override
	public void preInit() {
		CommonPacketHandler.init();
		PacketLib.registerPacketHandlers();
		MinecraftForge.EVENT_BUS.register(new WorldServerLoadEvent());
		MinecraftForge.EVENT_BUS.register(new WorldServerUnloadEvent());
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
		TickRegistry.registerTickHandler(	new LittleWorldServerTickHandler(),
											Side.SERVER);
	}

	@Override
	public IPacketHandling getPacketHandler() {
		return null;
	}

	@Override
	public ILittleWorld getLittleWorld(IBlockAccess iblockaccess, boolean needsRefresh) {
		World world = (World) iblockaccess;
		if (world != null) {
			int dimension = world.provider.dimensionId;
			if (LBCore.littleWorldServer.containsKey(dimension)) {
				World littleWorld = DimensionManager.getWorld(LBCore.littleWorldServer.get(dimension));
				if (littleWorld instanceof ILittleWorld) {
					return (ILittleWorld) littleWorld;
				}
			}
		}
		return null;
	}

	@Override
	public void registerConfigurationProperties(File configFile) {
		ConfigurationLib.CommonConfig(configFile);
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
		// System.out.println("LoggedIn");
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
		return FMLCommonHandler.instance().getSide() == Side.CLIENT
				|| (world != null && world.isRemote);
	}
}
