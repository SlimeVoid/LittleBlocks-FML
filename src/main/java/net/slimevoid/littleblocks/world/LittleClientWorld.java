package net.slimevoid.littleblocks.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fml.client.FMLClientHandler;

public class LittleClientWorld extends LittleWorld {

    public LittleClientWorld(World world, WorldProvider worldprovider, boolean isRemote) {
        super(world, worldprovider, isRemote);
    }

	@Override
	/** getRenderViewDistance()**/
	protected int getRenderDistanceChunks() {
        return FMLClientHandler.instance().getClient().gameSettings.renderDistanceChunks;
	}

}
