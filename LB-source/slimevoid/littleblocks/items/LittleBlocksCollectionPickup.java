package slimevoid.littleblocks.items;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class LittleBlocksCollectionPickup {

	@ForgeSubscribe
	public void CollectionPickup(EntityItemPickupEvent event) {
		EntityItem item = event.item;
		EntityPlayer entityplayer = event.entityPlayer;
		if (item instanceof EntityItemLittleBlocksCollection) {
			((EntityItemLittleBlocksCollection)item).dropItems(entityplayer);
			event.setResult(Result.ALLOW);
		}
	}
}
