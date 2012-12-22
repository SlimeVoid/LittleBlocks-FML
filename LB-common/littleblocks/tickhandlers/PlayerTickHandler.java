package littleblocks.tickhandlers;

import java.util.EnumSet;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;






public class PlayerTickHandler implements ITickHandler {

	public static HashMap<EntityPlayer, Boolean> chestsActivated = new HashMap();
	public static HashMap<EntityPlayer, IInventory> openContainers = new HashMap();

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (tickData.length > 0 && tickData[0] instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer)tickData[0];
			if (entityplayer.openContainer != null && openContainers.containsKey(entityplayer)) {
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (tickData.length > 0 && tickData[0] instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer)tickData[0];
			if (entityplayer.openContainer != null && !(entityplayer.openContainer instanceof ContainerChest) && openContainers.containsKey(entityplayer)) {
				//entityplayer.displayGUIChest((IInventory)openContainers.get(entityplayer));
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER, TickType.CLIENT, TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "LittleBlock Chests";
	}

}
