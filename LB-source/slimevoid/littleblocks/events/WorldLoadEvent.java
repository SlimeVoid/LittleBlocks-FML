package slimevoid.littleblocks.events;

import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.world.LittleWorldClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Load;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WorldLoadEvent {

	@ForgeSubscribe
	public void onWorldLoad(Load event) {
		if (event.world instanceof WorldClient && !(event.world instanceof ILittleWorld)) {
			WorldClient world = (WorldClient) event.world;
			int dimension = world.provider.dimensionId;
			
			int littleDimension = ConfigurationLib.getLittleDimension(dimension);
			
			DimensionManager.registerProviderType(littleDimension, WorldProviderSurface.class, true);
			DimensionManager.registerDimension(littleDimension, littleDimension);
			//this, new WorldSettings(0L, par1Packet1Login.gameType, false, par1Packet1Login.hardcoreMode, par1Packet1Login.terrainType), par1Packet1Login.dimension, par1Packet1Login.difficultySetting, this.mc.mcProfiler, this.mc.getLogAgent()
			WorldProvider provider = WorldProvider.getProviderForDimension(littleDimension);
			
			LBCore.littleWorldClient = new LittleWorldClient(world, world.getSaveHandler(), "LittleWorldClient", provider, new WorldSettings(world.getWorldInfo().getSeed(), world.getWorldInfo().getGameType(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().isHardcoreModeEnabled(), world.getWorldInfo().getTerrainType()), littleDimension, null, null);
			
			System.out.println("World Loaded: " + world.getWorldInfo().getWorldName() + " | Dimension: " + dimension + " | LittleDimension: " + littleDimension);
			// TODO :: Load LittleWorldClient
		}
	}

}
