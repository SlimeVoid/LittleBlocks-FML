package littleblocks.core;

import java.io.File;

import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.tileentities.TileEntityLittleBlocks;
import littleblocks.world.LittleWorld;
import net.minecraft.src.Block;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;
import net.minecraft.src.WorldProviderSurface;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.registry.GameRegistry;
import eurysmods.api.ICommonProxy;

public class LBCore {
	public static File configFile;
	public static Configuration configuration;
	public static String metaDataModifiedCommand = "METADATA";
	public static String idModifiedCommand = "IDMOD";
	public static String blockClickCommand = "BLOCKCLICK";
	public static String blockActivateCommand = "BLOCKACTIVATE";
	public static Block littleBlocks;
	private static LittleWorld littleWorld;
	public static int littleBlocksID;
	public static boolean littleBlocksClip;
	public static int renderingMethod;
	public static int renderType;
	public static boolean optifine;
	public static String denyBlockMessage = "Sorry, you cannot place that here!";
	public static String denyUseMessage = "Sorry, you cannot use that here!";
	public static String littleNotifyCommand = "LITTLENOTIFY";

	public static void initialize(ICommonProxy proxy) {
		LBInit.initialize(proxy);
	}

	public static void addItems() {
		littleBlocksID = configurationProperties();
		littleBlocks = new BlockLittleBlocks(
				littleBlocksID,
				TileEntityLittleBlocks.class,
				Material.wood,
				2F,
				true).setBlockName("littleBlocks");
		GameRegistry.registerBlock(littleBlocks);
	}

	public static void addNames() {
		ModLoader.addName(littleBlocks, "Little Blocks Block");
	}

	public static void addRecipes() {
		GameRegistry.addRecipe(new ItemStack(littleBlocks), new Object[] {
				"#",
				Character.valueOf('#'),
				Block.dirt });
	}

	public static int configurationProperties() {
		configuration.load();
		littleBlocksID = Integer.parseInt(configuration.getOrCreateIntProperty(
				"littleBlocksID",
				Configuration.CATEGORY_BLOCK,
				140).value);
		littleBlocksClip = Boolean.parseBoolean(configuration
				.getOrCreateBooleanProperty(
						"littleBlocksClip",
						Configuration.CATEGORY_GENERAL,
						true).value);
		renderingMethod = Integer.parseInt(configuration
				.getOrCreateIntProperty(
						"renderingMethod",
						Configuration.CATEGORY_GENERAL,
						0).value);
		renderType = RenderingRegistry.getNextAvailableRenderId();
		configuration.save();
		return littleBlocksID;
	}

	public static LittleWorld getLittleWorld() {
		return littleWorld;
	}
	
	public static LittleWorld getLittleWorld(World world, boolean needsRefresh) {
		if (needsRefresh) {
			littleWorld = null;
		}
		if (littleWorld == null || littleWorld.isOutdated(world)) {
			if (world == null) {
				return null;
			}
			if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
				littleWorld = new LittleWorld(world, new WorldProviderSurface());
				// if (!world.isRemote) {
				// littleWorld = new LittleWorld(
				// new WorldProviderSurface(),
				// world);
				// }
				// if (!ModLoader
				// .getMinecraftInstance()
				// .isIntegratedServerRunning()) {
				// littleWorld = new LittleWorld(
				// new WorldProviderSurface(),
				// world);
				// }
			} else {
				littleWorld = new LittleWorld(world, new WorldProviderSurface());
			}
		}
		return littleWorld;
	}

	public static void setLittleWorld(LittleWorld littleWorld) {
		LBCore.littleWorld = littleWorld;
	}
}
