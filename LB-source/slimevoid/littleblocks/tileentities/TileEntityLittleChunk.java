package slimevoid.littleblocks.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import net.minecraft.world.World;
import slimevoid.littleblocks.api.ILittleBlocks;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLittleChunk extends TileEntity implements ILittleBlocks {
	public int					size			= ConfigurationLib.littleBlocksSize;
	private int					content[][][]	= new int[size][size][size];
	private int					metadatas[][][]	= new int[size][size][size];
	private List<TileEntity>	tiles			= new ArrayList<TileEntity>();

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
						lightcount[Block.blocksList[this.content[x][y][z]].getLightValue(	this.getLittleWorld(),
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
		return LittleBlocks.proxy.getLittleWorld(	this.worldObj,
													false);
	}

	public int getBlockMetadata(int x, int y, int z) {
		if (x >= size | y >= size | z >= size) {
			if (this.worldObj.getBlockId(	xCoord + (x >= size ? 1 : 0),
											yCoord + (y >= size ? 1 : 0),
											zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(	xCoord
																												+ (x >= size ? 1 : 0),
																										yCoord
																												+ (y >= size ? 1 : 0),
																										zCoord
																												+ (z >= size ? 1 : 0));
				return tile.getBlockMetadata(	x >= size ? x - size : x,
												y >= size ? y - size : y,
												z >= size ? z - size : z);
			}
			if (this.worldObj.getBlockId(	xCoord + (x >= size ? 1 : 0),
											yCoord + (y >= size ? 1 : 0),
											zCoord + (z >= size ? 1 : 0)) == 0) {
				return 0;
			}
			return -1;
		} else if (x < 0 | z < 0 | y < 0) {
			if (this.worldObj.getBlockId(	xCoord - (x < 0 ? 1 : 0),
											yCoord - (y < 0 ? 1 : 0),
											zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(	xCoord
																												- (x < 0 ? 1 : 0),
																										yCoord
																												- (y < 0 ? 1 : 0),
																										zCoord
																												- (z < 0 ? 1 : 0));
				return tile.getBlockMetadata(	x < 0 ? x + size : x,
												y < 0 ? y + size : y,
												z < 0 ? z + size : z);
			}
			if (this.worldObj.getBlockId(	xCoord - (x < 0 ? 1 : 0),
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
			if (this.worldObj.getBlockId(	xCoord + (x >= size ? 1 : 0),
											yCoord + (y >= size ? 1 : 0),
											zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(	xCoord
																												+ (x >= size ? 1 : 0),
																										yCoord
																												+ (y >= size ? 1 : 0),
																										zCoord
																												+ (z >= size ? 1 : 0));
				return tile.getBlockID(	x >= size ? x - size : x,
										y >= size ? y - size : y,
										z >= size ? z - size : z);
			}
			if (this.worldObj.getBlockId(	xCoord + (x >= size ? 1 : 0),
											yCoord + (y >= size ? 1 : 0),
											zCoord + (z >= size ? 1 : 0)) == 0) {
				return 0;
			}
			return -1;
		} else if (x < 0 | z < 0 | y < 0) {
			if (this.worldObj.getBlockId(	xCoord - (x < 0 ? 1 : 0),
											yCoord - (y < 0 ? 1 : 0),
											zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(	xCoord
																												- (x < 0 ? 1 : 0),
																										yCoord
																												- (y < 0 ? 1 : 0),
																										zCoord
																												- (z < 0 ? 1 : 0));
				return tile.getBlockID(	x < 0 ? x + size : x,
										y < 0 ? y + size : y,
										z < 0 ? z + size : z);
			}
			if (this.worldObj.getBlockId(	xCoord - (x < 0 ? 1 : 0),
											yCoord - (y < 0 ? 1 : 0),
											zCoord - (z < 0 ? 1 : 0)) == 0) {
				return 0;
			}
			return -1;
		} else {
			return this.content[x][y][z];
		}
	}

	public void updateTick(Random rand) {
		for (int xx = 0; xx < ConfigurationLib.littleBlocksSize; xx++) {
			for (int yy = 0; yy < ConfigurationLib.littleBlocksSize; yy++) {
				for (int zz = 0; zz < ConfigurationLib.littleBlocksSize; zz++) {
					Block littleBlock = Block.blocksList[this.content[xx][yy][zz]];
					if (littleBlock != null && littleBlock.getTickRandomly()) {
						int x = (this.xCoord << 3) + xx, y = (this.yCoord << 3)
																+ yy, z = (this.zCoord << 3)
																			+ zz;
						littleBlock.updateTick(	(World) this.getLittleWorld(),
												x,
												y,
												z,
												rand);
					}
				}
			}
		}
	}

	public void setBlockID(int x, int y, int z, int id) {
		/*
		 * if (x >= size | y >= size | z >= size) { if
		 * (this.worldObj.getBlockId( xCoord + (x >= size ? 1 : 0), yCoord + (y
		 * >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0)) == 0) {
		 * this.worldObj.setBlockWithNotify( xCoord + (x >= size ? 1 : 0),
		 * yCoord + (y >= size ? 1 : 0), zCoord + (z >= size ? 1 : 0),
		 * LBCore.littleBlocksID); } if (this.worldObj.getBlockId( xCoord + (x
		 * >= size ? 1 : 0), yCoord + (y >= size ? 1 : 0), zCoord + (z >= size ?
		 * 1 : 0)) == LBCore.littleBlocksID) { TileEntityLittleBlocks tile =
		 * (TileEntityLittleBlocks) worldObj .getBlockTileEntity( xCoord + (x >=
		 * size ? 1 : 0), yCoord + (y >= size ? 1 : 0), zCoord + (z >= size ? 1
		 * : 0)); tile.setBlockID( x >= size ? x - size : x, y >= size ? y -
		 * size : y, z >= size ? z - size : z, id); } return; } else if (x < 0 |
		 * z < 0 | y < 0) { if (this.worldObj.getBlockId( xCoord - (x < 0 ? 1 :
		 * 0), yCoord - (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == 0) {
		 * this.worldObj.setBlockWithNotify( xCoord - (x < 0 ? 1 : 0), yCoord -
		 * (y < 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0), LBCore.littleBlocksID); }
		 * if (this.worldObj.getBlockId( xCoord - (x < 0 ? 1 : 0), yCoord - (y <
		 * 0 ? 1 : 0), zCoord - (z < 0 ? 1 : 0)) == LBCore.littleBlocksID) {
		 * TileEntityLittleBlocks tile = (TileEntityLittleBlocks) worldObj
		 * .getBlockTileEntity( xCoord - (x < 0 ? 1 : 0), yCoord - (y < 0 ? 1 :
		 * 0), zCoord - (z < 0 ? 1 : 0)); tile.setBlockID( x < 0 ? x + size : x,
		 * y < 0 ? y + size : y, z < 0 ? z + size : z, id); } return; } int
		 * lastId = this.content[x][y][z]; content[x][y][z] = id;
		 * setBlockMetadata(x, y, z, 0); if (lastId != id) {
		 * LittleBlocks.proxy.getLittleWorld( this.worldObj, false).idModified(
		 * lastId, xCoord, this.yCoord, this.zCoord, 0, x, y, z, id, 0); }
		 */
		this.setBlockIDWithMetadata(x,
									y,
									z,
									id,
									0);
	}

	public void setBlockMetadata(int x, int y, int z, int metadata) {
		if (x >= size | y >= size | z >= size) {
			if (this.worldObj.getBlockId(	xCoord + (x >= size ? 1 : 0),
											yCoord + (y >= size ? 1 : 0),
											zCoord + (z >= size ? 1 : 0)) == 0) {
				this.worldObj.setBlock(	xCoord + (x >= size ? 1 : 0),
										yCoord + (y >= size ? 1 : 0),
										zCoord + (z >= size ? 1 : 0),
										ConfigurationLib.littleChunkID,
										0,
										0x02);
			}
			if (this.worldObj.getBlockId(	xCoord + (x >= size ? 1 : 0),
											yCoord + (y >= size ? 1 : 0),
											zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(	xCoord
																												+ (x >= size ? 1 : 0),
																										yCoord
																												+ (y >= size ? 1 : 0),
																										zCoord
																												+ (z >= size ? 1 : 0));
				tile.setBlockMetadata(	x >= size ? x - size : x,
										y >= size ? y - size : y,
										z >= size ? z - size : z,
										metadata);
			}
			return;
		} else if (x < 0 | z < 0 | y < 0) {
			if (this.worldObj.getBlockId(	xCoord - (x < 0 ? 1 : 0),
											yCoord - (y < 0 ? 1 : 0),
											zCoord - (z < 0 ? 1 : 0)) == 0) {
				this.worldObj.setBlock(	xCoord - (x < 0 ? 1 : 0),
										yCoord - (y < 0 ? 1 : 0),
										zCoord - (z < 0 ? 1 : 0),
										ConfigurationLib.littleChunkID,
										0,
										0x02);
			}
			if (this.worldObj.getBlockId(	xCoord - (x < 0 ? 1 : 0),
											yCoord - (y < 0 ? 1 : 0),
											zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(	xCoord
																												- (x < 0 ? 1 : 0),
																										yCoord
																												- (y < 0 ? 1 : 0),
																										zCoord
																												- (z < 0 ? 1 : 0));
				tile.setBlockMetadata(	x < 0 ? x + size : x,
										y < 0 ? y + size : y,
										z < 0 ? z + size : z,
										metadata);
			}
			return;
		}
		int lastData = metadatas[x][y][z];
		this.metadatas[x][y][z] = metadata;
		int blockId = this.content[x][y][z];

		if (lastData != metadata) {
			this.getLittleWorld().metadataModified(	xCoord,
													yCoord,
													zCoord,
													0,
													x,
													y,
													z,
													blockId,
													metadata);
		}
	}

	public void setBlockIDWithMetadata(int x, int y, int z, int id, int metadata) {
		if (x >= size | y >= size | z >= size) {
			if (this.worldObj.getBlockId(	xCoord + (x >= size ? 1 : 0),
											yCoord + (y >= size ? 1 : 0),
											zCoord + (z >= size ? 1 : 0)) == 0) {
				this.worldObj.setBlock(	xCoord + (x >= size ? 1 : 0),
										yCoord + (y >= size ? 1 : 0),
										zCoord + (z >= size ? 1 : 0),
										ConfigurationLib.littleChunkID,
										0,
										0x02);
			}
			if (this.worldObj.getBlockId(	xCoord + (x >= size ? 1 : 0),
											yCoord + (y >= size ? 1 : 0),
											zCoord + (z >= size ? 1 : 0)) == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(	xCoord
																												+ (x >= size ? 1 : 0),
																										yCoord
																												+ (y >= size ? 1 : 0),
																										zCoord
																												+ (z >= size ? 1 : 0));
				tile.setBlockIDWithMetadata(x >= size ? x - size : x,
											y >= size ? y - size : y,
											z >= size ? z - size : z,
											id,
											metadata);
			}
			return;
		} else if (x < 0 | z < 0 | y < 0) {
			if (this.worldObj.getBlockId(	xCoord - (x < 0 ? 1 : 0),
											yCoord - (y < 0 ? 1 : 0),
											zCoord - (z < 0 ? 1 : 0)) == 0) {
				this.worldObj.setBlock(	xCoord - (x < 0 ? 1 : 0),
										yCoord - (y < 0 ? 1 : 0),
										zCoord - (z < 0 ? 1 : 0),
										ConfigurationLib.littleChunkID,
										0,
										0x02);
			}
			if (this.worldObj.getBlockId(	xCoord - (x < 0 ? 1 : 0),
											yCoord - (y < 0 ? 1 : 0),
											zCoord - (z < 0 ? 1 : 0)) == ConfigurationLib.littleChunkID) {
				TileEntityLittleChunk tile = (TileEntityLittleChunk) this.worldObj.getBlockTileEntity(	xCoord
																												- (x < 0 ? 1 : 0),
																										yCoord
																												- (y < 0 ? 1 : 0),
																										zCoord
																												- (z < 0 ? 1 : 0));
				tile.setBlockIDWithMetadata(x < 0 ? x + size : x,
											y < 0 ? y + size : y,
											z < 0 ? z + size : z,
											id,
											metadata);
			}
			return;
		}
		int lastId = this.content[x][y][z];
		int lastData = this.metadatas[x][y][z];
		this.content[x][y][z] = id;
		this.metadatas[x][y][z] = metadata;
		if (lastId != id) {
			this.getLittleWorld().idModified(	lastId,
												xCoord,
												yCoord,
												zCoord,
												0,
												x,
												y,
												z,
												id,
												metadata);
		}

		if (lastData != metadata) {
			this.getLittleWorld().metadataModified(	xCoord,
													yCoord,
													zCoord,
													0,
													x,
													y,
													z,
													id,
													metadata);
		}
	}

	public void setBlockIDs(int[][][] content) {
		this.content = content;
	}

	private void setTileEntityWorldObjs() {
		for (TileEntity tile : this.tiles) {
			tile.setWorldObj((World) this.getLittleWorld());
		}
	}

	public void setTileEntity(int x, int y, int z, TileEntity tile) {
		tile.setWorldObj((World) this.getLittleWorld());
		tile.xCoord = (xCoord << 3) + x;
		tile.yCoord = (yCoord << 3) + y;
		tile.zCoord = (zCoord << 3) + z;
		Block block = Block.blocksList[this.getBlockID(	x,
														y,
														z)];
		if (block != null && block.hasTileEntity(this.getBlockMetadata(	x,
																		y,
																		z))) {
			removeTileEntity(	x,
								y,
								z);
			tile.validate();
			addTileEntity(tile);
		}
	}

	public TileEntity getTileEntityFromList(int x, int y, int z) {
		for (TileEntity tile : this.tiles) {
			if (tile.xCoord == (xCoord << 3) + x
				&& tile.yCoord == (yCoord << 3) + y
				&& tile.zCoord == (zCoord << 3) + z) {
				return tile;
			}
		}
		return null;
	}

	@Override
	public void onChunkUnload() {
		/*
		 * Iterator<TileEntity> tilelist = this.tiles.iterator(); while
		 * (tilelist.hasNext()) { TileEntity tileentity = (TileEntity)
		 * tilelist.next(); ((World)
		 * this.getLittleWorld()).markTileEntityForDespawn(tileentity); }
		 */
	}

	public TileEntity getTileEntity(int x, int y, int z) {
		TileEntity tileentity = this.getTileEntityFromList(	x,
															y,
															z);
		if (tileentity == null) {
			int id = this.getBlockID(	x,
										y,
										z);
			int meta = this.getBlockMetadata(	x,
												y,
												z);
			if (id <= 0 || !Block.blocksList[id].hasTileEntity(meta)) {
				return null;
			}
			Block littleBlock = Block.blocksList[id];
			tileentity = littleBlock.createTileEntity(	this.worldObj,
														meta);
			this.getLittleWorld().setBlockTileEntity(	(xCoord << 3) + x,
														(yCoord << 3) + y,
														(zCoord << 3) + z,
														tileentity);
			tileentity = this.getTileEntityFromList(x,
													y,
													z);
		}
		return tileentity;
	}

	private void addTileEntity(TileEntity tile) {
		this.tiles.add(tile);
		ILittleWorld littleWorld = this.getLittleWorld();
		if (littleWorld != null) {
			littleWorld.addTileEntity(tile);
		}
	}

	public void cleanTileEntity(int x, int y, int z) {
		TileEntity tileentity = this.getTileEntityFromList(	x,
															y,
															z);
		if (tileentity != null && tileentity.isInvalid()) {
			removeTileEntity(tileentity);
		}
	}

	public void removeTileEntity(int x, int y, int z) {
		TileEntity toRm = getTileEntityFromList(x,
												y,
												z);

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
		if (ConfigurationLib.littleBlocksForceUpdate) {
			for (int x = 0; x < this.content.length; x++) {
				for (int y = 0; y < this.content[x].length; y++) {
					for (int z = 0; z < this.content[x][y].length; z++) {
						if (this.content[x][y][z] != 0
							&& Block.blocksList[content[x][y][z]] == null) {
							this.content[x][y][z] = 0;
							this.removeTileEntity(	x,
													y,
													z);
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

		this.tiles.clear();
		NBTTagList tilesTag = nbttagcompound.getTagList("Tiles");
		for (int i = 0; i < tilesTag.tagCount(); i++) {
			TileEntity tile = TileEntity.createAndLoadEntity((NBTTagCompound) tilesTag.tagAt(i));
			setTileEntity(	tile.xCoord & 7,
							tile.yCoord & 7,
							tile.zCoord & 7,
							tile);
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
		nbttagcompound.setTag(	"Content",
								list);

		NBTTagList list2 = new NBTTagList();
		for (int x = 0; x < this.metadatas.length; x++) {
			for (int y = 0; y < this.metadatas[x].length; y++) {
				for (int z = 0; z < this.metadatas[x][y].length; z++) {
					list2.appendTag(new NBTTagInt(null, this.metadatas[x][y][z]));
				}
			}
		}
		nbttagcompound.setTag(	"Metadatas",
								list2);

		NBTTagList tilesTag = new NBTTagList();
		for (TileEntity tile : this.tiles) {
			NBTTagCompound tileTag = new NBTTagCompound();
			tile.writeToNBT(tileTag);
			tilesTag.appendTag(tileTag);
		}
		nbttagcompound.setTag(	"Tiles",
								tilesTag);
	}

	public void clearContents() {
		this.content = new int[ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize];
		this.metadatas = new int[ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize][ConfigurationLib.littleBlocksSize];
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
		Packet packet = new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbttagcompound);
		return packet;
	}

	public void setMetadatas(int[][][] metadata) {
		this.metadatas = metadata;
	}

	public int[][][] getMetadatas() {
		return this.metadatas;
	}

	public List<NBTTagCompound> getTiles() {
		List<NBTTagCompound> tileList = new ArrayList<NBTTagCompound>();
		for (TileEntity tile : this.tiles) {
			NBTTagCompound tileTag = new NBTTagCompound();
			tile.writeToNBT(tileTag);
			tileList.add(tileTag);
		}
		return tileList;
	}

	@SideOnly(Side.CLIENT)
	public void setTiles(List<NBTTagCompound> tileentities) {
		for (int i = 0; i < tileentities.size(); i++) {
			TileEntity tile = TileEntity.createAndLoadEntity(tileentities.get(i));
			this.setTileEntity(	tile.xCoord & 7,
								tile.yCoord & 7,
								tile.zCoord & 7,
								tile);
		}
	}

	@SideOnly(Side.CLIENT)
	public void handleBlockAdded(World world, EntityPlayer entityplayer, PacketLittleBlocks packetLB) {
		int id = packetLB.getBlockID(), meta = packetLB.getMetadata(), x = packetLB.xPosition, y = packetLB.yPosition, z = packetLB.zPosition;
		// System.out.println("X| " + x + " Y| " + y + " Z| " + z + " ID| " + id
		// + " META| " + meta);
		this.setBlockIDWithMetadata(x & 7,
									y & 7,
									z & 7,
									id,
									meta);
		Block.blocksList[id].onBlockAdded(	(World) this.getLittleWorld(),
											x,
											y,
											z);
		// this.onInventoryChanged();
	}

	@SideOnly(Side.CLIENT)
	public void handleBreakBlock(World world, EntityPlayer entityplayer, PacketLittleBlocks packetLB) {
		this.setBlockID(packetLB.xPosition & 7,
						packetLB.yPosition & 7,
						packetLB.zPosition & 7,
						0);
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
		this.setBlockMetadata(	x & 7,
								y & 7,
								z & 7,
								meta);
		// this.onInventoryChanged();
	}

	@SideOnly(Side.CLIENT)
	public void handleLittleTilePacket(World world, PacketLittleBlocks packetLB) {
		int x = packetLB.xPosition, y = packetLB.yPosition, z = packetLB.zPosition;
		TileEntity littleTile = TileEntity.createAndLoadEntity(packetLB.getTileEntityData());
		if (littleTile != null) {
			this.setTileEntity(	x & 7,
								y & 7,
								z & 7,
								littleTile);
		}
		// this.onInventoryChanged();
	}
}