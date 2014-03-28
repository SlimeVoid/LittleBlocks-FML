package net.slimevoid.littleblocks.world.events;

import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class LittleBlockEventList extends ArrayList {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private LittleBlockEventList() {
    }

    public LittleBlockEventList(LittleBlockEvent serverBlockEvent) {
        this();
    }
}
