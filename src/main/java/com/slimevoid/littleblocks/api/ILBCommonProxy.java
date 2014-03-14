package com.slimevoid.littleblocks.api;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.slimevoid.library.ICommonProxy;

public interface ILBCommonProxy extends ICommonProxy {

    ILittleWorld getLittleWorld(IBlockAccess world, boolean needsRefresh);

    World getParentWorld(ILittleWorld littleWorld, int realDimension);
}
