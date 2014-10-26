package net.slimevoid.littleblocks.network.packets.executors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.slimevoid.library.IPacketExecutor;
import net.slimevoid.library.network.PacketUpdate;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlock;

public class PacketLittleBlockActivatedExecutor implements IPacketExecutor {

    @Override
    public void execute(PacketUpdate packet, World world, EntityPlayer entityplayer) {
        if (packet instanceof PacketLittleBlock
            && packet.getCommand().equals(CommandLib.BLOCK_ACTIVATED)) {
        	ILittleWorld littleworld = LittleBlocks.proxy.getLittleWorld(world,
                    false);
        	//System.out.println(littleworld);
	            ((BlockLittleChunk) ConfigurationLib.littleChunk).onServerBlockActivated((World) littleworld,
	                                                                                     entityplayer,
	                                                                                     // littlePacket.getItemStack(),
	                                                                                     packet.xPosition,
	                                                                                     packet.yPosition,
	                                                                                     packet.zPosition,
	                                                                                     packet.side,
	                                                                                     packet.hitX,
	                                                                                     packet.hitY,
	                                                                                     packet.hitZ);
        }
    }
}
