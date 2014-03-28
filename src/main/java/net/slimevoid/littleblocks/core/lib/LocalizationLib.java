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
package net.slimevoid.littleblocks.core.lib;

import cpw.mods.fml.common.registry.LanguageRegistry;

public class LocalizationLib {

    private static final String LANGUAGE_PATH = "/assets/littleblocks/locale/";

    public static String[]      localeFiles   = {
            LANGUAGE_PATH + "en_US.xml",
            LANGUAGE_PATH + "nb_NO.xml"      };

    public static void registerLanguages() {
        // For every file specified in the localeFiles class, load them into the
        // Language Registry
        for (String localizationFile : localeFiles) {
            LanguageRegistry.instance().loadLocalization(localizationFile,
                                                         getLocaleFromFileName(localizationFile),
                                                         isXMLLanguageFile(localizationFile));
        }
        // System.out.println(LanguageRegistry.instance().getStringLocalization("tile.lb.littleblocks.name"));
    }

    /***
     * Simple test to determine if a specified file name represents a XML file
     * or not
     * 
     * @param fileName
     *            String representing the file name of the file in question
     * @return True if the file name represents a XML file, false otherwise
     */
    public static boolean isXMLLanguageFile(String fileName) {
        return fileName.endsWith(".xml");
    }

    /***
     * Returns the locale from file name
     * 
     * @param fileName
     *            String representing the file name of the file in question
     * @return String representation of the locale snipped from the file name
     */
    public static String getLocaleFromFileName(String fileName) {
        return fileName.substring(fileName.lastIndexOf('/') + 1,
                                  fileName.lastIndexOf('.'));
    }

    public static String getLocalizedString(String key) {
        return LanguageRegistry.instance().getStringLocalization(key);
    }

}
