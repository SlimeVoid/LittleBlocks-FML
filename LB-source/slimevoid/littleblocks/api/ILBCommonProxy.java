package slimevoid.littleblocks.api;

import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import slimevoid.lib.ICommonProxy;
import slimevoid.littleblocks.world.LittleWorld;

public interface ILBCommonProxy extends ICommonProxy {

	LittleWorld getLittleWorld(World world, boolean needsRefresh);

	void setLittleDimension(World world, Configuration configuration, int nextFreeDimId);

	int getLittleDimension();

	void resetLittleBlocks();
}
