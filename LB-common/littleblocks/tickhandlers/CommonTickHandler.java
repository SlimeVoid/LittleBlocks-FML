package littleblocks.tickhandlers;

import java.util.EnumSet;

import littleblocks.api.ILBCommonProxy;
import littleblocks.core.LBCore;
import littleblocks.core.LBInit;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class CommonTickHandler implements ITickHandler {
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		World world = DimensionManager.getWorld(0);
		if (world != null) {
			((ILBCommonProxy)LBInit.LBM.getProxy()).getLittleWorld(world, false).tickUpdates(false);
		}
		System.out.println("WorldServer: " + LBCore.littleWorldServer);
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
