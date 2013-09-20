package slimevoid.littleblocks.events;

import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LBCore;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Unload;

public class WorldServerUnloadEvent {
	
	@ForgeSubscribe
	public void onWorldUnload(Unload event) {
		if (event.world instanceof WorldServer && !(event.world instanceof ILittleWorld)) {
			WorldServer world = (WorldServer) event.world;
			int dimension = world.provider.dimensionId;
			
			if (LBCore.littleWorldServer.containsKey(dimension)) {
				int littleDimension = LBCore.littleWorldServer.get(dimension);
				if (DimensionManager.isDimensionRegistered(littleDimension)) {
					DimensionManager.setWorld(littleDimension, null);
					DimensionManager.unregisterDimension(littleDimension);
					LBCore.littleWorldServer.remove(dimension);
				}
			}
		}
	}

}
