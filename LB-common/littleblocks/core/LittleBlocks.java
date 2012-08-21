package littleblocks.core;

import littleblocks.network.ClientPacketHandler;
import littleblocks.network.CommonPacketHandler;
import net.minecraft.src.EurysMods.api.ICommonProxy;
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

@Mod(modid = "LittleBlocksMod", name = "Little Blocks", version = "1.3.2.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, clientPacketHandlerSpec = @SidedPacketHandler(channels = { "LITTLEBLOCKS" }, packetHandler = ClientPacketHandler.class), serverPacketHandlerSpec = @SidedPacketHandler(channels = { "LITTLEBLOCKS" }, packetHandler = CommonPacketHandler.class), versionBounds = "[1.3]")
public class LittleBlocks {
	@SidedProxy(clientSide = "littleblocks.proxy.ClientProxy", serverSide = "littleblocks.proxy.CommonProxy")
	public static ICommonProxy proxy;
	public static LittleBlocks instance;

	@Init
	public void LittleBlocksInit(FMLInitializationEvent event) {
		instance = this;
		LBCore.initialize(proxy);
	}

	@PreInit
	public void LittleBlocksPreInit(FMLPreInitializationEvent event) {
	}

	@PostInit
	public void LittleBlocksPostInit(FMLPostInitializationEvent event) {
	}
}