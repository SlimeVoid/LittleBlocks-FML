package littleblocks.tileentities;

import java.util.ArrayList;
import java.util.List;

import littleblocks.api.ILBCommonProxy;
import littleblocks.blocks.BlockLittleBlocksBlock;
import littleblocks.core.LBCore;
import littleblocks.core.LBInit;
import littleblocks.network.packets.PacketTileEntityLB;
import littleblocks.world.LittleWorld;
import net.minecraft.src.Block;
import net.minecraft.src.BlockFlowing;
import net.minecraft.src.BlockStationary;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagInt;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet132TileEntityData;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;
import eurysmods.network.packets.core.PacketPayload;
import eurysmods.network.packets.core.PacketUpdate;

public class TileEntityLittleBlocks extends TileEntity {
	public int size = LBCore.littleBlocksSize;
	private int content[][][] = new int[size][size][size];
	private int metadatas[][][] = new int[size][size][size];
	private List<TileEntity> tiles = new ArrayList<TileEntity>();
	private boolean upToDate = false;

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		if (FMLCommonHandler.instance().getSide().isClient()) {
			ModLoader.getMinecraftInstance().thePlayer
					.addChatMessage("Data Packet");
		}
	}

	@Override
	public void setWorldObj(World par1World) {
		this.worldObj = par1World;
		((ILBCommonProxy) LBInit.LBM.getProxy()).getLittleWorld(
				this.worldObj,
				false);
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
		return ((ILBCommonProxy) LBInit.LBM.getProxy()).getLittleWorld(
				this.worldObj,
				false);
	}

	public int getMetadata(int x, int y, int z) {
		if (x >= size | y >= size | z >= size) {
			if (worldObj.getBlockId(
					xCoord + (x >= size ? 1 : 0),
					yCoord + (y >= size ? 1 : 0),
					zCoord + (z >= size ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(
								xCoord + (x >= size ? 1 : 0),
								yCoord + (y >= size ? 1 : 0),
								zCoord + (z >= size ? 1 : 0));
				return tile.getMetadata(
						x >= size ? x - size : x,
						y >= size ? y - size : y,
						z >= size ? z - size : z);
			}
			if (worldObj.getBlockId(
					xCoord + (x >= size ? 1 : 0),
					yCoord + (y >= size ? 1 : 0),
					zCoord + (z >= size ? 1 : 0)) == 0) {
				return 0;
			}
			return -1;
		} else if (x < 0 | z < 0 | y < 0) {
			if (worldObj.getBlockId(
					xCoord - (x < 0 ? 1 : 0),
					yCoord - (y < 0 ? 1 : 0),
					zCoord - (z < 0 ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(
								xCoord - (x < 0 ? 1 : 0),
								yCoord - (y < 0 ? 1 : 0),
								zCoord - (z < 0 ? 1 : 0));
				return tile.getMetadata(
						x < 0 ? x + size : x,
						y < 0 ? y + size : y,
						z < 0 ? z + size : z);
			}
			if (worldObj.getBlockId(
					xCoord - (x < 0 ? 1 : 0),
					yCoord - (y < 0 ? 1 : 0),
					zCoord - (z < 0 ? 1 : 0)) == 0) {
				return 0;
			}
			return -1;
		} else {
			return metadatas[x][y][z];
		}
	}

	public int getContent(int x, int y, int z) {
		if (x >= size | y >= size | z >= size) {
			if (worldObj.getBlockId(
					xCoord + (x >= size ? 1 : 0),
					yCoord + (y >= size ? 1 : 0),
					zCoord + (z >= size ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(
								xCoord + (x >= size ? 1 : 0),
								yCoord + (y >= size ? 1 : 0),
								zCoord + (z >= size ? 1 : 0));
				return tile.getContent(
						x >= size ? x - size : x,
						y >= size ? y - size : y,
						z >= size ? z - size : z);
			}
			if (worldObj.getBlockId(
					xCoord + (x >= size ? 1 : 0),
					yCoord + (y >= size ? 1 : 0),
					zCoord + (z >= size ? 1 : 0)) == 0) {
				return 0;
			}
			return -1;
		} else if (x < 0 | z < 0 | y < 0) {
			if (worldObj.getBlockId(
					xCoord - (x < 0 ? 1 : 0),
					yCoord - (y < 0 ? 1 : 0),
					zCoord - (z < 0 ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(
								xCoord - (x < 0 ? 1 : 0),
								yCoord - (y < 0 ? 1 : 0),
								zCoord - (z < 0 ? 1 : 0));
				return tile.getContent(
						x < 0 ? x + size : x,
						y < 0 ? y + size : y,
						z < 0 ? z + size : z);
			}
			if (worldObj.getBlockId(
					xCoord - (x < 0 ? 1 : 0),
					yCoord - (y < 0 ? 1 : 0),
					zCoord - (z < 0 ? 1 : 0)) == 0) {
				return 0;
			}
			return -1;
		} else {
			return content[x][y][z];
		}
	}

	public void setContent(int x, int y, int z, int id) {
		if (x >= size | y >= size | z >= size) {
			if (worldObj.getBlockId(
					xCoord + (x >= size ? 1 : 0),
					yCoord + (y >= size ? 1 : 0),
					zCoord + (z >= size ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(
						xCoord + (x >= size ? 1 : 0),
						yCoord + (y >= size ? 1 : 0),
						zCoord + (z >= size ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(
					xCoord + (x >= size ? 1 : 0),
					yCoord + (y >= size ? 1 : 0),
					zCoord + (z >= size ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(
								xCoord + (x >= size ? 1 : 0),
								yCoord + (y >= size ? 1 : 0),
								zCoord + (z >= size ? 1 : 0));
				tile.setContent(
						x >= size ? x - size : x,
						y >= size ? y - size : y,
						z >= size ? z - size : z,
						id);
			}
			return;
		} else if (x < 0 | z < 0 | y < 0) {
			if (worldObj.getBlockId(
					xCoord - (x < 0 ? 1 : 0),
					yCoord - (y < 0 ? 1 : 0),
					zCoord - (z < 0 ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(
						xCoord - (x < 0 ? 1 : 0),
						yCoord - (y < 0 ? 1 : 0),
						zCoord - (z < 0 ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(
					xCoord - (x < 0 ? 1 : 0),
					yCoord - (y < 0 ? 1 : 0),
					zCoord - (z < 0 ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(
								xCoord - (x < 0 ? 1 : 0),
								yCoord - (y < 0 ? 1 : 0),
								zCoord - (z < 0 ? 1 : 0));
				tile.setContent(
						x < 0 ? x + size : x,
						y < 0 ? y + size : y,
						z < 0 ? z + size : z,
						id);
			}
			return;
		}
		int lastId = content[x][y][z];
		content[x][y][z] = id;
		setMetadata(x, y, z, 0);
		if (lastId != id) {
			((ILBCommonProxy) LBInit.LBM.getProxy()).getLittleWorld(
					this.worldObj,
					false).idModified(lastId, this.xCoord, this.yCoord, this.zCoord, 0, x, y, z, id, 0);
		}
	}

	public void setMetadata(int x, int y, int z, int metadata) {
		if (x >= size | y >= size | z >= size) {
			if (worldObj.getBlockId(
					xCoord + (x >= size ? 1 : 0),
					yCoord + (y >= size ? 1 : 0),
					zCoord + (z >= size ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(
						xCoord + (x >= size ? 1 : 0),
						yCoord + (y >= size ? 1 : 0),
						zCoord + (z >= size ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(
					xCoord + (x >= size ? 1 : 0),
					yCoord + (y >= size ? 1 : 0),
					zCoord + (z >= size ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(
								xCoord + (x >= size ? 1 : 0),
								yCoord + (y >= size ? 1 : 0),
								zCoord + (z >= size ? 1 : 0));
				tile.setMetadata(
						x >= size ? x - size : x,
						y >= size ? y - size : y,
						z >= size ? z - size : z,
						metadata);
			}
			return;
		} else if (x < 0 | z < 0 | y < 0) {
			if (worldObj.getBlockId(
					xCoord - (x < 0 ? 1 : 0),
					yCoord - (y < 0 ? 1 : 0),
					zCoord - (z < 0 ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(
						xCoord - (x < 0 ? 1 : 0),
						yCoord - (y < 0 ? 1 : 0),
						zCoord - (z < 0 ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(
					xCoord - (x < 0 ? 1 : 0),
					yCoord - (y < 0 ? 1 : 0),
					zCoord - (z < 0 ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(
								xCoord - (x < 0 ? 1 : 0),
								yCoord - (y < 0 ? 1 : 0),
								zCoord - (z < 0 ? 1 : 0));
				tile.setMetadata(
						x < 0 ? x + size : x,
						y < 0 ? y + size : y,
						z < 0 ? z + size : z,
						metadata);
			}
			return;
		}
		int lastData = metadatas[x][y][z];
		metadatas[x][y][z] = metadata;
		int blockId = content[x][y][z];

		if (lastData != metadata) {
			((ILBCommonProxy) LBInit.LBM.getProxy()).getLittleWorld(
					this.worldObj,
					false).metadataModified(this.xCoord, this.yCoord, this.zCoord, 0, x, y, z, blockId, metadata);
		}
	}

	public void setContent(int x, int y, int z, int id, int metadata) {
		if (x >= size | y >= size | z >= size) {
			if (worldObj.getBlockId(
					xCoord + (x >= size ? 1 : 0),
					yCoord + (y >= size ? 1 : 0),
					zCoord + (z >= size ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(
						xCoord + (x >= size ? 1 : 0),
						yCoord + (y >= size ? 1 : 0),
						zCoord + (z >= size ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(
					xCoord + (x >= size ? 1 : 0),
					yCoord + (y >= size ? 1 : 0),
					zCoord + (z >= size ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(
								xCoord + (x >= size ? 1 : 0),
								yCoord + (y >= size ? 1 : 0),
								zCoord + (z >= size ? 1 : 0));
				tile.setContent(
						x >= size ? x - size : x,
						y >= size ? y - size : y,
						z >= size ? z - size : z,
						id,
						metadata);
			}
			return;
		} else if (x < 0 | z < 0 | y < 0) {
			if (worldObj.getBlockId(
					xCoord - (x < 0 ? 1 : 0),
					yCoord - (y < 0 ? 1 : 0),
					zCoord - (z < 0 ? 1 : 0)) == 0) {
				worldObj.setBlockWithNotify(
						xCoord - (x < 0 ? 1 : 0),
						yCoord - (y < 0 ? 1 : 0),
						zCoord - (z < 0 ? 1 : 0),
						LBCore.littleBlocksID);
			}
			if (worldObj.getBlockId(
					xCoord - (x < 0 ? 1 : 0),
					yCoord - (y < 0 ? 1 : 0),
					zCoord - (z < 0 ? 1 : 0)) == LBCore.littleBlocksID) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
						.getBlockTileEntity(
								xCoord - (x < 0 ? 1 : 0),
								yCoord - (y < 0 ? 1 : 0),
								zCoord - (z < 0 ? 1 : 0));
				tile.setContent(
						x < 0 ? x + size : x,
						y < 0 ? y + size : y,
						z < 0 ? z + size : z,
						id,
						metadata);
			}
			return;
		}
		int lastId = content[x][y][z];
		int lastData = metadatas[x][y][z];
		content[x][y][z] = id;
		metadatas[x][y][z] = metadata;

		if (lastData != metadata) {
			((ILBCommonProxy) LBInit.LBM.getProxy()).getLittleWorld(
					this.worldObj,
					false).metadataModified(this.xCoord, this.yCoord, this.zCoord, 0, x, y, z, id, metadata);
		}
		if (lastId != id) {
			if (!(Block.blocksList[lastId] instanceof BlockFlowing || Block.blocksList[lastId] instanceof BlockStationary)) {
				((ILBCommonProxy) LBInit.LBM.getProxy()).getLittleWorld(
						this.worldObj,
						false).idModified(lastId, this.xCoord, this.yCoord, this.zCoord, 0, x, y, z, id, metadata);
			}
		}
	}

	public void setContent(int[][][] content) {
		this.content = content;
	}

	public void setTileEntity(int x, int y, int z, TileEntity tile) {
		tile.setWorldObj(getLittleWorld());
		tile.xCoord = (xCoord << 3) + x;
		tile.yCoord = (yCoord << 3) + y;
		tile.zCoord = (zCoord << 3) + z;
		removeTileEntity(x, y, z);
		tile.validate();
		addTileEntity(tile);
	}

	public TileEntity getTileEntity(int x, int y, int z) {
		for (TileEntity tile : tiles) {
			if (tile.xCoord == (xCoord << 3) + x && tile.yCoord == (yCoord << 3) + y && tile.zCoord == (zCoord << 3) + z) {
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
			TileEntity tile = tiles.get(i);
			if (tile.worldObj == null || tile.worldObj != getLittleWorld()) {
				tile.worldObj = getLittleWorld();
			}
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
					content[x][y][z] = ((NBTTagInt) list
							.tagAt((x << 6) + (y << 3) + z)).data;
				}
			}
		}
		NBTTagList list2 = nbttagcompound.getTagList("Metadatas");
		for (int x = 0; x < metadatas.length; x++) {
			for (int y = 0; y < metadatas[x].length; y++) {
				for (int z = 0; z < metadatas[x][y].length; z++) {
					metadatas[x][y][z] = ((NBTTagInt) list2
							.tagAt((x << 6) + (y << 3) + z)).data;
				}
			}
		}

		tiles.clear();
		NBTTagList tilesTag = nbttagcompound.getTagList("Tiles");
		for (int i = 0; i < tilesTag.tagCount(); i++) {
			TileEntity tile = TileEntity
					.createAndLoadEntity((NBTTagCompound) tilesTag.tagAt(i));
			setTileEntity(
					tile.xCoord & 7,
					tile.yCoord & 7,
					tile.zCoord & 7,
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
	public void onInventoryChanged() {
		this.upToDate = false;
		super.onInventoryChanged();
	}

	@Override
	public Packet getDescriptionPacket() {
		return this.getUpdatePacket();
	}

	public Packet getUpdatePacket() {
		return getPacketUpdate().getPacket();
	}

	public PacketUpdate getPacketUpdate() {
		return new PacketTileEntityLB(this);
	}

	public PacketPayload getTileEntityPayload() {
		return getPayload();
	}

	public PacketPayload getPayload() {
		int MAX_SIZE = ((size * size * size) * 2) * 3;

		int[] data = new int[MAX_SIZE];
		int position = 0;
		int numberOfBlocks = 0;
		for (int x = 0; x < content.length; x++) {
			for (int y = 0; y < content[x].length; y++) {
				for (int z = 0; z < content[x][y].length; z++) {
					if (content[x][y][z] > 0) {
						data[position] = content[x][y][z];
						data[position + 1] = metadatas[x][y][z];
						data[position + 2] = x;
						data[position + 3] = y;
						data[position + 4] = z;
						position += 5;
						numberOfBlocks += 1;
					}
				}
			}
		}

		PacketPayload p = new PacketPayload(numberOfBlocks * 5 + 1, 0, 0, 0);
		p.setIntPayload(0, numberOfBlocks);
		for (int i = 0; i < (numberOfBlocks * 5); i += 5) {
			p.setIntPayload(i + 1, data[i]);
			p.setIntPayload(i + 2, data[i + 1]);
			p.setIntPayload(i + 3, data[i + 2]);
			p.setIntPayload(i + 4, data[i + 3]);
			p.setIntPayload(i + 5, data[i + 4]);
		}
		this.upToDate = true;
		return p;
	}

	public void setMetadata(int[][][] metadata) {
		this.metadatas = metadata;
	}
}
