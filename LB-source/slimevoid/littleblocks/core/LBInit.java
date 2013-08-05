/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * Lesser General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package slimevoid.littleblocks.core;

import java.io.File;

import net.minecraftforge.common.Configuration;
import slimevoid.lib.core.SlimevoidCore;
import slimevoid.littleblocks.api.util.LittleBlocksHelper;
import slimevoid.littleblocks.core.lib.EnumWandAction;
import slimevoid.littleblocks.core.lib.CoreLib;

public class LBInit {
	private static boolean initialized = false;

	public static void initialize() {
		if (initialized)
			return;
		initialized = true;
		LittleBlocks.proxy.preInit();
		LBCore.configFile = new File(
				LittleBlocks.proxy.getMinecraftDir(),
					"config/LittleBlocks.cfg");
		LBCore.configuration = new Configuration(LBCore.configFile);
		load();
	}

	public static void load() {
		LittleBlocks.proxy.registerConfigurationProperties();
		
		SlimevoidCore.console(CoreLib.MOD_ID, "Registering names...");
		LBCore.registerNames();
		
		SlimevoidCore.console(CoreLib.MOD_ID, "Registering items...");
		LBCore.registerItems();
		
		SlimevoidCore.console(CoreLib.MOD_ID, "Registering blocks...");
		LBCore.registerBlocks();
		
		SlimevoidCore.console(CoreLib.MOD_ID, "Registering events...");
		LBCore.registerEvents();
		
		LittleBlocks.proxy.registerRenderInformation();
		
		LittleBlocks.proxy.registerTickHandler();
		
		SlimevoidCore.console(CoreLib.MOD_ID, "Registering recipes...");
		LBCore.registerRecipes();

		LittleBlocksHelper.init(LittleBlocks.proxy, LBCore.littleBlocksSize);
		EnumWandAction.registerWandActions();
	}
}
