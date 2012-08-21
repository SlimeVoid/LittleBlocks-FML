package littleblocks.api;

import littleblocks.blocks.BlockLittleBlocks;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraft.src.EurysMods.api.ICommonProxy;

public interface ILBCommonProxy extends ICommonProxy {
	public void registerTickHandler();

	public boolean onBlockActivated(World world, BlockLittleBlocks block,
			int x, int y, int z, EntityPlayer entityplayer, int q, float a,
			float b, float c);

	public void onBlockClicked(World world,
			BlockLittleBlocks blockLittleBlocks, int x, int y, int z,
			EntityPlayer entityplayer);
}
