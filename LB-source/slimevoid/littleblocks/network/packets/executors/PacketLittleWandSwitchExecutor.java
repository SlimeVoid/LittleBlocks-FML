package slimevoid.littleblocks.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.items.wand.EnumWandAction;
import slimevoid.littleblocks.network.packets.PacketLittleNotify;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;

public class PacketLittleWandSwitchExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleNotify
            && packet.getCommand().equals(CommandLib.WAND_SWITCH)) {
            EnumWandAction.setNextActionForPlayer(entityplayer);
        }
    }
}
