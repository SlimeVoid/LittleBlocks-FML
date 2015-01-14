package net.slimevoid.littleblocks.world;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

public class LittleClientWorld extends LittleWorld {

    public LittleClientWorld(World world, WorldProvider worldprovider, String worldName) {
        super(world, worldprovider, worldName);
    }

	@Override
	/** getRenderViewDistance()**/
	protected int func_152379_p() {
        return FMLClientHandler.instance().getClient().gameSettings.renderDistanceChunks;
	}

}
