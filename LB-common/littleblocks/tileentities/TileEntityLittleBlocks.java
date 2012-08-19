package littleblocks.tileentities;

import java.util.ArrayList;
import java.util.List;

import littleblocks.core.LBCore;
import littleblocks.core.LittleWorld;
import littleblocks.network.packets.PacketTileEntityLB;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagInt;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.WorldProviderSurface;
import net.minecraft.src.EurysMods.network.packets.core.PacketPayload;

public class TileEntityLittleBlocks extends TileEntity {
	public static int size = 8;
	private int content[][][] = new int[size][size][size];
	private int metadatas[][][] = new int[size][size][size];
	private List<TileEntity> tiles = new ArrayList<TileEntity>();
	private boolean upToDate = false;

	private static LittleWorld littleWorld;
	
	@Override
    public void func_70308_a(World par1World)
    {
        this.worldObj = par1World;
        this.littleWorld = getLittleWorld(this.worldObj);
    }

	public TileEntityLittleBlocks() {
		for (int x = 0; x < content.length; x++) {
			for (int y = 0; y < content[x].length; y++) {
				for (int z = 0; z < content[x][y].length; z++) {
					content[x][y][z] = 0;
				}
			}
		}

		for (int x = 0; x < metadatas.length; x++) {
			for (int y = 0; y < metadatas[x].length; y++) {
				for (int z = 0; z < metadatas[x][y].length; z++) {
					metadatas[x][y][z] = 0;
				}
			}
		}
	}

