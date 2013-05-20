package slimevoid.littleblocks.core.lib;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import slimevoid.lib.core.SlimevoidCore;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.List;

public class ConfigurationLib {
	
	@SideOnly(Side.CLIENT)
	public static void ClientConfig() {
		
	}
	
	public static void CommonConfig() {
		LBCore.configuration.load();
		LBCore.littleChunkID = LBCore.configuration.get(
				Configuration.CATEGORY_BLOCK,
				"littleChunkID",
				1150).getInt();
		LBCore.littleBlocksWandID = LBCore.configuration.get(
				Configuration.CATEGORY_ITEM,
				"littleBlocksWandID",
				29999).getInt();
		LBCore.littleBlocksCollectionID = LBCore.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksCollectionID",
				EntityRegistry.findGlobalUniqueEntityId()).getInt();
		LBCore.littleBlocksClip = LBCore.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksClip",
				true).getBoolean(true);
		LBCore.littleBlocksForceUpdate = LBCore.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"littleBlocksForceUpdate",
				false).getBoolean(false);
		LBCore.renderingMethod = LBCore.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"renderingMethod",
				0).getInt();
		LBCore.renderType = RenderingRegistry.getNextAvailableRenderId();
		LBCore.loggerLevel = LBCore.configuration.get(
				Configuration.CATEGORY_GENERAL,
				"loggerLevel",
				"INFO").getString();
		
		// In Config prohibit id into littleblock for others mods prevent crash
		
				
		Property property = LBCore.configuration.get(Configuration.CATEGORY_GENERAL, "ProhibitID", "");
		property.comment = "Add the id list block or item for don't used in littleblock\nEx : Prohibit ID = 2, 4096, 100";
		LBCore.prohibitIDList = new HashSet<Integer>();
		String log = "LittleBlock List prohibit id : ";
		boolean first = true;
		for (String idStr: property.getString().split(",")) {
			try {
				int id = Integer.parseInt(idStr.trim());
				if (id != 0 && !LBCore.prohibitIDList.contains(id)) {
					LBCore.prohibitIDList.add(id);
					log += ((!first) ? ", ": "")+id; first = false;
				}
			} catch (Exception e) { }
		}
		SlimevoidCore.console(ReferenceLib.MOD_ID, log);
		
		LBCore.configuration.save();
		LoggerLittleBlocks.getInstance("LittleBlocksConfig").setFilterLevel(LBCore.loggerLevel);
	}

}
