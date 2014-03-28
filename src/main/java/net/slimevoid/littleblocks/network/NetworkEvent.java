package net.slimevoid.littleblocks.network;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.slimevoid.library.util.helpers.PacketHelper;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.items.wand.EnumWandAction;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;

public class NetworkEvent {

    @SubscribeEvent
    public void onClientJoined(ServerConnectionFromClientEvent event) {
        EntityPlayer entityplayer = ((NetHandlerPlayServer) event.handler).playerEntity;
        World world = entityplayer.worldObj;
        EnumWandAction.getWandActionForPlayer(entityplayer);
        PacketLittleBlocksSettings packet = new PacketLittleBlocksSettings();
        packet.setCommand(CommandLib.SETTINGS);
        packet.setClipMode(ConfigurationLib.littleBlocksClip);
        PacketHelper.sendToPlayer(packet,
                                  (EntityPlayerMP) entityplayer);
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
