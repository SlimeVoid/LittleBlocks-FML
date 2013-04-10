package slimevoid.littleblocks.core;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import slimevoid.littleblocks.blocks.BlockLittleBlocks;
import slimevoid.littleblocks.core.lib.BlockLib;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.core.lib.ItemLib;
import slimevoid.littleblocks.core.lib.LocalizationLib;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.items.ItemLittleBlocks;
import slimevoid.littleblocks.items.ItemLittleBlocksChisel;
import slimevoid.littleblocks.items.ItemLittleBlocksCopier;
import slimevoid.littleblocks.items.LittleBlocksCollectionPickup;
import slimevoid.littleblocks.recipe.RecipesChisel;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
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
	public static int littleBlockID;
	public static Item littleBlock;
	public static Item littleBlockChisel;
	public static int littleBlockChiselID;

	public static void registerItems() {
		littleBlocks = new BlockLittleBlocks(
				littleBlocksID,
					TileEntityLittleBlocks.class,
					Material.wood,
					2F,
					true).setUnlocalizedName(BlockLib.LITTLEBLOCKS);
		littleBlocksCopier = new ItemLittleBlocksCopier(littleBlocksCopierID).setUnlocalizedName(ItemLib.COPIER_TOOL);
		littleBlock = new ItemLittleBlocks(littleBlockID).setUnlocalizedName(ItemLib.CHISELEDBLOCKS);
		//littleBlockChisel = new ItemLittleBlocksChisel(littleBlockChiselID).setUnlocalizedName(ItemLib.CHISEL);
		MinecraftForge.EVENT_BUS.register(new LittleBlocksCollectionPickup());
		// MinecraftForge.EVENT_BUS.register(new LittleContainerInteract());
		// MinecraftForge.EVENT_BUS.register(new PistonOrientation());
	}

	public static void registerNames() {
		LocalizationLib.registerLanguages();
	}

	public static void registerRecipes() {
		GameRegistry.addRecipe(new ItemStack(littleBlocks), new Object[] {
				"#",
				Character.valueOf('#'),
				Block.dirt });
		//GameRegistry.addRecipe(new RecipesChisel());
	}
	
	public static void registerBlocks() {
		GameRegistry.registerBlock(littleBlocks, BlockLib.LITTLEBLOCKS);
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
				BlockLib.TILE_LITTLEBLOCKS);
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
