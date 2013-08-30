package slimevoid.littleblocks.core;

import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.client.network.ClientPacketHandler;
import slimevoid.littleblocks.core.lib.CoreLib;
import slimevoid.littleblocks.network.CommonPacketHandler;
import slimevoid.littleblocks.proxy.CommonProxy;
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
		modid = CoreLib.MOD_ID,
		name = CoreLib.MOD_NAME,
		version = CoreLib.MOD_VERSION,
		dependencies = CoreLib.MOD_DEPENDENCIES)
@NetworkMod(
		clientSideRequired = true,
		serverSideRequired = false,
		clientPacketHandlerSpec = @SidedPacketHandler(
				channels = { CoreLib.MOD_CHANNEL },
				packetHandler = ClientPacketHandler.class),
		serverPacketHandlerSpec = @SidedPacketHandler(
				channels = { CoreLib.MOD_CHANNEL },
				packetHandler = CommonPacketHandler.class),
		connectionHandler = CommonProxy.class)
public class LittleBlocks {
	@SidedProxy(
			clientSide = CoreLib.CLIENT_PROXY,
			serverSide = CoreLib.COMMON_PROXY)
	public static ILBCommonProxy proxy;
	
	@Instance(CoreLib.MOD_ID)
	public static LittleBlocks instance;

	@PreInit
	public void LittleBlocksPreInit(FMLPreInitializationEvent event) {
		proxy.registerConfigurationProperties(event.getSuggestedConfigurationFile());
		proxy.preInit();
	}

	@Init
	public void LittleBlocksInit(FMLInitializationEvent event) {
		LBInit.initialize();
	}

	@PostInit
	public void LittleBlocksPostInit(FMLPostInitializationEvent event) {
	}
}