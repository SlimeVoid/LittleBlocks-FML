package littleblocks.proxy;

import littleblocks.api.ILBCommonProxy;
import littleblocks.core.LBCore;
import littleblocks.tickhandlers.CommonTickHandler;
import littleblocks.world.LittleWorld;
import littleblocks.world.LittleWorldServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.DimensionManager;
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
		LBCore.littleDimensionServer = -1;
		LBCore.littleProviderTypeServer = -1;
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
		return null;
	}

	@Override
	public LittleWorld getLittleWorld(World world, boolean needsRefresh) {
		if (world != null) {
			if (LBCore.littleDimensionServer == -1) {
				LBCore.littleDimensionServer = DimensionManager.getNextFreeDimId();
				LBCore.littleProviderTypeServer = DimensionManager.getProviderType(world.provider.dimensionId);
				DimensionManager.registerDimension(LBCore.littleDimensionServer, LBCore.littleProviderTypeServer);
				LBCore.littleProviderServer = DimensionManager.createProviderFor(LBCore.littleDimensionServer);
			}
			if (LBCore.littleWorldServer == null || LBCore.littleWorldServer
					.isOutdated(world) || needsRefresh) {
				LBCore.littleWorldServer = new LittleWorldServer(
						world,
						LBCore.littleProviderServer);
			}
		}
		return LBCore.littleWorldServer;
	}

	@Override
	public World getWorld(NetHandler handler) {
		return null;
	}

	@Override
	public EntityPlayer getPlayer() {
		return null;
	}
}
