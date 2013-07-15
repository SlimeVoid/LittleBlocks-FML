package slimevoid.littleblocks.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimevoid.lib.util.SlimevoidHelper;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.api.util.LittleBlocksHelper;
import slimevoid.littleblocks.blocks.core.CollisionRayTrace;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.IconLib;
import slimevoid.littleblocks.core.lib.MessageLib;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.items.EntityItemLittleBlocksCollection;
import slimevoid.littleblocks.items.ItemLittleBlocksWand;
import slimevoid.littleblocks.network.packets.PacketLittleBlocksCollection;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLittleChunk extends BlockContainer {

    public void registerIcons(IconRegister iconRegister) {
    	this.blockIcon = iconRegister.registerIcon(IconLib.LB_CHUNK);
    }
	
	public int xSelected = -10, ySelected = -10, zSelected = -10, side = -1;

	public boolean updateEveryone = true;

	private Class<? extends TileEntity> clazz;

	public BlockLittleChunk(int id, Class<? extends TileEntity> clazz, Material material, float hardness, boolean selfNotify) {
		super(id, material);
		this.clazz = clazz;
		setHardness(hardness);
		if (selfNotify) {
			// TODO :: setRequiresSelfNotify();
		}
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		return false;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}
	
	/**
	 * Retrieves whether or not the block the entity has collided with is a Ladder
	 * (Mainly used in Gulliver)
	 * 
	 * Credits : Tarig and Unclemion
	 */
	@Override
	public boolean isLadder(World world, int x, int y, int z, EntityLivingBase entity) {
		if (entity != null) {
			SlimevoidHelper.isLadder(world, x, y, z, entity);
		}
		return false;
	}

	@Override
    public int idPicked(World world, int x, int y, int z) {
		TileEntity tileentity = world.getBlockTileEntity(x, y, z);
		if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
			TileEntityLittleChunk tilelb = (TileEntityLittleChunk) tileentity;
			int pickedID = tilelb.getBlockID(this.xSelected, this.ySelected, this.zSelected);
			if (pickedID > 0) {
				return pickedID;
			}
		}
		return LBCore.littleBlocksWandID;
	}
	
	@Override
    public int getDamageValue(World world, int x, int y, int z) {
		TileEntity tileentity = world.getBlockTileEntity(x, y, z);
		if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
			TileEntityLittleChunk tilelb = (TileEntityLittleChunk) tileentity;
			int pickedID = tilelb.getBlockID(this.xSelected, this.ySelected, this.zSelected);
			if (pickedID > 0) {
				int xx = (x << 3) + this.xSelected,
					yy = (y << 3) + this.ySelected,
					zz = (z << 3) + this.zSelected;
				return Block.blocksList[pickedID].getDamageValue((World)tilelb.getLittleWorld(), xx, yy, zz);
			}
		}
		return 0;
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer entityplayer, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		
		if (id == LBCore.littleChunkID) {
			TileEntityLittleChunk tile = (TileEntityLittleChunk) world
					.getBlockTileEntity(x, y, z);
			EntityItemLittleBlocksCollection collection = new EntityItemLittleBlocksCollection(world, x, y, z, new ItemStack(LBCore.littleChunk));
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
					int[][][] content = tile.getContents();
					if (collection != null) {
						for (int x1 = 0; x1 < content.length; x1++) {
							for (int y1 = 0; y1 < content[x1].length; y1++) {
								for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
									int blockId = content[x1][y1][z1];
									int contentMeta = tile.getBlockMetadata(x1, y1, z1);
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
				if (!collection.isEmpty()) {
					world.spawnEntityInWorld(collection);
					PacketLittleBlocksCollection packet = new PacketLittleBlocksCollection(collection);
					PacketDispatcher.sendPacketToAllPlayers(packet.getPacket());
				}
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
		//System.out.println("Activated");
		if (world.isRemote) {
			PacketLib.blockUpdate(
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
					CommandLib.BLOCK_ACTIVATED);
		}
		return true;
	}
	
	private boolean canPlayerPlaceBlock(World world, EntityPlayer entityplayer) {
		boolean denyPlacement = false;
		String placementMessage = "";
		ItemStack itemstack = entityplayer.getCurrentEquippedItem();
		if (itemstack != null) {
			Object itemHeld = itemstack.getItem();
			ItemBlock itemBlock = null;
			Block block = null;
			Item item = null;
			if (itemHeld instanceof Block) {
				block = (Block) itemHeld;
			}
			if (itemHeld instanceof Item) {
				item = (Item) itemHeld;
			}
			if (itemHeld instanceof ItemBlock) {
				itemBlock = (ItemBlock) itemHeld;
				block = Block.blocksList[itemBlock.getBlockID()];
			}
			if (block != null) {
				if (!BlockUtil.isBlockAllowed(block)) {
					denyPlacement = true;
					placementMessage = MessageLib.DENY_PLACEMENT;
				}
				if (BlockUtil.hasTile(block.blockID)) {
					if (!BlockUtil.isTileEntityAllowed(block.createTileEntity(
							world,
							0))) {
						denyPlacement = true;
						placementMessage = MessageLib.DENY_PLACEMENT;
					}
				}
				if (block.getRenderType() == 1) {
					denyPlacement = true;
					placementMessage = MessageLib.DENY_PLACEMENT;
				}
			}
			if (item != null) {
				if (item instanceof ItemBlock) {
					int itemBlockId = ((ItemBlock) item).getBlockID();
					if (BlockUtil.hasTile(itemBlockId)) {
						if (!BlockUtil
								.isTileEntityAllowed(Block.blocksList[itemBlockId]
										.createTileEntity(world, 0))) {
							denyPlacement = true;
							placementMessage = MessageLib.DENY_PLACEMENT;
						}
					}
				}
				if (!BlockUtil.isItemAllowed(item)) {
					denyPlacement = true;
					placementMessage = MessageLib.DENY_USE;
				}
			}
		}
		if (denyPlacement) {
			entityplayer.addChatMessage(LanguageRegistry.instance().getStringLocalization(placementMessage));
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
			if (entityplayer.getHeldItem() != null && entityplayer.getHeldItem().getItem() instanceof ItemLittleBlocksWand) {
				return true;
			}
			if (canPlayerPlaceBlock(world, entityplayer)) {
				TileEntity tileentity = world.getBlockTileEntity(x, y, z);
				if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
					TileEntityLittleChunk tile = (TileEntityLittleChunk) tileentity;
					int xx = (x << 3) + xSelected, yy = (y << 3) + ySelected, zz = (z << 3) + zSelected;
					World littleWorld = (World) tile.getLittleWorld();
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

	private void checkPlacement(World littleWorld, EntityPlayer entityplayer, int x, int y, int z, int l, int xx, int yy, int zz, int side) {
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
					((ILittleWorld) littleWorld).getRealWorld(),
					x,
					y,
					z,
					entityplayer);
			littleWorld.setBlock/**.setBlockAndMetadataWithNotify**/(
					xx,
					yy,
					zz,
					block.blockID,
					newData,
					3);
		}
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entityplayer) {
		if (world.isRemote) {
			PacketLib.blockUpdate(
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
					CommandLib.BLOCK_CLICKED);
		}
	}

	public void onServerBlockClicked(
			World world,
			int x, int y, int z, int side,
			EntityPlayer entityplayer,
			int xSelected, int ySelected, int zSelected) {
		if (!entityplayer.canPlayerEdit(x, y, z, side, entityplayer.getHeldItem())) {
			return;
		}
		TileEntityLittleChunk tile = (TileEntityLittleChunk) world
				.getBlockTileEntity(x, y, z);
		int content = tile.getBlockID(
				xSelected,
				ySelected,
				zSelected);
		int xx = (x << 3) + xSelected, yy = (y << 3) + ySelected, zz = (z << 3) + zSelected;
		if (content > 0) {
			Block blockClicked = Block.blocksList[content];
			if (blockClicked != null) {
				World littleWorld = (World) tile.getLittleWorld();
				if (littleWorld != null) {
					if (entityplayer instanceof EntityPlayerMP) {
						EntityPlayerMP player = (EntityPlayerMP) entityplayer;
						BlockUtil.getLittleItemManager(player).onBlockClicked(xx, yy, zz, side);
					}
				}
			}
		}
	}

	public void dropLittleBlockAsItem_do(World world, int x, int y, int z, ItemStack itemStack) {
		this.dropBlockAsItem_do(world, x, y, z, itemStack);
	}

	@Override
	public TileEntity createNewTileEntity(World par1World) {
		try {
			return this.clazz.newInstance();
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

	private void setBlockBoundsBasedOnSelection(World world, int x, int y, int z) {
		float m = LBCore.littleBlocksSize;
		if (this.xSelected == -10) {
			setBlockBounds(0f, 0f, 0f, 0f, 0f, 0f);
		} else {
			TileEntityLittleChunk tile = (TileEntityLittleChunk) world
					.getBlockTileEntity(x, y, z);
			int content = tile.getBlockID(this.xSelected, this.ySelected, this.zSelected);
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
					if(BlockStairs.isBlockStairsID(block.blockID)) {
						block.setBlockBounds(0, 0, 0, 1, 1, 1);
					} else {
						block.setBlockBoundsBasedOnState(
								tile.getLittleWorld(),
								(x << 3) + this.xSelected,
								(y << 3) + this.ySelected,
								(z << 3) + this.zSelected);
					}
					setBlockBounds(
							(float) (this.xSelected + block.getBlockBoundsMinX()) / m,
							(float) (this.ySelected + block.getBlockBoundsMinY()) / m,
							(float) (this.zSelected + block.getBlockBoundsMinZ()) / m,
							(float) (this.xSelected + block.getBlockBoundsMaxX()) / m,
							(float) (this.ySelected + block.getBlockBoundsMaxY()) / m,
							(float) (this.zSelected + block.getBlockBoundsMaxZ()) / m);
				}
			}
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		if (LBCore.littleBlocksClip) {
			return super.getCollisionBoundingBoxFromPool(world, x, y, z);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List list, Entity entity) {
		TileEntity tileentity = world.getBlockTileEntity(x, y, z);
		if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
			TileEntityLittleChunk tile = (TileEntityLittleChunk) tileentity;

			int[][][] content = tile.getContents();
			float m = LBCore.littleBlocksSize;

			AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(
					axisalignedbb.minX * m,
					axisalignedbb.minY * m,
					axisalignedbb.minZ * m,
					axisalignedbb.maxX * m,
					axisalignedbb.maxY * m,
					axisalignedbb.maxZ * m);
			List<AxisAlignedBB> bbs = new ArrayList<AxisAlignedBB>();
			for (int xx = 0; xx < content.length; xx++) {
				for (int yy = 0; yy < content[xx].length; yy++) {
					for (int zz = 0; zz < content[xx][yy].length; zz++) {
						if (content[xx][yy][zz] > 0) {
							Block block = Block.blocksList[content[xx][yy][zz]];
							if (block != null) {
								block.addCollisionBoxesToList((World) tile.getLittleWorld(), (x << 3) + xx, (y << 3) + yy, (z << 3) + zz, bb, bbs, entity);
							}
						}
					}
				}
			}
			for(AxisAlignedBB aabb : bbs) {
				aabb.setBounds(
						aabb.minX / m,
						aabb.minY / m,
						aabb.minZ / m,
						aabb.maxX / m,
						aabb.maxY / m,
						aabb.maxZ / m);
				list.add(aabb);
			}
			setBlockBoundsBasedOnSelection(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 player, Vec3 view) {
		TileEntityLittleChunk tile = (TileEntityLittleChunk) world
				.getBlockTileEntity(x, y, z);

		if (tile == null) {
			return null;
		}

		int[][][] content = tile.getContents();

		List<MovingObjectPosition> returns = new ArrayList<MovingObjectPosition>();

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
		player = player.addVector(-x, -y, -z);
		view = view.addVector(-x, -y, -z);
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
			MovingObjectPosition min = null;
			double distMin = 0;
			boolean isLiquid = false;
			for (MovingObjectPosition ret : returns) {
				double dist = (double) ret.hitVec.squareDistanceTo(player);
				
				int retBlockID = tile.getBlockID(ret.blockX, ret.blockY, ret.blockZ);
				if (retBlockID > 0) {
					Block retBlock = Block.blocksList[retBlockID];//.isBlockSolid(tile.worldObj, ret.blockX, ret.blockY, ret.blockZ, ret.sideHit);
					isLiquid = retBlock instanceof BlockFluid;
				}
				
				if ((min == null || dist < distMin) && !isLiquid) {
					distMin = dist;
					min = ret;
				}
			}
			int littleBlockID = tile.getBlockID(this.xSelected, this.ySelected, this.zSelected);
			
			if (min != null) {
				this.side = (byte) min.sideHit;
				this.xSelected = (int) min.blockX;
				this.ySelected = (int) min.blockY;
				this.zSelected = (int) min.blockZ;
				if (littleBlockID > 0) {
					Block littleBlock = Block.blocksList[littleBlockID];
					if (littleBlock != null) {
//						littleBlock.collisionRayTrace(
//								(World) tile.getLittleWorld(),
//								(x << 3) + this.xSelected,
//								(y << 3) + this.ySelected,
//								(z << 3) + this.zSelected,
//								player,
//								view);
					}
				}
				setBlockBoundsBasedOnSelection(world, x, y, z);
				return new MovingObjectPosition(
							x,
							y,
							z,
							(byte) min.sideHit,
							((Vec3) min.hitVec).addVector(x, y, z));
			}
		}
		this.xSelected = -10;
		this.ySelected = -10;
		this.zSelected = -10;
		this.side = -1;
		setBlockBoundsBasedOnSelection(world, x, y, z);

		return null;
	}

	public MovingObjectPosition rayTraceBound(AxisAlignedBB bound, int i, int j, int k, Vec3 player, Vec3 view) {
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
		if (minX != null && (tracedBound == null || player.squareDistanceTo(minX) < player
				.squareDistanceTo(tracedBound))) {
			tracedBound = minX;
		}
		if (maxX != null && (tracedBound == null || player.squareDistanceTo(maxX) < player
				.squareDistanceTo(tracedBound))) {
			tracedBound = maxX;
		}
		if (minY != null && (tracedBound == null || player.squareDistanceTo(minY) < player
				.squareDistanceTo(tracedBound))) {
			tracedBound = minY;
		}
		if (maxY != null && (tracedBound == null || player.squareDistanceTo(maxY) < player
				.squareDistanceTo(tracedBound))) {
			tracedBound = maxY;
		}
		if (minZ != null && (tracedBound == null || player.squareDistanceTo(minZ) < player
				.squareDistanceTo(tracedBound))) {
			tracedBound = minZ;
		}
		if (maxZ != null && (tracedBound == null || player.squareDistanceTo(maxZ) < player
				.squareDistanceTo(tracedBound))) {
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
		return new MovingObjectPosition(i, j, k, side, tracedBound);
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
	public int isProvidingWeakPower(IBlockAccess iblockaccess, int x, int y, int z, int side) {
		int weakPower = super.isProvidingWeakPower(iblockaccess, x, y, z, side); 
		if (weakPower > 0) {
			return weakPower;
		} else {
			TileEntityLittleChunk tile = (TileEntityLittleChunk) iblockaccess
					.getBlockTileEntity(x, y, z);
			if (tile != null) {
				int[][][] content = tile.getContents();
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
								if (littleBlock != null) {
									return littleBlock
											.isProvidingWeakPower(
													tile.getLittleWorld(),
													(x << 3) + xx,
													(y << 3) + yy,
													(z << 3) + zz,
													side);
								}
							}
						}
					}
				}
			}
			return 0;
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		super.onNeighborBlockChange(world, x, y, z, blockId);
		if (updateEveryone) {
			TileEntityLittleChunk tile = (TileEntityLittleChunk) world
					.getBlockTileEntity(x, y, z);
			if (tile != null) {
				int[][][] content = tile.getContents();
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
													(World) tile.getLittleWorld(),
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

	@Override
	public boolean addBlockDestroyEffects(World world, int x, int y, int z,
			int meta, EffectRenderer effectRenderer) {
		int xx = (x << 3) + this.xSelected;
		int yy = (y << 3) + this.ySelected;
		int zz = (z << 3) + this.zSelected;
		World littleWorld = (World) LittleBlocks.proxy.getLittleWorld(world,
				false);
		int blockID = littleWorld.getBlockId(xx, yy, zz);
		Block block = Block.blocksList[blockID];
		int littleMeta = littleWorld.getBlockMetadata(xx, yy, zz);
		if (block != null) {
			byte b0 = 4;

			for (int j1 = 0; j1 < b0; ++j1) {
				for (int k1 = 0; k1 < b0; ++k1) {
					for (int l1 = 0; l1 < b0; ++l1) {
						double d0 = (double) x + ((double) j1 + 0.5D)
								/ (double) b0;
						double d1 = (double) y + ((double) k1 + 0.5D)
								/ (double) b0;
						double d2 = (double) z + ((double) l1 + 0.5D)
								/ (double) b0;
						effectRenderer.addEffect((new EntityDiggingFX(world,
								d0, d1, d2, d0 - (double) x - 0.5D, d1
										- (double) y - 0.5D, d2 - (double) z
										- 0.5D, block, littleMeta))
								.applyColourMultiplier(x, y, z));
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean addBlockHitEffects(World world, MovingObjectPosition target,
			EffectRenderer effectRenderer) {
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		int xx = (x << 3) + this.xSelected;
		int yy = (y << 3) + this.ySelected;
		int zz = (z << 3) + this.zSelected;
		World littleWorld = (World) LittleBlocks.proxy.getLittleWorld(world,
				false);
		int blockID = littleWorld.getBlockId(xx, yy, zz);
		Block block = Block.blocksList[blockID];
		int littleMeta = littleWorld.getBlockMetadata(xx, yy, zz);
		if (block != null) {
			float f = 0.1F;
			double d0 = (double) x
					+ world.rand.nextDouble()
					* (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double) (f * 2.0F))
					+ (double) f + block.getBlockBoundsMinX();
			double d1 = (double) y
					+ world.rand.nextDouble()
					* (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double) (f * 2.0F))
					+ (double) f + block.getBlockBoundsMinY();
			double d2 = (double) z
					+ world.rand.nextDouble()
					* (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double) (f * 2.0F))
					+ (double) f + block.getBlockBoundsMinZ();

			if (this.side == 0) {
				d1 = (double) y + block.getBlockBoundsMinY() - (double) f;
			}

			if (this.side == 1) {
				d1 = (double) y + block.getBlockBoundsMaxY() + (double) f;
			}

			if (this.side == 2) {
				d2 = (double) z + block.getBlockBoundsMinZ() - (double) f;
			}

			if (this.side == 3) {
				d2 = (double) z + block.getBlockBoundsMaxZ() + (double) f;
			}

			if (this.side == 4) {
				d0 = (double) x + block.getBlockBoundsMinX() - (double) f;
			}

			if (this.side == 5) {
				d0 = (double) x + block.getBlockBoundsMaxX() + (double) f;
			}

			effectRenderer.addEffect((new EntityDiggingFX(world, d0, d1, d2,
					0.0D, 0.0D, 0.0D, block, littleMeta))
					.applyColourMultiplier(x, y, z).multiplyVelocity(0.2F)
					.multipleParticleScaleBy(0.6F));
		}
		return true;
	}

	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		// TODO :: Correct Light Values
		return super.getLightValue(world, x, y, z);
	}
}
