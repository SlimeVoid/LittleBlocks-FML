package slimevoid.littleblocks.network.packets;

import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.CoreLib;
import slimevoidlib.network.PacketNotifyPlayer;

public class PacketLittleNotify extends PacketNotifyPlayer {

	public PacketLittleNotify() {
		super();
		this.setChannel(CoreLib.MOD_CHANNEL);
	}

	public PacketLittleNotify(String command) {
		this();
		this.setCommand(command);
	}

	@Override
	public boolean targetExists(World world) {
		return false;
	}

}
