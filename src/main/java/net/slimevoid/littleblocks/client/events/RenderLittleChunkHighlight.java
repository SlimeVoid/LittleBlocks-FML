package net.slimevoid.littleblocks.client.events;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderLittleChunkHighlight {

    @SubscribeEvent
    public void onRenderHelmetOverlay(RenderGameOverlayEvent event) {
        if (event.type == RenderGameOverlayEvent.ElementType.HELMET) {
            // System.out.println("Rendering Helmet!");
        }
    }

}
