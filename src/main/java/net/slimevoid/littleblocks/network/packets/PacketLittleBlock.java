package net.slimevoid.littleblocks.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.slimevoid.library.network.PacketIds;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.library.util.helpers.NBTHelper;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.CoreLib;

public class PacketLittleBlock extends PacketUpdate {

    private ItemStack itemStack;

    @Override
    public void writeData(ByteBuf data) {
        super.writeData(data);
        if (this.itemStack == null) {
            data.writeBoolean(false);
        } else {
            data.writeBoolean(true);
            NBTHelper.writeItemStack(data,
                    this.itemStack);
        }
    }

    @Override
    public void readData(ByteBuf data) {
        super.readData(data);
        if (data.readBoolean() == true) {
            this.itemStack = NBTHelper.readItemStack(data);
        }
    }

    public PacketLittleBlock() {
        super(PacketIds.TILE);
        this.setChannel(CoreLib.MOD_CHANNEL);
    }

    /**
     * CLICK BLOCK
     */
    public PacketLittleBlock(BlockPos pos, int face) {
        this();
        this.setPosition(pos,
                         face);
        this.setCommand(CommandLib.BLOCK_CLICKED);
    }

    /**
     * ACTIVATE BLOCK
     */
    public PacketLittleBlock(BlockPos pos, int direction, ItemStack itemStack, float xOff, float yOff, float zOff) {
        this();
        this.setPosition(pos,
                         direction);
        this.setHitVectors(xOff,
                           yOff,
                           zOff);
        this.itemStack = itemStack != null ? itemStack.copy() : null;
        this.setCommand(CommandLib.BLOCK_ACTIVATED);
    }

    public int getDirection() {
        return this.side;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public boolean targetExists(World world) {
        return false;
    }
}
