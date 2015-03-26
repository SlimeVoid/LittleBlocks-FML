package net.slimevoid.littleblocks.api;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.slimevoid.library.proxy.ICommonProxy;

public interface ILBCommonProxy extends ICommonProxy {

    ILittleWorld getLittleWorld(IBlockAccess world, boolean needsRefresh);

    World getParentWorld(ILittleWorld littleWorld, int realDimension);
}
