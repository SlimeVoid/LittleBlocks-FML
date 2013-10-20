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
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.blocks.core.LittleContainerInteract;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.events.WorldServerLoadEvent;
import slimevoid.littleblocks.events.WorldServerUnloadEvent;
import slimevoid.littleblocks.items.LittleBlocksCollectionPickup;
import slimevoid.littleblocks.network.CommonPacketHandler;
import slimevoid.littleblocks.tickhandlers.LittleWorldServerTickHandler;
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
	public void registerTickHandlers() {
		TickRegistry.registerTickHandler(	new LittleWorldServerTickHandler(),
											Side.SERVER);
	}

	@Override
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new WorldServerLoadEvent());
		MinecraftForge.EVENT_BUS.register(new WorldServerUnloadEvent());
		MinecraftForge.EVENT_BUS.register(new LittleBlocksCollectionPickup());
		MinecraftForge.EVENT_BUS.register(new LittleContainerInteract());
		// MinecraftForge.EVENT_BUS.register(new PlayerInteractInterrupt());
		// MinecraftForge.EVENT_BUS.register(new LittleLadderHandler());
		// MinecraftForge.EVENT_BUS.register(new PistonOrientation());
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
			if (ConfigurationLib.littleWorldServer.containsKey(dimension)) {
				World littleWorld = DimensionManager.getWorld(ConfigurationLib.littleWorldServer.get(dimension));
				if (littleWorld == null) {
					throw new NullPointerException("A LittleWorld does not exist for reference world - "
													+ world.getWorldInfo().getWorldName());
				}
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
