package com.slimevoid.littleblocks.client.proxy;

import java.io.File;

import com.slimevoid.littleblocks.api.ILittleWorld;
import com.slimevoid.littleblocks.blocks.events.LittleChunkBucketEvent;
import com.slimevoid.littleblocks.blocks.events.LittleChunkShiftRightClick;
import com.slimevoid.littleblocks.client.events.RenderLittleChunkHighlight;
import com.slimevoid.littleblocks.client.events.WorldClientEvent;
import com.slimevoid.littleblocks.client.handlers.DrawCopierHighlight;
import com.slimevoid.littleblocks.client.handlers.KeyBindingHandler;
import com.slimevoid.littleblocks.client.network.ClientPacketHandler;
import com.slimevoid.littleblocks.client.render.blocks.LittleBlocksRenderer;
import com.slimevoid.littleblocks.client.render.entities.LittleBlocksCollectionRenderer;
import com.slimevoid.littleblocks.client.render.tileentities.TileEntityLittleBlocksRenderer;
import com.slimevoid.littleblocks.core.lib.BlockUtil;
import com.slimevoid.littleblocks.core.lib.CommandLib;
import com.slimevoid.littleblocks.core.lib.ConfigurationLib;
import com.slimevoid.littleblocks.core.lib.PacketLib;
import com.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksSettings;
import com.slimevoid.littleblocks.proxy.CommonProxy;
import com.slimevoid.littleblocks.tickhandlers.LittleWorldTickHandler;
import com.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import com.slimevoid.littleblocks.world.LittlePlayerController;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        ClientPacketHandler.init();
        PacketLib.registerClientPacketHandlers();
    }

    @Override
    public String getMinecraftDir() {
        return Minecraft.getMinecraft().mcDataDir.toString();
    }

    @Override
    public void registerRenderInformation() {
        MinecraftForge.EVENT_BUS.register(new DrawCopierHighlight());
        RenderingRegistry.registerBlockHandler(new LittleBlocksRenderer());
        RenderingRegistry.registerEntityRenderingHandler(EntityItemLittleBlocksCollection.class,
                                                         new LittleBlocksCollectionRenderer());
        this.registerTileEntitySpecialRenderer(TileEntityLittleChunk.class);
    }

    @Override
    public void registerTileEntitySpecialRenderer(Class<? extends TileEntity> clazz) {
        ClientRegistry.bindTileEntitySpecialRenderer(clazz,
                                                     new TileEntityLittleBlocksRenderer());
    }

    @Override
    public void registerTickHandlers() {
        TickRegistry.registerTickHandler(new LittleWorldTickHandler(),
                                         Side.CLIENT);
        KeyBindingRegistry.registerKeyBinding(new KeyBindingHandler());
        super.registerTickHandlers();
    }

    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();
        MinecraftForge.EVENT_BUS.register(new RenderLittleChunkHighlight());
        MinecraftForge.EVENT_BUS.register(new WorldClientEvent());
        MinecraftForge.EVENT_BUS.register(new LittleChunkShiftRightClick());
        MinecraftForge.EVENT_BUS.register(new LittleChunkBucketEvent());
    }

    public ILittleWorld getLittleWorld(IBlockAccess iblockaccess, boolean needsRefresh) {
        World world = (World) iblockaccess;
        if (world != null) {
            if (world.isRemote) {
                return ConfigurationLib.littleWorldClient;
            } else {
                return super.getLittleWorld(world,
                                            needsRefresh);
            }
        }
        return null;
    }

    @Override
    public void registerConfigurationProperties(File configFile) {
        super.registerConfigurationProperties(configFile);
        ConfigurationLib.ClientConfig(configFile);
    }

    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
        BlockUtil.setLittleController(new LittlePlayerController(FMLClientHandler.instance().getClient(), (NetClientHandler) clientHandler),
                                      login.gameType);
        World world = ((NetClientHandler) clientHandler).getPlayer().worldObj;
        if (world != null) {
            PacketLittleBlocksSettings packet = new PacketLittleBlocksSettings();
            packet.setCommand(CommandLib.FETCH);
            PacketDispatcher.sendPacketToServer(packet.getPacket());
        }
    }

    @Override
    public World getParentWorld(ILittleWorld littleWorld, int realDimension) {
        if (!((World) littleWorld).isRemote) {
            return super.getParentWorld(littleWorld,
                                      realDimension);
        }
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
