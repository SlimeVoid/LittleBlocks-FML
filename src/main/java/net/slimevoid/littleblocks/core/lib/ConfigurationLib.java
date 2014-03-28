package net.slimevoid.littleblocks.core.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.slimevoid.littleblocks.core.LoggerLittleBlocks;
import net.slimevoid.littleblocks.world.LittleWorldClient;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConfigurationLib {

    private static File                     configurationFile;
    private static Configuration            configuration;

    public static LittleWorldClient         littleWorldClient;
    public static HashMap<Integer, Integer> littleWorldServer      = new HashMap<Integer, Integer>();

    public static boolean                   littleBlocksForceUpdate;
    public static String                    loggerLevel            = "INFO";
    public static Block                     littleChunk;
    public static Item                      littleBlocksWand;
    public static int                       littleChunkID;
    public static int                       littleBlocksWandID;
    public static int                       littleBlocksCollectionID;
    public static boolean                   littleBlocksClip;
    public static int                       renderingMethod;
    public static int                       renderType;
    public static int                       littleBlocksSize       = 8;

    private static String                   CATEGORY_OVERRIDES     = "overrides";
    public static int                       pistonStickyIndex      = 50;
    public static int                       pistonBaseIndex        = 54;
    public static int                       doorWoodIndex          = 85;
    public static int                       doorIronIndex          = 92;

    private static List<Integer>            disallowedDimensionIds = new ArrayList<Integer>();

    @SideOnly(Side.CLIENT)
    public static RenderBlocks              littleRenderer;

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
        String disallowedBlockIDs[] = configuration.get(Configuration.CATEGORY_GENERAL,
                                                        "disallowedBlockIDs",
                                                        "").getString().split("\\;",
                                                                              -1);
        for (int i = 0; i < disallowedBlockIDs.length; i++) {
            if (!disallowedBlockIDs[i].isEmpty()) {
                BlockUtil.registerDisallowedBlockID(Integer.valueOf(disallowedBlockIDs[i]));
            }
        }
        String disallowedItemIDs[] = configuration.get(Configuration.CATEGORY_GENERAL,
                                                       "disallowedItemIDs",
                                                       "").getString().split("\\;",
                                                                             -1);
        for (int i = 0; i < disallowedItemIDs.length; i++) {
            if (!disallowedItemIDs[i].isEmpty()) {
                BlockUtil.registerDisallowedItemID(Integer.valueOf(disallowedItemIDs[i]));
            }
        }

        littleChunkID = configuration.get(Configuration.CATEGORY_GENERAL,
                                          "littleChunkID",
                                          1150).getInt();
        littleBlocksWandID = configuration.get(Configuration.CATEGORY_GENERAL,
                                               "littleBlocksWandID",
                                               29999).getInt();
        littleBlocksCollectionID = configuration.get(Configuration.CATEGORY_GENERAL,
                                                     "littleBlocksCollectionID",
                                                     EntityRegistry.findGlobalUniqueEntityId()).getInt();
        littleBlocksClip = configuration.get(Configuration.CATEGORY_GENERAL,
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

        int[] disallowedIds = configuration.get(Configuration.CATEGORY_GENERAL,
                                                "disallowedLittleDimensionIds",
                                                new int[] { 7, 20 }).getIntList();

        for (int disallowedId : disallowedIds) {
            disallowedDimensionIds.add(disallowedId);
        }

        pistonStickyIndex = configuration.get(CATEGORY_OVERRIDES,
                                              "pistonStickyIndex",
                                              pistonStickyIndex).getInt();
        pistonBaseIndex = configuration.get(CATEGORY_OVERRIDES,
                                            "pistonBaseIndex",
                                            pistonBaseIndex).getInt();

        doorWoodIndex = configuration.get(CATEGORY_OVERRIDES,
                                          "doorWoodIndex",
                                          doorWoodIndex).getInt();
        doorIronIndex = configuration.get(CATEGORY_OVERRIDES,
                                          "doorIronIndex",
                                          doorIronIndex).getInt();

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
        return littleRenderer = new RenderBlocks(littleWorldClient);
    }

    public static int getLittleServerDimension(int dimension) {
        configuration.load();
        int candidateDimensionId = DimensionManager.getNextFreeDimId();
        while (disallowedDimensionIds.contains(candidateDimensionId)
               || DimensionManager.isDimensionRegistered(candidateDimensionId)) {
            candidateDimensionId++;
        }
        int littleDimension = configuration.get(Configuration.CATEGORY_GENERAL,
                                                FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName()
                                                        + "-littleServerDimension["
                                                        + dimension + "]",
                                                candidateDimensionId).getInt();
        configuration.save();
        return littleDimension;
    }

    public static boolean isLittleDimension(int dimension) {
        return configuration.hasKey(Configuration.CATEGORY_GENERAL,
                                    "littleServerDimension[" + dimension + "]");
    }

}
