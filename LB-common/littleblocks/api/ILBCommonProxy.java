package littleblocks.api;

import littleblocks.blocks.BlockLittleBlocks;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraft.src.EurysMods.api.ICommonProxy;

public interface ILBCommonProxy extends ICommonProxy {
	public void registerTickHandler();
}
