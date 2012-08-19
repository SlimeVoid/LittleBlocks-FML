package net.minecraft.src;

import net.minecraft.src.EurysMods.proxy.ICommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.core.LBCore;
import littleblocks.network.ClientPacketHandler;
import littleblocks.network.CommonPacketHandler;
import littleblocks.network.LittleBlocksConnection;

@Mod( modid = "LittleBlocksMod", name = "Little Blocks", version="1.3.2.0")
@NetworkMod(
		clientSideRequired = true,
		serverSideRequired = false,
		channels = {"LITTLEBLOCKS"},
		clientPacketHandlerSpec = @SidedPacketHandler(
				channels = {"LITTLEBLOCKS"},
				packetHandler = ClientPacketHandler.class),
		serverPacketHandlerSpec = @SidedPacketHandler(
				channels = {"LITTLEBLOCKS"},
				packetHandler = CommonPacketHandler.class),
		//packetHandler = LittleBlocksConnection.class,
		connectionHandler = LittleBlocksConnection.class,
		versionBounds = "[1.3]"
		)
public class LittleBlocks {
	@SidedProxy
	(clientSide = "littleblocks.proxy.ClientProxy",
	serverSide = "littleblocks.proxy.CommonProxy")
	public static ICommonProxy proxy;

	@Init
	public void LittleBlocksInit(FMLInitializationEvent event) {
		LBCore.initialize(proxy);
	}
	
	@PreInit
	public void LittleBlocksPreInit(FMLPreInitializationEvent event) {		
	}
	
	@PostInit
	public void LittleBlocksPostInit(FMLPostInitializationEvent event) {
	}
}