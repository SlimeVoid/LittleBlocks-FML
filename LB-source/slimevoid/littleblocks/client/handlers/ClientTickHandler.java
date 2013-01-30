package slimevoid.littleblocks.client.handlers;

import java.util.EnumSet;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import slimevoid.littleblocks.core.LittleBlocks;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		World world = DimensionManager.getWorld(0);
		if (world != null) {
			LittleBlocks.proxy.getLittleWorld(
					world,
					false).updateEntities();
			LittleBlocks.proxy.getLittleWorld(
					world,
					false).tick();
		} else {
			LittleBlocks.proxy.resetLittleBlocks();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "LittleBlocks Client Tick Handler";
	}
}
