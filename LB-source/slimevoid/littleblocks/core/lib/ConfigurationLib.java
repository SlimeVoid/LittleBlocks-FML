package slimevoid.littleblocks.core.lib;

import java.io.File;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.DimensionManager;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConfigurationLib {

	private static File								configurationFile;
	private static Configuration					configuration;

	public static boolean							littleBlocksForceUpdate;
	public static String							loggerLevel			= "INFO";
	public static Block								littleChunk;
	public static Item								littleBlocksWand;
	@SideOnly(Side.CLIENT)
	public static HashMap<Integer, ILittleWorld>	littleWorldClient;
	public static HashMap<Integer, ILittleWorld>	littleWorldServer;
	public static int								littleChunkID;
	public static int								littleBlocksWandID;
	public static int								littleBlocksCollectionID;
	public static boolean							littleBlocksClip;
	public static int								renderingMethod;
	public static int								renderType;
	public static int								littleBlocksSize	= 8;

	@SideOnly(Side.CLIENT)
	public static RenderBlocks						littleRenderer;

	public static Configuration getConfiguration() {
		return configuration;
	}

	@SideOnly(Side.CLIENT)
	public static void ClientConfig(File configFile) {
		if (configurationFile == null) {
			configurationFile = configFile;
			configuration = new Configuration(configFile);
		}
	}

	public static void CommonConfig(File configFile) {
		if (configurationFile == null) {
			configurationFile = configFile;
			configuration = new Configuration(configFile);
		}

		configuration.load();

		// Illegal blocks
		// Parse the list of illegal blocks separated by ;
		String disallowedBlockIDs[] = configuration.get(Configuration.CATEGORY_BLOCK,
														"disallowedBlockIDs",
														"").getString().split(	"\\;",
																				-1);
		for (int i = 0; i < disallowedBlockIDs.length; i++) {
			if (!disallowedBlockIDs[i].isEmpty()) {
				BlockUtil.registerDisallowedBlockID(Integer.valueOf(disallowedBlockIDs[i]));
			}
		}
		String disallowedItemIDs[] = configuration.get(	Configuration.CATEGORY_ITEM,
														"disallowedItemIDs",
														"").getString().split(	"\\;",
																				-1);
		for (int i = 0; i < disallowedItemIDs.length; i++) {
			if (!disallowedItemIDs[i].isEmpty()) {
				BlockUtil.registerDisallowedItemID(Integer.valueOf(disallowedItemIDs[i]));
			}
		}

		littleChunkID = configuration.get(	Configuration.CATEGORY_BLOCK,
											"littleChunkID",
											1150).getInt();
		littleBlocksWandID = configuration.get(	Configuration.CATEGORY_ITEM,
												"littleBlocksWandID",
												29999).getInt();
		littleBlocksCollectionID = configuration.get(	Configuration.CATEGORY_GENERAL,
														"littleBlocksCollectionID",
														EntityRegistry.findGlobalUniqueEntityId()).getInt();
		littleBlocksClip = configuration.get(	Configuration.CATEGORY_GENERAL,
												"littleBlocksClip",
												true).getBoolean(true);
		littleBlocksForceUpdate = configuration.get(Configuration.CATEGORY_GENERAL,
													"littleBlocksForceUpdate",
													false).getBoolean(false);
		renderingMethod = configuration.get(Configuration.CATEGORY_GENERAL,
											"renderingMethod",
											0).getInt();
		renderType = RenderingRegistry.getNextAvailableRenderId();
		loggerLevel = configuration.get(Configuration.CATEGORY_GENERAL,
										"loggerLevel",
										"INFO").getString();
		configuration.save();

		LoggerLittleBlocks.getInstance("LittleBlocksConfig").setFilterLevel(loggerLevel);
	}

	@SideOnly(Side.CLIENT)
	public static RenderBlocks getLittleRenderer(World world) {
		/*
		 * if (littleRenderer != null &&
		 * !LBCore.littleWorldClient.isOutdated(world)) { return littleRenderer;
		 * }
		 */
		return setLittleRenderer(world);
	}

	@SideOnly(Side.CLIENT)
	public static RenderBlocks setLittleRenderer(World world) {
		if (world == null) {
			return littleRenderer = null;
		}
		return littleRenderer = new RenderBlocks(LittleBlocks.proxy.getLittleWorld(	world,
																					false));
	}

	@SideOnly(Side.CLIENT)
	public static int getLittleDimension(int dimension) {
		configuration.load();
		int littleDimension = configuration.get(Configuration.CATEGORY_GENERAL, "littleDimension[" + dimension + "]", DimensionManager.getNextFreeDimId()).getInt();
		configuration.save();
		return littleDimension;
	}

	public static int getLittleServerDimension(int dimension) {
		configuration.load();
		int littleDimension = configuration.get(Configuration.CATEGORY_GENERAL, "littleServerDimension[" + dimension + "]", DimensionManager.getNextFreeDimId()).getInt();
		configuration.save();
		return littleDimension;
	}

	public static boolean isLittleDimension(int dimension) {
		return configuration.hasKey(Configuration.CATEGORY_GENERAL, "littleServerDimension[" + dimension + "]");
	}

}
