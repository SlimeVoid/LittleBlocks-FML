package littleblocks.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import littleblocks.core.LBInit;
import littleblocks.tileentities.TileEntityLittleBlocks;
import eurysmods.network.packets.core.PacketTileEntity;

public class PacketTileEntityLB extends PacketTileEntity {

	private int sender;

	public PacketTileEntityLB() {
		super();
		this.setChannel(LBInit.LBM.getModChannel());
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		data.writeInt(this.sender);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		this.sender = data.readInt();
	}

	public PacketTileEntityLB(TileEntityLittleBlocks tileentity) {
		this();
		this.setPosition(
				tileentity.xCoord,
				tileentity.yCoord,
				tileentity.zCoord,
				0);
		this.payload = tileentity.getTileEntityPayload();
	}

	public int getSender() {
		return this.sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}
}
