package slimevoid.littleblocks.items.wand;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

public enum EnumWandAction {
	PLACE_LB, COPY_LB, ROTATE_LB;

	int														actionID;
	String													actionName;

	private static HashMap<EntityPlayer, EnumWandAction>	playerWandActions;

	public static void registerWandActions() {
		PLACE_LB.actionID = 0;
		PLACE_LB.actionName = "placeLB";

		ROTATE_LB.actionID = 1;
		ROTATE_LB.actionName = "rotateLB";

		COPY_LB.actionID = 2;
		COPY_LB.actionName = "copyLB";

		playerWandActions = new HashMap<EntityPlayer, EnumWandAction>();
	}

	public static EnumWandAction getAction(int actionID) {
		if (actionID >= 0 && actionID < EnumWandAction.values().length) {
			return EnumWandAction.values()[actionID];
		}
		return null;
	}

	public static EnumWandAction getWandActionForPlayer(EntityPlayer entityplayer) {
		return playerWandActions.containsValue(entityplayer) ? playerWandActions.get(entityplayer) : null;
	}
	
	public static void setWandActionForPlayer(EntityPlayer entityplayer, EnumWandAction action) {
		playerWandActions.put(entityplayer, action);
	}
}
