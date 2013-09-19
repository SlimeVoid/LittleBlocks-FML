package slimevoid.littleblocks.client.proxy;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.client.handlers.DrawCopierHighlight;
import slimevoid.littleblocks.client.network.ClientPacketHandler;
import slimevoid.littleblocks.client.render.blocks.LittleBlocksRenderer;
import slimevoid.littleblocks.client.render.entities.LittleBlocksCollectionRenderer;
import slimevoid.littleblocks.client.render.tileentities.TileEntityLittleBlocksRenderer;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.events.WorldLoadEvent;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import slimevoid.littleblocks.proxy.CommonProxy;
import slimevoid.littleblocks.tickhandlers.LittleWorldTickHandler;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
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
		MinecraftForge.EVENT_BUS.register(new WorldLoadEvent());
	}

	@Override
	public String getMinecraftDir() {
		return Minecraft.getMinecraft().mcDataDir.toString();
	}

	@Override
	public void registerRenderInformation() {
		MinecraftForge.EVENT_BUS.register(new DrawCopierHighlight());
		RenderingRegistry.registerBlockHandler(new LittleBlocksRenderer());
		RenderingRegistry.registerEntityRenderingHandler(	EntityItemLittleBlocksCollection.class,
															new LittleBlocksCollectionRenderer());
		this.registerTileEntitySpecialRenderer(TileEntityLittleChunk.class);
	}

	@Override
	public void registerTileEntitySpecialRenderer(Class<? extends TileEntity> clazz) {
		ClientRegistry.bindTileEntitySpecialRenderer(	clazz,
														new TileEntityLittleBlocksRenderer());
	}

	@Override
	public void registerTickHandler() {
		TickRegistry.registerTickHandler(	new LittleWorldTickHandler(),
											Side.CLIENT);
		super.registerTickHandler();
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
				int dimension = world.provider.dimensionId;
				return LBCore.littleWorldClient;
			} else {
				return super.getLittleWorld(world,
											needsRefresh);
			}
		}
		return null;
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
