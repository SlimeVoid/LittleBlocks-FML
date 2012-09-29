package littleblocks.proxy;

import littleblocks.api.ILBCommonProxy;
import littleblocks.core.LBCore;
import littleblocks.tickhandlers.CommonTickHandler;
import littleblocks.world.LittleWorld;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import eurysmods.api.IPacketHandling;

public class CommonProxy implements ILBCommonProxy {

	@Override
	public void registerRenderInformation() {
	}

	@Override
	public void registerTileEntitySpecialRenderer(Class<? extends TileEntity> clazz) {
	}

	@Override
	public void displayTileEntityGui(EntityPlayer entityplayer, TileEntity tileentity) {
	}

	@Override
	public String getMinecraftDir() {
		return "./";
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public int getBlockTextureFromMetadata(int par2) {
		return 0;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		return getBlockTextureFromMetadata(meta);
	}

	@Override
	public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player) {
	}

	@Override
	public void registerTickHandler() {
		TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
	}

	@Override
	public IPacketHandling getPacketHandler() {
		return null;
	}

	@Override
	public void login(NetHandler handler, NetworkManager manager, Packet1Login login) {
	}

	@Override
	public World getWorld() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public LittleWorld getLittleWorld(World world, boolean needsRefresh) {
		if (world != null) {
			if (LBCore.littleWorldServer == null || LBCore.littleWorldServer.isOutdated(world) || needsRefresh) {
				LBCore.littleWorldServer = new LittleWorld(world, world.provider);
			}
		}
		return LBCore.littleWorldServer;
	}

	@Override
	public World getWorld(NetHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityPlayer getPlayer() {
		// TODO Auto-generated method stub
		return null;
	}
}
