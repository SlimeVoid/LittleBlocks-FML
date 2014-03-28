package net.slimevoid.littleblocks.network.packets;

import net.slimevoid.library.network.PacketNotifyPlayer;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.minecraft.world.World;

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
