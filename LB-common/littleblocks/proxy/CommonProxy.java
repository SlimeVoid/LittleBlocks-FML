package littleblocks.proxy;

import littleblocks.api.ILBCommonProxy;
import littleblocks.blocks.BlockLittleBlocks;
import littleblocks.core.CommonTickHandler;
import littleblocks.network.CommonPacketHandler;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.ItemBucket;
import net.minecraft.src.ItemInWorldManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.EurysMods.api.IPacketHandling;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;

public class CommonProxy implements ILBCommonProxy {

	@Override
	public void registerRenderInformation() {
	}

	@Override
	public void registerTileEntitySpecialRenderer(
			Class<? extends TileEntity> clazz) {
	}

	@Override
	public void displayTileEntityGui(EntityPlayer entityplayer,
			TileEntity tileentity) {
	}

	@Override
	public String getMinecraftDir() {
		return "./";
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

	@Override
	public int getBlockTextureFromMetadata(int par2) {
		return 0;
	}

	@Override
	public void onPacketData(NetworkManager manager,
			Packet250CustomPayload packet, Player player) {
	}

	@Override
	public void registerTickHandler() {
		TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
	}

	@Override
	public boolean onBlockActivated(World world, BlockLittleBlocks block,
			int x, int y, int z, EntityPlayer entityplayer, int q, float a,
			float b, float c) {
		if (entityplayer.getCurrentEquippedItem() != null) {
			int itemID = entityplayer.getCurrentEquippedItem().itemID;
			Block[] blocks = Block.blocksList;
			for (int i = 0; i < blocks.length; i++) {
				if (blocks[i] != null && blocks[i].blockID == itemID) {
					Block theBlock = blocks[i];
					if (theBlock.hasTileEntity(0)) {
						return false;
					}
				}
			}
		}
		if (block.xSelected == -10) {
			return true;
		}
		TileEntity tileentity = world.getBlockTileEntity(x, y, z);
		if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
			TileEntityLittleBlocks tileEntityLittleBlocks = (TileEntityLittleBlocks) tileentity;
			EntityPlayerMP player = (EntityPlayerMP) entityplayer;

			ItemInWorldManager itemManager = player.theItemInWorldManager;
			if (itemManager.activateBlockOrUseItem(entityplayer,
					TileEntityLittleBlocks.getLittleWorld(world),
					entityplayer.getCurrentEquippedItem(), (x << 3)
							+ block.xSelected, (y << 3) + block.ySelected,
					(z << 3) + block.zSelected, block.side, a, b, c)) {
				tileEntityLittleBlocks.onInventoryChanged();
				world.markBlockNeedsUpdate(x, y, z);
				return true;
			} else if (entityplayer.getCurrentEquippedItem() != null
					&& entityplayer.getCurrentEquippedItem().getItem() instanceof ItemBucket) {
				itemManager.tryUseItem(entityplayer,
						TileEntityLittleBlocks.getLittleWorld(world),
						entityplayer.getCurrentEquippedItem());
				tileEntityLittleBlocks.onInventoryChanged();
				world.markBlockNeedsUpdate(x, y, z);
				return true;
			}
		}
		return false;
	}

	@Override
	public IPacketHandling getPacketHandler() {
		return null;
	}

	@Override
	public void onBlockClicked(World world, BlockLittleBlocks block, int x,
			int y, int z, EntityPlayer entityplayer) {
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(x, y, z);

		EntityPlayerMP player = (EntityPlayerMP) entityplayer;

		int content = tile.getContent(block.xSelected, block.ySelected,
				block.zSelected);
		if (content > 0 && Block.blocksList[content] != null) {
			if (player.theItemInWorldManager.getGameType() != EnumGameType.CREATIVE) {
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
		tile.onInventoryChanged();
		world.markBlockNeedsUpdate(x, y, z);
		CommonPacketHandler.sendToAll(tile.getPacketUpdate());
	}
}
