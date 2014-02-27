package com.slimevoid.littleblocks.client.network.packets.executors;

import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksCollection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import slimevoidlib.IPacketExecutor;
import slimevoidlib.network.PacketUpdate;

public class ClientLittleCollectionExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlocksCollection
            && packet.getCommand().equals(CommandLib.ENTITY_COLLECTION)) {
            Entity entity = ((PacketLittleBlocksCollection) packet).getEntity(world);
            if (entity instanceof EntityItemLittleBlocksCollection) {
                ((EntityItemLittleBlocksCollection) entity).setCollection(((PacketLittleBlocksCollection) packet).itemstackCollection);
            }
        }
    }

}
