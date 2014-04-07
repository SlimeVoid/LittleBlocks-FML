package net.slimevoid.littleblocks.client.proxy;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.slimevoid.library.core.SlimevoidCore;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.blocks.events.LittleChunkBucketEvent;
import net.slimevoid.littleblocks.blocks.events.LittleChunkShiftRightClick;
import net.slimevoid.littleblocks.client.events.RenderLittleChunkHighlight;
import net.slimevoid.littleblocks.client.events.WorldClientEvent;
import net.slimevoid.littleblocks.client.handlers.DrawCopierHighlight;
import net.slimevoid.littleblocks.client.handlers.KeyBindingHandler;
import net.slimevoid.littleblocks.client.render.blocks.LittleBlocksRenderer;
import net.slimevoid.littleblocks.client.render.entities.LittleBlocksCollectionRenderer;
import net.slimevoid.littleblocks.client.render.tileentities.TileEntityLittleBlocksRenderer;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.slimevoid.littleblocks.core.lib.PacketLib;
import net.slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import net.slimevoid.littleblocks.proxy.CommonProxy;
import net.slimevoid.littleblocks.tickhandlers.LittleWorldTickHandler;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
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
        MinecraftForge.EVENT_BUS.register(new LittleWorldTickHandler());
        MinecraftForge.EVENT_BUS.register(new KeyBindingHandler());
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
    public World getParentWorld(ILittleWorld littleWorld, int realDimension) {
        if (!((World) littleWorld).isRemote) {
            return super.getParentWorld(littleWorld,
                                        realDimension);
        }
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
