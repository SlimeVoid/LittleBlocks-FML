package littleblocks.network.packets;

import littleblocks.core.LBInit;
import net.minecraft.src.World;
import eurysmods.network.packets.core.PacketIds;
import eurysmods.network.packets.core.PacketPayload;
import eurysmods.network.packets.core.PacketUpdate;

public class PacketLittleBlocksSettings extends PacketUpdate {

	public PacketLittleBlocksSettings() {
		super(PacketIds.LOGIN);
		this.setChannel(LBInit.LBM.getModChannel());
		this.payload = new PacketPayload(1, 0, 0, 1);
	}

	@Override
	public boolean targetExists(World world) {
		return false;
	}

	public void setClipMode(boolean littleBlocksClip) {
		this.payload.setBoolPayload(0, littleBlocksClip);
	}

	public boolean getClipMode() {
		return this.payload.getBoolPayload(0);
	}

	public void setCommand(int command) {
		this.payload.setIntPayload(0, command);
	}

	public int getCommand() {
		return this.payload.getIntPayload(0);
	}

}
