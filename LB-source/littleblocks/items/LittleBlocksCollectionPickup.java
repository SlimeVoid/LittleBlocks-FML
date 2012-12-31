package littleblocks.items;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class LittleBlocksCollectionPickup {

	@ForgeSubscribe
	public void CollectionPickup(EntityItemPickupEvent event) {
		EntityItem item = event.item;
		if (item instanceof EntityItemLittleBlocksCollection) {
			((EntityItemLittleBlocksCollection)item).dropItems(event.entityPlayer);
		}
	}
}
