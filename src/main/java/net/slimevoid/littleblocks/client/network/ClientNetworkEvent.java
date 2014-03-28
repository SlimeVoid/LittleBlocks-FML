package net.slimevoid.littleblocks.client.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.slimevoid.littleblocks.core.lib.BlockUtil;
import net.slimevoid.littleblocks.world.LittlePlayerController;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class ClientNetworkEvent {

    @SubscribeEvent
    public void onClientJoined(ClientConnectedToServerEvent event) {
        EntityPlayer entityplayer = FMLClientHandler.instance().getClientPlayerEntity();
        World world = entityplayer.worldObj;
        BlockUtil.setLittleController(new LittlePlayerController(FMLClientHandler.instance().getClient(), (NetHandlerPlayClient) event.handler),
                                      world.getWorldInfo().getGameType());
    }

}
