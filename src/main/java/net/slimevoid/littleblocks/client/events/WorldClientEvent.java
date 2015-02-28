package net.slimevoid.littleblocks.client.events;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.world.LittleWorldClient;

@SideOnly(Side.CLIENT)
public class WorldClientEvent {

    @SubscribeEvent
    public void onWorldLoad(Load event) {
        if (event.world instanceof WorldClient
            && !(event.world instanceof ILittleWorld)) {
            WorldClient world = (WorldClient) event.world;
            int dimension = world.provider.dimensionId;
            WorldProvider provider = WorldProvider.getProviderForDimension(dimension);

            ConfigurationLib.littleWorldClient = new LittleWorldClient(world, world.getSaveHandler(), "LittleWorldClient", provider, new WorldSettings(world.getWorldInfo().getSeed(), world.getWorldInfo().getGameType(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().isHardcoreModeEnabled(), world.getWorldInfo().getTerrainType()), world.difficultySetting.getDifficultyId(), null);
        }
    }

}