	public boolean isEmpty() {
		for (int x = 0; x < content.length; x++) {
			for (int y = 0; y < content[x].length; y++) {
				for (int z = 0; z < content[x][y].length; z++) {
					if (content[x][y][z] > 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public int[][][] getContent() {
		return content;
	}

	public LittleWorld getLittleWorld() {
		if (littleWorld == null || littleWorld.isOutdated(worldObj)) {
			littleWorld = new LittleWorld(this.worldObj,
					new WorldProviderSurface());
		}
		return littleWorld;
	}

	public static LittleWorld getLittleWorld(World world) {
		if (littleWorld == null) {
			if (world == null) {
				return null;
			}
			littleWorld = new LittleWorld(world, new WorldProviderSurface());
		}
		return littleWorld;
	}

	public int getMetadata(int x, int y, int z) {
		if (x >= size | y >= size | z >= size) {
			if (worldObj.getBlockId(xCoord + (x >= size ? 1 : 0), yCoord
					+ (y >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(xCoord + (x >= size ? 1 : 0),
								yCoord + (y >= size ? 1 : 0), zCoord
										+ (z >= size ? 1 : 0));
				return tile.getMetadata(x >= size ? x - size : x, y >= size ? y
						- size : y, z >= size ? z - size : z);
			}
			if (worldObj.getBlockId(xCoord + (x >= size ? 1 : 0), yCoord
					+ (y >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0)) == 0) {
				return 0;
			}
			return -1;
		} else if (x < 0 | z < 0 | y < 0) {
			if (worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0), yCoord
					- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(xCoord - (x < 0 ? 1 : 0), yCoord
								- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0));
				return tile.getMetadata(x < 0 ? x + size : x, y < 0 ? y + size
						: y, z < 0 ? z + size : z);
			}
			if (worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0), yCoord
					- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == 0) {
				return 0;
			}
			return -1;
		} else {
			return metadatas[x][y][z];
		}
	}

	public int getContent(int x, int y, int z) {
		if (x >= size | y >= size | z >= size) {
			if (worldObj.getBlockId(xCoord + (x >= size ? 1 : 0), yCoord
					+ (y >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(xCoord + (x >= size ? 1 : 0),
								yCoord + (y >= size ? 1 : 0), zCoord
										+ (z >= size ? 1 : 0));
				return tile.getContent(x >= size ? x - size : x, y >= size ? y
						- size : y, z >= size ? z - size : z);
			}
			if (worldObj.getBlockId(xCoord + (x >= size ? 1 : 0), yCoord
					+ (y >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0)) == 0) {
				return 0;
			}
			return -1;
		} else if (x < 0 | z < 0 | y < 0) {
			if (worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0), yCoord
					- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(xCoord - (x < 0 ? 1 : 0), yCoord
								- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0));
				return tile.getContent(x < 0 ? x + size : x, y < 0 ? y + size
						: y, z < 0 ? z + size : z);
			}
			if (worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0), yCoord
					- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == 0) {
				return 0;
			}
			return -1;
		} else {
			return content[x][y][z];
		}
	}

	public void setContent(int x, int y, int z, int id) {
		if (x >= size | y >= size | z >= size) {
			if (worldObj.getBlockId(xCoord + (x >= size ? 1 : 0), yCoord
					+ (y >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(xCoord + (x >= size ? 1 : 0),
						yCoord + (y >= size ? 1 : 0), zCoord
								+ (z >= size ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(xCoord + (x >= size ? 1 : 0), yCoord
					+ (y >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(xCoord + (x >= size ? 1 : 0),
								yCoord + (y >= size ? 1 : 0), zCoord
										+ (z >= size ? 1 : 0));
				tile.setContent(x >= size ? x - size : x, y >= size ? y - size
						: y, z >= size ? z - size : z, id);
			}
			return;
		} else if (x < 0 | z < 0 | y < 0) {
			if (worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0), yCoord
					- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(xCoord - (x < 0 ? 1 : 0), yCoord
						- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0), yCoord
					- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(xCoord - (x < 0 ? 1 : 0), yCoord
								- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0));
				tile.setContent(x < 0 ? x + size : x, y < 0 ? y + size : y,
						z < 0 ? z + size : z, id);
			}
			return;
		}
		int lastId = content[x][y][z];
		content[x][y][z] = id;
		setMetadata(x, y, z, 0);

		if (lastId != id) {
			littleWorld.idModified((this.xCoord << 3) + x, (this.yCoord << 3)
					+ y, (this.zCoord << 3) + z, 0, 0, 0, 0, lastId, id);
		}
	}

	public void setMetadata(int x, int y, int z, int metadata) {
		if (x >= size | y >= size | z >= size) {
			if (worldObj.getBlockId(xCoord + (x >= size ? 1 : 0), yCoord
					+ (y >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(xCoord + (x >= size ? 1 : 0),
						yCoord + (y >= size ? 1 : 0), zCoord
								+ (z >= size ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(xCoord + (x >= size ? 1 : 0), yCoord
					+ (y >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(xCoord + (x >= size ? 1 : 0),
								yCoord + (y >= size ? 1 : 0), zCoord
										+ (z >= size ? 1 : 0));
				tile.setMetadata(x >= size ? x - size : x, y >= size ? y - size
						: y, z >= size ? z - size : z, metadata);
			}
			return;
		} else if (x < 0 | z < 0 | y < 0) {
			if (worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0), yCoord
					- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(xCoord - (x < 0 ? 1 : 0), yCoord
						- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0), yCoord
					- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(xCoord - (x < 0 ? 1 : 0), yCoord
								- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0));
				tile.setMetadata(x < 0 ? x + size : x, y < 0 ? y + size : y,
						z < 0 ? z + size : z, metadata);
			}
			return;
		}
		metadatas[x][y][z] = metadata;

		if (metadatas[x][y][z] != metadata) {
			littleWorld.metadataModified((this.xCoord << 3) + x,
					(this.yCoord << 3) + y, (this.zCoord << 3) + z,
					0, 0, 0, 0,
					metadatas[x][y][z], metadata);
		}
	}

	public void setContent(int x, int y, int z, int id, int metadata) {
		if (x >= size | y >= size | z >= size) {
			if (worldObj.getBlockId(xCoord + (x >= size ? 1 : 0), yCoord
					+ (y >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(xCoord + (x >= size ? 1 : 0),
						yCoord + (y >= size ? 1 : 0), zCoord
								+ (z >= size ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(xCoord + (x >= size ? 1 : 0), yCoord
					+ (y >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(xCoord + (x >= size ? 1 : 0),
								yCoord + (y >= size ? 1 : 0), zCoord
										+ (z >= size ? 1 : 0));
				tile.setContent(x >= size ? x - size : x, y >= size ? y - size
						: y, z >= size ? z - size : z, id, metadata);
			}
			return;
		} else if (x < 0 | z < 0 | y < 0) {
			if (worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0), yCoord
					- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(xCoord - (x < 0 ? 1 : 0), yCoord
						- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(xCoord - (x < 0 ? 1 : 0), yCoord
					- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(xCoord - (x < 0 ? 1 : 0), yCoord
								- (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0));
				tile.setContent(x < 0 ? x + size : x, y < 0 ? y + size : y,
						z < 0 ? z + size : z, id, metadata);
			}
			return;
		}
		int lastId = content[x][y][z];
		int lastData = metadatas[x][y][z];
		content[x][y][z] = id;
		metadatas[x][y][z] = metadata;

		if (lastData != metadata) {
			littleWorld.metadataModified((this.xCoord << 3) + x,
					(this.yCoord << 3) + y, (this.zCoord << 3) + z,
					0, 0, 0, 0,
					lastData, metadata);
		}
		if (lastId != id) {
			littleWorld.idModified((this.xCoord << 3) + x, (this.yCoord << 3)
					+ y, (this.zCoord << 3) + z, 
					0, 0, 0, 0, lastId, id);
		}
	}

	public void setContent(int[][][] content) {
		this.content = content;
	}

	public void setTileEntity(int x, int y, int z, TileEntity tile) {
		tile.worldObj = getLittleWorld();
		tile.xCoord = (xCoord << 3) + x;
		tile.yCoord = (yCoord << 3) + y;
		tile.zCoord = (zCoord << 3) + z;
		removeTileEntity(x, y, z);
		tile.validate();
		addTileEntity(tile);
	}

	public TileEntity getTileEntity(int x, int y, int z) {
		for (TileEntity tile : tiles) {
			if (tile.xCoord == (xCoord << 3) + x
					&& tile.yCoord == (yCoord << 3) + y
					&& tile.zCoord == (zCoord << 3) + z) {
				return tile;
			}
		}
		return null;
	}

	private void addTileEntity(TileEntity tile) {
		tiles.add(tile);
	}

	public void removeTileEntity(int x, int y, int z) {
		TileEntity toRm = getTileEntity(x, y, z);

		if (toRm != null) {
			removeTileEntity(toRm);
		}
	}

	private void removeTileEntity(TileEntity tile) {
		tiles.remove(tile);
	}

	@Override
	public void updateEntity() {
		for (int i = 0; i < tiles.size(); i++) {
			tiles.get(i).updateEntity();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList list = nbttagcompound.getTagList("Content");
		for (int x = 0; x < content.length; x++) {
			for (int y = 0; y < content[x].length; y++) {
				for (int z = 0; z < content[x][y].length; z++) {
					content[x][y][z] = ((NBTTagInt) list.tagAt((x << 6)
							+ (y << 3) + z)).data;
				}
			}
		}
		NBTTagList list2 = nbttagcompound.getTagList("Metadatas");
		for (int x = 0; x < metadatas.length; x++) {
			for (int y = 0; y < metadatas[x].length; y++) {
				for (int z = 0; z < metadatas[x][y].length; z++) {
					metadatas[x][y][z] = ((NBTTagInt) list2.tagAt((x << 6)
							+ (y << 3) + z)).data;
				}
			}
		}

		tiles.clear();
		NBTTagList tilesTag = nbttagcompound.getTagList("Tiles");
		for (int i = 0; i < tilesTag.tagCount(); i++) {
			TileEntity tile = TileEntity
					.createAndLoadEntity((NBTTagCompound) tilesTag.tagAt(i));
			setTileEntity(tile.xCoord & 7, tile.yCoord & 7, tile.zCoord & 7,
					tile);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		NBTTagList list = new NBTTagList();
		for (int x = 0; x < content.length; x++) {
			for (int y = 0; y < content[x].length; y++) {
				for (int z = 0; z < content[x][y].length; z++) {
					list.appendTag(new NBTTagInt(null, content[x][y][z]));
				}
			}
		}
		nbttagcompound.setTag("Content", list);

		NBTTagList list2 = new NBTTagList();
		for (int x = 0; x < metadatas.length; x++) {
			for (int y = 0; y < metadatas[x].length; y++) {
				for (int z = 0; z < metadatas[x][y].length; z++) {
					list2.appendTag(new NBTTagInt(null, metadatas[x][y][z]));
				}
			}
		}
		nbttagcompound.setTag("Metadatas", list2);

		NBTTagList tilesTag = new NBTTagList();
		for (TileEntity tile : tiles) {
			NBTTagCompound tileTag = new NBTTagCompound();
			tile.writeToNBT(tileTag);
			tilesTag.appendTag(tileTag);
		}
		nbttagcompound.setTag("Tiles", tilesTag);
	}

	@Override
	public Packet getAuxillaryInfoPacket() {
		return this.getUpdatePacket();
	}

	public Packet getUpdatePacket() {
		return new PacketTileEntityLB(this).getPacket();
	}

	public PacketPayload getTileEntityPayload() {
		int blocks = 0;
		for (int x = 0; x < content.length; x++) {
			for (int y = 0; y < content[x].length; y++) {
				for (int z = 0; z < content[x][y].length; z++) {
					if (content[x][y][z] > 0) {
						blocks++;
					}
				}
			}
		}
		int[] dataInt;
		if (upToDate) {
			dataInt = new int[] { 0, 0 };
		} else if (blocks > 204) {
			// (3 /* POS */ + 2 /* ID & DATA */)x > 8*8*8*2 /* ID & DATA */ <=>
			// 5x > 1024 <=> x > 204.8
			dataInt = new int[2 + 8 * 8 * 8 * 2];
			dataInt[0] = 0;
			dataInt[1] = 1;
			for (int x = 0; x < content.length; x++) {
				for (int y = 0; y < content[x].length; y++) {
					for (int z = 0; z < content[x][y].length; z++) {
						dataInt[1 + x + y * 8 + z * 8 * 8] = content[x][y][z];
					}
				}
			}

			for (int x = 0; x < metadatas.length; x++) {
				for (int y = 0; y < metadatas[x].length; y++) {
					for (int z = 0; z < metadatas[x][y].length; z++) {
						dataInt[1 + 8 * 8 * 8 + x + y * 8 + z * 8 * 8] = metadatas[x][y][z];
					}
				}
			}
		} else {
			dataInt = new int[2 + blocks * 5];
			dataInt[0] = 0;
			dataInt[1] = 2;
			int i = 0;
			for (int x = 0; x < content.length; x++) {
				for (int y = 0; y < content[x].length; y++) {
					for (int z = 0; z < content[x][y].length; z++) {
						if (content[x][y][z] > 0) {
							dataInt[2 + i * 5 + 0] = x;
							dataInt[2 + i * 5 + 1] = y;
							dataInt[2 + i * 5 + 2] = z;
							dataInt[2 + i * 5 + 3] = content[x][y][z];
							dataInt[2 + i * 5 + 4] = metadatas[x][y][z];
							i++;
						}
					}
				}
			}
		}

		upToDate = false;
		// return ModLoaderMp.getTileEntityPacket(mod_LittleBlocks.instance,
		// xCoord, yCoord, zCoord, getMetadata(xCoord, yCoord, zCoord), dataInt,
		// null, null);
		PacketPayload p = new PacketPayload(dataInt.length, 0, 1, 0);
		p.setStringPayload(0,
				String.valueOf(getMetadata(xCoord, yCoord, zCoord)));
		for (int i = 0; i < dataInt.length; i++) {
			p.setIntPayload(i, dataInt[i]);
		}
		return p;
	}
}
