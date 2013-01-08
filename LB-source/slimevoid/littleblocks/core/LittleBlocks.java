package slimevoid.littleblocks.core;

import slimevoid.lib.ICommonProxy;
import slimevoid.littleblocks.client.network.ClientPacketHandler;
import slimevoid.littleblocks.network.CommonPacketHandler;
import slimevoid.littleblocks.network.LBConnectionHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;

@Mod(
		modid = "LittleBlocksMod",
		name = "Little Blocks",
		version = "2.0.2.1",
		dependencies = "after:EurysCore")
@NetworkMod(
		clientSideRequired = true,
		serverSideRequired = false,
		clientPacketHandlerSpec = @SidedPacketHandler(
				channels = { "LITTLEBLOCKS" },
				packetHandler = ClientPacketHandler.class),
		serverPacketHandlerSpec = @SidedPacketHandler(
				channels = { "LITTLEBLOCKS" },
				packetHandler = CommonPacketHandler.class),
		connectionHandler = LBConnectionHandler.class)
public class LittleBlocks {
	@SidedProxy(
			clientSide = "slimevoid.littleblocks.client.proxy.ClientProxy",
			serverSide = "slimevoid.littleblocks.proxy.CommonProxy")
	public static ICommonProxy proxy;
	
	@Instance("LittleBlocksMod")
	public static LittleBlocks instance;

	@PreInit
	public void LittleBlocksPreInit(FMLPreInitializationEvent event) {
	}

	@Init
	public void LittleBlocksInit(FMLInitializationEvent event) {
		LBCore.initialize(proxy);
	}

	@PostInit
	public void LittleBlocksPostInit(FMLPostInitializationEvent event) {
	}
}