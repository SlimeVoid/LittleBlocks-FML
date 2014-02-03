package slimevoid.littleblocks.network.packets;

import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.CoreLib;
import slimevoidlib.network.PacketIds;
import slimevoidlib.network.PacketPayload;
import slimevoidlib.network.PacketUpdate;

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
