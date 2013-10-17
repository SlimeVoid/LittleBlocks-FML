package slimevoid.littleblocks.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
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

	public PacketLittleBlocks(int x, int y, int z, World world) {
		this();
		this.setPosition(	x,
							y,
							z,
							world.getBlockId(x, y, z));
		this.payload = new PacketPayload(1, 0, 0, 1);
		this.setCommand(CommandLib.UPDATE_CLIENT);
		this.setMetadata(world.getBlockMetadata(x, y, z));
		this.payload.setBoolPayload(0,
									false);
	}

	@Override
	public boolean targetExists(World world) {
		if (world.getBlockId(	this.xPosition >> 3,
								this.yPosition >> 3,
								this.zPosition >> 3) == ConfigurationLib.littleChunkID) {
			return true;
		}
		return false;
	}

	public void setMetadata(int metadata) {
		this.payload.setIntPayload(	0,
									metadata);
	}

	public int getBlockID() {
		return this.side;
	}

	public int getMetadata() {
		return this.payload.getIntPayload(0);
	}

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
