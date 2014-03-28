package net.slimevoid.littleblocks.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.slimevoid.library.IPacketExecutor;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.items.wand.EnumWandAction;
import net.slimevoid.littleblocks.network.packets.PacketLittleNotify;

public class PacketLittleWandSwitchExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleNotify
            && packet.getCommand().equals(CommandLib.WAND_SWITCH)) {
            EnumWandAction.setNextActionForPlayer(entityplayer);
        }
    }
}
