package com.slimevoid.littleblocks.client.events;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class RenderLittleChunkHighlight {

    @ForgeSubscribe
    public void onRenderHelmetOverlay(RenderGameOverlayEvent event) {
        if (event.type == RenderGameOverlayEvent.ElementType.HELMET) {
            // System.out.println("Rendering Helmet!");
        }
    }

}
