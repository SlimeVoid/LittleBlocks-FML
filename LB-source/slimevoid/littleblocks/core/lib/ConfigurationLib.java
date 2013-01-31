package slimevoid.littleblocks.core.lib;

import net.minecraftforge.common.Configuration;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConfigurationLib {
	
	@SideOnly(Side.CLIENT)
	public static void ClientConfig() {
		
	}
	
	public static void CommonConfig() {
		LBCore.configuration.load();
		LBCore.littleBlocksID = Integer.parseInt(LBCore.configuration.get(
				Configuration.CATEGORY_BLOCK,
				"littleBlocksID",
				150).value);
		LBCore.littleBlocksCopierID = Integer.parseInt(LBCore.configuration.get(
				Configuration.CATEGORY_ITEM,
				"littleBlocksCopierID",
				29999).value);
		LBCore.littleBlocksCollectionID = Integer.parseInt(LBCore.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksCollectionID",
				EntityRegistry.findGlobalUniqueEntityId()).value);
		LBCore.littleBlocksClip = Boolean.parseBoolean(LBCore.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksClip",
				true).value);
		LBCore.littleBlocksForceUpdate = Boolean.parseBoolean(LBCore.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksForceUpdate",
				false).value);
		LBCore.renderingMethod = Integer.parseInt(LBCore.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"renderingMethod",
				0).value);
		LBCore.renderType = RenderingRegistry.getNextAvailableRenderId();
		LBCore.loggerLevel = String.valueOf(LBCore.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"loggerLevel",
				"INFO").value);
		LBCore.configuration.save();
		LoggerLittleBlocks.getInstance("LittleBlocksConfig").setFilterLevel(LBCore.loggerLevel);
	}

}
