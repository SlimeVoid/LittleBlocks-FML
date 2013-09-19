package slimevoid.littleblocks.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.CoreLib;
import slimevoidlib.network.PacketIds;
import slimevoidlib.network.PacketPayload;
import slimevoidlib.network.PacketUpdate;

public class PacketLittleBlocks extends PacketUpdate {

	// private int sender;
	private NBTTagCompound	tileEntityData;

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		// data.writeInt(sender);
		writeTileEntityData(data);
	}

	private void writeTileEntityData(DataOutputStream data) throws IOException {
		writeNBTTagCompound(this.tileEntityData,
							data);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		// sender = data.readInt();
		readTileEntityData(data);
	}

	private void readTileEntityData(DataInputStream data) throws IOException {
		this.tileEntityData = readNBTTagCompound(data);
	}

	public PacketLittleBlocks() {
		super(PacketIds.UPDATE);
		this.setChannel(CoreLib.MOD_CHANNEL);
	}

	public PacketLittleBlocks(String command, int x, int y, int z, int side, float vecX, float vecY, float vecZ, int selectedX, int selectedY, int selectedZ, int blockId, int metadata) {
		this();
		this.setPosition(	x,
							y,
							z,
							side);
		this.setHitVectors(	vecX,
							vecY,
							vecZ);
		this.payload = new PacketPayload(5, 0, 0, 0);
		this.setCommand(command);
		this.setBlockId(blockId);
		this.setMetadata(metadata);
		this.setSelectedXYZ(selectedX,
							selectedY,
							selectedZ);
	}

	public PacketLittleBlocks(String command, int x, int y, int z, int side, int blockId, int metadata) {
		this();
		this.setPosition(	x,
							y,
							z,
							side);
		this.payload = new PacketPayload(2, 0, 0, 1);
		this.setCommand(command);
		this.setBlockId(blockId);
		this.setMetadata(metadata);
		this.payload.setBoolPayload(0,
									false);
	}

	public void setSelectedXYZ(int selectedX, int selectedY, int selectedZ) {
		this.payload.setIntPayload(	2,
									selectedX);
		this.payload.setIntPayload(	3,
									selectedY);
		this.payload.setIntPayload(	4,
									selectedZ);
	}

	@Override
	public boolean targetExists(World world) {
		if (world.blockExists(	this.xPosition,
								this.yPosition,
								this.zPosition)) {
			return true;
		}
		return false;
	}

	public void setBlockId(int blockId) {
		this.payload.setIntPayload(	0,
									blockId);
	}

	public void setMetadata(int metadata) {
		this.payload.setIntPayload(	1,
									metadata);
	}

	public int getBlockID() {
		return this.payload.getIntPayload(0);
	}

	public int getMetadata() {
		return this.payload.getIntPayload(1);
	}

	public int getSelectedX() {
		return this.payload.getIntPayload(2);
	}

	public int getSelectedY() {
		return this.payload.getIntPayload(3);
	}

	public int getSelectedZ() {
		return this.payload.getIntPayload(4);
	}

	/*
	 * public int getSender() { return this.sender; } public void setSender(int
	 * sender) { this.sender = sender; }
	 */

	public void setTileEntityData(TileEntity tileData) {
		NBTTagCompound tileTag = new NBTTagCompound();
		tileData.writeToNBT(tileTag);
		this.tileEntityData = tileTag;
		this.payload.setBoolPayload(0,
									true);
	}

	public NBTTagCompound getTileEntityData() {
		return this.tileEntityData;
	}

	public boolean hasTileEntity() {
		return this.payload.getBoolPayload(0);
	}
}
