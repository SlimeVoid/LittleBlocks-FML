package net.slimevoid.littleblocks.core;

import net.slimevoid.library.util.helpers.PacketHelper;
import net.slimevoid.littleblocks.api.ILBCommonProxy;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.slimevoid.littleblocks.core.lib.PacketLib;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = CoreLib.MOD_ID,
        name = CoreLib.MOD_NAME,
        version = CoreLib.MOD_VERSION,
        dependencies = CoreLib.MOD_DEPENDENCIES)
public class LittleBlocks {
    @SidedProxy(
            clientSide = CoreLib.CLIENT_PROXY,
            serverSide = CoreLib.COMMON_PROXY)
    public static ILBCommonProxy proxy;

    @Instance(CoreLib.MOD_ID)
    public static LittleBlocks   instance;

    @EventHandler
    public void LittleBlocksPreInit(FMLPreInitializationEvent event) {
        proxy.registerConfigurationProperties(event.getSuggestedConfigurationFile());
        proxy.preInit();
        LBInit.preInitialize();
    }

    @EventHandler
    public void LittleBlocksInit(FMLInitializationEvent event) {
        proxy.init();
        PacketHelper.registerHandler(CoreLib.MOD_CHANNEL,
                                     PacketLib.handler);
        LBInit.initialize();
    }

    @EventHandler
    public void LittleBlocksPostInit(FMLPostInitializationEvent event) {
        proxy.postInit();
        LBInit.postInitialize();
    }
}