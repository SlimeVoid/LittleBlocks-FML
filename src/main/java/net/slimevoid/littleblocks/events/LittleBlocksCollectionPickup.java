package net.slimevoid.littleblocks.events;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;

public class LittleBlocksCollectionPickup {

    @SubscribeEvent
    public void CollectionPickup(EntityItemPickupEvent event) {
        EntityItem item = event.item;
        EntityPlayer entityplayer = event.entityPlayer;
        if (item instanceof EntityItemLittleBlocksCollection) {
            FMLCommonHandler.instance().firePlayerItemPickupEvent(entityplayer,
                                                                  item);

            int size = ((EntityItemLittleBlocksCollection) item).dropItems(entityplayer);
            entityplayer.onItemPickup(event.item,
                                      size);
            event.item.setDead();
            event.setCanceled(true);
        }
    }
}
