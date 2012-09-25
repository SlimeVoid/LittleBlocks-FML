package littleblocks.blocks;

import java.util.ArrayList;
import java.util.List;

import littleblocks.core.LBCore;
import littleblocks.network.ClientPacketHandler;
import littleblocks.network.CommonPacketHandler;
import littleblocks.network.LBPacketIds;
import littleblocks.network.packets.PacketLittleBlocks;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemBucket;
import net.minecraft.src.ItemInWorldManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;

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
	}

	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if (id == LBCore.littleBlocksID) {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(x, y, z);
			if (tile.isEmpty()) {
				return super.removeBlockByPlayer(world, player, x, y, z);
			} else {
				if (world.getWorldInfo().getGameType() == EnumGameType.CREATIVE) {
					this.onBlockClicked(world, x, y, z, player);
					return false;
				}
				int[][][] content = tile.getContent();
				for (int x1 = 0; x1 < content.length; x1++) {
					for (int y1 = 0; y1 < content[x1].length; y1++) {
						for (int z1 = 0; z1 < content[x1][y1].length; z1++) {
							if (content[x1][y1][z1] > 0 && Block.blocksList[content[x1][y1][z1]] != null) {
								int idDropped = Block.blocksList[content[x1][y1][z1]]
										.idDropped(tile.getMetadata(
												this.xSelected,
												this.ySelected,
												this.zSelected), world.rand, 0);
								int quantityDropped = Block.blocksList[content[x1][y1][z1]]
										.quantityDropped(world.rand);
								if (idDropped > 0 && quantityDropped > 0) {
									this.dropLittleBlockAsItem_do(
											world,
											x,
											y,
											z,
											new ItemStack(
													idDropped,
													quantityDropped,
													tile.getMetadata(
															this.xSelected,
															this.ySelected,
															this.zSelected)));
								}
							}
						}
					}
				}
			}
		}
		return world.setBlockWithNotify(x, y, z, 0);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int q, float a, float b, float c) {
		if (entityplayer.getCurrentEquippedItem() != null) {
			int itemID = entityplayer.getCurrentEquippedItem().itemID;
			Block[] blocks = Block.blocksList;
			for (int i = 0; i < blocks.length; i++) {
				if (blocks[i] != null && blocks[i].blockID == itemID) {
					Block theBlock = blocks[i];
					if (theBlock.hasTileEntity(0)) {
						entityplayer
								.addChatMessage("Sorry, you cannot place that here!");
						return false;
					}
				}
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
		if (this.xSelected == -10) {
			return true;
		}
		TileEntity tileentity = world.getBlockTileEntity(x, y, z);
		if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
			TileEntityLittleBlocks tileentitylittleblocks = (TileEntityLittleBlocks) tileentity;
			if (entityplayer instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entityplayer;

				ItemInWorldManager itemManager = player.theItemInWorldManager;
				if (itemManager.activateBlockOrUseItem(
						entityplayer,
						TileEntityLittleBlocks.getLittleWorld(world),
						entityplayer.getCurrentEquippedItem(),
						(x << 3) + this.xSelected,
						(y << 3) + this.ySelected,
						(z << 3) + this.zSelected,
						this.side,
						a,
						b,
						c)) {
					tileentitylittleblocks.onInventoryChanged();
					world.markBlockNeedsUpdate(x, y, z);
					return true;
				} else if (entityplayer.getCurrentEquippedItem() != null && entityplayer
						.getCurrentEquippedItem()
							.getItem() instanceof ItemBucket) {
					itemManager.tryUseItem(
							entityplayer,
							TileEntityLittleBlocks.getLittleWorld(world),
							entityplayer.getCurrentEquippedItem());
					tileentitylittleblocks.onInventoryChanged();
					world.markBlockNeedsUpdate(x, y, z);
					return true;
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
		boolean isCreative = false;
		if (entityplayer != null && entityplayer instanceof EntityPlayerMP) {
			if (((EntityPlayerMP) entityplayer).theItemInWorldManager
					.getGameType() == EnumGameType.CREATIVE) {
				isCreative = true;
			}
		}
		int content = tile.getContent(
				this.xSelected,
				this.ySelected,
				this.zSelected);
		int contentMeta = tile.getMetadata(
				this.xSelected,
				this.ySelected,
				this.zSelected);
		if (content > 0 && Block.blocksList[content] != null) {
			int idDropped = Block.blocksList[content].idDropped(
					contentMeta,
					world.rand,
					0);
			int quantityDropped = Block.blocksList[content]
					.quantityDropped(world.rand);
			List<ItemStack> items = Block.blocksList[content].getBlockDropped(
					world,
					x,
					y,
					z,
					contentMeta,
					0);
			ItemStack itemstack = null;
			if (items.get(0) != null) {
				itemstack = items.get(0);
			}
			if (itemstack == null) {
				itemstack = new ItemStack(
						idDropped,
						quantityDropped,
						contentMeta);
			}

			if (idDropped > 0 && quantityDropped > 0) {
				if (!isCreative) {
					if (itemstack != null) {
						this
								.dropLittleBlockAsItem_do(
										world,
										x,
										y,
										z,
										itemstack);
					}
				}
			}
			tile.setContent(this.xSelected, this.ySelected, this.zSelected, 0);
			tile.onInventoryChanged();
			world.markBlockNeedsUpdate(x, y, z);
		}
		PacketLittleBlocks packetLB = new PacketLittleBlocks(
				"UPDATECLIENT",
				x,
				y,
				z,
				0,
				0,
				0,
				0,
				this.xSelected,
				this.ySelected,
				this.zSelected,
				0,
				0);
		packetLB.setSender(LBPacketIds.SERVER);
		CommonPacketHandler.sendToAllPlayers(
				world,
				entityplayer,
				packetLB.getPacket(),
				x,
				y,
				z,
				true);
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

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
	}

	public void setBlockBoundsBasedOnSelection(World world, int x, int y, int z) {
		float m = TileEntityLittleBlocks.size;
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
			float m = TileEntityLittleBlocks.size;

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
		float m = TileEntityLittleBlocks.size;

		List<Object[]> returns = new ArrayList<Object[]>();

		for (int x = 0; x < content.length; x++) {
			for (int y = 0; y < content[x].length; y++) {
				for (int z = 0; z < content[x][y].length; z++) {
					if (content[x][y][z] > 0) {
						Block block = Block.blocksList[content[x][y][z]];
						block.collisionRayTrace(
								tile.getLittleWorld(),
								(i << 3) + x,
								(j << 3) + y,
								(k << 3) + z,
								player,
								view);
						Object[] ret = rayTraceBound(
								AxisAlignedBB.getBoundingBox(
										(x + block.minX) / m,
										(y + block.minY) / m,
										(z + block.minZ) / m,
										(x + block.maxX) / m,
										(y + block.maxY) / m,
										(z + block.maxZ) / m),
								i,
								j,
								k,
								player,
								view);
						if (ret != null) {
							returns.add(new Object[] {
									ret[0],
									ret[1],
									ret[2],
									x,
									y,
									z });
						}
					}
				}
			}
		}

		int max = TileEntityLittleBlocks.size;

		int block = world.getBlockId(i, j - 1, k); // DOWN
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int x = 0; x < max; x++) {
				for (int z = 0; z < max; z++) {
					int y = -1;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(
							x / m,
							y / m,
							z / m,
							(x + 1) / m,
							(y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] {
								ret[0],
								ret[1],
								ret[2],
								x,
								y,
								z });
					}
				}
			}
		}

		block = world.getBlockId(i, j + 1, k); // UP
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int x = 0; x < max; x++) {
				for (int z = 0; z < max; z++) {
					int y = max;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(
							x / m,
							y / m,
							z / m,
							(x + 1) / m,
							(y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] {
								ret[0],
								ret[1],
								ret[2],
								x,
								y,
								z });
					}
				}
			}
		}

		block = world.getBlockId(i - 1, j, k); // -X
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int y = 0; y < max; y++) {
				for (int z = 0; z < max; z++) {
					int x = -1;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(
							x / m,
							y / m,
							z / m,
							(x + 1) / m,
							(y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] {
								ret[0],
								ret[1],
								ret[2],
								x,
								y,
								z });
					}
				}
			}
		}

		block = world.getBlockId(i + 1, j, k); // +X
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int y = 0; y < max; y++) {
				for (int z = 0; z < max; z++) {
					int x = max;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(
							x / m,
							y / m,
							z / m,
							(x + 1) / m,
							(y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] {
								ret[0],
								ret[1],
								ret[2],
								x,
								y,
								z });
					}
				}
			}
		}

		block = world.getBlockId(i, j, k - 1); // -Z
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int y = 0; y < max; y++) {
				for (int x = 0; x < max; x++) {
					int z = -1;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(
							x / m,
							y / m,
							z / m,
							(x + 1) / m,
							(y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] {
								ret[0],
								ret[1],
								ret[2],
								x,
								y,
								z });
					}
				}
			}
		}

		block = world.getBlockId(i, j, k + 1); // +Z
		if (block > 0 && Block.blocksList[block].isOpaqueCube()) {
			for (int y = 0; y < max; y++) {
				for (int x = 0; x < max; x++) {
					int z = max;
					Object[] ret = rayTraceBound(AxisAlignedBB.getBoundingBox(
							x / m,
							y / m,
							z / m,
							(x + 1) / m,
							(y + 1) / m,
							(z + 1) / m), i, j, k, player, view);
					if (ret != null) {
						returns.add(new Object[] {
								ret[0],
								ret[1],
								ret[2],
								x,
								y,
								z });
					}
				}
			}
		}

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

	private Object[] rayTraceBound(AxisAlignedBB bound, int i, int j, int k, Vec3 player, Vec3 view) {
		Vec3 Vec32 = player.getIntermediateWithXValue(view, bound.minX);
		Vec3 Vec33 = player.getIntermediateWithXValue(view, bound.maxX);
		Vec3 Vec34 = player.getIntermediateWithYValue(view, bound.minY);
		Vec3 Vec35 = player.getIntermediateWithYValue(view, bound.maxY);
		Vec3 Vec36 = player.getIntermediateWithZValue(view, bound.minZ);
		Vec3 Vec37 = player.getIntermediateWithZValue(view, bound.maxZ);
		if (!isVecInsideYZBounds(bound, Vec32)) {
			Vec32 = null;
		}
		if (!isVecInsideYZBounds(bound, Vec33)) {
			Vec33 = null;
		}
		if (!isVecInsideXZBounds(bound, Vec34)) {
			Vec34 = null;
		}
		if (!isVecInsideXZBounds(bound, Vec35)) {
			Vec35 = null;
		}
		if (!isVecInsideXYBounds(bound, Vec36)) {
			Vec36 = null;
		}
		if (!isVecInsideXYBounds(bound, Vec37)) {
			Vec37 = null;
		}
		Vec3 Vec38 = null;
		if (Vec32 != null && (Vec38 == null || player.distanceTo(Vec32) < player
				.distanceTo(Vec38))) {
			Vec38 = Vec32;
		}
		if (Vec33 != null && (Vec38 == null || player.distanceTo(Vec33) < player
				.distanceTo(Vec38))) {
			Vec38 = Vec33;
		}
		if (Vec34 != null && (Vec38 == null || player.distanceTo(Vec34) < player
				.distanceTo(Vec38))) {
			Vec38 = Vec34;
		}
		if (Vec35 != null && (Vec38 == null || player.distanceTo(Vec35) < player
				.distanceTo(Vec38))) {
			Vec38 = Vec35;
		}
		if (Vec36 != null && (Vec38 == null || player.distanceTo(Vec36) < player
				.distanceTo(Vec38))) {
			Vec38 = Vec36;
		}
		if (Vec37 != null && (Vec38 == null || player.distanceTo(Vec37) < player
				.distanceTo(Vec38))) {
			Vec38 = Vec37;
		}
		if (Vec38 == null) {
			return null;
		}
		byte side = -1;
		if (Vec38 == Vec32) {
			side = 4;
		}
		if (Vec38 == Vec33) {
			side = 5;
		}
		if (Vec38 == Vec34) {
			side = 0;
		}
		if (Vec38 == Vec35) {
			side = 1;
		}
		if (Vec38 == Vec36) {
			side = 2;
		}
		if (Vec38 == Vec37) {
			side = 3;
		}
		return new Object[] { Vec38, side, player.distanceTo(Vec38) };
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
	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		super.onNeighborBlockChange(world, i, j, k, l);
		if (updateEveryone) {
			TileEntityLittleBlocks tile = (TileEntityLittleBlocks) world
					.getBlockTileEntity(i, j, k);
			int[][][] content = tile.getContent();
			int maX = 8, maY = 8, maZ = 8;
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

				for (int x = startX; x < maX; x++) {
					for (int y = startY; y < maY; y++) {
						for (int z = startZ; z < maZ; z++) {
							if (content[x][y][z] > 0) {
								Block.blocksList[content[x][y][z]]
										.onNeighborBlockChange(
												tile.getLittleWorld(),
												(i << 3) + x,
												(j << 3) + y,
												(k << 3) + z,
												l);
							}
						}
					}
				}
			}
		}
	}
}
