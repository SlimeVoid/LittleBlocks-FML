package net.slimevoid.littleblocks.client.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.slimevoid.library.IPacketExecutor;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.library.util.helpers.ChatHelper;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.MessageLib;
import net.slimevoid.littleblocks.network.packets.PacketLittleNotify;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ClientCopierNotifyExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleNotify
            && packet.getCommand().equals(CommandLib.COPIER_MESSAGE)) {
            String message = LanguageRegistry.instance().getStringLocalization(MessageLib.DENY_COPY);
            if (message.equals("")) message = LanguageRegistry.instance().getStringLocalization(MessageLib.DENY_WAND);
            ChatHelper.addColouredMessageToPlayer(entityplayer,
                                                  EnumChatFormatting.BLUE,
                                                  message);
        }
    }

}
