package littleblocks.blocks;

import java.util.ArrayList;
import java.util.List;

import littleblocks.api.ILBCommonProxy;
import littleblocks.blocks.core.CollisionRayTrace;
import littleblocks.core.LBCore;
import littleblocks.core.LBInit;
import littleblocks.network.ClientPacketHandler;
import littleblocks.tileentities.TileEntityLittleBlocks;
import littleblocks.world.LittleWorld;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemBucket;
import net.minecraft.src.ItemInWorldManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.ModLoader;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class BlockLittleBlocks extends BlockContainer {

	public int xSelected = -10, ySelected = -10, zSelected = -10, side = -1;

	public boolean updateEveryone = true;

	private Class clazz;

	public BlockLittleBlocks(int id, Class clazz, Material material, float hardness, boolean selfNotify) {
		super(id, material);
		this.clazz = clazz;
		setHardness(hardness);
		if (selfNotify) {
			setRequiresSelfNotify();
		}
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer entityplayer, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if (id == LBCore.littleBlocksID) {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(x, y, z);
			if (tile.isEmpty()) {
				return super.removeBlockByPlayer(world, entityplayer, x, y, z);
			} else {
				if (FMLCommonHandler.instance().getSide() == Side.CLIENT && ModLoader
						.getMinecraftInstance().playerController
						.isInCreativeMode()) {
					this.onBlockClicked(world, x, y, z, entityplayer);
					return false;
				} else if (isCreative(entityplayer)) {
					this.onBlockClicked(world, x, y, z, entityplayer);
					return false;
				}
				int[][][] content = tile.getContent();
				for (int x1 = 0; x1 < content.length; x1++) {
					for (int y1 = 0; y1 < content[x1].length; y1++) {
						for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
							int blockId = content[x1][y1][z1];
							int contentMeta = tile.getMetadata(x1, y1, z1);
							if (blockId > 0 && Block.blocksList[blockId] != null) {
								dropLittleBlockAsNormalBlock(
										world,
										x,
										y,
										z,
										blockId,
										contentMeta);
							}
							tile.setContent(x1, y1, z1, 0);
							tile.onInventoryChanged();
							world.markBlockNeedsUpdate(x, y, z);
						}
					}
				}
			}
		}
		return super.removeBlockByPlayer(world, entityplayer, x, y, z);
	}

	private void dropLittleBlockAsNormalBlock(World world, int x, int y, int z, int blockId, int metaData) {
		int idDropped = Block.blocksList[blockId].idDropped(
				metaData,
				world.rand,
				0);
		int quantityDropped = Block.blocksList[blockId]
				.quantityDropped(world.rand);
		List<ItemStack> items = Block.blocksList[blockId].getBlockDropped(
				world,
				x,
				y,
				z,
				metaData,
				0);
		ItemStack itemstack = null;
		if (items != null && items.size() > 0) {
			if (items.get(0) != null) {
				itemstack = items.get(0);
			}
		}
		if (itemstack == null) {
			itemstack = new ItemStack(idDropped, quantityDropped, metaData);
		}

		if (idDropped > 0 && quantityDropped > 0) {
			this.dropLittleBlockAsItem_do(world, x, y, z, itemstack);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int q, float a, float b, float c) {
		if (entityplayer.getCurrentEquippedItem() != null) {
			int itemID = entityplayer.getCurrentEquippedItem().itemID;
			Block[] blocks = Block.blocksList;
			Item[] items = Item.itemsList;
			boolean denyPlacement = false;
			String placementMessage = "";
			if (itemID < blocks.length) {
				if (LBCore.hasTile(itemID)) {
					if (!LBCore.isTileEntityAllowed(Block.blocksList[itemID]
							.createTileEntity(world, 0))) {
						denyPlacement = true;
						placementMessage = LBCore.denyBlockMessage;
					}
				}
				if (blocks[itemID] != null) {
					Block theBlock = blocks[itemID];
					if (theBlock.getRenderType() == 1) {
						denyPlacement = true;
						placementMessage = LBCore.denyBlockMessage;
					}
					if (theBlock.getBlockName() != null) {
						if (theBlock.getBlockName().equals("tile.pistonBase") || theBlock
								.getBlockName()
									.equals("tile.pistonStickyBase")) {
							// denyPlacement = true;
							// placementMessage = LBCore.denyBlockMessage;
						}
					}
				}
			} else {
				if (itemID < items.length) {
					if (Item.itemsList[itemID] instanceof ItemBlock) {
						int itemBlockId = ((ItemBlock) Item.itemsList[itemID])
								.getBlockID();
						if (LBCore.hasTile(itemBlockId)) {
							if (!LBCore
									.isTileEntityAllowed(Block.blocksList[itemBlockId]
											.createTileEntity(world, 0))) {
								denyPlacement = true;
								placementMessage = LBCore.denyBlockMessage;
							}
						}
					}
				}
			}
			if (itemID == Item.hoeDiamond.shiftedIndex || itemID == Item.hoeGold.shiftedIndex || itemID == Item.hoeSteel.shiftedIndex || itemID == Item.hoeStone.shiftedIndex || itemID == Item.hoeWood.shiftedIndex) {
				denyPlacement = true;
				placementMessage = LBCore.denyUseMessage;
			}
			if (denyPlacement) {
				if (world.isRemote) {
					entityplayer.addChatMessage(placementMessage);
				}
				return true;
			}
		}
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			if (world.isRemote) {
				ClientPacketHandler.blockUpdate(
						world,
						entityplayer,
						x,
						y,
						z,
						q,
						a,
						b,
						c,
						this,
						LBCore.blockActivateCommand);
			}
		}
		return true;
	}

	public boolean onServerBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int q, float a, float b, float c) {
		if (entityplayer.canPlayerEdit(x, y, z)) {
			TileEntity tileentity = world.getBlockTileEntity(x, y, z);
			if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
				TileEntityLittleBlocks tile = (TileEntityLittleBlocks) tileentity;
				int xx = (x << 3) + this.xSelected, yy = (y << 3) + this.ySelected, zz = (z << 3) + this.zSelected;
				LittleWorld littleWorld = ((ILBCommonProxy) LBInit.LBM
						.getProxy()).getLittleWorld(world, false);
				int blockId = tile.getContent(
						this.xSelected,
						this.ySelected,
						this.zSelected);
				if (littleWorld != null) {
					if (entityplayer instanceof EntityPlayerMP) {
						EntityPlayerMP player = (EntityPlayerMP) entityplayer;
						ItemInWorldManager itemManager = player.theItemInWorldManager;
						if (entityplayer.getCurrentEquippedItem() != null && entityplayer
								.getCurrentEquippedItem()
									.getItem() instanceof ItemBucket) {
							itemManager.tryUseItem(
									entityplayer,
									littleWorld,
									entityplayer.getCurrentEquippedItem());
							world.markBlockNeedsUpdate(x, y, z);
							return true;
						} else if (itemManager.activateBlockOrUseItem(
								entityplayer,
								littleWorld,
								entityplayer.getCurrentEquippedItem(),
								xx,
								yy,
								zz,
								this.side,
								a,
								b,
								c)) {
							world.markBlockNeedsUpdate(x, y, z);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entityplayer) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			if (world.isRemote) {
				ClientPacketHandler.blockUpdate(
						world,
						entityplayer,
						x,
						y,
						z,
						0,
						0,
						0,
						0,
						this,
						LBCore.blockClickCommand);
			}
		}
	}

	public void onServerBlockClicked(World world, int x, int y, int z, EntityPlayer entityplayer) {
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(x, y, z);
		int content = tile.getContent(
				this.xSelected,
				this.ySelected,
				this.zSelected);
		int contentMeta = tile.getMetadata(
				this.xSelected,
				this.ySelected,
				this.zSelected);
		int xx = (x << 3) + this.xSelected, yy = (y << 3) + this.ySelected, zz = (z << 3) + this.zSelected;
		if (content > 0 && Block.blocksList[content] != null) {
			if (!isCreative(entityplayer)) {
				dropLittleBlockAsNormalBlock(
						world,
						x,
						y,
						z,
						content,
						contentMeta);
			}
			tile.setContent(this.xSelected, this.ySelected, this.zSelected, 0);
		}
		world.markBlockNeedsUpdate(x, y, z);
	}

	@SideOnly(Side.CLIENT)
	private boolean isCreative(EntityPlayer entityplayer, World world) {
		if (entityplayer instanceof EntityClientPlayerMP) {
			if (ModLoader.getMinecraftInstance().playerController
					.isInCreativeMode() || world.getWorldInfo().getGameType() == EnumGameType.CREATIVE) {
				return true;
			}
		}
		return false;
	}

	private boolean isCreative(EntityPlayer entityplayer) {
		if (entityplayer.worldObj.isRemote) {
			return isCreative(entityplayer, entityplayer.worldObj);
		}
		if (entityplayer != null && entityplayer instanceof EntityPlayerMP) {
			if (((EntityPlayerMP) entityplayer).theItemInWorldManager
					.getGameType() == EnumGameType.CREATIVE) {
				return true;
			}
		}
		return false;
	}

	public void dropLittleBlockAsItem_do(World world, int x, int y, int z, ItemStack itemStack) {
		this.dropBlockAsItem_do(world, x, y, z, itemStack);
	}

	public TileEntity createNewTileEntity(World par1World) {
		try {
			return (TileEntity) this.clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getRenderType() {
		return LBCore.renderType;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		if (LBCore.littleBlocksClip) {
			return super.getCollisionBoundingBoxFromPool(world, i, j, k);
		}
		return null;
	}

	public void setBlockBoundsBasedOnSelection(World world, int x, int y, int z) {
		float m = LBCore.littleBlocksSize;
		if (xSelected == -10) {
			setBlockBounds(0f, 0f, 0f, 0f, 0f, 0f);
		} else {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(x, y, z);
			int content = tile.getContent(xSelected, ySelected, zSelected);
			if (content <= 0) {
				setBlockBounds(
						xSelected / m,
						ySelected / m,
						zSelected / m,
						(xSelected + 1) / m,
						(ySelected + 1) / m,
						(zSelected + 1) / m);
			} else {
				Block block = Block.blocksList[content];
				block.setBlockBoundsBasedOnState(
						tile.getLittleWorld(),
						(x << 3) + xSelected,
						(y << 3) + ySelected,
						(z << 3) + zSelected);
				AxisAlignedBB bound = AxisAlignedBB.getBoundingBox(
						(xSelected + block.minX) / m,
						(ySelected + block.minY) / m,
						(zSelected + block.minZ) / m,
						(xSelected + block.maxX) / m,
						(ySelected + block.maxY) / m,
						(zSelected + block.maxZ) / m);
				setBlockBounds(
						(float) bound.minX,
						(float) bound.minY,
						(float) bound.minZ,
						(float) bound.maxX,
						(float) bound.maxY,
						(float) bound.maxZ);
			}
		}
	}

	@Override
	public void addCollidingBlockToList(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {
		TileEntity tileentity = world.getBlockTileEntity(i, j, k);
		if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) tileentity;

			int[][][] content = tile.getContent();
			float m = LBCore.littleBlocksSize;

			for (int x = 0; x < content.length; x++) {
				for (int y = 0; y < content[x].length; y++) {
					for (int z = 0; z < content[x][y].length; z++) {
						if (content[x][y][z] > 0) {
							Block block = Block.blocksList[content[x][y][z]];
							setBlockBounds(
									(float) (x + block.minX) / m,
									(float) (y + block.minY) / m,
									(float) (z + block.minZ) / m,
									(float) (x + block.maxX) / m,
									(float) (y + block.maxY) / m,
									(float) (z + block.maxZ) / m);
							super.addCollidingBlockToList(
									world,
									i,
									j,
									k,
									axisalignedbb,
									list,
									entity);
						}
					}
				}
			}
			setBlockBoundsBasedOnSelection(world, i, j, k);
		}
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 player, Vec3 view) {
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(i, j, k);

		player = player.addVector(-i, -j, -k);
		view = view.addVector(-i, -j, -k);
		if (tile == null) {
			return null;
		}

		int[][][] content = tile.getContent();

		List<Object[]> returns = new ArrayList<Object[]>();

		returns = CollisionRayTrace.rayTraceLittleBlocks(
				this,
				player,
				view,
				i,
				j,
				k,
				returns,
				content,
				tile);

		returns = CollisionRayTrace.collisionRayTracer(
				this,
				world,
				player,
				view,
				i,
				j,
				k,
				returns);

		if (!returns.isEmpty()) {
			Object[] min = null;
			double distMin = 0;
			for (Object[] ret : returns) {
				double dist = (Double) ret[2];
				if (min == null || dist < distMin) {
					distMin = dist;
					min = ret;
				}
			}
			if (min != null) {
				side = (Byte) min[1];
				xSelected = (Integer) min[3];
				ySelected = (Integer) min[4];
				zSelected = (Integer) min[5];
				if (tile.getContent(xSelected, ySelected, zSelected) > 0) {
					Block.blocksList[tile.getContent(
							xSelected,
							ySelected,
							zSelected)].collisionRayTrace(
							tile.getLittleWorld(),
							(i << 3) + xSelected,
							(j << 3) + ySelected,
							(k << 3) + zSelected,
							player,
							view);
				}
				setBlockBoundsBasedOnSelection(world, i, j, k);
				return new MovingObjectPosition(
						i,
						j,
						k,
						(Byte) min[1],
						((Vec3) min[0]).addVector(i, j, k));
			}
		}
		xSelected = -10;
		ySelected = -10;
		zSelected = -10;
		side = -1;
		setBlockBoundsBasedOnSelection(world, i, j, k);

		return null;
	}

	public Object[] rayTraceBound(AxisAlignedBB bound, int i, int j, int k, Vec3 player, Vec3 view) {
		Vec3 minX = player.getIntermediateWithXValue(view, bound.minX);
		Vec3 maxX = player.getIntermediateWithXValue(view, bound.maxX);
		Vec3 minY = player.getIntermediateWithYValue(view, bound.minY);
		Vec3 maxY = player.getIntermediateWithYValue(view, bound.maxY);
		Vec3 minZ = player.getIntermediateWithZValue(view, bound.minZ);
		Vec3 maxZ = player.getIntermediateWithZValue(view, bound.maxZ);
		if (!isVecInsideYZBounds(bound, minX)) {
			minX = null;
		}
		if (!isVecInsideYZBounds(bound, maxX)) {
			maxX = null;
		}
		if (!isVecInsideXZBounds(bound, minY)) {
			minY = null;
		}
		if (!isVecInsideXZBounds(bound, maxY)) {
			maxY = null;
		}
		if (!isVecInsideXYBounds(bound, minZ)) {
			minZ = null;
		}
		if (!isVecInsideXYBounds(bound, maxZ)) {
			maxZ = null;
		}
		Vec3 tracedBound = null;
		if (minX != null && (tracedBound == null || player.distanceTo(minX) < player
				.distanceTo(tracedBound))) {
			tracedBound = minX;
		}
		if (maxX != null && (tracedBound == null || player.distanceTo(maxX) < player
				.distanceTo(tracedBound))) {
			tracedBound = maxX;
		}
		if (minY != null && (tracedBound == null || player.distanceTo(minY) < player
				.distanceTo(tracedBound))) {
			tracedBound = minY;
		}
		if (maxY != null && (tracedBound == null || player.distanceTo(maxY) < player
				.distanceTo(tracedBound))) {
			tracedBound = maxY;
		}
		if (minZ != null && (tracedBound == null || player.distanceTo(minZ) < player
				.distanceTo(tracedBound))) {
			tracedBound = minZ;
		}
		if (maxZ != null && (tracedBound == null || player.distanceTo(maxZ) < player
				.distanceTo(tracedBound))) {
			tracedBound = maxZ;
		}
		if (tracedBound == null) {
			return null;
		}
		byte side = -1;
		if (tracedBound == minX) {
			side = 4;
		}
		if (tracedBound == maxX) {
			side = 5;
		}
		if (tracedBound == minY) {
			side = 0;
		}
		if (tracedBound == maxY) {
			side = 1;
		}
		if (tracedBound == minZ) {
			side = 2;
		}
		if (tracedBound == maxZ) {
			side = 3;
		}
		return new Object[] { tracedBound, side, player.distanceTo(tracedBound) };
	}

	private boolean isVecInsideYZBounds(AxisAlignedBB bound, Vec3 Vec3) {
		if (Vec3 == null) {
			return false;
		} else {
			return Vec3.yCoord >= bound.minY && Vec3.yCoord <= bound.maxY && Vec3.zCoord >= bound.minZ && Vec3.zCoord <= bound.maxZ;
		}
	}

	private boolean isVecInsideXZBounds(AxisAlignedBB bound, Vec3 Vec3) {
		if (Vec3 == null) {
			return false;
		} else {
			return Vec3.xCoord >= bound.minX && Vec3.xCoord <= bound.maxX && Vec3.zCoord >= bound.minZ && Vec3.zCoord <= bound.maxZ;
		}
	}

	private boolean isVecInsideXYBounds(AxisAlignedBB bound, Vec3 Vec3) {
		if (Vec3 == null) {
			return false;
		} else {
			return Vec3.xCoord >= bound.minX && Vec3.xCoord <= bound.maxX && Vec3.yCoord >= bound.minY && Vec3.yCoord <= bound.maxY;
		}
	}

	@Override
	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		if (super.isPoweringTo(iblockaccess, i, j, k, l)) {
			return true;
		} else {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) iblockaccess
					.getBlockTileEntity(i, j, k);

			int[][][] content = tile.getContent();
			int maX = 8, maY = 8, maZ = 8;
			int startX = 0, startY = 0, startZ = 0;

			switch (l) {
			case 1:
				maY = 1;
				break;

			case 0:
				startY = 7;
				break;

			case 3:
				maZ = 1;
				break;

			case 2:
				startZ = 7;
				break;

			case 5:
				maX = 1;
				break;

			case 4:
				startX = 7;
				break;
			}

			for (int x = startX; x < maX; x++) {
				for (int y = startY; y < maY; y++) {
					for (int z = startZ; z < maZ; z++) {
						if (content[x][y][z] > 0) {
							if (Block.blocksList[content[x][y][z]]
									.isPoweringTo(
											tile.getLittleWorld(),
											(i << 3) + x,
											(j << 3) + y,
											(k << 3) + z,
											l)) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		super.onNeighborBlockChange(world, x, y, z, blockId);
		if (updateEveryone) {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(x, y, z);
			int[][][] content = tile.getContent();
			int maX = tile.size, maY = tile.size, maZ = tile.size;
			int startX = 0, startY = 0, startZ = 0;
			for (int side = 0; side < 6; side++) {
				switch (side) {
				case 0:
					maY = 1;
					break;

				case 1:
					startY = 7;
					break;

				case 2:
					maZ = 1;
					break;

				case 3:
					startZ = 7;
					break;

				case 4:
					maX = 1;
					break;

				case 5:
					startX = 7;
					break;
				}

				for (int xx = startX; xx < maX; xx++) {
					for (int yy = startY; yy < maY; yy++) {
						for (int zz = startZ; zz < maZ; zz++) {
							if (content[xx][yy][zz] > 0) {
								Block.blocksList[content[xx][yy][zz]]
										.onNeighborBlockChange(
												tile.getLittleWorld(),
												(x << 3) + xx,
												(y << 3) + yy,
												(z << 3) + zz,
												blockId);
							}
						}
					}
				}
			}
		}
	}
}
