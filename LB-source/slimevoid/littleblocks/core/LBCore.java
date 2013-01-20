package slimevoid.littleblocks.core;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import slimevoid.lib.core.SlimevoidCore;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.api.util.LittleBlocksHelper;
import slimevoid.littleblocks.blocks.BlockLittleBlocks;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.items.ItemLittleBlocksCopier;
import slimevoid.littleblocks.items.LittleBlocksCollectionPickup;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
import slimevoid.littleblocks.world.LittleWorld;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LBCore {
	public static File configFile;
	public static Configuration configuration;
	public static boolean littleBlocksForceUpdate;
	public static String loggerLevel = "INFO";
	public static Block littleBlocks;
	public static Item littleBlocksCopier;
	@SideOnly(Side.CLIENT)
	public static LittleWorld littleWorldClient;
	public static LittleWorld littleWorldServer;
	public static int littleBlocksID;
	public static int littleBlocksCopierID;
	public static int littleBlocksCollectionID;
	public static boolean littleBlocksClip;
	public static int renderingMethod;
	public static int renderType;
	public static boolean optifine;
	public static String denyPlacementMessage = "Sorry, you cannot place that here!";
	public static String denyUseMessage = "Sorry, you cannot use that here!";
	public static String littleBlockCopierMessage = "Sorry, that feature is only available in Creative Mode!";
	public static int littleBlocksSize = 8;

	@SideOnly(Side.CLIENT)
	private static RenderBlocks littleRenderer;
	@SideOnly(Side.CLIENT)
	public static int littleDimensionClient;
	@SideOnly(Side.CLIENT)
	public static int littleProviderTypeClient;
	@SideOnly(Side.CLIENT)
	public static WorldProvider littleProviderClient;

	public static int littleDimensionServer;
	public static int littleProviderTypeServer;
	public static WorldProvider littleProviderServer;

	public static void initialize() {
		LBCore.configFile = new File(
				LBInit.LBM.getProxy().getMinecraftDir(),
					"config/LittleBlocks.cfg");
		LBCore.configuration = new Configuration(LBCore.configFile);
		littleBlocksID = configurationProperties();
		LittleBlocksHelper.init(LBInit.LBM.getProxy(), littleBlocksSize);
	}

	public static void addItems() {
		SlimevoidCore.console(LBInit.LBM.getModName(), "Registering items...");
		littleBlocks = new BlockLittleBlocks(
				littleBlocksID,
					TileEntityLittleBlocks.class,
					Material.wood,
					2F,
					true).setBlockName("littleBlocks");
		littleBlocksCopier = new ItemLittleBlocksCopier(littleBlocksCopierID).setItemName("LittleBlocksCopier");
		GameRegistry.registerBlock(littleBlocks, "littleBlocks");
		EntityRegistry.registerModEntity(
				EntityItemLittleBlocksCollection.class,
				"LittleBlocksCollection",
				littleBlocksCollectionID,
				LittleBlocks.instance,
				256,
				1,
				false);
		GameRegistry.registerTileEntity(
				TileEntityLittleBlocks.class,
				"littleBlocks");
		BlockUtil.registerPlacementInfo();
		MinecraftForge.EVENT_BUS.register(new LittleBlocksCollectionPickup());
		// MinecraftForge.EVENT_BUS.register(new LittleContainerInteract());
		// MinecraftForge.EVENT_BUS.register(new PistonOrientation());
	}

	public static void addNames() {
		SlimevoidCore.console(LBInit.LBM.getModName(), "Naming items...");
		ModLoader.addName(littleBlocks, "Little Blocks Block");
		ModLoader.addName(littleBlocksCopier, "Little Blocks Tool");
	}

	public static void addRecipes() {
		SlimevoidCore.console(LBInit.LBM.getModName(), "Registering recipes...");
		GameRegistry.addRecipe(new ItemStack(littleBlocks), new Object[] {
				"#",
				Character.valueOf('#'),
				Block.dirt });
	}

	public static int configurationProperties() {
		configuration.load();
		littleBlocksID = Integer.parseInt(configuration.get(
				Configuration.CATEGORY_BLOCK,
				"littleBlocksID",
				150).value);
		littleBlocksCopierID = Integer.parseInt(configuration.get(
				Configuration.CATEGORY_ITEM,
				"littleBlocksCopierID",
				29999).value);
		littleBlocksCollectionID = Integer.parseInt(configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksCollectionID",
				EntityRegistry.findGlobalUniqueEntityId()).value);
		littleBlocksClip = Boolean.parseBoolean(configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksClip",
				true).value);
		littleBlocksForceUpdate = Boolean.parseBoolean(configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksForceUpdate",
				false).value);
		renderingMethod = Integer.parseInt(configuration.get(
				Configuration.CATEGORY_GENERAL,
				"renderingMethod",
				0).value);
		renderType = RenderingRegistry.getNextAvailableRenderId();
		loggerLevel = String.valueOf(configuration.get(
				Configuration.CATEGORY_GENERAL,
				"loggerLevel",
				"INFO").value);
		configuration.save();
		LoggerLittleBlocks.getInstance("LittleBlocksConfig").setFilterLevel(loggerLevel);
		return littleBlocksID;
	}

	@SideOnly(Side.CLIENT)
	public static RenderBlocks getLittleRenderer(World world) {
		if (littleRenderer != null && ((ILBCommonProxy) LBInit.LBM.getProxy())
				.getLittleWorld(world, false)
					.getRealWorld() == world) {
			return littleRenderer;
		}
		return setLittleRenderer(world);
	}

	@SideOnly(Side.CLIENT)
	public static RenderBlocks setLittleRenderer(World world) {
		if (world == null) {
			return littleRenderer = null;
		}
		return littleRenderer = new RenderBlocks(
				((ILBCommonProxy) LBInit.LBM.getProxy()).getLittleWorld(
						world,
						false));
	}
}
