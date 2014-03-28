package net.slimevoid.littleblocks.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.slimevoid.littleblocks.blocks.BlockLBDoor;
import net.slimevoid.littleblocks.blocks.BlockLBPistonBase;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.lib.BlockLib;
import net.slimevoid.littleblocks.core.lib.BlockUtil;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.ItemLib;
import net.slimevoid.littleblocks.core.lib.LocalizationLib;
import net.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import net.slimevoid.littleblocks.items.ItemLittleBlocksWand;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class LBCore {

    public static void registerItems() {
        ConfigurationLib.littleBlocksWand = new ItemLittleBlocksWand(ConfigurationLib.littleBlocksWandID).setUnlocalizedName(ItemLib.WAND);
        GameRegistry.registerItem(ConfigurationLib.littleBlocksWand,
                                  ItemLib.WAND);
    }

    public static void registerNames() {
        LocalizationLib.registerLanguages();
    }

    public static void registerRecipes() {
        GameRegistry.addRecipe(new ItemStack(ConfigurationLib.littleBlocksWand),
                               new Object[] {
                                       "LIL",
                                       "BGB",
                                       "BWB",
                                       Character.valueOf('L'),
                                       new ItemStack(Items.dye, 9, 4),
                                       Character.valueOf('I'),
                                       Items.iron_ingot,
                                       Character.valueOf('B'),
                                       new ItemStack(Items.dye, 1, 0),
                                       Character.valueOf('G'),
                                       Items.gold_nugget,
                                       Character.valueOf('W'),
                                       Blocks.wool });
    }

    public static void registerBlocks() {
        ConfigurationLib.littleChunk = new BlockLittleChunk(ConfigurationLib.littleChunkID, TileEntityLittleChunk.class, Material.wood, 2F, true).setBlockName(BlockLib.LITTLECHUNK);

        GameRegistry.registerBlock(ConfigurationLib.littleChunk,
                                   BlockLib.LITTLECHUNK);
        EntityRegistry.registerModEntity(EntityItemLittleBlocksCollection.class,
                                         "LittleBlocksCollection",
                                         ConfigurationLib.littleBlocksCollectionID,
                                         LittleBlocks.instance,
                                         256,
                                         1,
                                         false);
        GameRegistry.registerTileEntity(TileEntityLittleChunk.class,
                                        BlockLib.LITTLEBLOCKS);
        BlockUtil.registerPlacementInfo();

        // registerPistonOverride();
        // registerDoorOverride();
    }

    public static void registerPistonOverride() {

        // BlockRemover.removeVanillaBlock(Block.pistonBase,
        // false);
        // BlockRemover.removeVanillaBlock(Block.pistonStickyBase,
        // false);
        Block newPistonSticky = new BlockLBPistonBase(29, true).setBlockName("pistonStickyBase");
        Block newPistonBase = new BlockLBPistonBase(33, false).setBlockName("pistonBase");
        GameRegistry.registerBlock(newPistonSticky,
                                   "pistonStickyBase");
        GameRegistry.registerBlock(newPistonBase,
                                   "pistonBase");
        // ReflectionHelper.getInstance(Block.class).setFinalStaticFieldAtIndex(ConfigurationLib.pistonStickyIndex,
        // newPistonSticky);
        // ReflectionHelper.getInstance(Block.class).setFinalStaticFieldAtIndex(ConfigurationLib.pistonBaseIndex,
        // newPistonBase);
    }

    public static void registerDoorOverride() {
        // BlockRemover.removeVanillaBlock(Block.doorWood,
        // false);
        // BlockRemover.removeVanillaBlock(Block.doorIron,
        // false);
        Block newDoorWood = new BlockLBDoor(64, Material.wood).setHardness(3.0F).setStepSound(Block.soundTypeWood).setBlockName("doorWood").setBlockTextureName("door_wood");
        Block newDoorIron = new BlockLBDoor(71, Material.iron).setHardness(5.0F).setStepSound(Block.soundTypeMetal).setBlockName("doorIron").setBlockTextureName("door_iron");
        GameRegistry.registerBlock(newDoorWood,
                                   "doorWood");
        GameRegistry.registerBlock(newDoorIron,
                                   "doorIron");

        // ReflectionHelper.getInstance(Block.class).setFinalStaticFieldAtIndex(ConfigurationLib.doorWoodIndex,
        // newDoorWood);
        // ReflectionHelper.getInstance(Block.class).setFinalStaticFieldAtIndex(ConfigurationLib.doorIronIndex,
        // newDoorIron);
    }
}
