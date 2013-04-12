package slimevoid.littleblocks.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LBCore;

public class LittleWorldClientAbstract extends World implements ILittleWorld {

	public LittleWorldClientAbstract(World world) {
		super(
				world.getSaveHandler(),
				"LittleWorldClient",
				LBCore.littleProviderClient,
				new WorldSettings(world.getWorldInfo().getSeed(), world
						.getWorldInfo()
							.getGameType(), world
						.getWorldInfo()
							.isMapFeaturesEnabled(), world
						.getWorldInfo()
							.isHardcoreModeEnabled(), world
						.getWorldInfo()
							.getTerrainType()), null, null/**field_98181_L**/);
	}
	
	private World realWorld;

	@Override
	public World getRealWorld() {
		// TODO :: Auto-generated method stub
		return null;
	}

	@Override
	public void idModified(int lastId, int xCoord, int yCoord, int zCoord,
			int i, int x, int y, int z, int id, int j) {
		// TODO :: Auto-generated method stub

	}

	@Override
	public void metadataModified(int xCoord, int yCoord, int zCoord, int i,
			int x, int y, int z, int blockId, int metadata) {
		// TODO :: Auto-generated method stub

	}

	@Override
	protected IChunkProvider createChunkProvider() {
		// TODO :: Auto-generated method stub
		return null;
	}

	@Override
	public Entity getEntityByID(int i) {
		// TODO :: Auto-generated method stub
		return null;
	}

}
