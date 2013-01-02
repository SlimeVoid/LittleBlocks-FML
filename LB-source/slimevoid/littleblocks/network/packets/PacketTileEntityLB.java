package slimevoid.littleblocks.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import slimevoid.littleblocks.core.LBInit;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;

import net.minecraft.nbt.NBTTagCompound;

import eurysmods.network.packets.core.PacketTileEntity;

public class PacketTileEntityLB extends PacketTileEntity {

	private int sender;
	private List<NBTTagCompound> tileEntities = new ArrayList<NBTTagCompound>();

	public PacketTileEntityLB() {
		super();
		this.setChannel(LBInit.LBM.getModChannel());
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeInt(this.sender);
		this.writeTileEntities(data);
	}

	private void writeTileEntities(DataOutputStream data) throws IOException {
		data.writeInt(tileEntities.size());
		for (int i = 0; i < tileEntities.size(); i++) {
			writeNBTTagCompound(tileEntities.get(i), data);
		}
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		this.sender = data.readInt();
		this.readTileEntities(data);
	}

	private void readTileEntities(DataInputStream data) throws IOException {
		this.tileEntities.clear();
		int numberOfTiles = data.readInt();
		for (int i = 0; i < numberOfTiles; i++) {
			this.tileEntities.add(readNBTTagCompound(data));
		}
	}

	public PacketTileEntityLB(TileEntityLittleBlocks tileentity) {
		this();
		this.setPosition(
				tileentity.xCoord,
				tileentity.yCoord,
				tileentity.zCoord,
				0);
		this.payload = tileentity.getTileEntityPayload();
		this.setTileEntities(tileentity.getTiles());
	}

	public int getSender() {
		return this.sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}
	
	public List<NBTTagCompound> getTileEntities() {
		return this.tileEntities;
	}
	
	public void setTileEntities(List<NBTTagCompound> tileentities) {
		this.tileEntities = tileentities;
	}
}
