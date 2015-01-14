package net.slimevoid.littleblocks.tickhandlers;

import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

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
