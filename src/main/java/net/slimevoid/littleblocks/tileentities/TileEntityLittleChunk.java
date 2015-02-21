package net.slimevoid.littleblocks.tileentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.collect.Maps;

import com.google.common.collect.Queues;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.slimevoid.library.core.SlimevoidCore;
import net.slimevoid.library.core.lib.CoreLib;
import net.slimevoid.littleblocks.api.ILittleBlocks;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.world.storage.ExtendedLittleBlockStorage;
import net.slimevoid.littleblocks.world.storage.NibbleLittleArray;

public class TileEntityLittleChunk extends TileEntity implements IUpdatePlayerListBox, ILittleBlocks {
    public int                             size               = ConfigurationLib.littleBlocksSize;
    private ExtendedBlockStorage     storageArray;
    private boolean                        isLit              = false;
    private Map chunkTileEntityMap;
    private ConcurrentLinkedQueue queuedTileEntity;
    
    public TileEntityLittleChunk() {
        super();
        this.chunkTileEntityMap = Maps.newHashMap();
        this.storageArray = new ExtendedBlockStorage(0, false);
        this.queuedTileEntity = Queues.newConcurrentLinkedQueue();
    }

    public boolean isEmpty() {
        return this.storageArray.isEmpty();
    }

    @Override
    public void setWorldObj(World world) {
        this.worldObj = world;
    }
    
    @Override
    public void validate() {
        this.setLittleWorldObjs();
    }

    public void updateContainingBlockInfo() {
    }

    public ILittleWorld getLittleWorld() {
        return LittleBlocks.proxy.getLittleWorld(this.worldObj,
                                                 false);
    }
    
    private Block getBlockByExtId/*getBlock0*/(int x, int y, int z) {
        Block block = Blocks.air;
        try {
            block = this.storageArray.getBlockByExtId(x, y & 7, z);
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block");
            throw new ReportedException(crashreport);
        }
        return block;
    }

    public Block getBlock(final int x, final int y, final int z) {
        try {
            return this.getBlockByExtId(x & 7, y, z & 7);
        } catch(ReportedException reportedexception) {
            CrashReportCategory crashreportcategory = reportedexception.getCrashReport().makeCategory("Block being got");
            crashreportcategory.addCrashSectionCallable("Location", new Callable()
            {
                private static final String __OBFID = "CL_00000374";
                public String call()
                {
                    return CrashReportCategory.getCoordinateInfo(pos);
                }
            });
            throw reportedexception;
        }
    }
    
    public Block getBlock(BlockPos position) {
        try {
            return this.getBlockByExtId(position.getX() & 7, position.getY(), position.getZ() & 7);
        } catch(ReportedException reportedexception) {
            CrashReportCategory crashreportcategory = reportedexception.getCrashReport().makeCategory("Block being got");
            crashreportcategory.addCrashSectionCallable("Location", new Callable() {
                private static final String __OBFID = "CL_00000374";

                public String call() {
                    return CrashReportCategory.getCoordinateInfo(pos);
                }
            });
            throw reportedexception;
        }
    }

	public IBlockState getBlockState(BlockPos pos) {
		int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        if (x >= this.size | y >= this.size | z >= this.size) {
        	BlockPos actualPos = new BlockPos(
                	this.getPos().getX() + (x >= this.size ? 1 : 0),
                	this.getPos().getY() + (y >= this.size ? 1 : 0),
                	this.getPos().getZ() + (z >= this.size ? 1 : 0));
        	IBlockState blockState = this.getWorld().getBlockState(pos);
        	if (blockState.getBlock().isAssociatedBlock(this.blockType)) {
        		TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getWorld().getTileEntity(actualPos);
        		BlockPos newLittlePos = new BlockPos(x >= this.size ? x - this.size : x,
		                                             y >= this.size ? y - this.size : y,
		                                             z >= this.size ? z - this.size : z);
        		return tile.getBlockState(newLittlePos);
        	}
        	if (blockState.getBlock().isAssociatedBlock(Blocks.air)) {
        		return Blocks.air.getDefaultState();
        	}
        	return null;
        } else if (x < 0 | z < 0 | y < 0) {
        	BlockPos actualPos = new BlockPos(
                	this.getPos().getX() - (x < 0 ? 1 : 0),
                	this.getPos().getY() - (y < 0 ? 1 : 0),
                	this.getPos().getZ() - (z < 0 ? 1 : 0));
        	IBlockState blockState = this.getWorld().getBlockState(pos);
        	if (blockState.getBlock().isAssociatedBlock(this.blockType)) {
        		TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getWorld().getTileEntity(actualPos);
        		BlockPos newLittlePos = new BlockPos(x < 0 ? x + this.size : x,
							                         y < 0 ? y + this.size : y,
							                         z < 0 ? z + this.size : z);
        		return tile.getBlockState(newLittlePos);
        	}
        	if (blockState.getBlock().isAssociatedBlock(Blocks.air)) {
        		return Blocks.air.getDefaultState();
        	}
        	return null;
        } else {
        	return this.storageArray.get(x,
        	                             y,
        	                             z);
        }
	}

