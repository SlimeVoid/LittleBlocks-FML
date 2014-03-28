package net.slimevoid.littleblocks.tickhandlers;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class LittleWorldServerTickHandler {

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            WorldServer[] worlds = DimensionManager.getWorlds();
            if (worlds != null && worlds.length > 0) {
                for (World world : worlds) {
                    if (world != null && !world.isRemote
                        && !(world instanceof ILittleWorld)) {
                        int dimension = world.provider.dimensionId;
                        if (!ConfigurationLib.littleWorldServer.containsKey(dimension)) {
                            System.out.println("WARNING! No LittleWorld loaded for Dimension "
                                               + dimension);
                        } else {
                            /*
                             * LittleWorldServer worldServer =
                             * (LittleWorldServer) DimensionManager
                             * .getWorld(LBCore
                             * .littleWorldServer.get(dimension)); if
                             * (worldServer != null) { worldServer.littleTick();
                             * worldServer.updateLittleEntities(); }
                             */
                        }
                    }
                }
            }
        }
    }

}
