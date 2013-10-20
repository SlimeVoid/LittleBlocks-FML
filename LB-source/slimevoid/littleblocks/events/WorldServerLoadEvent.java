package slimevoid.littleblocks.events;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.world.LittleWorldServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class WorldServerLoadEvent {

	@ForgeSubscribe
	public void onWorldLoad(Load event) {
		if (event.world instanceof WorldServer
			&& !(event.world instanceof ILittleWorld)) {
			WorldServer world = (WorldServer) event.world;
			int dimension = world.provider.dimensionId;
			System.out.println("Loading WorldServer: "
								+ world.getWorldInfo().getWorldName()
								+ " | Dimension: " + dimension);

			int littleDimension = ConfigurationLib.getLittleServerDimension(dimension);

			registerLittleWorldServer(	world,
										dimension,
										littleDimension);
		}
		if (event.world instanceof ILittleWorld) {
			System.out.println("ENOUGH WORLD INCEPTION ALREADY!!!!	");
			WorldServer littleWorldServer = (WorldServer) event.world;
			Chunk chunk = new Chunk(littleWorldServer, new byte[] { 0 }, 0, 0);
			MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(chunk, new NBTTagCompound()));
		}
	}

	public void registerLittleWorldServer(WorldServer world, int dimension, int littleDimension) {
		if (!DimensionManager.isDimensionRegistered(littleDimension)) {
			DimensionManager.registerDimension(	littleDimension,
												0);

			ConfigurationLib.littleWorldServer.put(	dimension,
													littleDimension);

			String worldName = world.getWorldInfo().getWorldName()
								+ ".littleWorld";

			WorldSettings worldSettings = new WorldSettings(world.getWorldInfo().getSeed(), world.getWorldInfo().getGameType(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().isHardcoreModeEnabled(), world.getWorldInfo().getTerrainType());

			LittleWorldServer littleWorldServer = new LittleWorldServer(world, FMLCommonHandler.instance().getMinecraftServerInstance(), world.getSaveHandler(), worldName, littleDimension, worldSettings, null, null);
			Chunk chunk = new Chunk(littleWorldServer, new byte[] { 0 }, 0, 0);
			MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(chunk, new NBTTagCompound()));

			System.out.println("WorldServer Loaded: "
								+ world.getWorldInfo().getWorldName()
								+ " | Dimension: " + dimension
								+ " | LittleDimension: " + littleDimension);
		}
	}

}
