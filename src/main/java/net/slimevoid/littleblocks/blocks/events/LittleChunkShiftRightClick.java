package net.slimevoid.littleblocks.blocks.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

public class LittleChunkShiftRightClick {

    @SubscribeEvent
    public void onShiftRightClickEvent(PlayerInteractEvent event) {
        EntityPlayer entityplayer = event.entityPlayer;
        if (entityplayer.isSneaking()) {
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                World world = FMLClientHandler.instance().getClient().theWorld;
                int x = event.x, y = event.y, z = event.z;
                if (world.getBlock(x,
                                   y,
                                   z) == ConfigurationLib.littleChunk) {
                    BlockLittleChunk littleChunk = ((BlockLittleChunk) ConfigurationLib.littleChunk);
                    if (littleChunk.onBlockActivated(world,
                                                     x,
                                                     y,
                                                     z,
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
