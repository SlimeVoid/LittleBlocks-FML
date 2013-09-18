package slimevoid.littleblocks.core;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.blocks.core.BlockLittleChunkBucketEvent;
import slimevoid.littleblocks.blocks.core.BlockLittleChunkShiftRightClick;
//import slimevoid.littleblocks.blocks.core.LittleContainerInteract;
import slimevoid.littleblocks.core.lib.BlockLib;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.core.lib.ItemLib;
import slimevoid.littleblocks.core.lib.LocalizationLib;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.items.ItemLittleBlocksWand;
import slimevoid.littleblocks.items.LittleBlocksCollectionPickup;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import slimevoid.littleblocks.world.LittleWorld;
import slimevoid.littleblocks.world.LittleWorldServer;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LBCore {
	public static boolean littleBlocksForceUpdate;
	public static String loggerLevel = "INFO";
	public static Block littleChunk;
	public static Item littleBlocksWand;
	@SideOnly(Side.CLIENT)
	public static HashMap<Integer, LittleWorld> littleWorldClient;
	public static HashMap<Integer, LittleWorld> littleWorldServer;
	public static int littleChunkID;
	public static int littleBlocksWandID;
	public static int littleBlocksCollectionID;
	public static boolean littleBlocksClip;
	public static int renderingMethod;
	public static int renderType;
	public static int littleBlocksSize = 8;

	@SideOnly(Side.CLIENT)
	private static RenderBlocks littleRenderer;
	@SideOnly(Side.CLIENT)
	public static HashMap<Integer, Integer> littleDimensionClient;
	@SideOnly(Side.CLIENT)
	public static HashMap<Integer, Integer>  littleProviderTypeClient;
	@SideOnly(Side.CLIENT)
	public static HashMap<Integer, WorldProvider> littleProviderClient;
	@SideOnly(Side.CLIENT)
	public static HashMap<Integer, Integer> loadedDimensionMapClient;

	// First Integer value is the 'RealWorld' Dimension ID
	public static HashMap<Integer, Integer> littleDimensionServer;
	public static HashMap<Integer, Integer> littleProviderTypeServer;
	public static HashMap<Integer, WorldProvider> littleProviderServer;
	public static HashMap<Integer, Integer> loadedDimensionMapServer;

	public static void registerItems() {
		littleChunk = new BlockLittleChunk(
				littleChunkID,
					TileEntityLittleChunk.class,
					Material.wood,
					2F,
					true).setUnlocalizedName(BlockLib.LITTLECHUNK);
		littleBlocksWand = new ItemLittleBlocksWand(littleBlocksWandID).setUnlocalizedName(ItemLib.WAND);
	}

	public static void registerNames() {
		LocalizationLib.registerLanguages();
	}

	public static void registerRecipes() {
		GameRegistry.addRecipe(new ItemStack(littleBlocksWand), new Object[] {
				"#",
				Character.valueOf('#'),
				Block.dirt });
	}
	
	public static void registerBlocks() {
		GameRegistry.registerBlock(littleChunk, BlockLib.LITTLECHUNK);
		EntityRegistry.registerModEntity(
				EntityItemLittleBlocksCollection.class,
				"LittleBlocksCollection",
				littleBlocksCollectionID,
				LittleBlocks.instance,
				256,
				1,
				false);
		GameRegistry.registerTileEntity(
				TileEntityLittleChunk.class,
				BlockLib.LITTLEBLOCKS);
		BlockUtil.registerPlacementInfo();
	}

	public static void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new LittleBlocksCollectionPickup());
		MinecraftForge.EVENT_BUS.register(new BlockLittleChunkShiftRightClick());
		MinecraftForge.EVENT_BUS.register(new BlockLittleChunkBucketEvent());
		// MinecraftForge.EVENT_BUS.register(new LittleLadderHandler());
		// MinecraftForge.EVENT_BUS.register(new LittleContainerInteract());
		// MinecraftForge.EVENT_BUS.register(new PistonOrientation());
	}

	@SideOnly(Side.CLIENT)
	public static RenderBlocks getLittleRenderer(World world) {
		/*if (littleRenderer != null && !LBCore.littleWorldClient.isOutdated(world)) {
			return littleRenderer;
		}*/
		return setLittleRenderer(world);
	}

	@SideOnly(Side.CLIENT)
	public static RenderBlocks setLittleRenderer(World world) {
		if (world == null) {
			return littleRenderer = null;
		}
		return littleRenderer = new RenderBlocks(
				LittleBlocks.proxy.getLittleWorld(
						world,
						false));
	}
	
	public static void registerLittleWorldServer(World referenceWorld) {
		int realWorldDimension = referenceWorld.provider.dimensionId;
		int littleWorldDimension = DimensionManager.getNextFreeDimId();
		littleDimensionServer.put(realWorldDimension, littleWorldDimension);
		DimensionManager.registerProviderType(littleWorldDimension, WorldProviderSurface.class, true);
		DimensionManager.registerDimension(
				littleWorldDimension,
				littleWorldDimension);
		littleProviderTypeServer.put(realWorldDimension, DimensionManager.getProviderType(littleWorldDimension));
		littleProviderServer.put(realWorldDimension, DimensionManager.createProviderFor(littleWorldDimension));
		littleWorldServer.put(realWorldDimension, new LittleWorldServer(
				referenceWorld,
				littleProviderServer.get(realWorldDimension)));
	}

	public static void registerLittleWorldServers() {
		if (littleWorldServer == null) {
			littleWorldServer = new HashMap<Integer, LittleWorld>();
			littleDimensionServer = new HashMap<Integer, Integer>();
			littleProviderTypeServer = new HashMap<Integer, Integer>();
			littleProviderServer = new HashMap<Integer, WorldProvider>();
		}
		if (littleWorldServer.isEmpty()) {
			World[] worlds = DimensionManager.getWorlds();
			for (World world : worlds) {
				registerLittleWorldServer(world);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerLittleWorldClient(World referenceWorld) {
		int realWorldDimension = referenceWorld.provider.dimensionId;
		int littleWorldDimension = DimensionManager.getNextFreeDimId(); 
		littleDimensionClient.put(realWorldDimension, littleWorldDimension);
		DimensionManager.registerProviderType(littleWorldDimension, WorldProviderSurface.class, false);
		DimensionManager.registerDimension(
				littleWorldDimension,
				littleWorldDimension);
		littleProviderTypeClient.put(realWorldDimension, DimensionManager.getProviderType(littleWorldDimension));
		littleProviderClient.put(realWorldDimension, DimensionManager
				.createProviderFor(littleWorldDimension));
		littleWorldClient.put(realWorldDimension, new LittleWorld(
				referenceWorld, littleProviderClient.get(realWorldDimension)));
	}
	
	@SideOnly(Side.CLIENT)
	public static void registerLittleWorldClients() {
		if (littleWorldClient == null) {
			littleWorldClient = new HashMap<Integer, LittleWorld>();
			littleDimensionClient = new HashMap<Integer, Integer>();
			littleProviderTypeClient = new HashMap<Integer, Integer>();
			littleProviderClient = new HashMap<Integer, WorldProvider>();
		}
		if (littleWorldClient.isEmpty()) {
			World[] worlds = DimensionManager.getWorlds();
			for (World world : worlds) {
				registerLittleWorldClient(world);
			}
		}
	}
}
