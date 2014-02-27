package com.slimevoid.littleblocks.events;

import com.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class LittleBlocksCollectionPickup {

    @ForgeSubscribe
    public void CollectionPickup(EntityItemPickupEvent event) {
        EntityItem item = event.item;
        EntityPlayer entityplayer = event.entityPlayer;
        if (item instanceof EntityItemLittleBlocksCollection) {
            GameRegistry.onPickupNotification(event.entityPlayer,
                                              event.item);

            int size = ((EntityItemLittleBlocksCollection) item).dropItems(entityplayer);
            entityplayer.onItemPickup(event.item,
                                      size);
            event.item.setDead();
            event.setCanceled(true);
        }
    }
}
