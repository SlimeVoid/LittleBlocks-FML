package slimevoid.littleblocks.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.CoreLib;
import slimevoidlib.network.PacketIds;
import slimevoidlib.network.PacketUpdate;

public class PacketLittleBlocksPlace extends PacketUpdate {

	private ItemStack	itemStack;

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		super.writeData(data);
		Packet.writeItemStack(	itemStack,
								data);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		super.readData(data);
		this.itemStack = Packet.readItemStack(data);
	}

	public PacketLittleBlocksPlace() {
		super(PacketIds.UPDATE);
		this.setChannel(CoreLib.MOD_CHANNEL);
	}

	public PacketLittleBlocksPlace(String command, int x, int y, int z, int direction, ItemStack itemStack, float xOff, float yOff, float zOff) {
		this();
		this.setPosition(	x,
							y,
							z,
							direction);
		this.setHitVectors(	xOff,
							yOff,
							zOff);
		this.itemStack = itemStack != null ? itemStack.copy() : null;
		this.setCommand(CommandLib.BLOCK_ACTIVATED);
	}

	public int getDirection() {
		return this.side;
	}

	public ItemStack getItemStack() {
		return this.itemStack;
	}

	@Override
	public boolean targetExists(World world) {
		return false;
	}
}
