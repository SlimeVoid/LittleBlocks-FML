package com.slimevoid.littleblocks.proxy;

import java.io.File;

import com.slimevoid.library.IPacketHandling;
import com.slimevoid.littleblocks.api.ILBCommonProxy;
import com.slimevoid.littleblocks.api.ILittleWorld;
import com.slimevoid.littleblocks.blocks.events.LittleContainerInteract;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;
import com.slimevoid.littleblocks.core.lib.PacketLib;
import com.slimevoid.littleblocks.events.LittleBlocksCollectionPickup;
import com.slimevoid.littleblocks.events.LittleChunkEvent;
import com.slimevoid.littleblocks.events.WorldServerEvent;
import com.slimevoid.littleblocks.network.CommonPacketHandler;
import com.slimevoid.littleblocks.tickhandlers.LittleWorldServerTickHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeDummyContainer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements ILBCommonProxy {

    @Override
    public void preInit() {
        CommonPacketHandler.init();
        PacketLib.registerPacketHandlers();
        ForgeDummyContainer.fullBoundingBoxLadders = true;
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
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
    }

    @Override
    public void registerTickHandlers() {
        TickRegistry.registerTickHandler(new LittleWorldServerTickHandler(),
                                         Side.SERVER);
    }

    @Override
    public void registerEventHandlers() {
        MinecraftForge.EVENT_BUS.register(new LittleChunkEvent());
        MinecraftForge.EVENT_BUS.register(new WorldServerEvent());
        MinecraftForge.EVENT_BUS.register(new LittleBlocksCollectionPickup());
        MinecraftForge.EVENT_BUS.register(new LittleContainerInteract());
    }

    @Override
    public IPacketHandling getPacketHandler() {
        return null;
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
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
        // System.out.println("LoggedIn");
    }

    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
        return null;
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
    }

    @Override
    public void connectionClosed(INetworkManager manager) {
    }

    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
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
