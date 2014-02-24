package slimevoid.littleblocks.tileentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeDirection;
import slimevoid.littleblocks.api.ILittleBlocks;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLittleChunk extends TileEntity implements ILittleBlocks {
    public int                             size               = ConfigurationLib.littleBlocksSize;
    private int                            content[][][]      = new int[size][size][size];
    private int                            metadatas[][][]    = new int[size][size][size];
    private int                            lightmap[][][]     = new int[size][size][size];
    private boolean                        isLit              = false;
    private Map<ChunkPosition, TileEntity> chunkTileEntityMap = new HashMap<ChunkPosition, TileEntity>();

    @Override
    public void setWorldObj(World par1World) {
        this.worldObj = par1World;
        this.setTileEntityWorldObjs();
    }

    public TileEntityLittleChunk() {
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
                    if (Block.blocksList[this.content[x][y][z]] == null) {
                        lightcount[0]++;
                    } else {
                        lightcount[Block.blocksList[this.content[x][y][z]].getLightValue(this.getLittleWorld(),
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
            if (this.worldObj.getBlockId(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
                                                                                                              + (x >= size ? 1 : 0),
                                                                                                      yCoord
                                                                                                              + (y >= size ? 1 : 0),
                                                                                                      zCoord
                                                                                                              + (z >= size ? 1 : 0));
                return tile.getBlockMetadata(x >= size ? x - size : x,
                                             y >= size ? y - size : y,
                                             z >= size ? z - size : z);
            }
            if (this.worldObj.getBlockId(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == 0) {
                return 0;
            }
            return -1;
        } else if (x < 0 | z < 0 | y < 0) {
            if (this.worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
                                                                                                              - (x < 0 ? 1 : 0),
                                                                                                      yCoord
                                                                                                              - (y < 0 ? 1 : 0),
                                                                                                      zCoord
                                                                                                              - (z < 0 ? 1 : 0));
                return tile.getBlockMetadata(x < 0 ? x + size : x,
                                             y < 0 ? y + size : y,
                                             z < 0 ? z + size : z);
            }
            if (this.worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == 0) {
                return 0;
            }
            return -1;
        } else {
            return metadatas[x][y][z];
        }
    }

    public int getBlockID(int x, int y, int z) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlockId(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
                                                                                                              + (x >= size ? 1 : 0),
                                                                                                      yCoord
                                                                                                              + (y >= size ? 1 : 0),
                                                                                                      zCoord
                                                                                                              + (z >= size ? 1 : 0));
                return tile.getBlockID(x >= size ? x - size : x,
                                       y >= size ? y - size : y,
                                       z >= size ? z - size : z);
            }
            if (this.worldObj.getBlockId(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == 0) {
                return 0;
            }
            return -1;
        } else if (x < 0 | z < 0 | y < 0) {
            if (this.worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
                                                                                                              - (x < 0 ? 1 : 0),
                                                                                                      yCoord
                                                                                                              - (y < 0 ? 1 : 0),
                                                                                                      zCoord
                                                                                                              - (z < 0 ? 1 : 0));
                return tile.getBlockID(x < 0 ? x + size : x,
                                       y < 0 ? y + size : y,
                                       z < 0 ? z + size : z);
            }
            if (this.worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == 0) {
                return 0;
            }
            return -1;
        } else {
            return this.content[x][y][z];
        }
    }

    public int getBlockLightValue(int x, int y, int z, int side) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlockId(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
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
            if (this.worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
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

    // public void updateTick(Random rand) {
    // for (int xx = 0; xx < ConfigurationLib.littleBlocksSize; xx++) {
    // for (int yy = 0; yy < ConfigurationLib.littleBlocksSize; yy++) {
    // for (int zz = 0; zz < ConfigurationLib.littleBlocksSize; zz++) {
    // Block littleBlock = Block.blocksList[this.content[xx][yy][zz]];
    // if (littleBlock != null && littleBlock.getTickRandomly()) {
    // int x = (this.xCoord << 3) + xx, y = (this.yCoord << 3)
    // + yy, z = (this.zCoord << 3)
    // + zz;
    // littleBlock.updateTick( (World) this.getLittleWorld(),
    // x,
    // y,
    // z,
    // rand);
    // }
    // }
    // }
    // }
    // }

    public int getSavedLightValue(int x, int y, int z) {
        return 0;
    }

    public void setLightValue(int x, int y, int z, int value) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlockId(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
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
            if (this.worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
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
            this.onInventoryChanged();
        }
    }

    public boolean setBlockMetadata(int x, int y, int z, int metadata) {
        if (x >= size | y >= size | z >= size) {
            if (this.worldObj.getBlockId(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == 0) {
                this.worldObj.setBlock(xCoord + (x >= size ? 1 : 0),
                                       yCoord + (y >= size ? 1 : 0),
                                       zCoord + (z >= size ? 1 : 0),
                                       ConfigurationLib.littleChunkID,
                                       0,
                                       0x02);
            }
            if (this.worldObj.getBlockId(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
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
            if (this.worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == 0) {
                this.worldObj.setBlock(xCoord - (x < 0 ? 1 : 0),
                                       yCoord - (y < 0 ? 1 : 0),
                                       zCoord - (z < 0 ? 1 : 0),
                                       ConfigurationLib.littleChunkID,
                                       0,
                                       0x02);
            }
            if (this.worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
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
            this.onInventoryChanged();
            this.metadatas[x][y][z] = metadata;
            int blockId = this.content[x][y][z];

            if (blockId > 0 && Block.blocksList[blockId] != null
                && Block.blocksList[blockId].hasTileEntity(metadata)) {
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
            if (this.worldObj.getBlockId(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == 0) {
                this.worldObj.setBlock(xCoord + (x >= size ? 1 : 0),
                                       yCoord + (y >= size ? 1 : 0),
                                       zCoord + (z >= size ? 1 : 0),
                                       ConfigurationLib.littleChunkID,
                                       0,
                                       0x02);
            }
            if (this.worldObj.getBlockId(xCoord + (x >= size ? 1 : 0),
                                         yCoord + (y >= size ? 1 : 0),
                                         zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
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
            if (this.worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == 0) {
                this.worldObj.setBlock(xCoord - (x < 0 ? 1 : 0),
                                       yCoord - (y < 0 ? 1 : 0),
                                       zCoord - (z < 0 ? 1 : 0),
                                       ConfigurationLib.littleChunkID,
                                       0,
                                       0x02);
            }
            if (this.worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0),
                                         yCoord - (y < 0 ? 1 : 0),
                                         zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunkID) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(xCoord
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
                Block.blocksList[lastId].onBlockPreDestroy(this.getWorldObj(),
                                                           ((this.xCoord << 3) + x),
                                                           ((this.yCoord << 3) + y),
                                                           ((this.zCoord << 3) + z),
                                                           lastData);
            }

            content[x][y][z] = id;

            if (lastId != 0) {
                if (!this.worldObj.isRemote) {
                    Block.blocksList[lastId].breakBlock((World) this.getLittleWorld(),
                                                        (this.xCoord << 3) + x,
                                                        (this.yCoord << 3) + y,
                                                        (this.zCoord << 3) + z,
                                                        lastId,
                                                        lastData);
                } else if (Block.blocksList[lastId] != null
                           && Block.blocksList[lastId].hasTileEntity(lastData)) {
                    TileEntity te = this.getLittleWorld().getBlockTileEntity((this.xCoord << 3)
                                                                                     + x,
                                                                             (this.yCoord << 3)
                                                                                     + y,
                                                                             (this.zCoord << 3)
                                                                                     + z);
                    if (te != null
                        && te.shouldRefresh(lastId,
                                            id,
                                            lastData,
                                            metadata,
                                            (World) this.getLittleWorld(),
                                            (this.xCoord << 3) + x,
                                            (this.yCoord << 3) + y,
                                            (this.zCoord << 3) + z)) {
                        ((World) this.getLittleWorld()).removeBlockTileEntity((this.xCoord << 3)
                                                                                      + x,
                                                                              (this.yCoord << 3)
                                                                                      + y,
                                                                              (this.zCoord << 3)
                                                                                      + z);
                    }
                }
            }
            if (content[x][y][z] != id) {
                return false;
            } else {
                metadatas[x][y][z] = metadata;

                TileEntity tileentity;

                if (id != 0) {
                    Block.blocksList[id].onBlockAdded((World) this.getLittleWorld(),
                                                      (this.xCoord << 3) + x,
                                                      (this.yCoord << 3) + y,
                                                      (this.zCoord << 3) + z);

                    if (Block.blocksList[id] != null
                        && Block.blocksList[id].hasTileEntity(metadata)) {
                        tileentity = this.getChunkBlockTileEntity(x,
                                                                  y,
                                                                  z);

                        if (tileentity == null) {
                            tileentity = Block.blocksList[id].createTileEntity((World) this.getLittleWorld(),
                                                                               metadata);
                            ((World) this.getLittleWorld()).setBlockTileEntity(x,
                                                                               y,
                                                                               z,
                                                                               tileentity);
                        }

                        if (tileentity != null) {
                            tileentity.updateContainingBlockInfo();
                            tileentity.blockMetadata = metadata;
                        }
                    }
                }
                this.onInventoryChanged();
                return true;
            }
        }
    }

    public void setBlockIDs(int[][][] content) {
        this.content = content;
    }

    private void setTileEntityWorldObjs() {
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

            ((World) this.getLittleWorld()).scheduleBlockUpdateFromLoad(pendingTick.getInteger("x"),
                                                                        pendingTick.getInteger("y"),
                                                                        pendingTick.getInteger("z"),
                                                                        pendingTick.getInteger("i"),
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
        Block block = Block.blocksList[this.getBlockID(x,
                                                       y,
                                                       z)];
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
            ((World) this.getLittleWorld()).markTileEntityForDespawn(tile);
        }
    }

    public TileEntity getChunkBlockTileEntity(int x, int y, int z) {
        ChunkPosition chunkposition = new ChunkPosition(x, y, z);
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(chunkposition);
        // TileEntity tileentity = this.getTileEntityFromList( x,
        // y,
        // z);

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
            if (id <= 0 || !Block.blocksList[id].hasTileEntity(meta)) {
                return null;
            }

            if (tileentity == null) {
                Block littleBlock = Block.blocksList[id];
                tileentity = littleBlock.createTileEntity((World) this.getLittleWorld(),
                                                          meta);
                ((World) this.getLittleWorld()).setBlockTileEntity((xCoord << 3)
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
                            && Block.blocksList[content[x][y][z]] == null) {
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
        NBTTagList list = nbttagcompound.getTagList("Content");
        for (int x = 0; x < this.content.length; x++) {
            for (int y = 0; y < this.content[x].length; y++) {
                for (int z = 0; z < this.content[x][y].length; z++) {
                    this.content[x][y][z] = ((NBTTagInt) list.tagAt((x << 6)
                                                                    + (y << 3)
                                                                    + z)).data;
                }
            }
        }
        NBTTagList list2 = nbttagcompound.getTagList("Metadatas");
        for (int x = 0; x < this.metadatas.length; x++) {
            for (int y = 0; y < this.metadatas[x].length; y++) {
                for (int z = 0; z < this.metadatas[x][y].length; z++) {
                    this.metadatas[x][y][z] = ((NBTTagInt) list2.tagAt((x << 6)
                                                                       + (y << 3)
                                                                       + z)).data;
                }
            }
        }

        // this.chunkTileEntityMap.clear();
        // this.tiles.clear();
        NBTTagList tilesTag = nbttagcompound.getTagList("Tiles");
        if (tilesTag != null) {
            for (int i = 0; i < tilesTag.tagCount(); i++) {
                NBTTagCompound tileCompound = (NBTTagCompound) tilesTag.tagAt(i);
                TileEntity tile = TileEntity.createAndLoadEntity(tileCompound);

                if (tile != null) {
                    this.addTileEntity(tile);
                }
            }
        }

        if (nbttagcompound.hasKey("TileTicks")) {
            NBTTagList tickList = nbttagcompound.getTagList("TileTicks");

            if (tickList != null) {
                for (int i = 0; i < tickList.tagCount(); i++) {
                    NBTTagCompound pendingTick = (NBTTagCompound) tickList.tagAt(i);
                    this.pendingBlockUpdates.add(pendingTick);
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        NBTTagList list = new NBTTagList();
        for (int x = 0; x < this.content.length; x++) {
            for (int y = 0; y < this.content[x].length; y++) {
                for (int z = 0; z < this.content[x][y].length; z++) {
                    list.appendTag(new NBTTagInt(null, this.content[x][y][z]));
                }
            }
        }
        nbttagcompound.setTag("Content",
                              list);

        NBTTagList list2 = new NBTTagList();
        for (int x = 0; x < this.metadatas.length; x++) {
            for (int y = 0; y < this.metadatas[x].length; y++) {
                for (int z = 0; z < this.metadatas[x][y].length; z++) {
                    list2.appendTag(new NBTTagInt(null, this.metadatas[x][y][z]));
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
                FMLLog.log(Level.SEVERE,
                           e,
                           "A TileEntity type %s has throw an exception trying to write state into a LittleWorld. It will not persist. Report this to the mod author",
                           tileentity.getClass().getName());
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
                                         nextticklistentry.blockID);
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
    public void onInventoryChanged() {
        this.worldObj.markBlockForUpdate(xCoord,
                                         yCoord,
                                         zCoord);
        super.onInventoryChanged();
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
        this.readFromNBT(pkt.data);
        this.onInventoryChanged();
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        Packet packet = new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbttagcompound);
        return packet;
    }

    public void setMetadatas(int[][][] metadata) {
        this.metadatas = metadata;
    }

    public int[][][] getMetadatas() {
        return this.metadatas;
    }

    @SideOnly(Side.CLIENT)
    public void setTiles(List<NBTTagCompound> tileentities) {
        for (int i = 0; i < tileentities.size(); i++) {
            TileEntity tile = TileEntity.createAndLoadEntity(tileentities.get(i));
            if (tile != null) {
                this.addTileEntity(tile);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleBlockAdded(World world, EntityPlayer entityplayer, PacketLittleBlocks packetLB) {
        int id = packetLB.getBlockID(), meta = packetLB.getMetadata(), x = packetLB.xPosition, y = packetLB.yPosition, z = packetLB.zPosition;
        // System.out.println("X| " + x + " Y| " + y + " Z| " + z + " ID| " + id
        // + " META| " + meta);
        // this.setBlockIDWithMetadata(x & 7,
        // y & 7,
        // z & 7,
        // id,
        // meta);
        this.content[x & 7][y & 7][z & 7] = id;
        this.metadatas[x & 7][y & 7][z & 7] = meta;
        /*
         * Block.blocksList[id].onBlockAdded( (World) this.getLittleWorld(), x,
         * y, z);
         */
        // this.onInventoryChanged();
    }

    @SideOnly(Side.CLIENT)
    public void handleBreakBlock(World world, EntityPlayer entityplayer, PacketLittleBlocks packetLB) {
        // this.setBlockID(packetLB.xPosition & 7,
        // packetLB.yPosition & 7,
        // packetLB.zPosition & 7,
        // 0);
        this.content[packetLB.xPosition & 7][packetLB.yPosition & 7][packetLB.zPosition & 7] = 0;
        /*
         * Block.blocksList[packetLB.getBlockID()].breakBlock( (World)
         * this.getLittleWorld(), packetLB.xPosition, packetLB.yPosition,
         * packetLB.zPosition, packetLB.side, packetLB.getMetadata());
         */
        // this.onInventoryChanged();
    }

    @SideOnly(Side.CLIENT)
    public void handleUpdateMetadata(World world, EntityPlayer entityplayer, PacketLittleBlocks packetLB) {
        int id = packetLB.getBlockID(), meta = packetLB.getMetadata(), x = packetLB.xPosition, y = packetLB.yPosition, z = packetLB.zPosition;
        // System.out.println("X| " + x + " Y| " + y + " Z| " + z + " ID| " + id
        // + " META| " + meta);
        // this.setBlockMetadata( x & 7,
        // y & 7,
        // z & 7,
        // meta);
        this.metadatas[x & 7][y & 7][z & 7] = meta;
        // this.onInventoryChanged();
    }

    @SideOnly(Side.CLIENT)
    public void handleLittleTilePacket(World world, PacketLittleBlocks packetLB) {
        int x = packetLB.xPosition, y = packetLB.yPosition, z = packetLB.zPosition;
        TileEntity littleTile = TileEntity.createAndLoadEntity(packetLB.getTileEntityData());
        if (littleTile != null) {
            // this.setChunkBlockTileEntity( x & 7,
            // y & 7,
            // z & 7,
            // littleTile);
            this.chunkTileEntityMap.put(new ChunkPosition(x & 7, y & 7, z & 7),
                                        littleTile);
        }
        // this.onInventoryChanged();
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
                        Block littleBlock = Block.blocksList[littleBlockID];
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

    public void updateTick(Random rand) {
        this.littleUpdateTick(rand);
    }

    public void littleUpdateTick(Random rand) {
        for (int x = 0; x < this.content.length; x++) {
            for (int y = 0; y < this.content[x].length; y++) {
                for (int z = 0; z < this.content[x][y].length; z++) {
                    Block littleBlock = content[x][y][z] != 0 ? Block.blocksList[content[x][y][z]] : null;
                    if (littleBlock != null && littleBlock.getTickRandomly()) {
                        littleBlock.updateTick((World) this.getLittleWorld(),
                                               this.getX(x),
                                               this.getY(y),
                                               this.getZ(z),
                                               rand);
                    }
                }
            }
        }
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

}
