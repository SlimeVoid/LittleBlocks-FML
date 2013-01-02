package slimevoid.littleblocks.api;

import slimevoid.littleblocks.blocks.BlockLittleBlocks;
import slimevoid.littleblocks.world.LittleWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import eurysmods.api.IPacketHandling;

public interface ILBPacketHandling extends IPacketHandling {
	public void metadataModified(LittleWorld littleWorld, int x, int y, int z, int side, float vecX, float vecY, float vecZ, int lastMetadata, int newMetadata);

	public void idModified(int x, int y, int z, int side, float vecX, float vecY, float vecZ, int lastId, int newId, LittleWorld littleWorld);

	public void blockUpdate(World world, EntityPlayer entityplayer, int x, int y, int z, int q, float a, float b, float c, BlockLittleBlocks block, String blockActivateCommand);
}
