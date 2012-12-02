package littleblocks.core;

import java.io.File;

import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.registry.GameRegistry;
import eurysmods.api.ICommonProxy;
import eurysmods.api.ICore;
import eurysmods.core.Core;
import eurysmods.core.EurysCore;

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
		EurysCore.console(LBM.getModName(), "Registering items...");
		LBCore.addItems();
		GameRegistry.registerTileEntity(
				TileEntityLittleBlocks.class,
				"littleBlocks");
		LBM.getProxy().registerRenderInformation();
		LBM.getProxy().registerTileEntitySpecialRenderer(
				TileEntityLittleBlocks.class);
		LBM.getProxy().registerTickHandler();
		EurysCore.console(LBM.getModName(), "Naming items...");
		LBCore.addNames();
		EurysCore.console(LBM.getModName(), "Registering recipes...");
		LBCore.addRecipes();
	}
}
