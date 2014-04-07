package net.slimevoid.littleblocks.proxy;

import java.io.File;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.slimevoid.library.core.SlimevoidCore;
import net.slimevoid.littleblocks.api.ILBCommonProxy;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.blocks.events.LittleContainerInteract;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.slimevoid.littleblocks.core.lib.PacketLib;
import net.slimevoid.littleblocks.events.LittleBlocksCollectionPickup;
import net.slimevoid.littleblocks.events.LittleChunkEvent;
import net.slimevoid.littleblocks.events.WorldServerEvent;
import net.slimevoid.littleblocks.tickhandlers.LittleWorldServerTickHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements ILBCommonProxy {

    @Override
    public void preInit() {
        ForgeModContainer.fullBoundingBoxLadders = true;
    }

    @Override
    public void init() {
        PacketLib.registerPacketHandlers();
    }

    @Override
    public void postInit() {

    }

    @Override
    public void registerRenderInformation() {
    }

    @Override
    public void registerTileEntitySpecialRenderer(Class<? extends TileEntity> clazz) {
    }

    @Override
    public String getMinecraftDir() {
        return "./";
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public void registerTickHandlers() {
        MinecraftForge.EVENT_BUS.register(new LittleWorldServerTickHandler());
    }

    @Override
    public void registerEventHandlers() {
        MinecraftForge.EVENT_BUS.register(new LittleChunkEvent());
        MinecraftForge.EVENT_BUS.register(new WorldServerEvent());
        MinecraftForge.EVENT_BUS.register(new LittleBlocksCollectionPickup());
        MinecraftForge.EVENT_BUS.register(new LittleContainerInteract());
    }

    @Override
    public ILittleWorld getLittleWorld(IBlockAccess iblockaccess, boolean needsRefresh) {
        World world = (World) iblockaccess;
        if (world != null) {
            int dimension = world.provider.dimensionId;
            if (ConfigurationLib.littleWorldServer.containsKey(dimension)) {
                World littleWorld = DimensionManager.getWorld(ConfigurationLib.littleWorldServer.get(dimension));
                if (littleWorld == null) {
                    throw new NullPointerException("A LittleWorld does not exist for reference world - "
                                                   + world.getWorldInfo().getWorldName());
                }
                if (littleWorld instanceof ILittleWorld) {
                    return (ILittleWorld) littleWorld;
                }
            }
        }
        return null;
    }

    @Override
    public void registerConfigurationProperties(File configFile) {
        ConfigurationLib.CommonConfig(configFile);
    }

    @Override
    public boolean isClient(World world) {
        return FMLCommonHandler.instance().getSide() == Side.CLIENT
               || (world != null && world.isRemote);
    }

    @Override
    public World getParentWorld(ILittleWorld littleWorld, int realDimension) {
        return DimensionManager.getWorld(realDimension);
    }
}
