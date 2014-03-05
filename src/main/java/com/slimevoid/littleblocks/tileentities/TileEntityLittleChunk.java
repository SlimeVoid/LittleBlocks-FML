package com.slimevoid.littleblocks.tileentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

import com.slimevoid.library.core.SlimevoidCore;
import com.slimevoid.library.core.lib.CoreLib;
import com.slimevoid.littleblocks.api.ILittleBlocks;
import com.slimevoid.littleblocks.api.ILittleWorld;
import com.slimevoid.littleblocks.core.LittleBlocks;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;

import cpw.mods.fml.common.FMLLog;

public class TileEntityLittleChunk extends TileEntity implements ILittleBlocks {
    public int                             size               = ConfigurationLib.littleBlocksSize;
    private int                            content[][][]      = new int[size][size][size];
    private int                            metadatas[][][]    = new int[size][size][size];
    private int                            lightmap[][][]     = new int[size][size][size];
    private boolean                        isLit              = false;
    private Map<ChunkPosition, TileEntity> chunkTileEntityMap = new HashMap<ChunkPosition, TileEntity>();
    private int                            tickRefCount       = 0;

    @Override
    public void setWorldObj(World world) {
        this.worldObj = world;
        this.setLittleWorldObjs();
    }

    public void updateContainingBlockInfo() {
        for (int x = 0; x < this.content.length; x++) {
            for (int y = 0; y < this.content[x].length; y++) {
                for (int z = 0; z < this.content[x][y].length; z++) {
                    this.content[x][y][z] = 0;
                }
            }
        }

        for (int x = 0; x < this.metadatas.length; x++) {
            for (int y = 0; y < this.metadatas[x].length; y++) {
                for (int z = 0; z < this.metadatas[x][y].length; z++) {
                    this.metadatas[x][y][z] = 0;
                }
            }
        }
        this.getLittleWorld().activeChunkPosition(new ChunkPosition(this.xCoord, this.yCoord, this.zCoord),
                                                  true);
    }

