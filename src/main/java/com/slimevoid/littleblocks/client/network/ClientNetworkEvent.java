package com.slimevoid.littleblocks.client.network;

import java.util.List;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.slimevoid.library.util.helpers.PacketHelper;
import com.slimevoid.littleblocks.core.lib.BlockUtil;
import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;
import com.slimevoid.littleblocks.items.wand.EnumWandAction;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import com.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import com.slimevoid.littleblocks.world.LittlePlayerController;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class ClientNetworkEvent {
    
    @SubscribeEvent
    public void onClientJoined(ClientConnectedToServerEvent event) {
        EntityPlayer entityplayer = FMLClientHandler.instance().getClientPlayerEntity();
        World world = entityplayer.worldObj;
        BlockUtil.setLittleController(new LittlePlayerController(FMLClientHandler.instance().getClient(), (NetHandlerPlayClient) event.handler), world.getWorldInfo().getGameType());
    }

}
