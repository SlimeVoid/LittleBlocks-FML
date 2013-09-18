package slimevoid.littleblocks.tickhandlers;

import java.util.EnumSet;

import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LittleBlocks;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class LittleWorldServerTickHandler implements ITickHandler {
	
	public void doLittleWorldServerTickStart(Object... tickData) {
		WorldServer[] worlds = DimensionManager.getWorlds();
		if (worlds != null && worlds.length > 0) {
			for (World world : worlds) {
				if (world != null && !world.isRemote) {
					World littleWorld = (World) LittleBlocks.proxy.getLittleWorld(
							world,
							false);
					if (littleWorld != null) {
						littleWorld.updateEntities();
						littleWorld.tick();
					}
				}
			}
		}
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.SERVER))) {
			LBCore.registerLittleWorldServers();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.SERVER))) {
			this.doLittleWorldServerTickStart(tickData);
		}
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
