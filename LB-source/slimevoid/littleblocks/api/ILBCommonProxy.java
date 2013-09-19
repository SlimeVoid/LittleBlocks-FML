package slimevoid.littleblocks.api;

import net.minecraft.world.IBlockAccess;
import slimevoidlib.ICommonProxy;

public interface ILBCommonProxy extends ICommonProxy {

	ILittleWorld getLittleWorld(IBlockAccess world, boolean needsRefresh);
}
