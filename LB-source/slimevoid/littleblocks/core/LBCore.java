package slimevoid.littleblocks.core;

import java.io.File;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.lib.BlockLib;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.core.lib.ItemLib;
import slimevoid.littleblocks.core.lib.LocalizationLib;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.items.ItemLittleBlocksWand;
import slimevoid.littleblocks.items.LittleBlocksCollectionPickup;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import slimevoid.littleblocks.world.LittleWorld;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LBCore {
	public static File configFile;
	public static Configuration configuration;
	public static boolean littleBlocksForceUpdate;
	public static String loggerLevel = "INFO";
	public static Set<Integer> prohibitIDList;
	public static Block littleChunk;
	public static Item littleBlocksWand;
	@SideOnly(Side.CLIENT)
	public static LittleWorld littleWorldClient;
	public static LittleWorld littleWorldServer;
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
	public static int littleDimensionClient;
	@SideOnly(Side.CLIENT)
	public static int littleProviderTypeClient;
	@SideOnly(Side.CLIENT)
	public static WorldProvider littleProviderClient;

	public static int littleDimensionServer;
	public static int littleProviderTypeServer;
	public static WorldProvider littleProviderServer;

	public static void registerItems() {
		littleChunk = new BlockLittleChunk(
				littleChunkID,
					TileEntityLittleChunk.class,
					Material.wood,
					2F,
					true).setUnlocalizedName(BlockLib.LITTLECHUNK);
		littleBlocksWand = new ItemLittleBlocksWand(littleBlocksWandID).setUnlocalizedName(ItemLib.COPIER_TOOL);
		MinecraftForge.EVENT_BUS.register(new LittleBlocksCollectionPickup());
		// MinecraftForge.EVENT_BUS.register(new LittleContainerInteract());
		// MinecraftForge.EVENT_BUS.register(new PistonOrientation());
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
				BlockLib.TILE_LITTLECHUNK);
		BlockUtil.registerPlacementInfo();
	}

	@SideOnly(Side.CLIENT)
	public static RenderBlocks getLittleRenderer(World world) {
		if (littleRenderer != null && LittleBlocks.proxy
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
				LittleBlocks.proxy.getLittleWorld(
						world,
						false));
	}
}
