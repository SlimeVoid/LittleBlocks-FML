package slimevoid.littleblocks.client.proxy;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.client.handlers.ClientTickHandler;
import slimevoid.littleblocks.client.handlers.DrawCopierHighlight;
import slimevoid.littleblocks.client.network.ClientPacketHandler;
import slimevoid.littleblocks.client.render.blocks.LittleBlocksRenderer;
import slimevoid.littleblocks.client.render.entities.LittleBlocksCollectionRenderer;
import slimevoid.littleblocks.client.render.tileentities.TileEntityLittleBlocksRenderer;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import slimevoid.littleblocks.proxy.CommonProxy;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import slimevoid.littleblocks.world.LittleWorld;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit() {		
		super.preInit();
		ClientPacketHandler.init();
		PacketLib.registerClientPacketHandlers();
	}

	@Override
	public String getMinecraftDir() {
		return Minecraft.getMinecraft().mcDataDir.toString();
	}

	@Override
	public void registerRenderInformation() {
		MinecraftForge.EVENT_BUS.register(new DrawCopierHighlight());
		RenderingRegistry.registerBlockHandler(new LittleBlocksRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityItemLittleBlocksCollection.class, new LittleBlocksCollectionRenderer());
		this.registerTileEntitySpecialRenderer(
				TileEntityLittleChunk.class);
	}

	@Override
	public void registerTileEntitySpecialRenderer(Class<? extends TileEntity> clazz) {
		ClientRegistry.bindTileEntitySpecialRenderer(
				clazz,
				new TileEntityLittleBlocksRenderer());
	}

	@Override
	public void registerTickHandler() {
		LBCore.littleDimensionClient = -1;
		LBCore.littleProviderTypeClient = -1;
		if (FMLCommonHandler.instance().getSide().isClient() && ModLoader
				.getMinecraftInstance()
					.isSingleplayer()) {
			LBCore.littleDimensionServer = -1;
			LBCore.littleProviderTypeServer = -1;
		}
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
	}

	public World getWorld(NetHandler handler) {
		if (handler instanceof NetClientHandler) {
			return ((NetClientHandler) handler).getPlayer().worldObj;
		}
		return null;
	}

	public ILittleWorld getLittleWorld(IBlockAccess iblockaccess, boolean needsRefresh) {
		World world = (World) iblockaccess;
		if (world != null) {
			if (world.isRemote) {
				if (LBCore.littleWorldClient == null || LBCore.littleWorldClient
						.isOutdated(world) || needsRefresh) {
					if (LBCore.littleDimensionClient < 0) {
						this.setLittleDimension(
								world,
								DimensionManager.getNextFreeDimId());
						LBCore.littleProviderTypeClient = DimensionManager
								.getProviderType(world.provider.dimensionId);
						if (LBCore.littleProviderClient == null) {
							//System.out
							//		.println("Registering Dimension: " + LBCore.littleDimensionClient);
							DimensionManager.registerDimension(
									LBCore.littleDimensionClient,
									LBCore.littleProviderTypeClient);
							LBCore.littleProviderClient = DimensionManager
									.createProviderFor(LBCore.littleDimensionClient);
						}
					}
					LBCore.littleWorldClient = new LittleWorld(
							world,
								LBCore.littleProviderClient);
				}
				return LBCore.littleWorldClient;
			} else {
				return super.getLittleWorld(world, needsRefresh);
			}
		}
		return null;
	}

	@Override
	public void setLittleDimension(World world, int nextFreeDimId) {
		ConfigurationLib.getConfiguration().load();
		LBCore.littleDimensionClient = ConfigurationLib.getConfiguration().get(
				Configuration.CATEGORY_GENERAL,
				"littleDimensionClient",
				nextFreeDimId).getInt();
		ConfigurationLib.getConfiguration().save();
		if (!world.isRemote) {
			super.setLittleDimension(world, nextFreeDimId);
		}
	}

	@Override
	public int getLittleDimension() {
		return LBCore.littleDimensionClient;
	}

	@Override
	public void resetLittleBlocks() {
		if (LBCore.littleProviderClient != null) {
			DimensionManager.unregisterDimension(LBCore.littleDimensionClient);
			LBCore.littleProviderClient = null;
			LBCore.littleDimensionClient = -2;
			if (LBCore.littleProviderServer != null) {
				super.resetLittleBlocks();
			}
		}
		LBCore.setLittleRenderer(null);
	}

	@Override
	public void registerConfigurationProperties(File configFile) {
		super.registerConfigurationProperties(configFile);
		ConfigurationLib.ClientConfig(configFile);
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
		World world = ((NetClientHandler) clientHandler).getPlayer().worldObj;
		if (world != null) {
			PacketLittleBlocksSettings packet = new PacketLittleBlocksSettings();
			packet.setCommand(CommandLib.FETCH);
			PacketDispatcher.sendPacketToServer(packet.getPacket());
		}
	}
}
