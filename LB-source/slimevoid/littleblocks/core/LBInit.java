package slimevoid.littleblocks.core;

import java.io.File;

import net.minecraftforge.common.Configuration;
import slimevoid.lib.ICommonProxy;
import slimevoid.lib.ICore;
import slimevoid.lib.core.Core;
import slimevoid.lib.core.SlimevoidCore;
import slimevoid.littleblocks.tileentities.TileEntityLittleBlocks;
import cpw.mods.fml.common.registry.GameRegistry;

public class LBInit {
	public static ICore LBM;
	private static boolean initialized = false;

	public static void initialize(ICommonProxy proxy) {
		if (initialized)
			return;
		initialized = true;
		LBM = new Core(proxy);
		LBM.setModName("LittleBlocks");
		LBM.setModChannel("LITTLEBLOCKS");
		LBCore.configFile = new File(
				LBM.getProxy().getMinecraftDir(),
					"config/LittleBlocks.cfg");
		LBCore.configuration = new Configuration(LBCore.configFile);
		load();
	}

	public static void load() {
		LBM.getProxy().preInit();
		SlimevoidCore.console(LBM.getModName(), "Registering items...");
		LBCore.addItems();
		GameRegistry.registerTileEntity(
				TileEntityLittleBlocks.class,
				"littleBlocks");
		LBM.getProxy().registerRenderInformation();
		LBM.getProxy().registerTileEntitySpecialRenderer(
				TileEntityLittleBlocks.class);
		LBM.getProxy().registerTickHandler();
		SlimevoidCore.console(LBM.getModName(), "Naming items...");
		LBCore.addNames();
		SlimevoidCore.console(LBM.getModName(), "Registering recipes...");
		LBCore.addRecipes();
	}
}
