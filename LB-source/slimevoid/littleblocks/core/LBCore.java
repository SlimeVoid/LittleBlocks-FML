package slimevoid.littleblocks.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import slimevoid.littleblocks.blocks.BlockLBPistonBase;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.lib.BlockLib;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.core.lib.ItemLib;
import slimevoid.littleblocks.core.lib.LocalizationLib;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.items.ItemLittleBlocksWand;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import slimevoidlib.util.BlockRemover;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class LBCore {

	public static void registerItems() {
		ConfigurationLib.littleChunk = new BlockLittleChunk(ConfigurationLib.littleChunkID, TileEntityLittleChunk.class, Material.wood, 2F, true).setUnlocalizedName(BlockLib.LITTLECHUNK);
		ConfigurationLib.littleBlocksWand = new ItemLittleBlocksWand(ConfigurationLib.littleBlocksWandID).setUnlocalizedName(ItemLib.WAND);
		BlockRemover.removeVanillaBlock(Block.pistonBase,
										false);
		BlockRemover.removeVanillaBlock(Block.pistonStickyBase,
										false);
		new BlockLBPistonBase(Block.pistonStickyBase.blockID, true).setUnlocalizedName("pistonStickyBase");
		new BlockLBPistonBase(Block.pistonBase.blockID, false).setUnlocalizedName("pistonBase");
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
}
