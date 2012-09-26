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
		if (type.equals(EnumSet.of(TickType.CLIENT))) {
			if (this.tickedCount >= 40) {
				EntityPlayer entityplayer = ModLoader.getMinecraftInstance().thePlayer;
				if (entityplayer != null && entityplayer instanceof EntityClientPlayerMP) {
					World world = ((EntityClientPlayerMP)entityplayer).worldObj;
					if (world != null) {
					}
				}
				this.tickedCount = 0;
			}
			tickedCount++;
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
