package littleblocks.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import littleblocks.api.ILBCommonProxy;
import littleblocks.blocks.core.CollisionRayTrace;
import littleblocks.client.network.ClientPacketHandler;
import littleblocks.core.LBCore;
import littleblocks.core.LBInit;
import littleblocks.items.EntityItemLittleBlocksCollection;
import littleblocks.network.CommonPacketHandler;
import littleblocks.network.packets.PacketLittleBlocksCollection;
import littleblocks.tileentities.TileEntityLittleBlocks;
import littleblocks.world.LittleWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

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
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer entityplayer, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if (id == LBCore.littleBlocksID) {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(x, y, z);
			EntityItemLittleBlocksCollection collection = new EntityItemLittleBlocksCollection(world, x, y, z);
			if (!tile.isEmpty()) {
				if (FMLCommonHandler.instance().getSide() == Side.CLIENT && ModLoader
						.getMinecraftInstance().playerController
						.isInCreativeMode()) {
					this.onBlockClicked(world, x, y, z, entityplayer);
					return false;
				} else if (entityplayer.capabilities.isCreativeMode) {
					this.onBlockClicked(world, x, y, z, entityplayer);
					return false;
				} else {
					int[][][] content = tile.getContent();
					if (collection != null) {
						for (int x1 = 0; x1 < content.length; x1++) {
							for (int y1 = 0; y1 < content[x1].length; y1++) {
								for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
									int blockId = content[x1][y1][z1];
									int contentMeta = tile.getMetadata(x1, y1, z1);
									if (blockId > 0 && Block.blocksList[blockId] != null) {
										ItemStack itemToDrop = dropLittleBlockAsNormalBlock(
												world,
												x,
												y,
												z,
												blockId,
												contentMeta);
										if (itemToDrop != null) {
											collection.addItemToDrop(itemToDrop);
										}
									}
								}
							}
						}
						tile.clearContents();
						tile.onInventoryChanged();
						world.markBlockForRenderUpdate(x, y, z);
					}
				}
			}
			if (!world.isRemote) {
				world.spawnEntityInWorld(collection);
				PacketLittleBlocksCollection packet = new PacketLittleBlocksCollection(collection);
				CommonPacketHandler.sendToAll(packet);
			}
		}
		return super.removeBlockByPlayer(world, entityplayer, x, y, z);
	}

	private ItemStack dropLittleBlockAsNormalBlock(World world, int x, int y, int z, int blockId, int metaData) {
		boolean dropsBlocks = Block.blocksList[blockId].getBlockDropped(
				world,
				x,
				y,
				z,
				metaData,
				0).size() > 0 ? true : false;
		if (dropsBlocks) {
			int idDropped = Block.blocksList[blockId].idDropped(
					metaData,
					world.rand,
					0);
			int quantityDropped = Block.blocksList[blockId]
					.quantityDropped(world.rand); 
			int damageDropped = Block.blocksList[blockId]
					.damageDropped(metaData);
			ItemStack itemstack = new ItemStack(idDropped, quantityDropped, damageDropped);
	
			if (idDropped > 0 && quantityDropped > 0) {
				return itemstack;
			}
		}
		return null;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int q, float a, float b, float c) {
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
		return true;
	}
	
	private boolean canPlayerPlaceBlock(World world, EntityPlayer entityplayer) {
		boolean denyPlacement = false;
		String placementMessage = "";
		ItemStack itemstack = entityplayer.getCurrentEquippedItem();
		if (itemstack != null) {
			Object itemHeld = itemstack.getItem();
			Block block = null;
			Item item = null;
			if (itemHeld instanceof Block) {
				block = (Block) itemHeld;
			}
			if (itemHeld instanceof Item) {
				item = (Item) itemHeld;
			}
			if (block != null) {
				if (!LBCore.isBlockAllowed(block)) {
					denyPlacement = true;
					placementMessage = LBCore.denyPlacementMessage;
				}
				if (LBCore.hasTile(block.blockID)) {
					if (!LBCore.isTileEntityAllowed(block.createTileEntity(
							world,
							0))) {
						denyPlacement = true;
						placementMessage = LBCore.denyPlacementMessage;
					}
				}
				if (block.getRenderType() == 1) {
					denyPlacement = true;
					placementMessage = LBCore.denyPlacementMessage;
				}
			}
			if (item != null) {
				if (item instanceof ItemBlock) {
					int itemBlockId = ((ItemBlock) item).getBlockID();
					if (LBCore.hasTile(itemBlockId)) {
						if (!LBCore
								.isTileEntityAllowed(Block.blocksList[itemBlockId]
										.createTileEntity(world, 0))) {
							denyPlacement = true;
							placementMessage = LBCore.denyPlacementMessage;
						}
					}
				}
				if (!LBCore.isItemAllowed(item)) {
					denyPlacement = true;
					placementMessage = LBCore.denyUseMessage;
				}
			}
		}
		if (denyPlacement) {
			entityplayer.addChatMessage(placementMessage);
			return false;
		}
		return true;
	}

	public boolean onServerBlockActivated(
			World world,
			int x, int y, int z,
			EntityPlayer entityplayer,
			int q, float a, float b, float c,
			int xSelected, int ySelected, int zSelected, int side) {
		if (entityplayer.canPlayerEdit(x, y, z, q, entityplayer.getHeldItem())) {
			if (entityplayer.getHeldItem() != null && entityplayer.getHeldItem().getItem() == LBCore.littleBlocksCopier) {
				return true;
			}
			if (canPlayerPlaceBlock(world, entityplayer)) {
				TileEntity tileentity = world.getBlockTileEntity(x, y, z);
				if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
					TileEntityLittleBlocks tile = (TileEntityLittleBlocks) tileentity;
					int xx = (x << 3) + xSelected, yy = (y << 3) + ySelected, zz = (z << 3) + zSelected;
					LittleWorld littleWorld = ((ILBCommonProxy) LBInit.LBM
							.getProxy()).getLittleWorld(world, false);
					int blockId = tile.getContent(
							xSelected,
							ySelected,
							zSelected);
					if (littleWorld != null) {
						if (entityplayer instanceof EntityPlayerMP) {
							EntityPlayerMP player = (EntityPlayerMP) entityplayer;
							ItemInWorldManager itemManager = player.theItemInWorldManager;
							if (itemManager.activateBlockOrUseItem(
									entityplayer,
									littleWorld,
									entityplayer.getCurrentEquippedItem(),
									xx,
									yy,
									zz,
									side,
									a,
									b,
									c)) {
								checkPlacement(littleWorld, entityplayer, x, y, z, q, xx, yy, zz, side);
								return true;
							}
							if (entityplayer.getCurrentEquippedItem() != null) {
								if (itemManager.tryUseItem(
										entityplayer,
										littleWorld,
										entityplayer.getCurrentEquippedItem())) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	private void checkPlacement(LittleWorld littleWorld, EntityPlayer entityplayer, int x, int y, int z, int l, int xx, int yy, int zz, int side) {
		if (side == 0) {
			--yy;
		}

		if (side == 1) {
			++yy;
		}

		if (side == 2) {
			--zz;
		}

		if (side == 3) {
			++zz;
		}

		if (side == 4) {
			--xx;
		}

		if (side == 5) {
			++xx;
		}
		Block block = Block.blocksList[littleWorld.getBlockId(xx, yy, zz)];
		if (block != null && block instanceof BlockPistonBase) {
			int newData = BlockPistonBase.determineOrientation(
					littleWorld.getRealWorld(),
					x,
					y,
					z,
					entityplayer);
			littleWorld.setBlockAndMetadataWithNotify(
					xx,
					yy,
					zz,
					block.blockID,
					newData);
		}
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entityplayer) {
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

	public void onServerBlockClicked(
			World world,
			int x, int y, int z,
			EntityPlayer entityplayer,
			int xSelected, int ySelected, int zSelected) {
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(x, y, z);
		int content = tile.getContent(
				xSelected,
				ySelected,
				zSelected);
		int contentMeta = tile.getMetadata(
				xSelected,
				ySelected,
				zSelected);
		int xx = (x << 3) + xSelected, yy = (y << 3) + ySelected, zz = (z << 3) + zSelected;
		if (content > 0 && Block.blocksList[content] != null) {
			if (!entityplayer.capabilities.isCreativeMode) {
				ItemStack itemToDrop = dropLittleBlockAsNormalBlock(
						world,
						x,
						y,
						z,
						content,
						contentMeta);
				this.dropLittleBlockAsItem_do(world, x, y, z, itemToDrop);
			}
			tile.setContent(xSelected, ySelected, zSelected, 0);
		}
	}

	public void dropLittleBlockAsItem_do(World world, int x, int y, int z, ItemStack itemStack) {
		this.dropBlockAsItem_do(world, x, y, z, itemStack);
	}

	@Override
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
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		if (LBCore.littleBlocksClip) {
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
		return null;
	}

	public void setBlockBoundsBasedOnSelection(World world, int x, int y, int z) {
		float m = LBCore.littleBlocksSize;
		if (this.xSelected == -10) {
			setBlockBounds(0f, 0f, 0f, 0f, 0f, 0f);
		} else {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(x, y, z);
			int content = tile.getContent(this.xSelected, this.ySelected, this.zSelected);
			if (content <= 0) {
				setBlockBounds(
						this.xSelected / m,
						this.ySelected / m,
						this.zSelected / m,
						(this.xSelected + 1) / m,
						(this.ySelected + 1) / m,
						(this.zSelected + 1) / m);
			} else {
				Block block = Block.blocksList[content];
				if (block != null) {
					block.setBlockBoundsBasedOnState(
							tile.getLittleWorld(),
							(x << 3) + this.xSelected,
							(y << 3) + this.ySelected,
							(z << 3) + this.zSelected);
					AxisAlignedBB bound = AxisAlignedBB.getBoundingBox(
							(this.xSelected + block.getBlockBoundsMinX()) / m,
							(this.ySelected + block.getBlockBoundsMinY()) / m,
							(this.zSelected + block.getBlockBoundsMinZ()) / m,
							(this.xSelected + block.getBlockBoundsMaxX()) / m,
							(this.ySelected + block.getBlockBoundsMaxY()) / m,
							(this.zSelected + block.getBlockBoundsMaxZ()) / m);
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
	}

	@Override
	public void addCollidingBlockToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List list, Entity entity) {
		TileEntity tileentity = world.getBlockTileEntity(x, y, z);
		if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) tileentity;

			int[][][] content = tile.getContent();
			float m = LBCore.littleBlocksSize;

			for (int xx = 0; xx < content.length; xx++) {
				for (int yy = 0; yy < content[xx].length; yy++) {
					for (int zz = 0; zz < content[xx][yy].length; zz++) {
						if (content[xx][yy][zz] > 0) {
							Block block = Block.blocksList[content[xx][yy][zz]];
							if (block != null) {
								setBlockBounds(
										(float) (xx + block.getBlockBoundsMinX()) / m,
										(float) (yy + block.getBlockBoundsMinY()) / m,
										(float) (zz + block.getBlockBoundsMinZ()) / m,
										(float) (xx + block.getBlockBoundsMaxX()) / m,
										(float) (yy + block.getBlockBoundsMaxY()) / m,
										(float) (zz + block.getBlockBoundsMaxZ()) / m);
								super.addCollidingBlockToList(
										world,
										x,
										y,
										z,
										axisalignedbb,
										list,
										entity);
							}
						}
					}
				}
			}
			setBlockBoundsBasedOnSelection(world, x, y, z);
		}
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 player, Vec3 view) {
		TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
				.getBlockTileEntity(x, y, z);

		player = player.addVector(-x, -y, -z);
		view = view.addVector(-x, -y, -z);
		if (tile == null) {
			return null;
		}

		int[][][] content = tile.getContent();

		List<Object[]> returns = new ArrayList<Object[]>();

		returns = CollisionRayTrace.rayTraceLittleBlocks(
				this,
				player,
				view,
				x,
				y,
				z,
				returns,
				content,
				tile);

		returns = CollisionRayTrace.collisionRayTracer(
				this,
				world,
				player,
				view,
				x,
				y,
				z,
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
				this.side = (Byte) min[1];
				this.xSelected = (Integer) min[3];
				this.ySelected = (Integer) min[4];
				this.zSelected = (Integer) min[5];
				if (tile.getContent(this.xSelected, this.ySelected, this.zSelected) > 0) {
					Block littleBlock = Block.blocksList[tile.getContent(
							this.xSelected,
							this.ySelected,
							this.zSelected)];
					if (littleBlock != null) {
						littleBlock.collisionRayTrace(
								tile.getLittleWorld(),
								(x << 3) + this.xSelected,
								(y << 3) + this.ySelected,
								(z << 3) + this.zSelected,
								player,
								view);
					}
				}
				setBlockBoundsBasedOnSelection(world, x, y, z);
				return new MovingObjectPosition(
						x,
							y,
							z,
							(Byte) min[1],
							((Vec3) min[0]).addVector(x, y, z));
			}
		}
		this.xSelected = -10;
		this.ySelected = -10;
		this.zSelected = -10;
		this.side = -1;
		setBlockBoundsBasedOnSelection(world, x, y, z);

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
	public boolean isProvidingWeakPower(IBlockAccess iblockaccess, int x, int y, int z, int side) {
		if (super.isProvidingWeakPower(iblockaccess, x, y, z, side)) {
			return true;
		} else {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) iblockaccess
					.getBlockTileEntity(x, y, z);
			if (tile != null) {
				int[][][] content = tile.getContent();
				int maX = tile.size, maY = tile.size, maZ = tile.size;
				int startX = 0, startY = 0, startZ = 0;
	
				switch (side) {
				case 1:
					maY = 1;
					break;
	
				case 0:
					startY = maY - 1;
					break;
	
				case 3:
					maZ = 1;
					break;
	
				case 2:
					startZ = maZ - 1;
					break;
	
				case 5:
					maX = 1;
					break;
	
				case 4:
					startX = maX - 1;
					break;
				}
	
				for (int xx = startX; xx < maX; xx++) {
					for (int yy = startY; yy < maY; yy++) {
						for (int zz = startZ; zz < maZ; zz++) {
							if (content[xx][yy][zz] > 0) {
								Block littleBlock = Block.blocksList[content[xx][yy][zz]];
								if (littleBlock != null && littleBlock
										.isProvidingWeakPower(
												tile.getLittleWorld(),
												(x << 3) + xx,
												(y << 3) + yy,
												(z << 3) + zz,
												side)) {
									return true;
								}
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
			if (tile != null) {
				int[][][] content = tile.getContent();
				int maX = tile.size, maY = tile.size, maZ = tile.size;
				int startX = 0, startY = 0, startZ = 0;
				for (int side = 0; side < 6; side++) {
					switch (side) {
					case 0:
						maY = 1;
						break;
	
					case 1:
						startY = maY - 1;
						break;
	
					case 2:
						maZ = 1;
						break;
	
					case 3:
						startZ = maZ - 1;
						break;
	
					case 4:
						maX = 1;
						break;
	
					case 5:
						startX = maX - 1;
						break;
					}
	
					for (int xx = startX; xx < maX; xx++) {
						for (int yy = startY; yy < maY; yy++) {
							for (int zz = startZ; zz < maZ; zz++) {
								if (content[xx][yy][zz] > 0) {
									Block littleBlock = Block.blocksList[content[xx][yy][zz]];
									if (littleBlock != null) {
											littleBlock.onNeighborBlockChange(
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
			} else {
				super.onNeighborBlockChange(world, x, y, z, blockId);
			}
		}
	}
}
