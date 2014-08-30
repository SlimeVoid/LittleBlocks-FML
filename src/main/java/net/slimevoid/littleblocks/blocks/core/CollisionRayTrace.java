package net.slimevoid.littleblocks.blocks.core;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.common.FMLCommonHandler;

public class CollisionRayTrace {

    public static List<MovingObjectPosition> rayTraceLittleBlocks(BlockLittleChunk littleBlocks, Vec3 player, Vec3 view, int i, int j, int k, List<MovingObjectPosition> returns, TileEntityLittleChunk tile, boolean isFluid) {
        float m = ConfigurationLib.littleBlocksSize;
        for (int x = 0; x < tile.size; x++) {
            for (int y = 0; y < tile.size; y++) {
                for (int z = 0; z < tile.size; z++) {
                    if (tile.getBlock(x,
                                      y,
                                      z) != Blocks.air) {
                        Block block = tile.getBlock(x,
                                                    y,
                                                    z);
                        if (block != null
                            && (!(block instanceof IFluidBlock) || isFluid)) {
                            try {
                                MovingObjectPosition ret = block.collisionRayTrace((World) tile.getLittleWorld(),
                                                                                   (i << 3)
                                                                                           + x,
                                                                                   (j << 3)
                                                                                           + y,
                                                                                   (k << 3)
                                                                                           + z,
                                                                                   Vec3.createVectorHelper(player.xCoord * 8,
                                                                                                                         player.yCoord * 8,
                                                                                                                         player.zCoord * 8),
                                                                                   Vec3.createVectorHelper(view.xCoord * 8,
                                                                                                                       view.yCoord * 8,
                                                                                                                       view.zCoord * 8));
                                if (ret != null) {
                                    ret.blockX -= (i << 3);
                                    ret.blockY -= (j << 3);
                                    ret.blockZ -= (k << 3);
                                    ret.hitVec = Vec3.createVectorHelper(ret.hitVec.xCoord / 8.0,
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

    public static List<MovingObjectPosition> collisionRayTracerX(BlockLittleChunk littleBlocks, World world, Vec3 player, Vec3 view, int x, int y, int z, int xx, List<MovingObjectPosition> returns) {
        int m = ConfigurationLib.littleBlocksSize;
        Block block = world.getBlock(x,
                                     y,
                                     z); // -X
        if (block.getMaterial() != Material.air && block.isOpaqueCube()) {
            for (int yy = 0; yy < m; yy++) {
                for (int zz = 0; zz < m; zz++) {
                    MovingObjectPosition ret = littleBlocks.rayTraceBound(AxisAlignedBB.getBoundingBox(xx
                                                                                                               / (float) m,
                                                                                                       yy
                                                                                                               / (float) m,
                                                                                                       zz
                                                                                                               / (float) m,
                                                                                                       (xx + 1)
                                                                                                               / (float) m,
                                                                                                       (yy + 1)
                                                                                                               / (float) m,
                                                                                                       (zz + 1)
                                                                                                               / (float) m),
                                                                          xx,
                                                                          yy,
                                                                          zz,
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

    public static List<MovingObjectPosition> collisionRayTracerY(BlockLittleChunk littleBlocks, World world, Vec3 player, Vec3 view, int x, int y, int z, int yy, List<MovingObjectPosition> returns) {
        int m = ConfigurationLib.littleBlocksSize;
        Block block = world.getBlock(x,
                                     y,
                                     z); // DOWN
        if (block.getMaterial() != Material.air && block.isOpaqueCube()) {
            for (int xx = 0; xx < m; xx++) {
                for (int zz = 0; zz < m; zz++) {
                    MovingObjectPosition ret = littleBlocks.rayTraceBound(AxisAlignedBB.getBoundingBox(xx
                                                                                                               / (float) m,
                                                                                                       yy
                                                                                                               / (float) m,
                                                                                                       zz
                                                                                                               / (float) m,
                                                                                                       (xx + 1)
                                                                                                               / (float) m,
                                                                                                       (yy + 1)
                                                                                                               / (float) m,
                                                                                                       (zz + 1)
                                                                                                               / (float) m),
                                                                          xx,
                                                                          yy,
                                                                          zz,
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

    public static List<MovingObjectPosition> collisionRayTracerZ(BlockLittleChunk littleBlocks, World world, Vec3 player, Vec3 view, int x, int y, int z, int zz, List<MovingObjectPosition> returns) {
        int m = ConfigurationLib.littleBlocksSize;
        Block block = world.getBlock(x,
                                     y,
                                     z); // -Z
        if (block.getMaterial() != Material.air && block.isOpaqueCube()) {
            for (int yy = 0; yy < m; yy++) {
                for (int xx = 0; xx < m; xx++) {
                    MovingObjectPosition ret = littleBlocks.rayTraceBound(AxisAlignedBB.getBoundingBox(xx
                                                                                                               / (float) m,
                                                                                                       yy
                                                                                                               / (float) m,
                                                                                                       zz
                                                                                                               / (float) m,
                                                                                                       (xx + 1)
                                                                                                               / (float) m,
                                                                                                       (yy + 1)
                                                                                                               / (float) m,
                                                                                                       (zz + 1)
                                                                                                               / (float) m),
                                                                          xx,
                                                                          yy,
                                                                          zz,
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

    public static List<MovingObjectPosition> collisionRayTracer(BlockLittleChunk littleBlocks, World world, Vec3 player, Vec3 view, int x, int y, int z, List<MovingObjectPosition> returns) {
        int m = ConfigurationLib.littleBlocksSize;
        /*
         * UP
         */
        returns = collisionRayTracerY(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      x,
                                      y - 1,
                                      z,
                                      -1,
                                      returns);
        /*
         * DOWN
         */
        returns = collisionRayTracerY(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      x,
                                      y + 1,
                                      z,
                                      m,
                                      returns);
        /*
         * -X
         */
        returns = collisionRayTracerX(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      x - 1,
                                      y,
                                      z,
                                      -1,
                                      returns);
        /*
         * +X
         */
        returns = collisionRayTracerX(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      x + 1,
                                      y,
                                      z,
                                      m,
                                      returns);
        /*
         * -Z
         */
        returns = collisionRayTracerZ(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      x,
                                      y,
                                      z - 1,
                                      -1,
                                      returns);
        /*
         * +Z
         */
        returns = collisionRayTracerZ(littleBlocks,
                                      world,
                                      player,
                                      view,
                                      x,
                                      y,
                                      z + 1,
                                      m,
                                      returns);
        return returns;
    }
}
