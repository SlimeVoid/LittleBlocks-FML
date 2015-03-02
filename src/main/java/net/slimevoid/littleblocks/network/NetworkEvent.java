package net.slimevoid.littleblocks.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import net.slimevoid.library.util.helpers.PacketHelper;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.items.wand.EnumWandAction;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;

import java.util.List;

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
                world.markBlockForUpdate(tileentity.getPos());
            }
        }
    }

}
