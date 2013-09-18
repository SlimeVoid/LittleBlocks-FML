package slimevoid.littleblocks.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.logging.ILogAgent;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

@SideOnly(Side.CLIENT)
public class LittleWorldClient extends WorldClient {
	
	private final LittleWorld littleWorld;

	public LittleWorldClient(World referenceWorld, NetClientHandler par1NetClientHandler, WorldSettings par2WorldSettings, int par3, int par4, Profiler par5Profiler, ILogAgent par6iLogAgent) {
		super(par1NetClientHandler, par2WorldSettings, par3, par4, par5Profiler, par6iLogAgent);
		this.littleWorld = new LittleWorld(referenceWorld);
	}

}
