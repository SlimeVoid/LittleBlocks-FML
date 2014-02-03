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

import slimevoid.littleblocks.api.util.LittleBlocksHelper;
import slimevoid.littleblocks.core.lib.ConfigurationLib;
import slimevoid.littleblocks.core.lib.CoreLib;
import slimevoid.littleblocks.items.wand.EnumWandAction;
import slimevoidlib.core.SlimevoidCore;

public class LBInit {
    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        load();
    }

    public static void load() {
        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Registering names...");
        LBCore.registerNames();

        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Registering items...");
        LBCore.registerItems();

        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Registering blocks...");
        LBCore.registerBlocks();

        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Registering event handlers...");
        LittleBlocks.proxy.registerEventHandlers();

        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Registering render information...");
        LittleBlocks.proxy.registerRenderInformation();

        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Registering tick handlers...");
        LittleBlocks.proxy.registerTickHandlers();

        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Registering recipes...");
        LBCore.registerRecipes();

        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Initializing Little helper...");
        LittleBlocksHelper.init(LittleBlocks.proxy,
                                ConfigurationLib.littleBlocksSize);

        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Registering Little Wand...");
        EnumWandAction.registerWandActions();
    }
}
