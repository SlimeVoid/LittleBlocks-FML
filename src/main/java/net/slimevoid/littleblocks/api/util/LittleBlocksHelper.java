package net.slimevoid.littleblocks.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.slimevoid.library.ICommonProxy;
import net.slimevoid.library.ISlimevoidHelper;
import net.slimevoid.library.util.helpers.SlimevoidHelper;
import net.slimevoid.littleblocks.api.ILBCommonProxy;
import net.slimevoid.littleblocks.api.ILittleBlocks;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.lib.BlockUtil;
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
    public IBlockState getBlockState(World world, BlockPos pos) {
        if (world != null) {
            return getWorld(world,
                            pos).getBlockState(pos);
        }
        return Blocks.air.getDefaultState();
    }

    @Override
    public TileEntity getBlockTileEntity(IBlockAccess world, BlockPos pos) {
        if (world != null) {
            IBlockAccess newWorld = this.getWorld(world,
                                                  pos);
            if (newWorld != null) {
                TileEntity tileentity = newWorld.getTileEntity(pos);
                return tileentity;
            }
        }
        return null;
    }

    @Override
    public boolean targetExists(World world, BlockPos pos) {
        if (world != null) {
            return ((World) getWorld(world,
                                     pos)).isBlockLoaded(pos);
        }
        return false;
    }

    private IBlockAccess getWorld(IBlockAccess world, BlockPos pos) {
        if (isLittleBlock(world,
                          pos.getX(),
                          pos.getY(),
                          pos.getZ())) {
            return (World) ((ILBCommonProxy) proxy).getLittleWorld(world,
                                                                   false);
        }
        return world;
    }

    private boolean isLittleBlock(IBlockAccess world, BlockPos pos) {
        if (world instanceof ILittleWorld) {
            return true;
        }
        BlockPos parent = BlockUtil.getParentPos(pos);
        if (world.getTileEntity(parent) instanceof ILittleBlocks) {
            return true;
        }
        return false;
    }

    @Deprecated
    private boolean isLittleBlock(IBlockAccess world, int x, int y, int z) {
        return this.isLittleBlock(world, new BlockPos(x, y, z));
    }

    @Override
    @Deprecated
    public boolean isUseableByPlayer(World world, EntityPlayer player, int x, int y, int z, double xDiff, double yDiff, double zDiff, double distance) {
        return this.isUseableByPlayer(world, player, new BlockPos(x, y, z), xDiff, yDiff, zDiff, distance);
    }

    @Override
    public boolean isUseableByPlayer(World world, EntityPlayer player, BlockPos pos, double xDiff, double yDiff, double zDiff, double distance) {
        if (isLittleBlock(
                world,
                pos)) {
            return player.getDistanceSq(
                    (pos.getX() / size) + xDiff,
                    (pos.getY() / size) + yDiff,
                    (pos.getZ() / size) + zDiff) <= distance;
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
    public boolean isLadder(IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        if (entity == null || entity.getBoundingBox() == null) {
            return false;
        }
    	int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        TileEntityLittleChunk tile = (TileEntityLittleChunk) world.getTileEntity(pos);
        double minX = entity.getBoundingBox().minX + 0.001D;

        double minY = entity.getBoundingBox().minY + 0.001D;

        double minZ = entity.getBoundingBox().minZ + 0.001D;

        double maxX = entity.getBoundingBox().maxX - 0.001D;

        double maxZ = entity.getBoundingBox().maxZ - 0.001D;
        
        BlockPos from = new BlockPos(MathHelper.floor_double(minX),
                                             MathHelper.floor_double(minY),
                                             MathHelper.floor_double(minZ));

        BlockPos to = new BlockPos(MathHelper.floor_double(maxX),
                MathHelper.floor_double(minY),
                MathHelper.floor_double(maxZ));
        
        if (((World) world).isAreaLoaded(from,
                                         to)) {

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
                        BlockPos littlePos = new BlockPos(xx, yy, zz);
                        if (block.isLadder((World) tile.getLittleWorld(),
                                           littlePos,
                                           entity)) {
                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }

	@Override
	public Block getBlock(World world, BlockPos pos) {
		return this.getBlockState(world, pos).getBlock();
	}

	@Override
	@Deprecated
	public boolean isLadder(IBlockAccess world, int x, int y, int z,
			EntityLivingBase entity) {
		return this.isLadder(world, new BlockPos(x, y, z), entity);
	}

}
