package littleblocks.core;

import java.io.File;

import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.blocks.core.PistonOrientation;
import littleblocks.tileentities.TileEntityLittleBlocks;
import littleblocks.world.LittleWorld;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import eurysmods.api.ICommonProxy;

public class LBCore {
	public static File configFile;
	public static Configuration configuration;
	public static String metaDataModifiedCommand = "METADATA";
	public static String idModifiedCommand = "IDMOD";
	public static String blockClickCommand = "BLOCKCLICK";
	public static String blockActivateCommand = "BLOCKACTIVATE";
	public static String littleNotifyCommand = "LITTLENOTIFY";
	public static String updateClientCommand = "UPDATECLIENT";
	public static Block littleBlocks;
	public static LittleWorld littleWorldClient;
	public static LittleWorld littleWorldServer;
	public static int littleBlocksID;
	public static boolean littleBlocksClip;
	public static int renderingMethod;
	public static int renderType;
	public static boolean optifine;
	public static String denyBlockMessage = "Sorry, you cannot place that here!";
	public static String denyUseMessage = "Sorry, you cannot use that here!";
	public static int littleBlocksSize = 8;

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
		MinecraftForge.EVENT_BUS.register(new PistonOrientation());
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
}
