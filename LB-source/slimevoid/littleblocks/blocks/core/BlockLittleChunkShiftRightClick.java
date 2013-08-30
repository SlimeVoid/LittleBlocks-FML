package slimevoid.littleblocks.blocks.core;

import slimevoid.littleblocks.core.LBCore;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class BlockLittleChunkShiftRightClick {
	
	@ForgeSubscribe
	public void onShiftRightClickEvent(PlayerInteractEvent event) {
		EntityPlayer entityplayer = event.entityPlayer;
		if (entityplayer.isSneaking()) {
			int x = event.x,
				y = event.y,
				z = event.z;
			World world = event.entityPlayer.worldObj;
			if (world.getBlockId(x, y, z) == LBCore.littleChunkID) {
				if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
					Block.blocksList[LBCore.littleChunkID].onBlockActivated(world, x, y, z, entityplayer, event.face, 0, 0, 0);
				}
				event.setCanceled(true);
			}
		}
	}
}
