package slimevoid.littleblocks.events;

import cpw.mods.fml.common.FMLCommonHandler;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.world.LittleWorldServer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Load;

public class WorldServerLoadEvent {

	@ForgeSubscribe
	public void onWorldLoad(Load event) {
		if (event.world instanceof WorldServer && !(event.world instanceof ILittleWorld)) {
			WorldServer world = (WorldServer) event.world;
			int dimension = world.provider.dimensionId;
			System.out.println("Loading WorldServer: " + world.getWorldInfo().getWorldName() + " | Dimension: " + dimension);
			
			int littleDimension = ConfigurationLib.getLittleServerDimension(dimension);
			
			if (!DimensionManager.isDimensionRegistered(littleDimension)) {
				DimensionManager.registerDimension(littleDimension, dimension);
				
				LBCore.littleWorldServer.put(dimension, littleDimension);
				
				String worldName = world.getWorldInfo().getWorldName() + ".littleWorld";
				
				WorldSettings worldSettings = new WorldSettings(world.getWorldInfo().getSeed(), world.getWorldInfo().getGameType(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().isHardcoreModeEnabled(), world.getWorldInfo().getTerrainType());
				
				new LittleWorldServer(world, FMLCommonHandler.instance().getMinecraftServerInstance(), world.getSaveHandler(), worldName, littleDimension, worldSettings, null, null);
				System.out.println("WorldServer Loaded: " + world.getWorldInfo().getWorldName() + " | Dimension: " + dimension + " | LittleDimension: " + littleDimension);
				// TODO :: Load LittleWorldServer
			}
		}
	}

}
