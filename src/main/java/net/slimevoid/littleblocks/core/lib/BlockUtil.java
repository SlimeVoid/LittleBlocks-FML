package net.slimevoid.littleblocks.core.lib;

import ibxm.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.slimevoid.library.data.Logger;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.LBCore;
import net.slimevoid.littleblocks.core.LoggerLittleBlocks;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import net.slimevoid.littleblocks.world.ItemInLittleWorldManager;
import net.slimevoid.littleblocks.world.LittlePlayerController;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockUtil {

    @SideOnly(Side.CLIENT)
    private static LittlePlayerController                            littlePlayerController;

    private static HashMap<EntityPlayerMP, ItemInLittleWorldManager> itemInLittleWorldManagers;

    @SideOnly(Side.CLIENT)
    public static void setLittleController(LittlePlayerController controller, WorldSettings.GameType gameType) {
        littlePlayerController = controller;
        littlePlayerController.setGameType(gameType);
    }

    @SideOnly(Side.CLIENT)
    public static LittlePlayerController getLittleController() {
        if (littlePlayerController == null) {
            EntityPlayer entityplayer = FMLClientHandler.instance().getClientPlayerEntity();
            World world = entityplayer.worldObj;
            setLittleController(new LittlePlayerController(FMLClientHandler.instance().getClient(), (NetHandlerPlayClient) FMLClientHandler.instance().getClientPlayHandler()),
                                world.getWorldInfo().getGameType());
        }
        return littlePlayerController;
    }

    public static ItemInLittleWorldManager getLittleItemManager(EntityPlayerMP entityplayer, World world) {
        if (itemInLittleWorldManagers.containsKey(entityplayer)
            && itemInLittleWorldManagers.get(entityplayer).theWorld.equals(world)) {
            return itemInLittleWorldManagers.get(entityplayer);
        }
        return setLittleItemManagerForPlayer(entityplayer);
    }

    private static ItemInLittleWorldManager setLittleItemManagerForPlayer(EntityPlayerMP entityplayer) {
        itemInLittleWorldManagers.put(entityplayer,
                                      new ItemInLittleWorldManager(entityplayer.worldObj, entityplayer));
        return itemInLittleWorldManagers.get(entityplayer);
    }

    public static void registerPlacementInfo() {
        itemInLittleWorldManagers = new HashMap<EntityPlayerMP, ItemInLittleWorldManager>();
        // registerDisallowedBlockTick(BlockFluid.class);
        // registerDisallowedBlockTick(BlockFlowing.class);
        registerDisallowedTile(TileEntityLittleChunk.class);
        registerDisallowedItem(ItemHoe.class);
        registerDisallowedItem(ItemMonsterPlacer.class);
    }

    private static Set<Integer>                     disallowedItemIDs           = new HashSet<Integer>();
    private static Set<Integer>                     disallowedBlockIDs          = new HashSet<Integer>();
    private static Set<Class<? extends Item>>       disallowedItems             = new HashSet<Class<? extends Item>>();
    private static Set<Class<? extends Block>>      disallowedBlocks            = new HashSet<Class<? extends Block>>();
    private static Set<Class<? extends TileEntity>> disallowedBlockTileEntities = new HashSet<Class<? extends TileEntity>>();
    private static Set<Class<? extends Block>>      disallowedBlocksToTick      = new HashSet<Class<? extends Block>>();

    private static void registerDisallowedBlockTick(Class<? extends Block> blockClass) {
        if (blockClass != null) {
            if (!disallowedBlocksToTick.contains(blockClass)) {
                disallowedBlocksToTick.add(blockClass);
            }
        }
    }

    public static boolean isBlockAllowedToTick(Block littleBlock) {
        if (littleBlock != null
            && disallowedBlocksToTick.contains(littleBlock.getClass())) {
            return false;
        }
        return true;
    }

    public static void registerDisallowedBlockID(Integer blockID) {
        if (!disallowedBlocks.contains(blockID)) {
            disallowedBlockIDs.add(blockID);
        }
    }

    private static void registerDisallowedBlock(Class<? extends Block> blockClass) {
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
            if (disallowedBlockIDs.contains(Block.getIdFromBlock(block))) {
                return false;
            }
        }
        return true;
    }

    public static void registerDisallowedItemID(Integer itemID) {
        if (!disallowedItemIDs.contains(itemID)) {
            disallowedItemIDs.add(itemID);
        }
    }

    private static void registerDisallowedItem(Class<? extends Item> itemClass) {
        if (itemClass != null) {
            if (!disallowedItems.contains(itemClass)) {
                disallowedItems.add(itemClass);
            }
        }
    }

    public static boolean isItemAllowed(Item item) {
        if (item != null) {
            if (disallowedItems.contains(item.getClass())) {
                return false;
            }
            if (disallowedItemIDs.contains(Item.getIdFromItem(item))) {
                return false;
            }
        }
        return true;
    }

    private static void registerDisallowedTile(Class<? extends TileEntity> tileclass) {
        if (tileclass != null) {
            if (!disallowedBlockTileEntities.contains(tileclass)) {
                disallowedBlockTileEntities.add(tileclass);
            } else {
                LoggerLittleBlocks.getInstance(Logger.filterClassName(LBCore.class.toString())).write(true,
                                                                                                      "Tried to add a tileentity to the disallowed list that already exists",
                                                                                                      Logger.LogLevel.DEBUG);
            }
        }
    }

    public static boolean isTileEntityAllowed(TileEntity tileentity) {
        boolean flag = true;
        if (tileentity != null) {
            if (disallowedBlockTileEntities.contains(tileentity.getClass())) {
                return false;
            }
        }
        return flag;
    }

    public static boolean isLittleChunk(World world, int x, int y, int z) {
        if (world instanceof ILittleWorld) {
            return ((ILittleWorld) world).getParentWorld().getBlock(x >> 3,
                                                                    y >> 3,
                                                                    z >> 3) == ConfigurationLib.littleChunk;
        }
        return false;
    }

    public static boolean isLittleChunk(World world, MovingObjectPosition target) {
        return isLittleChunk(world,
                             target.blockX,
                             target.blockY,
                             target.blockZ);
    }

    public static void onServerBlockActivated(World world, EntityPlayer entityplayer, ItemStack stack, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
    	MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        boolean flag = false;
        // ((EntityPlayerMP) entityplayer).func_143004_u();

        if (side == 255) {
            if (itemstack == null) {
                return;
            }

            PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(entityplayer,
                                                                           PlayerInteractEvent.Action.RIGHT_CLICK_AIR,
                                                                           0,
                                                                           0,
                                                                           0,
                                                                           -1,
                                                                           world);
            if (event.useItem != Event.Result.DENY) {
                getLittleItemManager((EntityPlayerMP) entityplayer,
                                     world).tryUseItem(entityplayer,
                                                       world,
                                                       itemstack);
            }
        } else if (y >= world.getHeight() - 1
                   && (side == 1 || y >= world.getHeight())) {
            // TODO :: Send Player Build height message
            flag = true;
        } else {
            // double dist = this.getBlockReachDistance() + 1;
            // dist *= dist;
            if (!mcServer.isBlockProtected(((ILittleWorld) world).getParentWorld(),
                                           x >> 3,
                                           y >> 3,
                                           z >> 3,
                                           entityplayer)) {
            	getLittleItemManager((EntityPlayerMP) entityplayer,
                                     world).activateBlockOrUseItem(entityplayer,
                                                                   world,
                                                                   itemstack,
                                                                   x,
                                                                   y,
                                                                   z,
                                                                   side,
                                                                   hitX,
                                                                   hitY,
                                                                   hitZ);
            	flag = true;
            }

        }

        if (flag) {
            checkPlacement(world,
                           entityplayer,
                           x,
                           y,
                           z,
                           side);
        }

        itemstack = entityplayer.inventory.getCurrentItem();

        if (itemstack != null && itemstack.stackSize == 0) {
            entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
            itemstack = null;
        }

        if (itemstack == null || itemstack.getMaxItemUseDuration() == 0) {
            ((EntityPlayerMP) entityplayer).isChangingQuantityOnly = true;
            entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = ItemStack.copyItemStack(entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem]);
            Slot slot = entityplayer.openContainer.getSlotFromInventory(entityplayer.inventory,
                                                                        entityplayer.inventory.currentItem);
            entityplayer.openContainer.detectAndSendChanges();
            ((EntityPlayerMP) entityplayer).isChangingQuantityOnly = false;

            if (!ItemStack.areItemStacksEqual(entityplayer.inventory.getCurrentItem(),
                                              stack)) {
                ((EntityPlayerMP) entityplayer).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(entityplayer.openContainer.windowId, slot.slotNumber, entityplayer.inventory.getCurrentItem()));
            }
        }
    }

    private static void checkPlacement(World world, EntityPlayer entityplayer, int x, int y, int z, int side) {
        PacketLib.sendBlockChange(world,
                                  entityplayer,
                                  x,
                                  y,
                                  z);
        if (side == 0) {
            --y;
        }

        if (side == 1) {
            ++y;
        }

        if (side == 2) {
            --z;
        }

        if (side == 3) {
            ++z;
        }

        if (side == 4) {
            --x;
        }

        if (side == 5) {
            ++x;
        }
        Block block = world.getBlock(x,
                                     y,
                                     z);
        if (block != null && block instanceof BlockPistonBase) {
            int newData = BlockPistonBase.determineOrientation(((ILittleWorld) world).getParentWorld(),
                                                               x >> 3,
                                                               y >> 3,
                                                               z >> 3,
                                                               entityplayer);
            world.setBlock/** .setBlockAndMetadataWithNotify **/
            (x,
             y,
             z,
             block,
             newData,
             3);
        }

        PacketLib.sendBlockChange(world,
                                  entityplayer,
                                  x,
                                  y,
                                  z);
    }
}
