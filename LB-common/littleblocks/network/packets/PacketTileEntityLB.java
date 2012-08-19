package littleblocks.network.packets;

import littleblocks.core.LBInit;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.src.EurysMods.network.packets.core.PacketTileEntity;

public class PacketTileEntityLB extends PacketTileEntity {

	public PacketTileEntityLB() {
		super();
		this.setChannel(LBInit.LBM.getModChannel());
	}

	public PacketTileEntityLB(TileEntityLittleBlocks tileentity) {
		this();
		this.setPosition(tileentity.xCoord, tileentity.yCoord,
				tileentity.zCoord, 0);
		this.payload = tileentity.getTileEntityPayload();
	}
}
