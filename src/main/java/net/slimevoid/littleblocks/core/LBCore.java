package net.slimevoid.littleblocks.core;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.slimevoid.library.core.SlimevoidCore;
import net.slimevoid.library.util.helpers.PacketHelper;
import net.slimevoid.littleblocks.api.util.LittleBlocksHelper;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.lib.*;
import net.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import net.slimevoid.littleblocks.items.ItemLittleBlocksWand;
import net.slimevoid.littleblocks.items.wand.EnumWandAction;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

public class LBCore {

    private static boolean initialized = false;

    public static void preInitialize() {
        if (initialized) {
            return;
        }
        PacketHelper.registerHandler();
        LittleBlocks.proxy.preInit();

        SlimevoidCore.console(CoreLib.MOD_ID,
                "Registering names...");
        registerNames();

        SlimevoidCore.console(CoreLib.MOD_ID,
                "Registering items...");
        registerItems();

        SlimevoidCore.console(CoreLib.MOD_ID,
                "Registering blocks...");
        registerBlocks();
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        LittleBlocks.proxy.init();

        SlimevoidCore.console(CoreLib.MOD_ID,
                "Registering event handlers...");
        LittleBlocks.proxy.registerEventHandlers();

        SlimevoidCore.console(CoreLib.MOD_ID,
                "Registering render information...");
        LittleBlocks.proxy.registerRenderInformation();

        SlimevoidCore.console(CoreLib.MOD_ID,
                "Registering tick handlers...");
        LittleBlocks.proxy.registerTickHandlers();

        SlimevoidCore.console(CoreLib.MOD_ID,
                "Registering recipes...");
        registerRecipes();

        SlimevoidCore.console(CoreLib.MOD_ID,
                "Initializing Little helper...");
        LittleBlocksHelper.init(LittleBlocks.proxy,
                ConfigurationLib.littleBlocksSize);

        SlimevoidCore.console(CoreLib.MOD_ID,
                "Registering Little Wand...");
        EnumWandAction.registerWandActions();
    }

    public static void postInitialize() {
        if (initialized) {
            return;
        }
        LittleBlocks.proxy.postInit();
        initialized = true;
    }

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
        ConfigurationLib.littleChunk = new BlockLittleChunk(ConfigurationLib.littleChunkID, TileEntityLittleChunk.class, Material.wood, 2F, true).setUnlocalizedName(BlockLib.LITTLECHUNK);

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
        // Block newPistonSticky = new BlockLBPistonBase(29, true).setBlockName("pistonStickyBase");
        // Block newPistonBase = new BlockLBPistonBase(33, false).setBlockName("pistonBase");
        // GameRegistry.registerBlock(newPistonSticky,
        //                           "pistonStickyBase");
        // GameRegistry.registerBlock(newPistonBase,
        //                           "pistonBase");
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
        // Block newDoorWood = new BlockLBDoor(64, Material.wood).setHardness(3.0F).setStepSound(Block.soundTypeWood).setBlockName("doorWood").setBlockTextureName("door_wood");
        // Block newDoorIron = new BlockLBDoor(71, Material.iron).setHardness(5.0F).setStepSound(Block.soundTypeMetal).setBlockName("doorIron").setBlockTextureName("door_iron");
        // GameRegistry.registerBlock(newDoorWood,
        //                           "doorWood");
        // GameRegistry.registerBlock(newDoorIron,
        //                           "doorIron");

        // ReflectionHelper.getInstance(Block.class).setFinalStaticFieldAtIndex(ConfigurationLib.doorWoodIndex,
        // newDoorWood);
        // ReflectionHelper.getInstance(Block.class).setFinalStaticFieldAtIndex(ConfigurationLib.doorIronIndex,
        // newDoorIron);
    }
}
