package slimevoid.littleblocks.world;

import net.minecraft.logging.ILogAgent;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

public class LittleWorldServer extends WorldServer {
	
	private final LittleWorld littleWorld;

	public LittleWorldServer(World referenceWorld, MinecraftServer minecraftServer, ISaveHandler iSaveHandler, String par3Str, int par4, WorldSettings par5WorldSettings, Profiler par6Profiler, ILogAgent par7iLogAgent) {
		super(minecraftServer, iSaveHandler, par3Str, par4, par5WorldSettings, par6Profiler, par7iLogAgent);
		this.littleWorld = new LittleWorld(referenceWorld, this.provider);
	}
}
