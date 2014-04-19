package com.slimevoid.littleblocks.client.events;

import java.lang.reflect.Field;

import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Load;

import com.slimevoid.littleblocks.api.ILittleWorld;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;
import com.slimevoid.littleblocks.world.LittleWorldClient;

import cpw.mods.fml.client.FMLClientHandler;
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
            Field field[] = WorldClient.class.getDeclaredFields();
            field[0].setAccessible(true);

            NetClientHandler foo = null;
            try {
                foo = (NetClientHandler) field[0].get(world);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ConfigurationLib.littleWorldClient = new LittleWorldClient(foo, world, world.getSaveHandler(), "LittleWorldClient", provider, new WorldSettings(world.getWorldInfo().getSeed(), world.getWorldInfo().getGameType(), world.getWorldInfo().isMapFeaturesEnabled(), world.getWorldInfo().isHardcoreModeEnabled(), world.getWorldInfo().getTerrainType()), world.difficultySetting, null, null);
            RenderGlobal test = new RenderGlobal(FMLClientHandler.instance().getClient());
            test.theWorld = ConfigurationLib.littleWorldClient;
            ConfigurationLib.littleWorldClient.addWorldAccess(test);

        }
    }
}
