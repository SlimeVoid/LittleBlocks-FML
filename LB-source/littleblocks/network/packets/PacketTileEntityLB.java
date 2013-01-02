package littleblocks.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import littleblocks.core.LBInit;
import littleblocks.tileentities.TileEntityLittleBlocks;
import eurysmods.network.packets.core.PacketTileEntity;

public class PacketTileEntityLB extends PacketTileEntity {

	private int sender;
	private byte[] tileEntities;

	public PacketTileEntityLB() {
		super();
		this.setChannel(LBInit.LBM.getModChannel());
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeInt(this.sender);
		this.writeTileEntityBytes(data);
	}

	private void writeTileEntityBytes(DataOutputStream data) {
		try {
			data.write(tileEntities);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		this.sender = data.readInt();
		this.readTileEntityBytes(data);
	}

	private void readTileEntityBytes(DataInputStream data) {
		try {
			data.read(tileEntities);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public byte[] getTileEntities() {
		return this.tileEntities;
	}
	
	public void setTileEntities(byte[] tileentities) {
		this.tileEntities = tileentities;
	}
}
