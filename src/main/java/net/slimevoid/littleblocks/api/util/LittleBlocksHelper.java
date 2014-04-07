package net.slimevoid.littleblocks.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.slimevoid.library.ICommonProxy;
import net.slimevoid.library.ISlimevoidHelper;
import net.slimevoid.library.util.helpers.SlimevoidHelper;
import net.slimevoid.littleblocks.api.ILBCommonProxy;
import net.slimevoid.littleblocks.api.ILittleBlocks;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

public class LittleBlocksHelper implements ISlimevoidHelper {

    private static boolean initialized = false;
    private ICommonProxy   proxy;
    private int            size;

    /**
     * Constructor
     * 
     * @param littleProxy
     *            the LB Proxy
     * @param littleBlocksSize
     *            the size of LB
     */
    public LittleBlocksHelper(ICommonProxy littleProxy, int littleBlocksSize) {
        this.proxy = littleProxy;
        this.size = littleBlocksSize;
    }

    /**
     * Initialized the Helper
     */
    public static void init(ICommonProxy littleProxy, int littleBlocksSize) {
        if (!initialized) {
            ISlimevoidHelper littleBlocksHelper = new LittleBlocksHelper(littleProxy, littleBlocksSize);
            SlimevoidHelper.registerHelper(littleBlocksHelper);
            initialized = true;
        }
    }

    @Override
    public Block getBlock(World world, int x, int y, int z) {
        if (world != null) {
            return getWorld(world,
                            x,
                            y,
                            z).getBlock(x,
                                        y,
                                        z);
        }
        return Blocks.air;
    }

    @Override
    public TileEntity getBlockTileEntity(IBlockAccess world, int x, int y, int z) {
        if (world != null) {
            IBlockAccess newWorld = this.getWorld(world,
                                                  x,
                                                  y,
                                                  z);
            if (newWorld != null) {
                TileEntity tileentity = newWorld.getTileEntity(x,
                                                               y,
                                                               z);
                return tileentity;
            }
        }
        return null;
    }

    @Override
    public boolean targetExists(World world, int x, int y, int z) {
        if (world != null) {
            return ((World) getWorld(world,
                                     x,
                                     y,
                                     z)).blockExists(x,
                                                     y,
                                                     z);
        }
        return false;
    }

    private IBlockAccess getWorld(IBlockAccess world, int x, int y, int z) {
        if (isLittleBlock(world,
                          x,
                          y,
                          z)) {
            return (World) ((ILBCommonProxy) proxy).getLittleWorld(world,
                                                                   false);
        }
        return world;
    }

    private boolean isLittleBlock(IBlockAccess world, int x, int y, int z) {
        if (world instanceof ILittleWorld) {
            return true;
        }
        if (world.getTileEntity(x >> 3,
                                y >> 3,
                                z >> 3) instanceof ILittleBlocks) {
            return true;
        }
        return false;
    }

    public boolean isUseableByPlayer(World world, EntityPlayer player, int xCoord, int yCoord, int zCoord, double xDiff, double yDiff, double zDiff, double distance) {
        if (isLittleBlock(world,
                          xCoord,
                          yCoord,
                          zCoord)) {
            return player.getDistanceSq((xCoord / size) + xDiff,
                                        (yCoord / size) + yDiff,
                                        (zCoord / size) + zDiff) <= distance;
        }
        return false;
    }

    @Override
    public String getHelperName() {
        return "LittleBlocks Helper";
    }

    /**
     * Retrieves whether or not the block the entity has collided with is a
     * Ladder (Mainly used in Gulliver)
     * 
     * Credits : Tarig and Unclemion
     */
    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {

        TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(x,
                                                                                 y,
                                                                                 z);
        double minX = entity.boundingBox.minX + 0.001D;

        double minY = entity.boundingBox.minY + 0.001D;

        double minZ = entity.boundingBox.minZ + 0.001D;

        double maxX = entity.boundingBox.maxX - 0.001D;

        double maxZ = entity.boundingBox.maxZ - 0.001D;

        if (((World) world).checkChunksExist(MathHelper.floor_double(minX),
                                             MathHelper.floor_double(minY),
                                             MathHelper.floor_double(minZ),
                                             MathHelper.floor_double(maxX),
                                             MathHelper.floor_double(minY),
                                             MathHelper.floor_double(maxZ))) {

            boolean result = false;
            // X/8 = .125 solve for the floor of X .125 * 8 = 1 the floor of 1
            // is 1
            // X/8 = .123 solve for the floor of X .123 * 8 < 1 the floor of <1
            // is 0
            minX = MathHelper.floor_double((minX - MathHelper.floor_double(minX))
                                           * tile.size)
                   + ((MathHelper.floor_double(minX) - x) * tile.size);
            minY = MathHelper.floor_double((minY - MathHelper.floor_double(minY))
                                           * tile.size)
                   + ((MathHelper.floor_double(minY) - y) * tile.size);
            minZ = MathHelper.floor_double((minZ - MathHelper.floor_double(minZ))
                                           * tile.size)
                   + ((MathHelper.floor_double(minZ) - z) * tile.size);
            maxX = MathHelper.floor_double((maxX - MathHelper.floor_double(maxX))
                                           * tile.size)
                   + ((MathHelper.floor_double(maxX) - x) * tile.size);
            maxZ = MathHelper.floor_double((maxZ - MathHelper.floor_double(maxZ))
                                           * tile.size)
                   + ((MathHelper.floor_double(maxZ) - z) * tile.size);
            for (int littleX = (int) minX; littleX <= maxX; littleX++) {
                for (int littleZ = (int) minZ; littleZ <= maxZ; littleZ++) {
                    Block block = tile.getBlock(littleX,
                                                (int) minY,
                                                littleZ);
                    if (block != null && block.getMaterial() != Material.air) {
                        int xx = (x << 3) + littleX, yy = (y << 3) + (int) minY, zz = (z << 3)
                                                                                      + littleZ;
                        if (block.isLadder((World) tile.getLittleWorld(),
                                           xx,
                                           yy,
                                           zz,
                                           entity)) {
                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }

}
