package slimevoid.littleblocks.api;

import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import slimevoid.lib.ICommonProxy;

public interface ILBCommonProxy extends ICommonProxy {

	ILittleWorld getLittleWorld(World world, boolean needsRefresh);

	void setLittleDimension(World world, Configuration configuration, int nextFreeDimId);

	int getLittleDimension();

	void resetLittleBlocks();
}
