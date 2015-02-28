package net.slimevoid.littleblocks.items.wand;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.slimevoid.library.data.ReadWriteLock;
import net.slimevoid.library.util.helpers.ChatHelper;
import net.slimevoid.library.util.helpers.PacketHelper;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.LoggerLittleBlocks;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.network.packets.PacketLittleNotify;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

import java.util.HashMap;

public enum EnumWandAction {
    PLACE_LB, ROTATE_LB, COPY_LB, DESTROY_LB;

    int                                                        actionID;
    String                                                     actionName;
    String                                                     actionDescription;

    private static HashMap<EntityPlayer, EnumWandAction>       playerWandActions;
    private static EnumWandAction                              playerWandAction    = PLACE_LB;

    public static HashMap<EntityPlayer, TileEntityLittleChunk> selectedLittleTiles = new HashMap<EntityPlayer, TileEntityLittleChunk>();
    public static ReadWriteLock                                tileLock            = new ReadWriteLock();

    public static void registerWandActions() {
        PLACE_LB.actionID = 0;
        PLACE_LB.actionName = "placeLB";
        PLACE_LB.actionDescription = "Place Mode";

        ROTATE_LB.actionID = 1;
        ROTATE_LB.actionName = "rotateLB";
        ROTATE_LB.actionDescription = "Rotate Mode";

        COPY_LB.actionID = 2;
        COPY_LB.actionName = "copyLB";
        COPY_LB.actionDescription = "Copy Mode";

        DESTROY_LB.actionID = 3;
        DESTROY_LB.actionName = "destroyLB";
        DESTROY_LB.actionDescription = "Destroy Mode";

        playerWandActions = new HashMap<EntityPlayer, EnumWandAction>();
    }

    public static EnumWandAction getAction(int actionID) {
        if (actionID < 0) {
            actionID = EnumWandAction.values().length - 1;
        } else if (actionID >= EnumWandAction.values().length) {
            actionID = 0;
        }
        return EnumWandAction.values()[actionID];
    }

    public static EnumWandAction getWandActionForPlayer(EntityPlayer entityplayer) {
        if (!playerWandActions.containsKey(entityplayer)) {
            return setWandActionForPlayer(entityplayer,
                                          EnumWandAction.PLACE_LB);
        }
        return playerWandActions.get(entityplayer);
    }

    public static EnumWandAction setWandActionForPlayer(EntityPlayer entityplayer, EnumWandAction action) {
        playerWandActions.put(entityplayer,
                              action);
        return action;
    }

    public static void setNextActionForPlayer(EntityPlayer entityplayer) {
        EnumWandAction currentAction = getWandActionForPlayer(entityplayer);
        int nextActionID = currentAction.actionID;
        nextActionID++;
        EnumWandAction nextAction = getAction(nextActionID);
        setWandActionForPlayer(entityplayer,
                               nextAction);
        ChatHelper.addColouredMessageToPlayer(entityplayer,
                                              EnumChatFormatting.RED,
                                              "Little Wand now in "
                                                      + nextAction.actionDescription);
    }

    @SideOnly(Side.CLIENT)
    public static EnumWandAction getWandAction() {
        return playerWandAction;
    }

    @SideOnly(Side.CLIENT)
    public static void setNextWandAction() {
        EnumWandAction currentAction = getWandAction();
        int nextActionID = currentAction.actionID;
        EnumWandAction nextAction = getAction(nextActionID);
        setWandAction(nextAction);
    }

    @SideOnly(Side.CLIENT)
    private static void setWandAction(EnumWandAction action) {
        playerWandAction = action;
    }

    public static boolean doWandAction(ItemStack itemstack, EntityPlayer entityplayer, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        EnumWandAction playerWandAction = EnumWandAction.getWandActionForPlayer(entityplayer);
        if (playerWandAction.equals(EnumWandAction.COPY_LB)) {
            return doCopyLB(itemstack,
                            entityplayer,
                            world,
                            pos,
                            side,
                            hitX,
                            hitY,
                            hitZ);
        }
        if (playerWandAction.equals(EnumWandAction.ROTATE_LB)) {
            return doRotateLB(itemstack,
                              entityplayer,
                              world,
                              pos,
                              side,
                              hitX,
                              hitY,
                              hitZ);
        }
        if (playerWandAction.equals(EnumWandAction.PLACE_LB)) {
            return doPlaceLB(itemstack,
                             entityplayer,
                             world,
                             pos,
                             side,
                             hitX,
                             hitY,
                             hitZ);
        }
        if (playerWandAction.equals(EnumWandAction.DESTROY_LB)) {
            return doDestroyLB(itemstack,
                               entityplayer,
                               world,
                               pos,
                               side,
                               hitX,
                               hitY,
                               hitZ);
        }
        return false;
    }

