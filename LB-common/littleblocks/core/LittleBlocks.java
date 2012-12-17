package littleblocks.core;

import littleblocks.network.ClientPacketHandler;
import littleblocks.network.CommonPacketHandler;
import littleblocks.network.LBConnectionHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import eurysmods.api.ICommonProxy;
import eurysmods.data.Logger.LogLevel;

@Mod(
		modid = "LittleBlocksMod",
		name = "Little Blocks",
		version = "2.0.0.6",
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
			clientSide = "littleblocks.proxy.ClientProxy",
			serverSide = "littleblocks.proxy.CommonProxy")
	public static ICommonProxy proxy;

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