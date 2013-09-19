package slimevoid.littleblocks.events;

import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Load;

public class WorldServerLoadEvent {

	@ForgeSubscribe
	public void onWorldLoad(Load event) {
		World world = event.world;
		int dimension = world.provider.dimensionId;
		System.out.println("WorldServer Loaded: " + world.getWorldInfo().getWorldName() + " | Dimension: " + dimension);
		// TODO :: Load LittleWorldServer
	}

}
