package slimevoid.littleblocks.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import slimevoid.lib.IPacketHandling;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.handlers.CommonTickHandler;
import slimevoid.littleblocks.network.CommonPacketHandler;
import slimevoid.littleblocks.world.LittleWorld;
import slimevoid.littleblocks.world.LittleWorldServer;
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
	public void displayTileEntityGui(EntityPlayer entityplayer, TileEntity tileentity) {
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
	public int getBlockTextureFromMetadata(int par2) {
		return 0;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		return getBlockTextureFromMetadata(meta);
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
	public void login(NetHandler handler, INetworkManager manager, Packet1Login login) {
	}

	@Override
	public World getWorld() {
		return null;
	}

	@Override
	public LittleWorld getLittleWorld(World world, boolean needsRefresh) {
		if (world != null) {
			if (LBCore.littleDimensionServer == -1) {
				this.setLittleDimension(
						world,
						LBCore.configuration,
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
						LBCore.configuration,
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
	public World getWorld(NetHandler handler) {
		return null;
	}

	@Override
	public EntityPlayer getPlayer() {
		return null;
	}

	@Override
	public void setLittleDimension(World world, Configuration configuration, int nextFreeDimId) {
		configuration.load();
		LBCore.littleDimensionServer = Integer.parseInt(configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleDimension",
				nextFreeDimId).value);
		configuration.save();
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
}