    public int getBlockMetadata(int x, int y, int z) {
        return this.storageArray.getExtBlockMetadata(x,
                                                     y,
                                                     z);
    }
    
    public int getBlockMetadata(BlockPos pos) {
        return this.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());
    }

    public IBlockState setBlockState(BlockPos pos, IBlockState newState) {
        int x = pos.getX() & 7, y = pos.getY(), z = pos.getZ() & 7;
        if (x >= this.size | y >= this.size | z >= this.size) {
            BlockPos actualPos = new BlockPos(
                    this.getPos().getX() + (x >= this.size ? 1 : 0),
                    this.getPos().getY() + (y >= this.size ? 1 : 0),
                    this.getPos().getZ() + (z >= this.size ? 1 : 0));
            IBlockState oldState = this.getWorld().getBlockState(actualPos);
            if (oldState.getBlock().isAssociatedBlock(Blocks.air)) {
                this.getWorld().setBlockState(actualPos, ConfigurationLib.littleChunk.getDefaultState());
            }
            if (oldState.getBlock().isAssociatedBlock(this.blockType)) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getWorld().getTileEntity(actualPos);
                BlockPos newLittlePos = new BlockPos(x >= this.size ? x - this.size : x,
                        y >= this.size ? y - this.size : y,
                        z >= this.size ? z - this.size : z);
                return tile.setBlockState(newLittlePos, newState);
            }
            return null;
        } else if (x < 0 | z < 0 | y < 0) {
            BlockPos actualPos = new BlockPos(
                    this.getPos().getX() - (x < 0 ? 1 : 0),
                    this.getPos().getY() - (y < 0 ? 1 : 0),
                    this.getPos().getZ() - (z < 0 ? 1 : 0));
            IBlockState oldState = this.getWorld().getBlockState(pos);
            if (oldState.getBlock().isAssociatedBlock(Blocks.air)) {
                this.getWorld().setBlockState(actualPos, ConfigurationLib.littleChunk.getDefaultState());
            }
            if (oldState.getBlock().isAssociatedBlock(this.blockType)) {
                TileEntityLittleChunk tile = (TileEntityLittleChunk) this.getWorld().getTileEntity(actualPos);
                BlockPos newLittlePos = new BlockPos(x < 0 ? x + this.size : x,
                        y < 0 ? y + this.size : y,
                        z < 0 ? z + this.size : z);
                return tile.setBlockState(newLittlePos, newState);
            }
            return null;
        }
        IBlockState oldState = this.getBlockState(pos);
        if (oldState == newState) {
            return null;
        } else {
            Block newBlock = newState.getBlock();
            Block oldBlock = oldState.getBlock();
            boolean replaced = false;
            this.storageArray.set(x, y & 7, z, newState);
            if (!this.getWorld().isRemote) {
                if (oldBlock != newBlock) {
                    oldBlock.breakBlock((World) this.getLittleWorld(), pos, oldState);
                    TileEntity tileentity = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
                    if (tileentity != null && tileentity.shouldRefresh((World) this.getLittleWorld(), pos, oldState, newState)) {
                        ((World) this.getLittleWorld()).removeTileEntity(pos);
                    }
                }
            }
            if (this.storageArray.getBlockByExtId(x, y & 7, z) != newBlock) {
                return null;
            } else {
                TileEntity tileentity;
                if (this.getWorld().isRemote && oldBlock != newBlock) {
                    newBlock.onBlockAdded((World) this.getLittleWorld(), pos, newState);
                }
                if (newBlock.hasTileEntity(newState)) {
                    tileentity = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
                    if (tileentity == null) {
                        tileentity = newBlock.createTileEntity((World) this.getLittleWorld(), newState);
                        ((World) this.getLittleWorld()).setTileEntity(pos, tileentity);
                    }
                    if (tileentity != null) {
                        tileentity.updateContainingBlockInfo();
                    }
                }
                this.markDirty();
                return oldState;
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
                littleWorld.addLoadedTileEntity(tile);
            }
        }

        Iterator ticks = this.pendingBlockUpdates.iterator();
        while (ticks.hasNext()) {
            NBTTagCompound pendingTick = (NBTTagCompound) ticks.next();

            ((World) this.getLittleWorld()).func_180497_b/* scheduleBlockUpdateFromLoad */(new BlockPos(pendingTick.getInteger("x"),
                                                                                                       pendingTick.getInteger("y"),
                                                                                                       pendingTick.getInteger("z")),
                                                                                           Block.getBlockById(pendingTick.getInteger("i")),
                                                                                           pendingTick.getInteger("t"),
                                                                                           pendingTick.getInteger("p"));
        }
    }

    @Override
    public void onChunkUnload() {
        Collection<TileEntity> tiles = this.chunkTileEntityMap.values();
        for (TileEntity tile : tiles) {
            ((World) this.getLittleWorld()).markTileEntityForRemoval(tile);
        }
    }

    private TileEntity createNewTileEntity(BlockPos pos) {
        Block block = this.getBlock(pos);
        IBlockState state = block.getStateFromMeta(this.getBlockMetadata(pos));
        return !block.hasTileEntity(state) ? null : block.createTileEntity((World) this.getLittleWorld(), state);
    }

    public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType type) {
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(pos);

        if (tileentity != null && tileentity.isInvalid()) {
            this.chunkTileEntityMap.remove(pos);
            tileentity = null;
        }

        if (tileentity == null) {
            if (type == Chunk.EnumCreateEntityType.IMMEDIATE) {
                tileentity = this.createNewTileEntity(pos);
                ((World) this.getLittleWorld()).setTileEntity(pos, tileentity);
            } else if (type == Chunk.EnumCreateEntityType.QUEUED) {
                this.queuedTileEntity.add(pos);
            }
        }
        return tileentity;
    }

    public void addTileEntity(TileEntity tile) {
        this.addTileEntity(tile.getPos(), tile);
        if (!this.isInvalid() && this.getLittleWorld() != null) {
        	this.getLittleWorld().addLoadedTileEntity(tile);
        }
    }

    public void addTileEntity(BlockPos pos, TileEntity tile) {
        tile.setWorldObj((World) this.getLittleWorld());
        tile.setPos(pos);

        if (this.getBlock(pos).hasTileEntity(this.getBlock(pos).getStateFromMeta(this.getBlockMetadata(pos)))) {
            if (this.chunkTileEntityMap.containsKey(pos)) {
                ((TileEntity) this.chunkTileEntityMap.get(pos)).invalidate();
            }
            tile.validate();
            this.chunkTileEntityMap.put(pos, tile);
        }
    }

    public void removeTileEntity(BlockPos pos) {
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.remove(pos);

        if (tileentity != null) {
            tileentity.invalidate();
        }
    }

    public void removeInvalidTileEntity(BlockPos pos) {
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(pos);

        if (tileentity != null && tileentity.isInvalid()) {
            this.chunkTileEntityMap.remove(pos);
        }
    }

    public Collection<TileEntity> getTileEntityList() {
        return this.chunkTileEntityMap.values();
    }

    @Override
    public void update() {
        if (ConfigurationLib.littleBlocksForceUpdate) {
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    for (int z = 0; z < size; z++) {
                        if (this.getBlockByExtId(x,
                                                 y,
                                                 z) == null) {
                            this.storageArray.set(x,
                                                    y,
                                                    z,
                                                    Blocks.air.getDefaultState());
                            BlockPos pos = new BlockPos(x, y, z);
                            this.chunkTileEntityMap.remove(pos);
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

        ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(0, false);
        byte[] blocks = nbttagcompound.getByteArray("Blocks");
        NibbleArray metadata = new NibbleArray(nbttagcompound.getByteArray("Data"));
        NibbleArray msb = nbttagcompound.hasKey("Add", 7) ? new NibbleArray(nbttagcompound.getByteArray("Add")) : null;
        char[] blockdata = new char[blocks.length];

        for (int i = 0; i < blockdata.length; ++i)
        {
            int x = i & 15;
            int y = i >> 8 & 15;
            int z = i >> 4 & 15;
            int l1 = msb != null ? msb.get(x, y, z) : 0;
            blockdata[i] = (char)(l1 << 12 | (blocks[i] & 255) << 4 | metadata.get(x, y, z));
        }

        extendedblockstorage.setData(blockdata);

        extendedblockstorage.removeInvalidBlocks();
        this.storageArray = extendedblockstorage;
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

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        byte[] blocks = new byte[this.storageArray.getData().length];
        NibbleArray metadata = new NibbleArray();
        NibbleArray extdata = null;

        for (int i = 0; i < this.storageArray.getData().length; ++i)
        {
            char data = this.storageArray.getData()[i];
            int x = i & 15;
            int y = i >> 8 & 15;
            int z = i >> 4 & 15;

            if (data >> 12 != 0)
            {
                if (extdata == null)
                {
                    extdata = new NibbleArray();
                }

                extdata.set(x, y, z, data >> 12);
            }

            blocks[i] = (byte)(data >> 4 & 255);
            metadata.set(x, y, z, data & 15);
        }

        nbttagcompound.setByteArray("Blocks", blocks);
        nbttagcompound.setByteArray("Data", metadata.getData());

        if (extdata != null)
        {
            nbttagcompound.setByteArray("Add", extdata.getData());
        }

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
        List pendingUpdates = ((World) this.getLittleWorld()).getPendingBlockUpdates(new Chunk((World) this.getLittleWorld(), this.getPos().getX(), this.getPos().getZ()),
                                                                                     false);
        if (pendingUpdates != null) {
            long time = ((World) this.getLittleWorld()).getTotalWorldTime();
            NBTTagList pendingUpdateList = new NBTTagList();
            Iterator pendingIterator = pendingUpdates.iterator();

            while (pendingIterator.hasNext()) {
                NextTickListEntry nextticklistentry = (NextTickListEntry) pendingIterator.next();
                NBTTagCompound pendingUpdate = new NBTTagCompound();
                pendingUpdate.setInteger("i",
                                         Block.getIdFromBlock(nextticklistentry.getBlock()));
                pendingUpdate.setInteger("x",
                                         nextticklistentry.position.getX());
                pendingUpdate.setInteger("y",
                                         nextticklistentry.position.getY());
                pendingUpdate.setInteger("z",
                                         nextticklistentry.position.getZ());
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
        this.storageArray = new ExtendedBlockStorage(0, false);
    }

    @Override
    public void markDirty() {
        this.worldObj.markBlockForUpdate(this.getPos());
        super.markDirty();
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
        this.markDirty();
        this.getWorld().markBlockForUpdate(this.getPos());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        Packet packet = new S35PacketUpdateTileEntity(this.getPos(), 0, nbttagcompound);
        return packet;
    }

    public void rotateContents(EnumFacing axis) {
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
                        int meta = this.storageArray.getExtBlockMetadata(x,
                                                            y,
                                                            z);
                        if (littleBlock != null) {
                            if (littleBlock.rotateBlock((World) this.getLittleWorld(),
                                                        this.getLittlePos(x, y, z),
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
        return (this.getPos().getX() << 3) + x;
    }

    private int getY(int y) {
        return (this.getPos().getY() << 3) + y;
    }

    private int getZ(int z) {
        return (this.getPos().getZ() << 3) + z;
    }

    private BlockPos getLittlePos(int x, int y, int z) {
        return new BlockPos(this.getX(x), this.getY(y), this.getZ(z));
    }

    public void littleUpdateTick(ILittleWorld littleWorld, int updateLCG) {
        int baseCoord = updateLCG >> 2;
        int x = (baseCoord & 15) % 8;
        int y = (baseCoord >> 8 & 15) % 8;
        int z = (baseCoord >> 16 & 15) % 8;
        BlockPos pos = this.getLittlePos(x, y, z);
        // System.out.println("X: " + x + " | Y: " + y + " | Z: " + z);
        Block block = this.getBlockByExtId(x,
                                           y,
                                           z);
        IBlockState state = this.getBlockState(pos);

        if (block != null && block.getTickRandomly()) {
            block.updateTick((World) littleWorld,
                             pos,
                             state,
                             ((World) littleWorld).rand);
        }
    }
}
