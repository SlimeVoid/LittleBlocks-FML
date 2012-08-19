package littleblocks.core;

import java.io.File;

import littleblocks.proxy.ILBCommonProxy;
import littleblocks.tileentities.TileEntityLittleBlocks;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.src.EurysMods.core.Core;
import net.minecraft.src.EurysMods.core.EurysCore;
import net.minecraft.src.EurysMods.core.ICore;
import net.minecraft.src.EurysMods.proxy.ICommonProxy;
import net.minecraftforge.common.Configuration;

public class LBInit {
	public static ICore LBM;
	private static boolean initialized = false;

	public static void initialize(ICommonProxy proxy) {
		if (initialized)
			return;
		LBM = new Core(proxy);
		LBM.setModName("LittleBlocksMod");
		LBM.setModChannel("LITTLEBLOCKS");
		LBCore.configFile = new File(LBM.getProxy().getMinecraftDir(),
				"config/MultiTexturedSigns.cfg");
		LBCore.configuration = new Configuration(LBCore.configFile);
		load();
	}

	public static void load() {
		EurysCore.console(LBM.getModName(), "Registering items...");
		LBCore.addItems();
		GameRegistry.registerTileEntity(TileEntityLittleBlocks.class, "littleBlocks");
		LBM.getProxy().registerRenderInformation(); 
		LBM.getProxy().registerTileEntitySpecialRenderer(TileEntityLittleBlocks.class);
		((ILBCommonProxy)LBM.getProxy()).registerTickHandler();
		EurysCore.console(LBM.getModName(), "Naming items...");
		LBCore.addNames();
		EurysCore.console(LBM.getModName(), "Registering recipes...");
		LBCore.addRecipes();
	}
}
