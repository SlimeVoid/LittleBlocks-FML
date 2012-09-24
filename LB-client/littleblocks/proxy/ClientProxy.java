package littleblocks.proxy;

import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.core.CommonTickHandler;
import littleblocks.core.LBCore;
import littleblocks.network.ClientPacketHandler;
import littleblocks.render.BlockLittleBlocksRenderer;
import littleblocks.render.TileEntityLittleBlocksRenderer;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBucket;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.src.PlayerControllerMP;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.registry.TickRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public String getMinecraftDir() {
		return Minecraft.getMinecraftDir().toString();
	}

	@Override
	public void registerRenderInformation() {
		RenderingRegistry.registerBlockHandler(new BlockLittleBlocksRenderer());
	}

	@Override
	public void registerTileEntitySpecialRenderer(
			Class<? extends TileEntity> clazz) {
		ClientRegistry.bindTileEntitySpecialRenderer(clazz,
				new TileEntityLittleBlocksRenderer());
	}

	@Override
	public void displayTileEntityGui(EntityPlayer entityplayer,
			TileEntity tileentity) {
	}

	private static Minecraft mc = ModLoader.getMinecraftInstance();

	@Override
	public void registerTickHandler() {
		TickRegistry.registerTickHandler(new CommonTickHandler(), Side.CLIENT);
	}
}
