package slimevoid.littleblocks.core.lib;

import java.io.File;
import java.util.HashMap;

import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConfigurationLib {
	
	private static File configurationFile;
	private static Configuration configuration;
	
	public static Configuration getConfiguration() {
		return configuration;
	}
	
	@SideOnly(Side.CLIENT)
	public static void loadClientLittleDimensions() {
		String littleDimensionsMap = configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleDimensionsClient", "").getString();
		String littleDimensionList[] = littleDimensionsMap.split(",");
		for (String dimensionMap : littleDimensionList) {
			if (LBCore.littleDimensionClient == null) {
				LBCore.littleDimensionClient = new HashMap<Integer, Integer>();
			}
			String dimensionSplit[] = dimensionMap.split("-");
			String realWorldDimension = dimensionSplit[0];
			String littleWorldDimension = dimensionSplit[1];
			LBCore.littleDimensionClient.put(Integer.valueOf(realWorldDimension), Integer.valueOf(littleWorldDimension));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void saveClientLittleDimensions() {
		configuration.get(Configuration.CATEGORY_GENERAL, "littleDimensionsClient", "");
		String littleDimension = "";
		World worlds[] = DimensionManager.getWorlds();
		for (World world : worlds) {
			if (littleDimension != "") {
				littleDimension += ",";
			}
			int dimension = world.provider.dimensionId;
			if (LBCore.littleDimensionClient.containsKey(dimension)) {
				littleDimension += dimension + "-" + LBCore.littleDimensionClient.get(dimension);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void ClientConfig(File configFile) {
		if (configurationFile == null) {
			configurationFile = configFile;
			configuration = new Configuration(configFile);
		}
		configuration.load();
		loadClientLittleDimensions();
		configuration.save();
	}
	
	public static void CommonConfig(File configFile) {
		if (configurationFile == null) {
			configurationFile = configFile;
			configuration = new Configuration(configFile);
		}
		
		configuration.load();
		
		// Illegal blocks
		// Parse the list of illegal blocks separated by ;
		String disallowedBlockIDs[] = configuration.get(
				Configuration.CATEGORY_BLOCK,
				"disallowedBlockIDs",
				"").getString().split("\\;", -1);
		for (int i=0; i < disallowedBlockIDs.length; i++) {
			if (!disallowedBlockIDs[i].isEmpty()) {
				BlockUtil.registerDisallowedBlockID(Integer.valueOf(disallowedBlockIDs[i]));
			}
		}
		String disallowedItemIDs[] = configuration.get(
				Configuration.CATEGORY_ITEM,
				"disallowedItemIDs",
				"").getString().split("\\;", -1);
		for (int i=0; i < disallowedItemIDs.length; i++) {
			if (!disallowedItemIDs[i].isEmpty()) {
				BlockUtil.registerDisallowedItemID(Integer.valueOf(disallowedItemIDs[i]));
			}
		}
		
		LBCore.littleChunkID = configuration.get(
				Configuration.CATEGORY_BLOCK,
				"littleChunkID",
				1150).getInt();
		LBCore.littleBlocksWandID = configuration.get(
				Configuration.CATEGORY_ITEM,
				"littleBlocksWandID",
				29999).getInt();
		LBCore.littleBlocksCollectionID = configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksCollectionID",
				EntityRegistry.findGlobalUniqueEntityId()).getInt();
		LBCore.littleBlocksClip = configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksClip",
				true).getBoolean(true);
		LBCore.littleBlocksForceUpdate = configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksForceUpdate",
				false).getBoolean(false);
		LBCore.renderingMethod = configuration.get(
				Configuration.CATEGORY_GENERAL,
				"renderingMethod",
				0).getInt();
		LBCore.renderType = RenderingRegistry.getNextAvailableRenderId();
		LBCore.loggerLevel = configuration.get(
				Configuration.CATEGORY_GENERAL,
				"loggerLevel",
				"INFO").getString();
		configuration.save();
		
		LoggerLittleBlocks.getInstance("LittleBlocksConfig").setFilterLevel(LBCore.loggerLevel);
	}

}
