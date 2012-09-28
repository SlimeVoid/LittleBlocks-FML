package littleblocks.tickhandlers;

import java.util.EnumSet;

import littleblocks.core.LBCore;

import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {

	int tickedCount = 0;
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		World world = ModLoader.getMinecraftInstance().theWorld;
		if (world != null) {
			LBCore.getLittleWorld(world, false).tickUpdates(false);
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
