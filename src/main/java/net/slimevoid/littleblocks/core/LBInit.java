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
package net.slimevoid.littleblocks.core;

import net.slimevoid.library.core.SlimevoidCore;
import net.slimevoid.littleblocks.api.util.LittleBlocksHelper;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.CoreLib;
import net.slimevoid.littleblocks.items.wand.EnumWandAction;

public class LBInit {
    private static boolean initialized = false;

    public static void preInitialize() {
        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Registering names...");
        LBCore.registerNames();

        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Registering items...");
        LBCore.registerItems();

        SlimevoidCore.console(CoreLib.MOD_ID,
                              "Registering blocks...");
        LBCore.registerBlocks();
    }

    public static void initialize() {
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

    public static void postInitialize() {
        if (initialized) {
            return;
        }
        initialized = true;
    }
}
