package net.slimevoid.littleblocks.blocks.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;

public class LittleChunkShiftRightClick {

    @SubscribeEvent
    public void onShiftRightClickEvent(PlayerInteractEvent event) {
        EntityPlayer entityplayer = event.entityPlayer;
        if (entityplayer.isSneaking()) {
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                World world = FMLClientHandler.instance().getClient().theWorld;
                IBlockState state = world.getBlockState(event.pos);
                if (state.getBlock().isAssociatedBlock(ConfigurationLib.littleChunk)) {
                    BlockLittleChunk littleChunk = ((BlockLittleChunk) ConfigurationLib.littleChunk);
                    if (littleChunk.onBlockActivated(
                            world,
                            event.pos,
                            state,
                            entityplayer,
                            event.face,
                            (float) BlockLittleChunk.hitVec.xCoord,
                            (float) BlockLittleChunk.hitVec.yCoord,
                            (float) BlockLittleChunk.hitVec.zCoord)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
