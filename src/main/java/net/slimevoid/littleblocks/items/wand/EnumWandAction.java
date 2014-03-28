package net.slimevoid.littleblocks.items.wand;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import net.slimevoid.library.util.helpers.ChatHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public enum EnumWandAction {
	PLACE_LB, ROTATE_LB, COPY_LB, DESTROY_LB;

	int actionID;
	String actionName;
	String actionDescription;

	private static HashMap<EntityPlayer, EnumWandAction> playerWandActions;
	private static EnumWandAction playerWandAction = PLACE_LB;

	public static void registerWandActions() {
		PLACE_LB.actionID = 0;
		PLACE_LB.actionName = "placeLB";
		PLACE_LB.actionDescription = "Place Mode";

		ROTATE_LB.actionID = 1;
		ROTATE_LB.actionName = "rotateLB";
		ROTATE_LB.actionDescription = "Rotate Mode";

		COPY_LB.actionID = 2;
		COPY_LB.actionName = "copyLB";
		COPY_LB.actionDescription = "Copy Mode";

		DESTROY_LB.actionID = 3;
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

	public static EnumWandAction getWandActionForPlayer(
			EntityPlayer entityplayer) {
		if (!playerWandActions.containsKey(entityplayer)) {
			return setWandActionForPlayer(entityplayer, EnumWandAction.PLACE_LB);
		}
		return playerWandActions.get(entityplayer);
	}

	public static EnumWandAction setWandActionForPlayer(
			EntityPlayer entityplayer, EnumWandAction action) {
		playerWandActions.put(entityplayer, action);
		return action;
	}

	public static void setNextActionForPlayer(EntityPlayer entityplayer) {
		EnumWandAction currentAction = getWandActionForPlayer(entityplayer);
		int nextActionID = currentAction.actionID;
		nextActionID++;
		EnumWandAction nextAction = getAction(nextActionID);
		setWandActionForPlayer(entityplayer, nextAction);
		entityplayer.addChatMessage(ChatHelper.getMessage(
				EnumChatFormatting.RED, "Little Wand now in "
						+ nextAction.actionDescription));
	}

	@SideOnly(Side.CLIENT)
	public static EnumWandAction getWandAction() {
		return playerWandAction;
	}

	@SideOnly(Side.CLIENT)
	public static void setNextWandAction() {
		EnumWandAction currentAction = getWandAction();
		int nextActionID = currentAction.actionID;
		EnumWandAction nextAction = getAction(nextActionID);
		setWandAction(nextAction);
	}

	@SideOnly(Side.CLIENT)
	private static void setWandAction(EnumWandAction action) {
		playerWandAction = action;
	}
}
