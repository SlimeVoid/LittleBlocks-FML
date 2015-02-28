package net.slimevoid.littleblocks.blocks.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

import java.util.List;

public class CollisionRayTrace {

    public static List<MovingObjectPosition> rayTraceLittleBlocks(BlockLittleChunk littleBlocks, Vec3 player, Vec3 view, int i, int j, int k, List<MovingObjectPosition> returns, TileEntityLittleChunk tile, boolean isFluid) {
        float m = ConfigurationLib.littleBlocksSize;
        for (int x = 0; x < tile.size; x++) {
            for (int y = 0; y < tile.size; y++) {
                for (int z = 0; z < tile.size; z++) {
                    if (tile.getBlockState(x,
                                           y,
                                           z).getBlock() != Blocks.air) {
                        Block block = tile.getBlockState(x,
                                                         y,
                                                         z).getBlock();
                        if (block != null
                            && (!(block instanceof IFluidBlock) || isFluid)) {
                            try {
                                BlockPos pos = new BlockPos((i << 3)
                                                            + x,
                                                            (j << 3)
                                                            + y,
                                                            (k << 3)
                                                            + z);
                                MovingObjectPosition ret = block.collisionRayTrace((World) tile.getLittleWorld(),
                                                                                   pos,
                                                                                   new Vec3(player.xCoord * 8,
                                                                                            player.yCoord * 8,
                                                                                            player.zCoord * 8),
                                                                                   new Vec3(view.xCoord * 8,
                                                                                            view.yCoord * 8,
                                                                                            view.zCoord * 8));
                                if (ret != null) {
                                    ret.getBlockPos().add(-(i << 3), -(j << 3), - (k << 3));
                                    ret.hitVec = new Vec3(ret.hitVec.xCoord / 8.0,
                                                          ret.hitVec.yCoord / 8.0,
                                                          ret.hitVec.zCoord / 8.0);
                                    ret.hitVec = ret.hitVec.addVector(-i,
                                                                      -j,
                                                                      -k);
                                    returns.add(ret);
                                }
                            } catch (ClassCastException e) {
                                FMLCommonHandler.instance().getFMLLogger().warn(e.getLocalizedMessage());
                            }
                        }
                    }
                }
            }
        }
        return returns;
    }

    public static List<MovingObjectPosition> collisionRayTracerX(BlockLittleChunk littleBlocks, World world, Vec3 player, Vec3 view, BlockPos pos, int xx, List<MovingObjectPosition> returns) {
        int m = ConfigurationLib.littleBlocksSize;
        Block block = world.getBlockState(pos).getBlock(); // -X
        if (block.getMaterial() != Material.air && block.isOpaqueCube()) {
            for (int yy = 0; yy < m; yy++) {
                for (int zz = 0; zz < m; zz++) {
                    MovingObjectPosition ret = littleBlocks.rayTraceBound(
                            AxisAlignedBB.fromBounds(
                                    (double) xx / m,
                                    (double) yy / m,
                                    (double) zz / m,
                                    (double) (xx + 1) / m,
                                    (double) (yy + 1) / m,
                                    (double) (zz + 1) / m),
                            new BlockPos(
                                    xx,
                                    yy,
                                    zz),
                            player,
                            view);
                    if (ret != null) {
                        returns.add(ret);
                    }
                }
            }
        }
        return returns;
    }

    public static List<MovingObjectPosition> collisionRayTracerY(BlockLittleChunk littleBlocks, World world, Vec3 player, Vec3 view, BlockPos pos, int yy, List<MovingObjectPosition> returns) {
        int m = ConfigurationLib.littleBlocksSize;
        Block block = world.getBlockState(pos).getBlock(); // DOWN
        if (block.getMaterial() != Material.air && block.isOpaqueCube()) {
            for (int xx = 0; xx < m; xx++) {
                for (int zz = 0; zz < m; zz++) {
                    MovingObjectPosition ret = littleBlocks.rayTraceBound(
                            AxisAlignedBB.fromBounds(
                                    (double) xx / m,
                                    (double) yy / m,
                                    (double) zz / m,
                                    (double) (xx + 1) / m,
                                    (double) (yy + 1) / m,
                                    (double) (zz + 1) / m),
                            new BlockPos(
                                    xx,
                                    yy,
                                    zz),
                            player,
                            view);
                    if (ret != null) {
                        returns.add(ret);
                    }
                }
            }
        }
        return returns;
    }

    public static List<MovingObjectPosition> collisionRayTracerZ(BlockLittleChunk littleBlocks, World world, Vec3 player, Vec3 view, BlockPos pos, int zz, List<MovingObjectPosition> returns) {
        int m = ConfigurationLib.littleBlocksSize;
        Block block = world.getBlockState(pos).getBlock(); // -Z
        if (block.getMaterial() != Material.air && block.isOpaqueCube()) {
            for (int yy = 0; yy < m; yy++) {
                for (int xx = 0; xx < m; xx++) {
                    MovingObjectPosition ret = littleBlocks.rayTraceBound(
                            AxisAlignedBB.fromBounds(
                                    (double) xx / m,
                                    (double) yy / m,
                                    (double) zz / m,
                                    (double) (xx + 1) / m,
                                    (double) (yy + 1) / m,
                                    (double) (zz + 1) / m),
                            new BlockPos(
                                    xx,
                                    yy,
                                    zz),
                            player,
                            view);
                    if (ret != null) {
                        returns.add(ret);
                    }
                }
            }
        }
        return returns;
    }

    public static List<MovingObjectPosition> collisionRayTracer(BlockLittleChunk littleBlocks, World world, Vec3 player, Vec3 view, BlockPos pos, List<MovingObjectPosition> returns) {
        int m = ConfigurationLib.littleBlocksSize;
        /*
         * UP
         */
        returns = collisionRayTracerY(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      pos.down(),
                                      -1,
                                      returns);
        /*
         * DOWN
         */
        returns = collisionRayTracerY(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      pos.up(),
                                      m,
                                      returns);
        /*
         * -X
         */
        returns = collisionRayTracerX(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      pos.add(-1, 0, 0),
                                      -1,
                                      returns);
        /*
         * +X
         */
        returns = collisionRayTracerX(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      pos.add(1, 0, 0),
                                      m,
                                      returns);
        /*
         * -Z
         */
        returns = collisionRayTracerZ(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      pos.add(0, 0, -1),
                                      -1,
                                      returns);
        /*
         * +Z
         */
        returns = collisionRayTracerZ(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      pos.add(0, 0, 1),
                                      m,
                                      returns);
        return returns;
    }
}
