package slimevoid.littleblocks.tickhandlers;

import java.util.EnumSet;

import slimevoid.littleblocks.core.LittleBlocks;

import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LittleWorldTickHandler implements ITickHandler {

	@SideOnly(Side.CLIENT)
	public void doLittleWorldClientTick(Object... tickData) {
		World world = FMLClientHandler.instance().getClient().theWorld;
		if (world != null) {
			World littleWorld = (World) LittleBlocks.proxy.getLittleWorld(	world,
																			false);
			if (littleWorld != null) {
				littleWorld.updateEntities();
				littleWorld.tick();
			}
		}
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.CLIENT))) {
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.CLIENT))) {
			this.doLittleWorldClientTick(tickData);
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		return "LittleBlocks Tick Handler";
	}

}
