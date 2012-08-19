package littleblocks.core;

import java.io.File;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.ItemBucket;
import net.minecraft.src.ItemInWorldManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;
import net.minecraft.src.EurysMods.proxy.ICommonProxy;
import net.minecraftforge.common.Configuration;

import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.network.LittleBlocksConnection;
import littleblocks.tileentities.TileEntityLittleBlocks;

public class LBCore {
	public static File configFile;// = new File(LittleBlocks.minecraftDir, "config/LittleBlocks.cfg");
	public static Configuration configuration; // = new Configuration(configFile);
	public static String metaDataModifiedCommand = "METADATA";
	public static String idModifiedCommand = "IDMOD";
	public static String blockClickCommand = "BLOCKCLICK";
	public static String blockActivateCommand = "BLOCKACTIVATE";
	public static Block littleBlocks;
	public static int littleBlocksID;
	public static int renderingMethod;
	public static int renderType;
	public static boolean optifine;

	public static void initialize(ICommonProxy proxy) {
		LBInit.initialize(proxy);
	}

	public static void addItems() {
		littleBlocksID = configurationProperties();
		littleBlocks = new BlockLittleBlocks(littleBlocksID).setBlockName("littleBlocks");
		GameRegistry.registerBlock(littleBlocks);
	}
	
	public static void addNames() {
		ModLoader.addName(littleBlocks, "Little Blocks Block");
	}

	public static void addRecipes() {
		GameRegistry
					.addRecipe(new ItemStack(littleBlocks),
							new Object[] {"#",
							Character.valueOf('#'), Block.dirt });
	}
	
	public static int configurationProperties() {
		configuration.load();
		littleBlocksID = Integer.parseInt(configuration
				.getOrCreateIntProperty("littleBlocksID",
						Configuration.CATEGORY_BLOCK,
						140).value);
		renderingMethod = Integer.parseInt(configuration
				.getOrCreateIntProperty("renderingMethod",
						Configuration.CATEGORY_GENERAL,
						0).value);
		renderType = RenderingRegistry.getNextAvailableRenderId();
		configuration.save();
		return littleBlocksID;
	}
}
