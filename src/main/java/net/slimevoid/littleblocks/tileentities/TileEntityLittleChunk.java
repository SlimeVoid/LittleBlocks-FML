package net.slimevoid.littleblocks.tileentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.common.util.ForgeDirection;
import net.slimevoid.library.core.SlimevoidCore;
import net.slimevoid.library.core.lib.CoreLib;
import net.slimevoid.littleblocks.api.ILittleBlocks;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;

public class TileEntityLittleChunk extends TileEntity implements ILittleBlocks {
    public int                             size               = ConfigurationLib.littleBlocksSize;
    private int                            arraySize          = (size * size * size) * 4;
    private byte[]                         blockLSBArray      = new byte[arraySize];
    private NibbleArray                    blockMSBArray;
    private NibbleArray                    blockMetadataArray = new NibbleArray(this.blockLSBArray.length, 4);
    private int[][][]                      lightMap           = new int[size][size][size];
    private boolean                        isLit              = false;
    private Map<ChunkPosition, TileEntity> chunkTileEntityMap = new HashMap<ChunkPosition, TileEntity>();
    private int                            tickRefCount       = 0;
    private int                            blockRefCount      = 0;

    @Override
    public void setWorldObj(World world) {
        this.worldObj = world;
    }
    
    @Override
    public void validate() {
        this.setLittleWorldObjs();
    }

    public void updateContainingBlockInfo() {
        this.blockLSBArray = new byte[arraySize];

        this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length, 4);

