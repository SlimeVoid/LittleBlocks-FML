package slimevoid.littleblocks.handlers;

import java.util.EnumSet;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import slimevoid.littleblocks.api.ILBCommonProxy;
import slimevoid.littleblocks.core.LBInit;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class CommonTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		World world = DimensionManager.getWorld(0);
		if (world != null) {
			((ILBCommonProxy) LBInit.LBM.getProxy()).getLittleWorld(
					world,
					false).updateEntities();
			((ILBCommonProxy) LBInit.LBM.getProxy()).getLittleWorld(
					world,
					false).tick();
		} else {
			((ILBCommonProxy) LBInit.LBM.getProxy()).resetLittleBlocks();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "LittleBlocks Tick Handler";
	}

}
