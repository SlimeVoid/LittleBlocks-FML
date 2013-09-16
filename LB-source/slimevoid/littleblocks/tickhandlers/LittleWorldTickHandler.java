package slimevoid.littleblocks.tickhandlers;

import java.util.EnumSet;

import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LittleBlocks;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LittleWorldTickHandler implements ITickHandler {
	
	@SideOnly(Side.CLIENT)
	public void doLittleWorldClientTickStart(Object... tickData) {
		World world = FMLClientHandler.instance().getClient().theWorld;
		if (world != null) {
			ILittleWorld littleWorld = LittleBlocks.proxy.getLittleWorld(
					world,
					false);
			if (littleWorld != null) {
				littleWorld.updateEntities();
				littleWorld.tick();
			}
		}
	}
	
	public void doLittleWorldServerTickStart(Object... tickData) {
		WorldServer[] worlds = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers;
		if (worlds != null && worlds.length > 0) {
			for (World world : worlds) {
				if (world != null) {
					ILittleWorld littleWorld = LittleBlocks.proxy.getLittleWorld(
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
		if (type.equals(EnumSet.of(TickType.CLIENT))) {
			this.doLittleWorldClientTickStart(tickData);
		}
		if (type.equals(EnumSet.of(TickType.SERVER))) {
			this.doLittleWorldServerTickStart(tickData);
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
