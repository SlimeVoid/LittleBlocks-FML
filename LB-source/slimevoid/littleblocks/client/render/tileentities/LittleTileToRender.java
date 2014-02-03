package slimevoid.littleblocks.client.render.tileentities;

import net.minecraft.tileentity.TileEntity;

public class LittleTileToRender {
    public TileEntity tileentity;
    int               x, y, z;

    public LittleTileToRender(TileEntity tileentity) {
        this.tileentity = tileentity;
        this.x = tileentity.xCoord;
        this.y = tileentity.yCoord;
        this.z = tileentity.zCoord;
    }
}