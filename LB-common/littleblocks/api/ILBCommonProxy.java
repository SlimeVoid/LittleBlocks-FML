package littleblocks.api;

import littleblocks.world.LittleWorld;
import net.minecraft.src.World;
import net.minecraftforge.common.Configuration;
import eurysmods.api.ICommonProxy;

public interface ILBCommonProxy extends ICommonProxy {

	LittleWorld getLittleWorld(World world, boolean needsRefresh);

	void setLittleDimension(World world, Configuration configuration, int nextFreeDimId);

	int getLittleDimension();

	void resetLittleBlocks();
}
