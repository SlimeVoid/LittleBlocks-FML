package slimevoid.littleblocks.core;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.blocks.core.BlockLittleChunkBucketEvent;
import slimevoid.littleblocks.blocks.core.BlockLittleChunkShiftRightClick;
import slimevoid.littleblocks.blocks.core.LittleContainerInteract;
import slimevoid.littleblocks.blocks.core.PlayerInteractInterrupt;
import slimevoid.littleblocks.core.lib.BlockLib;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.core.lib.ItemLib;
import slimevoid.littleblocks.core.lib.LocalizationLib;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.items.ItemLittleBlocksWand;
import slimevoid.littleblocks.items.LittleBlocksCollectionPickup;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import slimevoid.littleblocks.world.LittleWorldClient;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class LBCore {

	public static LittleWorldClient			littleWorldClient;
	public static HashMap<Integer, Integer>	littleWorldServer	= new HashMap<Integer, Integer>();

	public static void registerItems() {
		ConfigurationLib.littleChunk = new BlockLittleChunk(ConfigurationLib.littleChunkID, TileEntityLittleChunk.class, Material.wood, 2F, true).setUnlocalizedName(BlockLib.LITTLECHUNK);
		ConfigurationLib.littleBlocksWand = new ItemLittleBlocksWand(ConfigurationLib.littleBlocksWandID).setUnlocalizedName(ItemLib.WAND);
	}

	public static void registerNames() {
		LocalizationLib.registerLanguages();
	}

	public static void registerRecipes() {
		GameRegistry.addRecipe(	new ItemStack(ConfigurationLib.littleBlocksWand),
								new Object[] {
										"#",
										Character.valueOf('#'),
										Block.dirt });
	}

	public static void registerBlocks() {
		GameRegistry.registerBlock(	ConfigurationLib.littleChunk,
									BlockLib.LITTLECHUNK);
		EntityRegistry.registerModEntity(	EntityItemLittleBlocksCollection.class,
											"LittleBlocksCollection",
											ConfigurationLib.littleBlocksCollectionID,
											LittleBlocks.instance,
											256,
											1,
											false);
		GameRegistry.registerTileEntity(TileEntityLittleChunk.class,
										BlockLib.LITTLEBLOCKS);
		BlockUtil.registerPlacementInfo();
	}

	public static void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new LittleBlocksCollectionPickup());
		MinecraftForge.EVENT_BUS.register(new BlockLittleChunkShiftRightClick());
		//MinecraftForge.EVENT_BUS.register(new PlayerInteractInterrupt());
		MinecraftForge.EVENT_BUS.register(new BlockLittleChunkBucketEvent());
		// MinecraftForge.EVENT_BUS.register(new LittleLadderHandler());
		MinecraftForge.EVENT_BUS.register(new LittleContainerInteract());
		// MinecraftForge.EVENT_BUS.register(new PistonOrientation());
	}
}
