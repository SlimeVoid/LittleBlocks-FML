package littleblocks.proxy;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.core.CommonTickHandler;
import littleblocks.core.LBCore;
import littleblocks.core.LBInit;
import littleblocks.network.ClientPacketHandler;
import littleblocks.network.CommonPacketHandler;
import littleblocks.network.ILBPacketHandling;
import littleblocks.network.packets.PacketLittleBlocks;
import littleblocks.network.packets.PacketTileEntityLB;
import littleblocks.render.LittleBlocksRenderer;
import littleblocks.tileentities.TileEntityLittleBlocks;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.ItemBucket;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.PlayerControllerMP;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import net.minecraft.src.EurysMods.core.EurysCore;
import net.minecraft.src.EurysMods.network.IPacketHandling;
import net.minecraft.src.EurysMods.network.packets.core.PacketIds;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

	@Override
	public String getMinecraftDir() {
		return Minecraft.getMinecraftDir().toString();
	}
	
	@Override
	public void registerRenderInformation() {
	}
	
	@Override
	public void registerTileEntitySpecialRenderer(Class<? extends TileEntity> clazz) {
		ClientRegistry.bindTileEntitySpecialRenderer(clazz, new LittleBlocksRenderer());
	}
	
	@Override
	public void displayTileEntityGui(EntityPlayer entityplayer, TileEntity tileentity) {
		if(!entityplayer.worldObj.isRemote) {
			
		}
	}
	
    @SideOnly(Side.CLIENT)
	private static Minecraft mc = ModLoader.getMinecraftInstance();


	@Override
	public void registerTickHandler() {
		TickRegistry.registerTickHandler(new CommonTickHandler(), Side.CLIENT);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockLittleBlocks block, int x, int y, int z,
			EntityPlayer entityplayer, int q, float a, float b, float c) {
		ClientPacketHandler.blockUpdate(world, entityplayer, x, y, z, q, a, b, c,
							block, LBCore.blockActivateCommand);
		return true;
	}
	
	@Override
	public void onBlockClicked(World world, BlockLittleBlocks block, int x, int y, int z, EntityPlayer entityplayer) {
		ClientPacketHandler.blockUpdate(world, entityplayer, x, y, z, 0,0,0,0,block,
				LBCore.blockClickCommand);		
	}
}