    private static boolean doRotateLB(ItemStack itemstack, EntityPlayer entityplayer, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.getBlockState(pos).getBlock() == ConfigurationLib.littleChunk) {
            ((BlockLittleChunk) ConfigurationLib.littleChunk).rotateLittleChunk(world,
                                                                                pos,
                                                                                EnumFacing.UP);
            return true;
        }
        return false;
    }

    private static boolean doPlaceLB(ItemStack itemstack, EntityPlayer entityplayer, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.getBlockState(pos).getBlock().isAssociatedBlock(ConfigurationLib.littleChunk)) {
            if (!world.getBlockState(pos).getBlock().isReplaceable(world,
                    pos) || world.getBlockState(pos).getBlock() instanceof IFluidBlock) {
                pos = pos.add(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
//                if (side == 0) {
//                    --y;
//                }
//
//                if (side == 1) {
//                    ++y;
//                }
//
//                if (side == 2) {
//                    --z;
//                }
//
//                if (side == 3) {
//                    ++z;
//                }
//
//                if (side == 4) {
//                    --x;
//                }
//
//                if (side == 5) {
//                    ++x;
//                }
            }
            if (world.isAirBlock(pos)
                || world.getBlockState(pos).getBlock().isReplaceable(world, pos)
                && !(world.getBlockState(pos).getBlock() instanceof IFluidBlock)) {
                world.setBlockState(pos,
                               ConfigurationLib.littleChunk.getDefaultState());
                TileEntity newtile = world.getTileEntity(pos);
                if (newtile != null) {
                    newtile.markDirty();
                    world.markBlockForUpdate(pos);
                } else {
                    FMLCommonHandler.instance().getFMLLogger().warn("Could not initialize LittleChunk");
                }
            }
            return true;
        }
        return false;
    }

    private static boolean doCopyLB(ItemStack itemstack, EntityPlayer entityplayer, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (((EntityPlayerMP) entityplayer).capabilities.isCreativeMode) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity != null
                && tileentity instanceof TileEntityLittleChunk) {
                try {
                    tileLock.writeLock(world);
                    selectedLittleTiles.put(entityplayer,
                                            (TileEntityLittleChunk) tileentity);
                    tileLock.writeUnlock();
                } catch (InterruptedException e) {
                    LoggerLittleBlocks.getInstance("ItemLittleBlocksCopier").writeStackTrace(e);
                }
                return true;
            } else if (tileentity == null) {
                try {
                    tileLock.readLock(world);
                    if (!selectedLittleTiles.containsKey(entityplayer)) {
                        tileLock.readUnlock();
                        return false;
                    }
                    TileEntityLittleChunk selectedLittleTile = selectedLittleTiles.get(entityplayer);
                    tileLock.readUnlock();
                    int xx = pos.getX(), yy = pos.getY(), zz = pos.getZ();
                    BlockPos newPos = new BlockPos(xx, yy, zz);
                    if (selectedLittleTile != null) {
                        newPos = newPos.add(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());
//                        if (side == 0) {
//                            --yy;
//                        }
//
//                        if (side == 1) {
//                            ++yy;
//                        }
//
//                        if (side == 2) {
//                            --zz;
//                        }
//
//                        if (side == 3) {
//                            ++zz;
//                        }
//
//                        if (side == 4) {
//                            --xx;
//                        }
//
//                        if (side == 5) {
//                            ++xx;
//                        }
                        world.setBlockState(newPos,
                                       ConfigurationLib.littleChunk.getDefaultState());
                        TileEntity newtile = world.getTileEntity(newPos);
                        if (newtile != null
                            && newtile instanceof TileEntityLittleChunk) {
                            TileEntityLittleChunk newtilelb = (TileEntityLittleChunk) newtile;
                            tileLock.readLock(world);
                            TileEntityLittleChunk oldtile = selectedLittleTiles.get(entityplayer);
                            tileLock.readUnlock();
                            BlockPos littlePos = new BlockPos(pos.getX() << 3, pos.getY() << 3, pos.getZ() << 3);
                            for (int x1 = 0; x1 < ConfigurationLib.littleBlocksSize; x1++) {
                                for (int y1 = 0; y1 < ConfigurationLib.littleBlocksSize; y1++) {
                                    for (int z1 = 0; z1 < ConfigurationLib.littleBlocksSize; z1++) {
                                        IBlockState state = oldtile.getBlockState(new BlockPos(x1, y1, z1));
                                        if (state.getBlock() != Blocks.air) {
                                            ((World) newtilelb.getLittleWorld()).setBlockState(littlePos.add(x1, y1, z1), state);
//                                            newtilelb.setBlockIDWithMetadata(x1,
//                                                                             y1,
//                                                                             z1,
//                                                                             blockId,
//                                                                             metadata);
//                                            TileEntity oldLittleTile = oldtile.getTileEntity(x1,
//                                                    y1,
//                                                    z1);
//                                            if (oldLittleTile != null) {
//                                                newtilelb.setTileEntity(x1,
//                                                                                  y1,
//                                                                                  z1,
//                                                                                  oldLittleTile);
//                                            }
                                        }
                                    }
                                }
                            }
                            newtilelb.markDirty();
                            world.markBlockForUpdate(newPos);
                        }
                    }
                    return true;
                } catch (InterruptedException e) {
                    LoggerLittleBlocks.getInstance("ItemLittleBlocksCopier").writeStackTrace(e);
                }
            }
        }
        PacketHelper.sendToPlayer(new PacketLittleNotify(CommandLib.COPIER_MESSAGE),
                                  (EntityPlayerMP) entityplayer);
        return false;
    }

    private static boolean doDestroyLB(ItemStack itemstack, EntityPlayer entityplayer, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == ConfigurationLib.littleChunk) {
            ConfigurationLib.littleChunk.removedByPlayer(world,
                                                         pos,
                                                         entityplayer,
                                                         true);
            world.setBlockToAir(pos);
            return true;
        }
        return false;
    }

	public static boolean forceDestroy(EntityPlayer entityplayer) {
		return getWandActionForPlayer(entityplayer).equals(EnumWandAction.DESTROY_LB);
	}
}
