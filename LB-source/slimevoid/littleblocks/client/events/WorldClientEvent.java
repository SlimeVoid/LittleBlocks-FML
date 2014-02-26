package slimevoid.littleblocks.client.events;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Load;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.world.LittleWorldClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WorldClientEvent {

    @ForgeSubscribe
    public void onWorldLoad(Load event) {
        if (event.world instanceof WorldClient
            && !(event.world instanceof ILittleWorld)) {
            WorldClient world = (WorldClient) event.world;
            int dimension = world.provider.dimensionId;
            WorldProvider provider = WorldProvider.getProviderForDimension(dimension);

            ConfigurationLib.littleWorldClient = new LittleWorldClient(world, world.getSaveHandler(), "LittleWorldClient", provider, new WorldSettings(world.getWorldInfo().getSeed(), world.getWorldInfo().getGameType(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().isHardcoreModeEnabled(), world.getWorldInfo().getTerrainType()), world.difficultySetting, null, null);
        }
    }

}
