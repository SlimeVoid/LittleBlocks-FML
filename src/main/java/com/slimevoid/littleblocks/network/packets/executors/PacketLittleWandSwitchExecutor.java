package com.slimevoid.littleblocks.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.slimevoid.library.IPacketExecutor;
import com.slimevoid.library.network.PacketUpdate;
import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.items.wand.EnumWandAction;
import com.slimevoid.littleblocks.network.packets.PacketLittleNotify;

public class PacketLittleWandSwitchExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleNotify
            && packet.getCommand().equals(CommandLib.WAND_SWITCH)) {
            EnumWandAction.setNextActionForPlayer(entityplayer);
        }
    }
}
