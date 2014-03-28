package net.slimevoid.littleblocks.api;

import net.slimevoid.library.ICommonProxy;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface ILBCommonProxy extends ICommonProxy {

    ILittleWorld getLittleWorld(IBlockAccess world, boolean needsRefresh);

    World getParentWorld(ILittleWorld littleWorld, int realDimension);
}
