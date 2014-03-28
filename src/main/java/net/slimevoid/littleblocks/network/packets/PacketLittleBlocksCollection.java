package net.slimevoid.littleblocks.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.slimevoid.library.nbt.NBTHelper;
import net.slimevoid.library.network.PacketEntity;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;

public class PacketLittleBlocksCollection extends PacketEntity {
    public HashMap<Item, ItemStack> itemstackCollection = new HashMap<Item, ItemStack>();

    public PacketLittleBlocksCollection() {
        super();
        this.setChannel(CoreLib.MOD_CHANNEL);
    }

    public PacketLittleBlocksCollection(EntityItemLittleBlocksCollection entitylb) {
        this();
        this.setPosition((int) entitylb.posX,
                         (int) entitylb.posY,
                         (int) entitylb.posZ,
                         0);
        this.setEntityId(entitylb.getEntityId());
        this.setCommand(CommandLib.ENTITY_COLLECTION);
        this.itemstackCollection = entitylb.getCollection();
    }

    @Override
    public void readData(ChannelHandlerContext ctx, ByteBuf data) {
        super.readData(ctx,
                       data);
        int stacks = data.readInt();
        for (int i = 0; i < stacks; i++) {
            this.addItemToDrop(ItemStack.loadItemStackFromNBT(NBTHelper.readNBTTagCompound(data)));
        }
    }

    @Override
    public void writeData(ChannelHandlerContext ctx, ByteBuf data) {
        super.writeData(ctx,
                        data);
        data.writeInt(itemstackCollection.size());
        for (ItemStack itemstack : itemstackCollection.values()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTHelper.writeNBTTagCompound(itemstack.writeToNBT(nbttagcompound),
                                          data);
        }
    }

    public void addItemToDrop(ItemStack itemstack) {
        if (itemstackCollection.containsKey(itemstack.getItem())) {
            itemstackCollection.get(itemstack.getItem()).stackSize++;
        } else {
            itemstackCollection.put(itemstack.getItem(),
                                    itemstack);
        }
    }
}
