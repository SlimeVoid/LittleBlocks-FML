package littleblocks.core;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import littleblocks.api.ILBCommonProxy;
import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.tileentities.TileEntityLittleBlocks;
import littleblocks.world.LittleWorld;
import net.minecraft.src.Block;
import net.minecraft.src.BlockFlowing;
import net.minecraft.src.BlockFluid;
import net.minecraft.src.BlockPistonBase;
import net.minecraft.src.Item;
import net.minecraft.src.ItemHoe;
import net.minecraft.src.ItemMonsterPlacer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityNote;
import net.minecraft.src.World;
import net.minecraft.src.WorldProvider;
import net.minecraftforge.common.Configuration;
import buildcraft.core.IItemPipe;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.registry.GameRegistry;
import eurysmods.api.ICommonProxy;
import eurysmods.data.Logger;

public class LBCore {
	public static File configFile;
	public static Configuration configuration;
	public static String metaDataModifiedCommand = "METADATA";
	public static String idModifiedCommand = "IDMOD";
	public static String blockClickCommand = "BLOCKCLICK";
	public static String blockActivateCommand = "BLOCKACTIVATE";
	public static String littleNotifyCommand = "LITTLENOTIFY";
	public static String updateClientCommand = "UPDATECLIENT";
	public static String loggerLevel = "INFO";
	public static Block littleBlocks;
	@SideOnly(Side.CLIENT)
	public static LittleWorld littleWorldClient;
	public static LittleWorld littleWorldServer;
	public static int littleBlocksID;
	public static boolean littleBlocksClip;
	public static int renderingMethod;
	public static int renderType;
	public static boolean optifine;
	public static String denyPlacementMessage = "Sorry, you cannot place that here!";
	public static String denyUseMessage = "Sorry, you cannot use that here!";
	public static int littleBlocksSize = 8;
	private static Set<Integer> disallowedItemIDs = new HashSet();
	private static Set<Class<? extends Item>> disallowedItems = new HashSet();
	private static Set<Class<? extends Block>> disallowedBlocks = new HashSet();
	private static Set<Class<? extends TileEntity>> allowedBlockTileEntities = new HashSet();
	private static Set<Class<? extends Block>> disallowedBlocksToTick = new HashSet();

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
		addDisallowedBlockTick(BlockFluid.class);
		addDisallowedBlockTick(BlockFlowing.class);
		addDisallowedBlock(BlockPistonBase.class);
		addAllowedTile(TileEntityNote.class);
		addDisallowedItem(ItemHoe.class);
		addDisallowedItem(ItemMonsterPlacer.class);
		// MinecraftForge.EVENT_BUS.register(new PistonOrientation());
	}

	public static void addDisallowedBlockTick(Class<? extends Block> blockClass) {
		if (blockClass != null) {
			if (!disallowedBlocksToTick.contains(blockClass)) {
				disallowedBlocksToTick.add(blockClass);
			}
		}
	}

	public static boolean isBlockAllowedToTick(Block littleBlock) {
		if (littleBlock != null) {
			if (disallowedBlocksToTick.contains(littleBlock.getClass())) {
				return false;
			}
		}
		return true;
	}

	private static void addDisallowedBlock(Class<? extends Block> blockClass) {
		if (blockClass != null) {
			if (!disallowedBlocks.contains(blockClass)) {
				disallowedBlocks.add(blockClass);
			}
		}
	}

	public static boolean isBlockAllowed(Block block) {
		if (block != null) {
			if (disallowedBlocks.contains(block.getClass())) {
				return false;
			}
		}
		return true;
	}

	public static void addDisallowedItemIDs(Integer itemID) {
		if (itemID > Block.blocksList.length) {
			if (!disallowedItemIDs.contains(itemID)) {
				disallowedItemIDs.add(itemID);
			}
		}
	}

	public static void addDisallowedItem(Class<? extends Item> itemClass) {
		if (itemClass != null) {
			if (!disallowedItems.contains(itemClass)) {
				disallowedItems.add(itemClass);
			}
		}
	}

	public static boolean isItemAllowed(Item item) {
		boolean isAllowed = false;
		if (item != null) {
			isAllowed = true;
			if (disallowedItems.contains(item.getClass())) {
				isAllowed = false;
			}
			if (disallowedItemIDs.contains(item.shiftedIndex)) {
				isAllowed = false;
			}
			if (item instanceof IItemPipe) {
				isAllowed = false;
			}
		}
		return isAllowed;
	}

	public static void addAllowedTile(Class<? extends TileEntity> tileclass) {
		if (tileclass != null) {
			if (!allowedBlockTileEntities.contains(tileclass)) {
				allowedBlockTileEntities.add(tileclass);
			} else {
				LoggerLittleBlocks.getInstance(
						LoggerLittleBlocks.filterClassName(LBCore.class.toString())
				).write(
						true,
						"Tried to add a tileentity to the disallowed list that already exists",
						Logger.LogLevel.DEBUG
				);
			}
		}
	}

	public static boolean isTileEntityAllowed(TileEntity tileentity) {
		if (tileentity != null) {
			if (allowedBlockTileEntities.contains(tileentity.getClass())) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasTile(int itemBlockId) {
		if (Block.blocksList[itemBlockId] != null) {
			Block theBlock = Block.blocksList[itemBlockId];
			if (theBlock.hasTileEntity(0)) {
				return true;
			}
		}
		return false;
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
		littleBlocksID = Integer.parseInt(configuration.get(
				Configuration.CATEGORY_BLOCK,
				"littleBlocksID",
				150).value);
		littleBlocksClip = Boolean.parseBoolean(configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksClip",
				true).value);
		renderingMethod = Integer.parseInt(configuration.get(
				Configuration.CATEGORY_GENERAL,
				"renderingMethod",
				0).value);
		renderType = RenderingRegistry.getNextAvailableRenderId();
		System.out.println(loggerLevel);
		loggerLevel = String.valueOf(configuration.get(
				Configuration.CATEGORY_GENERAL,
				"loggerLevel",
				"INFO").value);
		System.out.println(loggerLevel);
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
