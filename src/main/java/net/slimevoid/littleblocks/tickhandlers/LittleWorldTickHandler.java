package net.slimevoid.littleblocks.tickhandlers;

import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class LittleWorldTickHandler {

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            World world = FMLClientHandler.instance().getClient().theWorld;
            if (world != null) {
                // World littleWorld = (World)
                // LittleBlocks.proxy.getLittleWorld(
                // world,
                // false);
                // if (littleWorld != null) {
                // littleWorld.updateEntities();
                // littleWorld.tick();
                // }
            }
        }
    }

}
