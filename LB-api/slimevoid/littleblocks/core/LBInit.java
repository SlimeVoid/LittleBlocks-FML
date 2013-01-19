package slimevoid.littleblocks.core;

import java.io.File;

import net.minecraftforge.common.Configuration;
import slimevoid.lib.ICommonProxy;
import slimevoid.lib.ICore;
import slimevoid.lib.core.Core;

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
	}

	public static void load() {
		LBM.getProxy().registerRenderInformation();
		LBM.getProxy().registerTickHandler();
	}
}
