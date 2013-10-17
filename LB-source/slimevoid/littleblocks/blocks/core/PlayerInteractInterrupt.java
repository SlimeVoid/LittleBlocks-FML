package slimevoid.littleblocks.blocks.core;

import slimevoid.littleblocks.blocks.BlockLittleChunk;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class PlayerInteractInterrupt {
	
	@ForgeSubscribe
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			World world = FMLClientHandler.instance().getClient().theWorld;
			if (world != null && world.isRemote && world.getBlockId(event.x, event.y, event.z) == ConfigurationLib.littleChunkID) {
				if (event.action == Action.RIGHT_CLICK_BLOCK) {
					//this.onRightClick(event, world);
					//event.setCanceled(true);
				}
			}
		}
	}

	private void onRightClick(PlayerInteractEvent event, World world) {
		World littleWorld = (World) LittleBlocks.proxy.getLittleWorld(world, false);
		EntityPlayer entityplayer = event.entityPlayer;
		int x = (event.x << 3) + BlockLittleChunk.xSelected;
		int y = (event.y << 3) + BlockLittleChunk.ySelected;
		int z = (event.z << 3) + BlockLittleChunk.zSelected;
		int side = BlockLittleChunk.side;
		if (BlockUtil.getLittleController().onPlayerRightClick(	entityplayer,
															littleWorld,
															entityplayer.inventory.getCurrentItem(),
															x,
															y,
															z,
															side,
															BlockLittleChunk.hitVec)) {
			event.entityPlayer.swingItem();
		}
	}

}
