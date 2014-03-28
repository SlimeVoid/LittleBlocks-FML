package net.slimevoid.littleblocks.network.packets;

import net.minecraft.world.World;
import net.slimevoid.library.network.PacketIds;
import net.slimevoid.library.network.PacketPayload;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.littleblocks.core.lib.CoreLib;

public class PacketLittleBlocksSettings extends PacketUpdate {

    public PacketLittleBlocksSettings() {
        super(PacketIds.LOGIN);
        this.setChannel(CoreLib.MOD_CHANNEL);
        this.payload = new PacketPayload(1, 0, 0, 1);
    }

    @Override
    public boolean targetExists(World world) {
        return false;
    }

    public void setClipMode(boolean littleBlocksClip) {
        this.payload.setBoolPayload(0,
                                    littleBlocksClip);
    }

    public boolean getClipMode() {
        return this.payload.getBoolPayload(0);
    }

}
