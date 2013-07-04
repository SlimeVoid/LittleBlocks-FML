package slimevoid.littleblocks.core;

import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.client.network.ClientPacketHandler;
import slimevoid.littleblocks.core.lib.ReferenceLib;
import slimevoid.littleblocks.network.CommonPacketHandler;
import slimevoid.littleblocks.network.LBConnectionHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;

@Mod(
		modid = ReferenceLib.MOD_ID,
		name = ReferenceLib.MOD_NAME,
		version = ReferenceLib.MOD_VERSION,
		dependencies = ReferenceLib.MOD_DEPENDENCIES)
@NetworkMod(
		clientSideRequired = true,
		serverSideRequired = false,
		clientPacketHandlerSpec = @SidedPacketHandler(
				channels = { ReferenceLib.MOD_CHANNEL },
				packetHandler = ClientPacketHandler.class),
		serverPacketHandlerSpec = @SidedPacketHandler(
				channels = { ReferenceLib.MOD_CHANNEL },
				packetHandler = CommonPacketHandler.class),
		connectionHandler = LBConnectionHandler.class)
public class LittleBlocks {
	@SidedProxy(
			clientSide = ReferenceLib.CLIENT_PROXY,
			serverSide = ReferenceLib.COMMON_PROXY)
	public static ILBCommonProxy proxy;
	
	@Instance(ReferenceLib.MOD_ID)
	public static LittleBlocks instance;

	@EventHandler
	public void LittleBlocksPreInit(FMLPreInitializationEvent event) {
		LBInit.initialize();
	}

	@EventHandler
	public void LittleBlocksInit(FMLInitializationEvent event) {
	}

	@EventHandler
	public void LittleBlocksPostInit(FMLPostInitializationEvent event) {
	}
}