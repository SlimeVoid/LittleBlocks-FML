package littleblocks.api;

import littleblocks.world.LittleWorld;
import net.minecraft.src.World;
import eurysmods.api.ICommonProxy;

public interface ILBCommonProxy extends ICommonProxy {

	LittleWorld getLittleWorld(World world, boolean needsRefresh);
}
