package slimevoid.littleblocks.api;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimevoidlib.ICommonProxy;

public interface ILBCommonProxy extends ICommonProxy {

    ILittleWorld getLittleWorld(IBlockAccess world, boolean needsRefresh);

    World getParentWorld(ILittleWorld littleWorld, int realDimension);
}