    public boolean isEmpty() {
        for (int x = 0; x < this.content.length; x++) {
            for (int y = 0; y < this.content[x].length; y++) {
                for (int z = 0; z < this.content[x][y].length; z++) {
                    if (this.content[x][y][z] > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int[][][] getContents() {
        return this.content;
    }

    public int getLightlevel() {
        int blockX = (xCoord << 3), blockY = (yCoord << 3), blockZ = (zCoord << 3);
        int lightcount[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    if (Block.getBlockById(this.content[x][y][z]) == null) {
                        lightcount[0]++;
                    } else {
                        lightcount[Block.getBlockById(this.content[x][y][z]).getLightValue(this.getLittleWorld(),
                                                                                         blockX
                                                                                                 + x,
                                                                                         blockY
                                                                                                 + y,
                                                                                         blockZ
                                                                                                 + z)]++;
                    }
                }
            }
        }

        int calculatedLightLevel = 0;
        for (int i = 15; i > 0; i--) {
            if (lightcount[i] > 0) {
                int templightlvl = lightcount[i] >= size ? i : MathHelper.ceiling_double_int(((double) i / (double) size)
                                                                                             * (double) lightcount[i]);
                if (templightlvl > calculatedLightLevel) calculatedLightLevel = templightlvl;
            }
        }
        return calculatedLightLevel;
    }

    public ILittleWorld getLittleWorld() {
        return LittleBlocks.proxy.getLittleWorld(this.worldObj,
                                                 false);
    }

    public int getBlockMetadata(int x, int y, int z) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              + (x >= size ? 1 : 0),
                                                                                                      yCoord
                                                                                                              + (y >= size ? 1 : 0),
                                                                                                      zCoord
                                                                                                              + (z >= size ? 1 : 0));
                return tile.getBlockMetadata(x >= size ? x - size : x,
                                             y >= size ? y - size : y,
                                             z >= size ? z - size : z);
            }
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)).getMaterial() != Material.air) {
                return 0;
            }
            return -1;
        } else if (x < 0 | z < 0 | y < 0) {
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              - (x < 0 ? 1 : 0),
                                                                                                      yCoord
                                                                                                              - (y < 0 ? 1 : 0),
                                                                                                      zCoord
                                                                                                              - (z < 0 ? 1 : 0));
                return tile.getBlockMetadata(x < 0 ? x + size : x,
                                             y < 0 ? y + size : y,
                                             z < 0 ? z + size : z);
            }
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)).getMaterial() == Material.air) {
                return 0;
            }
            return -1;
        } else {
            return metadatas[x][y][z];
        }
    }

    public int getBlockID(int x, int y, int z) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              + (x >= size ? 1 : 0),
                                                                                                      yCoord
                                                                                                              + (y >= size ? 1 : 0),
                                                                                                      zCoord
                                                                                                              + (z >= size ? 1 : 0));
                return tile.getBlockID(x >= size ? x - size : x,
                                       y >= size ? y - size : y,
                                       z >= size ? z - size : z);
            }
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)).getMaterial() == Material.air) {
                return 0;
            }
            return -1;
        } else if (x < 0 | z < 0 | y < 0) {
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              - (x < 0 ? 1 : 0),
                                                                                                      yCoord
                                                                                                              - (y < 0 ? 1 : 0),
                                                                                                      zCoord
                                                                                                              - (z < 0 ? 1 : 0));
                return tile.getBlockID(x < 0 ? x + size : x,
                                       y < 0 ? y + size : y,
                                       z < 0 ? z + size : z);
            }
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)).getMaterial() == Material.air) {
                return 0;
            }
            return -1;
        } else {
            return this.content[x][y][z];
        }
    }

    public int getBlockLightValue(int x, int y, int z, int side) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              + (x >= size ? 1 : 0),
                                                                                                      yCoord
                                                                                                              + (y >= size ? 1 : 0),
                                                                                                      zCoord
                                                                                                              + (z >= size ? 1 : 0));
                return tile.getBlockLightValue(x >= size ? x - size : x,
                                               y >= size ? y - size : y,
                                               z >= size ? z - size : z,
                                               side);
            } else {
                return this.worldObj.getBlockLightValue(this.xCoord,
                                                        this.yCoord,
                                                        this.zCoord);
            }
        } else if (x < 0 | z < 0 | y < 0) {
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              - (x < 0 ? 1 : 0),
                                                                                                      yCoord
                                                                                                              - (y < 0 ? 1 : 0),
                                                                                                      zCoord
                                                                                                              - (z < 0 ? 1 : 0));
                return tile.getBlockLightValue(x < 0 ? x + size : x,
                                               y < 0 ? y + size : y,
                                               z < 0 ? z + size : z,
                                               side);
            } else {
                return this.worldObj.getBlockLightValue(this.xCoord,
                                                        this.yCoord,
                                                        this.zCoord);
            }
        } else {
            return lightmap[x][y][z];
        }
    }

    public int getSavedLightValue(int x, int y, int z) {
        return 0;
    }

    public void setLightValue(int x, int y, int z, int value) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              + (x >= size ? 1 : 0),
                                                                                                      yCoord
                                                                                                              + (y >= size ? 1 : 0),
                                                                                                      zCoord
                                                                                                              + (z >= size ? 1 : 0));
                tile.setLightValue(x >= size ? x - size : x,
                                   y >= size ? y - size : y,
                                   z >= size ? z - size : z,
                                   value);
            }
        } else if (x < 0 | z < 0 | y < 0) {
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              - (x < 0 ? 1 : 0),
                                                                                                      yCoord
                                                                                                              - (y < 0 ? 1 : 0),
                                                                                                      zCoord
                                                                                                              - (z < 0 ? 1 : 0));
                tile.setLightValue(x < 0 ? x + size : x,
                                   y < 0 ? y + size : y,
                                   z < 0 ? z + size : z,
                                   value);
            }
        }
        int lastLight = this.lightmap[x][y][z];
        if (lastLight == value) {
            return;
        } else {
            this.lightmap[x][y][z] = value;
            this.markDirty();
        }
    }

    public boolean setBlockMetadata(int x, int y, int z, int metadata) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)).getMaterial() == Material.air) {
                this.worldObj.setBlock(xCoord + (x >= size ? 1 : 0),
                                       yCoord + (y >= size ? 1 : 0),
                                       zCoord + (z >= size ? 1 : 0),
                                       ConfigurationLib.littleChunk,
                                       0,
                                       0x02);
            }
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              + (x >= size ? 1 : 0),
                                                                                                      yCoord
                                                                                                              + (y >= size ? 1 : 0),
                                                                                                      zCoord
                                                                                                              + (z >= size ? 1 : 0));
                return tile.setBlockMetadata(x >= size ? x - size : x,
                                             y >= size ? y - size : y,
                                             z >= size ? z - size : z,
                                             metadata);
            }
        } else if (x < 0 | z < 0 | y < 0) {
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)).getMaterial() == Material.air) {
                this.worldObj.setBlock(xCoord - (x < 0 ? 1 : 0),
                                       yCoord - (y < 0 ? 1 : 0),
                                       zCoord - (z < 0 ? 1 : 0),
                                       ConfigurationLib.littleChunk,
                                       0,
                                       0x02);
            }
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              - (x < 0 ? 1 : 0),
                                                                                                      yCoord
                                                                                                              - (y < 0 ? 1 : 0),
                                                                                                      zCoord
                                                                                                              - (z < 0 ? 1 : 0));
                return tile.setBlockMetadata(x < 0 ? x + size : x,
                                             y < 0 ? y + size : y,
                                             z < 0 ? z + size : z,
                                             metadata);
            }
        }
        int lastData = this.metadatas[x][y][z];
        if (lastData == metadata) {
            return false;
        } else {
            this.markDirty();
            this.metadatas[x][y][z] = metadata;
            int blockId = this.content[x][y][z];

            if (blockId > 0 && Block.getBlockById(blockId) != null
                && Block.getBlockById(blockId).hasTileEntity(metadata)) {
                TileEntity tileentity = this.getChunkBlockTileEntity(x,
                                                                     y,
                                                                     z);

                if (tileentity != null) {
                    tileentity.updateContainingBlockInfo();
                    tileentity.blockMetadata = metadata;
                }
            }
            return true;
        }
    }

    public void checkForLittleBlock(int x, int y, int z) {
    }

    public boolean setBlockIDWithMetadata(int x, int y, int z, int id, int metadata) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)).getMaterial() == Material.air) {
                this.worldObj.setBlock(xCoord + (x >= size ? 1 : 0),
                                       yCoord + (y >= size ? 1 : 0),
                                       zCoord + (z >= size ? 1 : 0),
                                       ConfigurationLib.littleChunk,
                                       0,
                                       0x02);
            }
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              + (x >= size ? 1 : 0),
                                                                                                      yCoord
                                                                                                              + (y >= size ? 1 : 0),
                                                                                                      zCoord
                                                                                                              + (z >= size ? 1 : 0));
                return tile.setBlockIDWithMetadata(x >= size ? x - size : x,
                                                   y >= size ? y - size : y,
                                                   z >= size ? z - size : z,
                                                   id,
                                                   metadata);
            }
        } else if (x < 0 | z < 0 | y < 0) {
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)).getMaterial() == Material.air) {
                this.worldObj.setBlock(xCoord - (x < 0 ? 1 : 0),
                                       yCoord - (y < 0 ? 1 : 0),
                                       zCoord - (z < 0 ? 1 : 0),
                                       ConfigurationLib.littleChunk,
                                       0,
                                       0x02);
            }
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunk) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getTileEntity(xCoord
                                                                                                              - (x < 0 ? 1 : 0),
                                                                                                      yCoord
                                                                                                              - (y < 0 ? 1 : 0),
                                                                                                      zCoord
                                                                                                              - (z < 0 ? 1 : 0));
                return tile.setBlockIDWithMetadata(x < 0 ? x + size : x,
                                                   y < 0 ? y + size : y,
                                                   z < 0 ? z + size : z,
                                                   id,
                                                   metadata);
            }
        }
        int lastId = this.getBlockID(x,
                                     y,
                                     z);// this.content[x][y][z];
        int lastData = this.getBlockMetadata(x,
                                             y,
                                             z);// this.metadatas[x][y][z];

        if (lastId == id && lastData == metadata) {
            return false;
        } else {
            if (lastId != 0 && !this.worldObj.isRemote) {
                Block.getBlockById(lastId).onBlockPreDestroy(this.getWorldObj(),
                                                           ((this.xCoord << 3) + x),
                                                           ((this.yCoord << 3) + y),
                                                           ((this.zCoord << 3) + z),
                                                           lastData);
            }

            content[x][y][z] = id;

            if (lastId != 0) {
                if (!this.worldObj.isRemote) {
                    Block.getBlockById(lastId).breakBlock((World) this.getLittleWorld(),
                                                        (this.xCoord << 3) + x,
                                                        (this.yCoord << 3) + y,
                                                        (this.zCoord << 3) + z,
                                                        Block.getBlockById(lastId),
                                                        lastData);
                } else if (Block.getBlockById(lastId) != null
                           && Block.getBlockById(lastId).hasTileEntity(lastData)) {
                    TileEntity te = this.getLittleWorld().getTileEntity((this.xCoord << 3)
                                                                                     + x,
                                                                             (this.yCoord << 3)
                                                                                     + y,
                                                                             (this.zCoord << 3)
                                                                                     + z);
                    if (te != null
                        && te.shouldRefresh(Block.getBlockById(lastId),
                                            Block.getBlockById(id),
                                            lastData,
                                            metadata,
                                            (World) this.getLittleWorld(),
                                            (this.xCoord << 3) + x,
                                            (this.yCoord << 3) + y,
                                            (this.zCoord << 3) + z)) {
                        ((World) this.getLittleWorld()).removeTileEntity((this.xCoord << 3)
                                                                                      + x,
                                                                              (this.yCoord << 3)
                                                                                      + y,
                                                                              (this.zCoord << 3)
                                                                                      + z);
                    }
                }
                if (Block.getBlockById(lastId).getTickRandomly()) {
                    --this.tickRefCount;
                }
            }
            if (content[x][y][z] != id) {
                return false;
            } else {
                metadatas[x][y][z] = metadata;

                TileEntity tileentity;

                if (id != 0) {
                    Block.getBlockById(id).onBlockAdded((World) this.getLittleWorld(),
                                                      (this.xCoord << 3) + x,
                                                      (this.yCoord << 3) + y,
                                                      (this.zCoord << 3) + z);

                    if (Block.getBlockById(id) != null
                        && Block.getBlockById(id).hasTileEntity(metadata)) {
                        tileentity = this.getChunkBlockTileEntity(x,
                                                                  y,
                                                                  z);

                        if (tileentity == null) {
                            tileentity = Block.getBlockById(id).createTileEntity((World) this.getLittleWorld(),
                                                                               metadata);
                            ((World) this.getLittleWorld()).setTileEntity(x,
                                                                               y,
                                                                               z,
                                                                               tileentity);
                        }

                        if (tileentity != null) {
                            tileentity.updateContainingBlockInfo();
                            tileentity.blockMetadata = metadata;
                        }
                    }
                    if (Block.getBlockById(id).getTickRandomly()) {
                        ++this.tickRefCount;
                    }
                }
                this.markDirty();
                return true;
            }
        }
    }

    public void setBlockIDs(int[][][] content) {
        this.content = content;
    }

    protected void setLittleWorldObjs() {
        ILittleWorld littleWorld = this.getLittleWorld();
        Iterator tiles = this.chunkTileEntityMap.values().iterator();
        while (tiles.hasNext()) {
            TileEntity tile = (TileEntity) tiles.next();
            tile.setWorldObj((World) littleWorld);
            if (littleWorld != null) {
                ((World) littleWorld).addTileEntity(tile);
            }
        }

        Iterator ticks = this.pendingBlockUpdates.iterator();
        while (ticks.hasNext()) {
            NBTTagCompound pendingTick = (NBTTagCompound) ticks.next();

            ((World) this.getLittleWorld()).func_147446_b/*scheduleBlockUpdateFromLoad*/(pendingTick.getInteger("x"),
                                                                        pendingTick.getInteger("y"),
                                                                        pendingTick.getInteger("z"),
                                                                        Block.getBlockById(pendingTick.getInteger("i")),
                                                                        pendingTick.getInteger("t"),
                                                                        pendingTick.getInteger("p"));
        }
    }

    public void setChunkBlockTileEntity(int x, int y, int z, TileEntity tile) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);

        tile.setWorldObj((World) this.getLittleWorld());
        tile.xCoord = (this.xCoord << 3) + x;
        tile.yCoord = (this.yCoord << 3) + y;
        tile.zCoord = (this.zCoord << 3) + z;
        Block block = Block.getBlockById(this.getBlockID(x,
                                                       y,
                                                       z));
        if (block != null && block.hasTileEntity(this.getBlockMetadata(x,
                                                                       y,
                                                                       z))) {
            if (this.chunkTileEntityMap.containsKey(chunkposition)) {
                ((TileEntity) this.chunkTileEntityMap.get(chunkposition)).invalidate();
            }
            tile.validate();
            this.chunkTileEntityMap.put(chunkposition,
                                        tile);
        }
    }

    public void removeChunkBlockTileEntity(int x, int y, int z) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.remove(chunkposition);

        if (tileentity != null) {
            tileentity.invalidate();
        }
    }

    public void cleanChunkBlockTileEntity(int x, int y, int z) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);

        if (tileentity != null && tileentity.isInvalid()) {
            this.chunkTileEntityMap.remove(chunkposition);
        }
    }

    public Collection<TileEntity> getTileEntityList() {
        return this.chunkTileEntityMap.values();
    }

    @Override
    public void onChunkUnload() {
        Iterator<TileEntity> tiles = this.chunkTileEntityMap.values().iterator();
        while (tiles.hasNext()) {
            TileEntity tile = tiles.next();
            ((World) this.getLittleWorld()).func_147457_a/*markTileEntityForDespawn*/(tile);
        }
    }

    public TileEntity getChunkBlockTileEntity(int x, int y, int z) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);

        if (tileentity != null && tileentity.isInvalid()) {
            this.chunkTileEntityMap.remove(chunkposition);
            tileentity = null;
        }

        if (tileentity == null) {
            int id = this.getBlockID(x,
                                     y,
                                     z);
            int meta = this.getBlockMetadata(x,
                                             y,
                                             z);
            if (id <= 0 || !Block.getBlockById(id).hasTileEntity(meta)) {
                return null;
            }

            if (tileentity == null) {
                Block littleBlock = Block.getBlockById(id);
                tileentity = littleBlock.createTileEntity((World) this.getLittleWorld(),
                                                          meta);
                ((World) this.getLittleWorld()).setTileEntity((xCoord << 3)
                                                                           + x,
                                                                   (yCoord << 3)
                                                                           + y,
                                                                   (zCoord << 3)
                                                                           + z,
                                                                   tileentity);
            }
            tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);
        }
        return tileentity;
    }

    private void addTileEntity(TileEntity tile) {
        int x = tile.xCoord & 7;
        int y = tile.yCoord & 7;
        int z = tile.zCoord & 7;
        this.setChunkBlockTileEntity(x,
                                     y,
                                     z,
                                     tile);
    }

    @Override
    public void updateEntity() {
        if (ConfigurationLib.littleBlocksForceUpdate) {
            for (int x = 0; x < this.content.length; x++) {
                for (int y = 0; y < this.content[x].length; y++) {
                    for (int z = 0; z < this.content[x][y].length; z++) {
                        if (this.content[x][y][z] != 0
                            && Block.getBlockById(content[x][y][z]) == null) {
                            this.content[x][y][z] = 0;
                            ChunkPosition chunkposition = new ChunkPosition(x, y, z);
                            this.chunkTileEntityMap.remove(chunkposition);
                        }
                    }
                }
            }
        }
    }

    List<NBTTagCompound> pendingBlockUpdates = new ArrayList<NBTTagCompound>();

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        int data = 0;
        NBTTagList list = nbttagcompound.getTagList("Content", 10);
        for (int x = 0; x < this.content.length; x++) {
            for (int y = 0; y < this.content[x].length; y++) {
                for (int z = 0; z < this.content[x][y].length; z++) {
                    NBTTagCompound tag = list.getCompoundTagAt((x << 6) + (y << 3) + z);
                    System.out.println(tag);
                    data = tag.getInteger("");
                    this.content[x][y][z] = data;
                    if (this.getTicksRandomly(data)) {
                        ++this.tickRefCount;
                    }
                }
            }
        }
        NBTTagList list2 = nbttagcompound.getTagList("Metadatas", 10);
        for (int x = 0; x < this.metadatas.length; x++) {
            for (int y = 0; y < this.metadatas[x].length; y++) {
                for (int z = 0; z < this.metadatas[x][y].length; z++) {
                    NBTTagCompound tag = list2.getCompoundTagAt((x << 6) + (y << 3) + z);
                    System.out.println(tag);
                    data = tag.getInteger("");
                    this.metadatas[x][y][z] = data;
                }
            }
        }

        // this.chunkTileEntityMap.clear();
        // this.tiles.clear();
        NBTTagList tilesTag = nbttagcompound.getTagList("Tiles", 10);
        if (tilesTag != null) {
            for (int i = 0; i < tilesTag.tagCount(); i++) {
                NBTTagCompound tileCompound = (NBTTagCompound) tilesTag.getCompoundTagAt(i);
                TileEntity tile = TileEntity.createAndLoadEntity(tileCompound);

                if (tile != null) {
                    this.addTileEntity(tile);
                }
            }
        }

        if (nbttagcompound.hasKey("TileTicks")) {
            NBTTagList tickList = nbttagcompound.getTagList("TileTicks", 10);

            if (tickList != null) {
                for (int i = 0; i < tickList.tagCount(); i++) {
                    NBTTagCompound pendingTick = (NBTTagCompound) tickList.getCompoundTagAt(i);
                    this.pendingBlockUpdates.add(pendingTick);
                }
            }
        }
    }

    private boolean getTicksRandomly(int blockId) {
        return blockId != 0 && Block.getBlockById(blockId) != null
               && Block.getBlockById(blockId).getTickRandomly();
    }

    public boolean getNeedsRandomTick() {
        return this.tickRefCount > 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        NBTTagList list = new NBTTagList();
        for (int x = 0; x < this.content.length; x++) {
            for (int y = 0; y < this.content[x].length; y++) {
                for (int z = 0; z < this.content[x][y].length; z++) {
                    list.appendTag(new NBTTagInt(this.content[x][y][z]));
                }
            }
        }
        nbttagcompound.setTag("Content",
                              list);

        NBTTagList list2 = new NBTTagList();
        for (int x = 0; x < this.metadatas.length; x++) {
            for (int y = 0; y < this.metadatas[x].length; y++) {
                for (int z = 0; z < this.metadatas[x][y].length; z++) {
                    list2.appendTag(new NBTTagInt(this.metadatas[x][y][z]));
                }
            }
        }
        nbttagcompound.setTag("Metadatas",
                              list2);

        NBTTagList tilesTag = new NBTTagList();
        Iterator iterator = this.chunkTileEntityMap.values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();
            NBTTagCompound tileTag = new NBTTagCompound();
            try {
                tileentity.writeToNBT(tileTag);
                tilesTag.appendTag(tileTag);
            } catch (Exception e) {
                SlimevoidCore.console(CoreLib.MOD_ID,
                           "A TileEntity type %s has throw an exception trying to write state into a LittleWorld. It will not persist. Report this to the mod author - " + 
                           e.getLocalizedMessage(),
                           2);
            }
        }
        nbttagcompound.setTag("Tiles",
                              tilesTag);
        List pendingUpdates = ((World) this.getLittleWorld()).getPendingBlockUpdates(new Chunk((World) this.getLittleWorld(), this.xCoord, this.zCoord),
                                                                                     false);
        if (pendingUpdates != null) {
            long time = ((World) this.getLittleWorld()).getTotalWorldTime();
            NBTTagList pendingUpdateList = new NBTTagList();
            Iterator pendingIterator = pendingUpdates.iterator();

            while (pendingIterator.hasNext()) {
                NextTickListEntry nextticklistentry = (NextTickListEntry) pendingIterator.next();
                NBTTagCompound pendingUpdate = new NBTTagCompound();
                pendingUpdate.setInteger("i",
                                         Block.getIdFromBlock(nextticklistentry.func_151351_a()));
                pendingUpdate.setInteger("x",
                                         nextticklistentry.xCoord);
                pendingUpdate.setInteger("y",
                                         nextticklistentry.yCoord);
                pendingUpdate.setInteger("z",
                                         nextticklistentry.zCoord);
                pendingUpdate.setInteger("t",
                                         (int) (nextticklistentry.scheduledTime - time));
                pendingUpdate.setInteger("p",
                                         nextticklistentry.priority);
                pendingUpdateList.appendTag(pendingUpdate);
            }
            nbttagcompound.setTag("TileTicks",
                                  pendingUpdateList);
        }
    }

    public void clearContents() {
        this.content = new int[ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize];
        this.metadatas = new int[ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize];
    }

    @Override
    public void markDirty() {
        this.worldObj.markBlockForUpdate(xCoord,
                                         yCoord,
                                         zCoord);
        super.markDirty();
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
        this.markDirty();
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        Packet packet = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbttagcompound);
        return packet;
    }

    public void setMetadatas(int[][][] metadata) {
        this.metadatas = metadata;
    }

    public int[][][] getMetadatas() {
        return this.metadatas;
    }

    public void rotateContents(ForgeDirection axis) {
        int max = ConfigurationLib.littleBlocksSize - 1;
        int[][][] newContent = new int[ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize];
        int[][][] newMetadata = new int[ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize];
        for (int y = 0; y < ConfigurationLib.littleBlocksSize; y++) {
            for (int x = 0; x < ConfigurationLib.littleBlocksSize; x++) {
                for (int z = 0; z < ConfigurationLib.littleBlocksSize; z++) {
                    int littleBlockID = this.getBlockID(x,
                                                        y,
                                                        z);
                    if (littleBlockID > 0) {
                        Block littleBlock = Block.getBlockById(littleBlockID);
                        int meta = this.metadatas[x][y][z];
                        if (littleBlock != null) {
                            if (littleBlock.rotateBlock((World) this.getLittleWorld(),
                                                        (this.xCoord << 3) + x,
                                                        (this.yCoord << 3) + y,
                                                        (this.zCoord << 3) + z,
                                                        axis)) {
                            }
                            newContent[max - z][y][x] = content[x][y][z];
                            newMetadata[max - z][y][x] = metadatas[x][y][z];
                        }
                    }
                }
            }
        }

        this.setBlockIDs(newContent);
        this.setMetadatas(newMetadata);
    }

    private int getX(int x) {
        return (this.xCoord << 3) + x;
    }

    private int getY(int y) {
        return (this.yCoord << 3) + y;
    }

    private int getZ(int z) {
        return (this.zCoord << 3) + z;
    }

    public void littleUpdateTick(ILittleWorld littleWorld, int updateLCG) {
        int baseCoord = updateLCG >> 2;
        int x = (baseCoord & 15) % 8;
        int y = (baseCoord >> 8 & 15) % 8;
        int z = (baseCoord >> 16 & 15) % 8;
        // System.out.println("X: " + x + " | Y: " + y + " | Z: " + z);
        int blockId = this.content[x][y][z];
        Block block = Block.getBlockById(blockId);

        if (block != null && block.getTickRandomly()) {
            block.updateTick((World) littleWorld,
                             this.getX(x),
                             this.getY(y),
                             this.getZ(z),
                             ((World) littleWorld).rand);
        }
    }
}
