package slimevoid.littleblocks.blocks.core;

import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import slimevoid.littleblocks.core.lib.CoreLib;

public class LittleContainerInteract {

	@ForgeSubscribe
	public void onInteractEvent(PlayerOpenContainerEvent event) {
		 FakePlayer fakePlayer = new FakePlayer(event.entityPlayer.worldObj, CoreLib.MOD_CHANNEL);
		 fakePlayer.posX = event.entityPlayer.posX * 8;
		 fakePlayer.posY = (event.entityPlayer.posY +
		 (event.entityPlayer.height * 0.9)) * 8;
		 fakePlayer.posZ = event.entityPlayer.posZ * 8;
		 fakePlayer.openContainer = event.entityPlayer.openContainer;
		 if (fakePlayer.openContainer.canInteractWith(fakePlayer)) {
			 event.setResult(Result.ALLOW);
		 }
	}
}