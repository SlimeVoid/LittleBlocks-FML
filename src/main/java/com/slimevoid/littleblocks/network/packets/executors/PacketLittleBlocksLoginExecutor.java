package com.slimevoid.littleblocks.network.packets.executors;

import java.util.List;

import com.slimevoid.library.IPacketExecutor;
import com.slimevoid.library.network.PacketUpdate;
import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;
import com.slimevoid.littleblocks.items.wand.EnumWandAction;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import com.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketLittleBlocksLoginExecutor implements IPacketExecutor {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlocksSettings
            && packet.getCommand() == CommandLib.FETCH) {
            EnumWandAction.getWandActionForPlayer(entityplayer);
            PacketLittleBlocksSettings packetSettings = new PacketLittleBlocksSettings();
            packetSettings.setCommand(CommandLib.SETTINGS);
            packetSettings.setClipMode(ConfigurationLib.littleBlocksClip);
            PacketDispatcher.sendPacketToPlayer(packet.getPacket(),
                                                (Player) entityplayer);
            List<TileEntity> tileEntities = world.loadedTileEntityList;
            for (TileEntity tileentity : tileEntities) {
                if (tileentity instanceof TileEntityLittleChunk) {
                    world.markBlockForUpdate(tileentity.xCoord,
                                             tileentity.yCoord + 1,
                                             tileentity.zCoord);
                }
            }
        }
    }

}
