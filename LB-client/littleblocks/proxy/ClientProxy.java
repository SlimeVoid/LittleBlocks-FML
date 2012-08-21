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

	@SideOnly(Side.CLIENT)
	private static Minecraft mc = ModLoader.getMinecraftInstance();

	@Override
	public void registerTickHandler() {
		TickRegistry.registerTickHandler(new CommonTickHandler(), Side.CLIENT);
	}

	@Override
	public boolean onBlockActivated(World world, BlockLittleBlocks block,
			int x, int y, int z, EntityPlayer entityplayer, int q, float a,
			float b, float c) {
		ClientPacketHandler.blockUpdate(world, entityplayer, x, y, z, q, a, b,
				c, block, LBCore.blockActivateCommand);
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(x, y, z);

		PlayerControllerMP playerControler = ModLoader.getMinecraftInstance().playerController;
		if (playerControler.onPlayerRightClick(entityplayer,
				tile.getLittleWorld(), entityplayer.getCurrentEquippedItem(),
				(x << 3) + block.xSelected, (y << 3) + block.ySelected,
				(z << 3) + block.zSelected, block.side,
				Vec3.createVectorHelper(a, b, c))) {
			return true;
		} else if (entityplayer.getCurrentEquippedItem() != null
				&& entityplayer.getCurrentEquippedItem().getItem() instanceof ItemBucket) {
			playerControler.sendUseItem(entityplayer, tile.getLittleWorld(),
					entityplayer.getCurrentEquippedItem());
			return true;
		}
		return false;
	}

	@Override
	public void onBlockClicked(World world, BlockLittleBlocks block, int x,
			int y, int z, EntityPlayer entityplayer) {
		ClientPacketHandler.blockUpdate(world, entityplayer, x, y, z, 0, 0, 0,
				0, block, LBCore.blockClickCommand);
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(x, y, z);

		int content = tile.getContent(block.xSelected, block.ySelected,
				block.zSelected);
		if (content > 0 && Block.blocksList[content] != null) {
			if (ModLoader.getMinecraftInstance().playerController
					.isNotCreative()) {
				int idDropped = Block.blocksList[content].idDropped(tile
						.getMetadata(block.xSelected, block.ySelected,
								block.zSelected), world.rand, 0);
				int quantityDropped = Block.blocksList[content]
						.quantityDropped(world.rand);
				if (idDropped > 0 && quantityDropped > 0) {
					block.dropLittleBlockAsItem_do(
							world,
							x,
							y,
							z,
							new ItemStack(idDropped, quantityDropped, tile
									.getMetadata(block.xSelected,
											block.ySelected, block.zSelected)));
				}
			}
		}

		tile.setContent(block.xSelected, block.ySelected, block.zSelected, 0);
	}
}
