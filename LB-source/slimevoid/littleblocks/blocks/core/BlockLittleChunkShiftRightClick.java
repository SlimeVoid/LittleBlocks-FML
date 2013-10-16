package slimevoid.littleblocks.blocks.core;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class BlockLittleChunkShiftRightClick {

	@ForgeSubscribe
	public void onShiftRightClickEvent(PlayerInteractEvent event) {
		EntityPlayer entityplayer = event.entityPlayer;
		if (entityplayer.isSneaking()) {
			int x = event.x, y = event.y, z = event.z;
			World world = event.entityPlayer.worldObj;
			if (world.getBlockId(	x,
									y,
									z) == ConfigurationLib.littleChunkID) {
				if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
					Block.blocksList[ConfigurationLib.littleChunkID].onBlockActivated(	world,
																						x,
																						y,
																						z,
																						entityplayer,
																						event.face,
																						(float) BlockLittleChunk.hitVec.xCoord,
																						(float) BlockLittleChunk.hitVec.yCoord,
																						(float) BlockLittleChunk.hitVec.zCoord);
				}
				event.setCanceled(true);
			}
		}
	}
}
