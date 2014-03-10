package com.slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.slimevoid.library.IPacketExecutor;
import com.slimevoid.library.network.PacketUpdate;
import com.slimevoid.library.util.helpers.ChatHelper;
import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.core.lib.MessageLib;
import com.slimevoid.littleblocks.network.packets.PacketLittleNotify;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class ClientCopierNotifyExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleNotify
            && packet.getCommand().equals(CommandLib.COPIER_MESSAGE)) {
            String message = LanguageRegistry.instance().getStringLocalization(MessageLib.DENY_COPY);
            if (message.equals("")) message = LanguageRegistry.instance().getStringLocalization(MessageLib.DENY_WAND);
            entityplayer.addChatMessage(ChatHelper.getMessage(EnumChatFormatting.BLUE,
                                                              message));
        }
    }

}
