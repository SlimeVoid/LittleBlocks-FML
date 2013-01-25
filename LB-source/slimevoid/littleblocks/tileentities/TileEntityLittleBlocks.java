package slimevoid.littleblocks.tileentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.api.ILittleBlocks;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LBInit;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLittleBlocks extends TileEntity implements ILittleBlocks {
	public int size = LBCore.littleBlocksSize;
	private int content[][][] = new int[size][size][size];
	private int metadatas[][][] = new int[size][size][size];
	private List<TileEntity> tiles = new ArrayList<TileEntity>();

	@Override
	public void setWorldObj(World par1World) {
		this.worldObj = par1World;
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

	public ILittleWorld getLittleWorld() {
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
					false).idModified(
					lastId,
					this.xCoord,
					this.yCoord,
					this.zCoord,
					0,
					x,
					y,
					z,
					id,
					0);
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
					false).metadataModified(
					this.xCoord,
					this.yCoord,
					this.zCoord,
					0,
					x,
					y,
					z,
					blockId,
					metadata);
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
					false).metadataModified(
					this.xCoord,
					this.yCoord,
					this.zCoord,
					0,
					x,
					y,
					z,
					id,
					metadata);
		}
		if (lastId != id) {
			((ILBCommonProxy) LBInit.LBM.getProxy()).getLittleWorld(
					this.worldObj,
					false).idModified(
					lastId,
					this.xCoord,
					this.yCoord,
					this.zCoord,
					0,
					x,
					y,
					z,
					id,
					metadata);
		}
	}

	public void setContent(int[][][] content) {
		this.content = content;
	}

	public void setTileEntity(int x, int y, int z, TileEntity tile) {
		tile.setWorldObj((World) getLittleWorld());
		tile.xCoord = (xCoord << 3) + x;
		tile.yCoord = (yCoord << 3) + y;
		tile.zCoord = (zCoord << 3) + z;
		Block block = Block.blocksList[this.getContent(x, y, z)];
		if (block != null && block.hasTileEntity(this.getMetadata(x, y, z))) {
			removeTileEntity(x, y, z);
			tile.validate();
			addTileEntity(tile);
		}
	}

	public TileEntity getTileEntity(int x, int y, int z) {
		for (TileEntity tile : tiles) {
			if (
					tile.xCoord == (this.xCoord << 3) + x && 
					tile.yCoord == (this.yCoord << 3) + y &&
					tile.zCoord == (this.zCoord << 3) + z	
				) {
				return tile;
			}
		}
		return null;
	}

	private void addTileEntity(TileEntity tile) {
		tiles.add(tile);
		World world = (World) this.getLittleWorld();
		if (world != null) {
			world.addTileEntity(tile);
		}
	}
	
	public void cleanTileEntity(int x, int y, int z) {
		TileEntity tileentity = this.getTileEntity(x, y, z);
		if (tileentity != null && tileentity.isInvalid()) {
			removeTileEntity(tileentity);
		}
	}

	public void removeTileEntity(int x, int y, int z) {
		TileEntity toRm = getTileEntity(x, y, z);

		if (toRm != null) {
			toRm.invalidate();
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
				tile.worldObj = (World) getLittleWorld();
			}
			tiles.get(i).updateEntity();
		}
		if (LBCore.littleBlocksForceUpdate) {
			for (int x = 0; x < content.length; x++) {
				for (int y = 0; y < content[x].length; y++) {
					for (int z = 0; z < content[x][y].length; z++) {
						if (content[x][y][z] != 0 && Block.blocksList[content[x][y][z]] == null) {
							content[x][y][z] = 0;
						}
					}
				}
			}
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

	public void clearContents() {
		this.content = new int[LBCore.littleBlocksSize][LBCore.littleBlocksSize][LBCore.littleBlocksSize];
		this.metadatas = new int[LBCore.littleBlocksSize][LBCore.littleBlocksSize][LBCore.littleBlocksSize];
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		this.readFromNBT(pkt.customParam1);
		this.onInventoryChanged();
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		Packet packet = new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 0, nbttagcompound);
		return packet;
	}

	public void setMetadata(int[][][] metadata) {
		this.metadatas = metadata;
	}

	public int[][][] getMetadata() {
		return this.metadatas;
	}
	
	public List<NBTTagCompound> getTiles() {
		List<NBTTagCompound> tileList = new ArrayList<NBTTagCompound>();
		for (TileEntity tile : tiles) {
			NBTTagCompound tileTag = new NBTTagCompound();
			tile.writeToNBT(tileTag);
			tileList.add(tileTag);
		}
		return tileList;
	}

	@SideOnly(Side.CLIENT)
	public void setTiles(List<NBTTagCompound> tileentities) {
		for (int i = 0; i < tileentities.size(); i++) {
			TileEntity tile = TileEntity
					.createAndLoadEntity(tileentities.get(i));
			this.setTileEntity(
					tile.xCoord & 7,
					tile.yCoord & 7,
					tile.zCoord & 7,
					tile);
		}
	}

	@SideOnly(Side.CLIENT)
	public void handleBlockAdded(World world, EntityPlayer entityplayer,
			PacketLittleBlocks packetLB) {
		int id = packetLB.getBlockID(),
			meta = packetLB.getMetadata(),
			x = packetLB.xPosition,
			y = packetLB.yPosition,
			z = packetLB.zPosition;
		//System.out.println("X| " + x + " Y| " + y + " Z| " + z + " ID| " + id + " META| " + meta);
		this.setContent(
				x & 7,
				y & 7,
				z & 7,
				id,
				meta);
		Block.blocksList[packetLB.getBlockID()].onBlockAdded(
				(World) this.getLittleWorld(),
				x,
				y,
				z);
		this.onInventoryChanged();
	}

	@SideOnly(Side.CLIENT)
	public void handleBreakBlock(World world, EntityPlayer entityplayer,
			PacketLittleBlocks packetLB) {
		this.setContent(
				packetLB.xPosition & 7,
				packetLB.yPosition & 7,
				packetLB.zPosition & 7,
				0);
		Block.blocksList[packetLB.getBlockID()].breakBlock(
				(World) this.getLittleWorld(),
				packetLB.xPosition,
				packetLB.yPosition,
				packetLB.zPosition,
				packetLB.side,
				packetLB.getMetadata());
		this.onInventoryChanged();
	}

	@SideOnly(Side.CLIENT)
	public void handleUpdateMetadata(World world, EntityPlayer entityplayer,
			PacketLittleBlocks packetLB) {
		int id = packetLB.getBlockID(),
			meta = packetLB.getMetadata(),
			x = packetLB.xPosition,
			y = packetLB.yPosition,
			z = packetLB.zPosition;
		//System.out.println("X| " + x + " Y| " + y + " Z| " + z + " ID| " + id + " META| " + meta);
		this.setMetadata(
				x & 7,
				y & 7,
				z & 7,
				meta);
		Block.blocksList[id].onSetBlockIDWithMetaData((World) this.getLittleWorld(), x, y, z, meta);
		this.onInventoryChanged();
	}

	@SideOnly(Side.CLIENT)
	public void handleLittleTilePacket(World world, PacketLittleBlocks packetLB) {
		int x = packetLB.xPosition,
			y =	packetLB.yPosition,
			z =	packetLB.zPosition;
		TileEntity littleTile = TileEntity.createAndLoadEntity(
				packetLB.getTileEntityData());
		if (littleTile != null) {
			this.setTileEntity(
				x & 7,
				y & 7,
				z & 7,
				littleTile);
		}
		this.onInventoryChanged();
	}
}
