package slimevoid.littleblocks.network.packets;

import net.minecraft.world.World;
import slimevoid.lib.network.PacketIds;
import slimevoid.lib.network.PacketPayload;
import slimevoid.lib.network.PacketUpdate;
import slimevoid.littleblocks.core.LBInit;

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

}
