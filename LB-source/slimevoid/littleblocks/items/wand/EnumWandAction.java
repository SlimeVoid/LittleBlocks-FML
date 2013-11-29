package slimevoid.littleblocks.items.wand;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.lib.PacketLib;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum EnumWandAction {
	PLACE_LB, ROTATE_LB, COPY_LB, DESTROY_LB;

	String													actionName;
	String													actionDescription;

	private static HashMap<EntityPlayer, EnumWandAction>	playerWandActions;
	private static EnumWandAction							playerWandAction	= PLACE_LB;

	public static void registerWandActions() {
		PLACE_LB.actionName = "placeLB";
		PLACE_LB.actionDescription = "Place Mode";

		ROTATE_LB.actionName = "rotateLB";
		ROTATE_LB.actionDescription = "Rotate Mode";

		COPY_LB.actionName = "copyLB";
		COPY_LB.actionDescription = "Copy Mode";

		DESTROY_LB.actionName = "destroyLB";
		DESTROY_LB.actionDescription = "Destroy Mode";

		playerWandActions = new HashMap<EntityPlayer, EnumWandAction>();
	}

	public static EnumWandAction getAction(int actionID) {
		if (actionID < 0) {
			actionID = EnumWandAction.values().length - 1;
		} else if (actionID >= EnumWandAction.values().length) {
			actionID = 0;
		}
		return EnumWandAction.values()[actionID];
	}

	public static EnumWandAction getWandActionForPlayer(EntityPlayer entityplayer) {
		if (!playerWandActions.containsKey(entityplayer)) {
			return setWandActionForPlayer(	entityplayer,
											EnumWandAction.PLACE_LB);
		}
		return playerWandActions.get(entityplayer);
	}

	public static EnumWandAction setWandActionForPlayer(EntityPlayer entityplayer, EnumWandAction action) {
		playerWandActions.put(	entityplayer,
								action);
		return action;
	}

	public static void setNextActionForPlayer(World world, EntityPlayer entityplayer) {
		EnumWandAction currentAction = getWandActionForPlayer(entityplayer);
		int nextActionID = currentAction.ordinal();
		nextActionID++;
		EnumWandAction nextAction = validateAction(	entityplayer,
													getAction(nextActionID));
		setWandActionForPlayer(	entityplayer,
								nextAction);
		entityplayer.sendChatToPlayer("Little Wand now in "
										+ nextAction.actionDescription);
		PacketLib.sendWandChange(	world,
									entityplayer,
									nextAction.ordinal());
	}

	private static EnumWandAction validateAction(EntityPlayer entityplayer, EnumWandAction action) {
		int nextActionID = action.ordinal();
		nextActionID++;
		return action.equals(EnumWandAction.COPY_LB) ? entityplayer.capabilities.isCreativeMode ? action : getAction(nextActionID) : action;
	}

	@SideOnly(Side.CLIENT)
	public static EnumWandAction getWandAction() {
		return playerWandAction;
	}

	@SideOnly(Side.CLIENT)
	public static void setNextWandAction(int actionID) {
		EnumWandAction nextAction = getAction(actionID);
		setWandAction(nextAction);
	}

	@SideOnly(Side.CLIENT)
	private static void setWandAction(EnumWandAction action) {
		playerWandAction = action;
	}
}
