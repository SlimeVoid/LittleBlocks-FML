package littleblocks.core;

import java.util.EnumSet;

import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.src.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class CommonTickHandler implements ITickHandler {

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.SERVER))
				|| type.equals(TickType.CLIENT)) {
			World[] worlds = DimensionManager.getWorlds();
			for (World world : worlds) {
				TileEntityLittleBlocks.getLittleWorld(world).tickUpdates(false);
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER, TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "LittleBlocks Tick Handler";
	}

}
