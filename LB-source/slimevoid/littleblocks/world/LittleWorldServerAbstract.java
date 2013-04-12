package slimevoid.littleblocks.world;

import cpw.mods.fml.common.FMLCommonHandler;
import slimevoid.littleblocks.api.ILittleWorld;
import net.minecraft.logging.ILogAgent;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

public class LittleWorldServerAbstract extends WorldServer implements ILittleWorld {
	
	private World realWorld;
	
	public LittleWorldServerAbstract(World world, int dimensionId) {
		/*
		MinecraftServer par1MinecraftServer,
		ISaveHandler par2ISaveHandler,
		String par3Str,
		int par4,
		WorldSettings par5WorldSettings,
		Profiler par6Profiler,
		ILogAgent par7ILogAgent
		*/
		super(FMLCommonHandler.instance().getMinecraftServerInstance(),
				world.getSaveHandler(),
				"LittleWorldServer",
				dimensionId,
				new WorldSettings(
				world.getWorldInfo().getSeed(),
				world.getWorldInfo().getGameType(),
				world.getWorldInfo().isMapFeaturesEnabled(),
				world.getWorldInfo().isHardcoreModeEnabled(),
				world.getWorldInfo().getTerrainType()), null, null);
		this.realWorld = world;
	}

	@Override
	public World getRealWorld() {
		return this.realWorld;
	}

	@Override
	public void idModified(int lastId, int xCoord, int yCoord, int zCoord,
			int i, int x, int y, int z, int id, int j) {

	}

	@Override
	public void metadataModified(int xCoord, int yCoord, int zCoord, int i,
			int x, int y, int z, int blockId, int metadata) {
		
	}

}