        this.getLittleWorld().activeChunkPosition(new ChunkPosition(this.xCoord, this.yCoord, this.zCoord),
                                                  true);
    }

    public Block getBlockByExtId(int x, int y, int z) {
        int l = this.blockLSBArray[y << 8 | z << 4 | x] & 255;

        if (this.blockMSBArray != null) {
            l |= this.blockMSBArray.get(x,
                                        y,
                                        z) << 8;
        }

        return Block.getBlockById(l);
    }

    public int getExtBlockMetadata(int x, int y, int z) {
        return this.blockMetadataArray.get(x,
                                           y,
                                           z);
    }

    public boolean isEmpty() {
        return this.blockRefCount == 0;
    }

    public int getLightlevel() {
        int blockX = (xCoord << 3), blockY = (yCoord << 3), blockZ = (zCoord << 3);
        int lightcount[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    if (this.getBlockByExtId(x,
                                             y,
                                             z) == null) {
                        lightcount[0]++;
                    } else {
                        lightcount[this.getBlockByExtId(x,
                                                        y,
                                                        z).getLightValue(this.getLittleWorld(),
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
                                       zCoord + (z >= size ? 1 : 0)) == Blocks.air) {
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
                                       zCoord - (z < 0 ? 1 : 0)) == Blocks.air) {
                return 0;
            }
            return -1;
        } else {
            return getExtBlockMetadata(x,
                                       y,
                                       z);
        }
    }

    public Block getBlock(int x, int y, int z) {
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
                return tile.getBlock(x >= size ? x - size : x,
                                     y >= size ? y - size : y,
                                     z >= size ? z - size : z);
            }
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                       yCoord + (y >= size ? 1 : 0),
                                       zCoord + (z >= size ? 1 : 0)) == Blocks.air) {
                return Blocks.air;
            }
            return null;
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
                return tile.getBlock(x < 0 ? x + size : x,
                                     y < 0 ? y + size : y,
                                     z < 0 ? z + size : z);
            }
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                       yCoord - (y < 0 ? 1 : 0),
                                       zCoord - (z < 0 ? 1 : 0)) == Blocks.air) {
                return Blocks.air;
            }
            return null;
        } else {
            return this.getBlockByExtId(x,
                                        y,
                                        z);
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
            return lightMap[x][y][z];
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
        int lastLight = this.lightMap[x][y][z];
        if (lastLight == value) {
            return;
        } else {
            this.lightMap[x][y][z] = value;
            this.markDirty();
        }
    }

    public boolean setBlockMetadata(int x, int y, int z, int metadata) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                       yCoord + (y >= size ? 1 : 0),
                                       zCoord + (z >= size ? 1 : 0)) == Blocks.air) {
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
                                       zCoord - (z < 0 ? 1 : 0)) == Blocks.air) {
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
        int lastData = this.getExtBlockMetadata(x,
                                                y,
                                                z);
        if (lastData == metadata) {
            return false;
        } else {
            this.setExtBlockMetadata(x,
                                     y,
                                     z,
                                     metadata);
            Block block = this.getBlockByExtId(x,
                                               y,
                                               z);

            if (block != Blocks.air && block.hasTileEntity(metadata)) {
                TileEntity tileentity = this.getChunkTileEntity(x,
                                                                y,
                                                                z);

                if (tileentity != null) {
                    tileentity.updateContainingBlockInfo();
                    tileentity.blockMetadata = metadata;
                }
            }
            this.markDirty();
            return true;
        }
    }

    public void setExtBlockMetadata(int x, int y, int z, int metadata) {
        this.blockMetadataArray.set(x,
                                    y,
                                    z,
                                    metadata);
    }

    public void setExtBlockId(int x, int y, int z, Block block) {
        int lastBlockId = this.blockLSBArray[y << 8 | z << 4 | x] & 255;

        if (this.blockMSBArray != null) {
            lastBlockId |= this.blockMSBArray.get(x,
                                                  y,
                                                  z) << 8;
        }

        Block lastBlock = Block.getBlockById(lastBlockId);

        if (lastBlock != Blocks.air) {
            --this.blockRefCount;

            if (lastBlock.getTickRandomly()) {
                --this.tickRefCount;
            }
        }

        if (block != Blocks.air) {
            ++this.blockRefCount;

            if (block.getTickRandomly()) {
                ++this.tickRefCount;
            }
        }

        int blockId = Block.getIdFromBlock(block);
        this.blockLSBArray[y << 8 | z << 4 | x] = (byte) (blockId & 255);

        if (blockId > 255) {
            if (this.blockMSBArray == null) {
                this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
            }

            this.blockMSBArray.set(x,
                                   y,
                                   z,
                                   (blockId & 3840) >> 8);
        } else if (this.blockMSBArray != null) {
            this.blockMSBArray.set(x,
                                   y,
                                   z,
                                   0);
        }
    }

    public TileEntity getTileEntityUnsafe(int x, int y, int z) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);

        if (tileentity != null && tileentity.isInvalid()) {
            chunkTileEntityMap.remove(chunkposition);
            tileentity = null;
        }

        return tileentity;
    }

    public boolean setBlockIDWithMetadata(int x, int y, int z, Block block, int metadata) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlock(xCoord + (x >= size ? 1 : 0),
                                       yCoord + (y >= size ? 1 : 0),
                                       zCoord + (z >= size ? 1 : 0)) == Blocks.air) {
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
                                                   block,
                                                   metadata);
            }
        } else if (x < 0 | z < 0 | y < 0) {
            if (this.worldObj.getBlock(xCoord - (x < 0 ? 1 : 0),
                                       yCoord - (y < 0 ? 1 : 0),
                                       zCoord - (z < 0 ? 1 : 0)) == Blocks.air) {
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
                                                   block,
                                                   metadata);
            }
        }
        Block lastBlock = this.getBlock(x,
                                        y,
                                        z);

        int lastData = this.getBlockMetadata(x,
                                             y,
                                             z);// this.metadatas[x][y][z];

        if (lastBlock == block && lastData == metadata) {
            return false;
        } else {
            if (!this.worldObj.isRemote) {
                lastBlock.onBlockPreDestroy(this.getWorldObj(),
                                            ((this.xCoord << 3) + x),
                                            ((this.yCoord << 3) + y),
                                            ((this.zCoord << 3) + z),
                                            lastData);
            }

            this.setExtBlockId(x,
                               y,
                               z,
                               block);
            this.setExtBlockMetadata(x,
                                     y,
                                     z,
                                     metadata);

            if (!this.worldObj.isRemote) {
                lastBlock.breakBlock((World) this.getLittleWorld(),
                                     (this.xCoord << 3) + x,
                                     (this.yCoord << 3) + y,
                                     (this.zCoord << 3) + z,
                                     lastBlock,
                                     lastData);
            } else if (lastBlock.hasTileEntity(lastData)) {
                TileEntity te = this.getTileEntityUnsafe(x,
                                                         y,
                                                         z);
                if (te != null
                    && te.shouldRefresh(lastBlock,
                                        block,
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
            if (this.getBlockByExtId(x,
                                     y,
                                     z) != block) {
                return false;
            } else {
            	this.setExtBlockMetadata(x, y, z, metadata);
                TileEntity tileentity;

                if (!this.worldObj.isRemote) {
                    block.onBlockAdded((World) this.getLittleWorld(),
                                       (this.xCoord << 3) + x,
                                       (this.yCoord << 3) + y,
                                       (this.zCoord << 3) + z);
                }
                if (block.hasTileEntity(metadata)) {
                    tileentity = this.getChunkTileEntity(x,
                                                         y,
                                                         z);

                    if (tileentity != null) {
                        tileentity.updateContainingBlockInfo();
                        tileentity.blockMetadata = metadata;
                    }
                }
                this.markDirty();
                return true;
            }
        }
    }

    protected void setLittleWorldObjs() {
        ILittleWorld littleWorld = this.getLittleWorld();
        Iterator tiles = this.chunkTileEntityMap.values().iterator();
        while (tiles.hasNext()) {
            TileEntity tile = (TileEntity) tiles.next();
            tile.setWorldObj((World) littleWorld);
            if (littleWorld != null) {
                littleWorld.addLoadedTileEntity(tile);;
            }
        }

        Iterator ticks = this.pendingBlockUpdates.iterator();
        while (ticks.hasNext()) {
            NBTTagCompound pendingTick = (NBTTagCompound) ticks.next();

            ((World) this.getLittleWorld()).func_147446_b/* scheduleBlockUpdateFromLoad */(pendingTick.getInteger("x"),
                                                                                           pendingTick.getInteger("y"),
                                                                                           pendingTick.getInteger("z"),
                                                                                           Block.getBlockById(pendingTick.getInteger("i")),
                                                                                           pendingTick.getInteger("t"),
                                                                                           pendingTick.getInteger("p"));
        }
    }

    public void setTileEntity(int x, int y, int z, TileEntity tile) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);

        tile.setWorldObj((World) this.getLittleWorld());
        tile.xCoord = (this.xCoord << 3) + x;
        tile.yCoord = (this.yCoord << 3) + y;
        tile.zCoord = (this.zCoord << 3) + z;
        Block block = this.getBlock(x,
                                    y,
                                    z);
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

    public void removeTileEntity(int x, int y, int z) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.remove(chunkposition);

        if (tileentity != null) {
            tileentity.invalidate();
        }
    }

    public void removeInvalidTileEntity(int x, int y, int z) {
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
            ((World) this.getLittleWorld()).func_147457_a/* markTileEntityForDespawn */(tile);
        }
    }

    public TileEntity getChunkTileEntity(int x, int y, int z) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);

        if (tileentity != null && tileentity.isInvalid()) {
            this.chunkTileEntityMap.remove(chunkposition);
            tileentity = null;
        }

        if (tileentity == null) {
            Block littleBlock = this.getBlock(x,
                                              y,
                                              z);
            int meta = this.getBlockMetadata(x,
                                             y,
                                             z);
            if (littleBlock == null || !littleBlock.hasTileEntity(meta)) {
                return null;
            }

            if (tileentity == null) {
                tileentity = littleBlock.createTileEntity((World) this.getLittleWorld(),
                                                          meta);
                ((World) this.getLittleWorld()).setTileEntity((this.xCoord << 3)
                                                                      + x,
                                                              (this.yCoord << 3)
                                                                      + y,
                                                              (this.zCoord << 3)
                                                                      + z,
                                                              tileentity);
            }
            tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);
        }
        return tileentity;
    }

    public void addTileEntity(TileEntity tile) {
        int x = tile.xCoord & 7;
        int y = tile.yCoord & 7;
        int z = tile.zCoord & 7;
        this.setTileEntity(x,
                                     y,
                                     z,
                                     tile);
        if (!this.isInvalid() && this.getLittleWorld() != null) {
        	this.getLittleWorld().addLoadedTileEntity(tile);
        }
    }

    @Override
    public void updateEntity() {
        if (ConfigurationLib.littleBlocksForceUpdate) {
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    for (int z = 0; z < size; z++) {
                        if (this.getBlockByExtId(x,
                                                 y,
                                                 z) == null) {
                            this.setExtBlockId(x,
                                               y,
                                               z,
                                               null);
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
        this.blockLSBArray = nbttagcompound.getByteArray("Blocks");
        if (nbttagcompound.hasKey("Add",
                                  7)) {
            this.blockMSBArray = new NibbleArray(nbttagcompound.getByteArray("Add"), 4);
        }
        this.blockMetadataArray = new NibbleArray(nbttagcompound.getByteArray("Data"), 4);
        this.removeInvalidBlocks();
        // this.chunkTileEntityMap.clear();
        // this.tiles.clear();
        NBTTagList tilesTag = nbttagcompound.getTagList("Tiles",
                                                        10);
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
            NBTTagList tickList = nbttagcompound.getTagList("TileTicks",
                                                            10);

            if (tickList != null) {
                for (int i = 0; i < tickList.tagCount(); i++) {
                    NBTTagCompound pendingTick = (NBTTagCompound) tickList.getCompoundTagAt(i);
                    this.pendingBlockUpdates.add(pendingTick);
                }
            }
        }
    }

    public void removeInvalidBlocks() {
        this.blockRefCount = 0;
        this.tickRefCount = 0;

        for (int x = 0; x < 8; ++x) {
            for (int y = 0; y < 8; ++y) {
                for (int z = 0; z < 8; ++z) {
                    Block block = this.getBlockByExtId(x,
                                                       y,
                                                       z);

                    if (block != Blocks.air) {
                        ++this.blockRefCount;

                        if (block.getTickRandomly()) {
                            ++this.tickRefCount;
                        }
                    }
                }
            }
        }
    }

    public boolean getNeedsRandomTick() {
        return this.tickRefCount > 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setByteArray("Blocks",
                                    this.blockLSBArray);
        if (this.blockMSBArray != null) {
            nbttagcompound.setByteArray("Add",
                                        this.blockMSBArray.data);
        }
        nbttagcompound.setByteArray("Data",
                                    this.blockMetadataArray.data);

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
                                      "A TileEntity type %s has throw an exception trying to write state into a LittleWorld. It will not persist. Report this to the mod author - "
                                              + e.getLocalizedMessage(),
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
        this.blockLSBArray = new byte[arraySize];
        this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length, 4);
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
        this.getWorldObj().markBlockForUpdate(this.xCoord,
                                              this.yCoord,
                                              this.zCoord);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        Packet packet = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbttagcompound);
        return packet;
    }

    public void rotateContents(ForgeDirection axis) {
        int max = ConfigurationLib.littleBlocksSize - 1;
        int[][][] newContent = new int[ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize];
        int[][][] newMetadata = new int[ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize];
        for (int y = 0; y < ConfigurationLib.littleBlocksSize; y++) {
            for (int x = 0; x < ConfigurationLib.littleBlocksSize; x++) {
                for (int z = 0; z < ConfigurationLib.littleBlocksSize; z++) {
                    Block littleBlock = this.getBlock(x,
                                                      y,
                                                      z);
                    if (littleBlock != Blocks.air) {
                        int meta = this.getExtBlockMetadata(x,
                                                            y,
                                                            z);
                        if (littleBlock != null) {
                            if (littleBlock.rotateBlock((World) this.getLittleWorld(),
                                                        (this.xCoord << 3) + x,
                                                        (this.yCoord << 3) + y,
                                                        (this.zCoord << 3) + z,
                                                        axis)) {
                            }
                            // newContent[max - z][y][x] = content[x][y][z];
                            // newMetadata[max - z][y][x] = metadatas[x][y][z];
                        }
                    }
                }
            }
        }

        // this.setBlockIDs(newContent);
        // this.setMetadatas(newMetadata);
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
        Block block = this.getBlockByExtId(x,
                                           y,
                                           z);

        if (block != null && block.getTickRandomly()) {
            block.updateTick((World) littleWorld,
                             this.getX(x),
                             this.getY(y),
                             this.getZ(z),
                             ((World) littleWorld).rand);
        }
    }
}
