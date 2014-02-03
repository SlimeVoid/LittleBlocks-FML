package slimevoid.littleblocks.blocks.events;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class LittleChunkShiftRightClick {

    @ForgeSubscribe
    public void onShiftRightClickEvent(PlayerInteractEvent event) {
        EntityPlayer entityplayer = event.entityPlayer;
        if (entityplayer.isSneaking()) {
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                World world = FMLClientHandler.instance().getClient().theWorld;
                int x = event.x, y = event.y, z = event.z;
                if (world.getBlockId(x,
                                     y,
                                     z) == ConfigurationLib.littleChunkID) {
                    BlockLittleChunk littleChunk = ((BlockLittleChunk) Block.blocksList[ConfigurationLib.littleChunkID]);
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
