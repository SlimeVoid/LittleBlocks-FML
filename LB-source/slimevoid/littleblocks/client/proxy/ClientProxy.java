package slimevoid.littleblocks.client.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.client.handlers.ClientTickHandler;
import slimevoid.littleblocks.client.handlers.DrawCopierHighlight;
import slimevoid.littleblocks.client.network.ClientPacketHandler;
import slimevoid.littleblocks.client.render.EntityItemLittleBlocksCollectionRenderer;
import slimevoid.littleblocks.client.render.blocks.LittleBlocksRenderer;
import slimevoid.littleblocks.client.render.tileentities.TileEntityLittleBlocksRenderer;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LBInit;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import slimevoid.littleblocks.proxy.CommonProxy;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
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
		try {
			this.getClass().getClassLoader().loadClass("TextureHDCompassFX");
			LBCore.optifine = true;
			LoggerLittleBlocks.getInstance(
					"ClientProxy"
			).write(
					true,
					"Optifine Loaded - RenderBlocks Configured",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
		} catch (ClassNotFoundException e) {
			LBCore.optifine = false;
			LoggerLittleBlocks.getInstance(
					"ClientProxy"
			).write(
					true,
					"Optifine not Loaded - RenderBlocks Configured",
					LoggerLittleBlocks.LogLevel.DEBUG
			);
		}
		
		super.preInit();
		ClientPacketHandler.init();
		PacketLib.registerClientPacketHandlers();
	}

	@Override
	public String getMinecraftDir() {
		return Minecraft.getMinecraftDir().toString();
	}

	@Override
	public void registerRenderInformation() {
		MinecraftForgeClient.preloadTexture(LBInit.LBM.getBlockSheet());
		MinecraftForgeClient.preloadTexture(LBInit.LBM.getItemSheet());
		MinecraftForge.EVENT_BUS.register(new DrawCopierHighlight());
		RenderingRegistry.registerBlockHandler(new LittleBlocksRenderer());
		RenderingRegistry.registerEntityRenderingHandler(EntityItemLittleBlocksCollection.class, new EntityItemLittleBlocksCollectionRenderer());
		this.registerTileEntitySpecialRenderer(
				TileEntityLittleBlocks.class);
	}

	@Override
	public void registerTileEntitySpecialRenderer(Class<? extends TileEntity> clazz) {
		ClientRegistry.bindTileEntitySpecialRenderer(
				clazz,
				new TileEntityLittleBlocksRenderer());
	}

	@Override
	public void displayTileEntityGui(EntityPlayer entityplayer, TileEntity tileentity) {
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

	@Override
	public void login(NetHandler handler, INetworkManager manager, Packet1Login login) {
		World world = getWorld(handler);
		if (world != null) {
			PacketLittleBlocksSettings packet = new PacketLittleBlocksSettings();
			packet.setCommand(CommandLib.FETCH);
			PacketDispatcher.sendPacketToServer(packet.getPacket());
		}
	}

	@Override
	public ILittleWorld getLittleWorld(World world, boolean needsRefresh) {
		if (world != null) {
			if (world.isRemote) {
				if (LBCore.littleWorldClient == null || LBCore.littleWorldClient
						.isOutdated(world) || needsRefresh) {
					if (LBCore.littleDimensionClient < 0) {
						this.setLittleDimension(
								world,
								LBCore.configuration,
								DimensionManager.getNextFreeDimId());
						LBCore.littleProviderTypeClient = DimensionManager
								.getProviderType(world.provider.dimensionId);
						if (LBCore.littleProviderClient == null) {
							System.out
									.println("Registering Dimension: " + LBCore.littleDimensionClient);
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
	public World getWorld() {
		return ModLoader.getMinecraftInstance().theWorld;
	}

	@Override
	public EntityPlayer getPlayer() {
		return ModLoader.getMinecraftInstance().thePlayer;
	}

	@Override
	public World getWorld(NetHandler handler) {
		if (handler instanceof NetClientHandler) {
			return ((NetClientHandler) handler).getPlayer().worldObj;
		}
		return null;
	}

	@Override
	public void setLittleDimension(World world, Configuration configuration, int nextFreeDimId) {
		configuration.load();
		LBCore.littleDimensionClient = Integer.parseInt(configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleDimensionClient",
				nextFreeDimId).value);
		configuration.save();
		if (!world.isRemote) {
			super.setLittleDimension(world, configuration, nextFreeDimId);
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
}
