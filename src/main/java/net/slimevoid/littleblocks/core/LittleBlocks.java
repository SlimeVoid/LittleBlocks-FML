package net.slimevoid.littleblocks.core;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.slimevoid.library.util.helpers.PacketHelper;
import net.slimevoid.littleblocks.api.ILBCommonProxy;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.slimevoid.littleblocks.core.lib.PacketLib;

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
        LBCore.preInitialize();
    }

    @EventHandler
    public void LittleBlocksInit(FMLInitializationEvent event) {
        LBCore.initialize();
        proxy.registerPacketHandlers();
    }

    @EventHandler
    public void LittleBlocksPostInit(FMLPostInitializationEvent event) {
        LBCore.postInitialize();
    }
}