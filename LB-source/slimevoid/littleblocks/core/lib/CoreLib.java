package slimevoid.littleblocks.core.lib;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CoreLib {

	public static final String	MOD_ID				= "LittleBlocks";
	public static final String	MOD_RESOURCES		= "littleblocks";
	public static final String	MOD_NAME			= "Little Blocks";
	public static final String	MOD_VERSION			= "2.2.1.3";
	public static final String	MOD_DEPENDENCIES	= "required-after:SlimevoidLib";
	public static final String	MOD_CHANNEL			= "LITTLEBLOCKS";
	public static final String	CLIENT_PROXY		= "slimevoid.littleblocks.client.proxy.ClientProxy";
	public static final String	COMMON_PROXY		= "slimevoid.littleblocks.proxy.CommonProxy";
	@SideOnly(Side.CLIENT)
	public static boolean		OPTIFINE_INSTALLED	= FMLClientHandler.instance().hasOptifine();
}
