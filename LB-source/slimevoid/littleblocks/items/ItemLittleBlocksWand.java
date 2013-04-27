package slimevoid.littleblocks.items;

import java.util.HashMap;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import slimevoid.lib.data.ReadWriteLock;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.core.LoggerLittleBlocks;
import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.core.lib.EnumWandAction;
import slimevoid.littleblocks.core.lib.ResourceLib;
import slimevoid.littleblocks.handlers.LittleBlocksRotationHandler;
import slimevoid.littleblocks.network.packets.PacketLittleNotify;
import slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLittleBlocksWand extends Item {

	public static HashMap<EntityPlayer, TileEntityLittleChunk> selectedLittleTiles = new HashMap<EntityPlayer, TileEntityLittleChunk>();
	public static ReadWriteLock tileLock = new ReadWriteLock();

	public ItemLittleBlocksWand(int id) {
		super(id);
		this.setNoRepair();
		this.setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l, float a, float b, float c) {
		if (!world.isRemote) {
			EnumWandAction playerWandAction = EnumWandAction.getWandActionForPlayer(entityplayer);
			if (playerWandAction != null) {
				if (playerWandAction.equals(EnumWandAction.COPY_LB)) {
					return this.doCopyLB(itemstack, entityplayer, world, x, y, z, l, a, b, c);
				}
				if (playerWandAction.equals(EnumWandAction.ROTATE_LB)) {
					return this.doRotateLB(itemstack, entityplayer, world, x, y, z, l, a, b, c);
				}	
			} else {
				return this.doPlaceLB(itemstack, entityplayer, world, x, y, z, l, a, b, c);
			}
		}
		return false;
	}

	private boolean doRotateLB(ItemStack itemstack, EntityPlayer entityplayer,
			World world, int x, int y, int z, int l, float a, float b, float c) {
		TileEntity tileentity = world.getBlockTileEntity(x, y, z);
		if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
			TileEntityLittleChunk tilelb = (TileEntityLittleChunk)tileentity;
			LittleBlocksRotationHandler tileToRotate = new LittleBlocksRotationHandler(world, entityplayer, tilelb, x, y, z, l);
			tileToRotate.rotateTile();
			return true;
		}
		return false;
	}

	private boolean doPlaceLB(ItemStack itemstack, EntityPlayer entityplayer,
			World world, int x, int y, int z, int l, float a, float b, float c) {
		if (world.getBlockId(x, y, z) != LBCore.littleChunkID) {
			System.out.println("Place");
			if (l == 0) {
				--y;
			}
	
			if (l == 1) {
				++y;
			}
	
			if (l == 2) {
				--z;
			}
	
			if (l == 3) {
				++z;
			}
	
			if (l == 4) {
				--x;
			}
	
			if (l == 5) {
				++x;
			}
			world.setBlock(x, y, z, LBCore.littleChunkID);
			TileEntity newtile = world.getBlockTileEntity(
					x,
					y,
					z);
			newtile.onInventoryChanged();
			world.markBlockForUpdate(x, y, z);
			return true;
		}
		return false;
	}

	private boolean doCopyLB(ItemStack itemstack,
			EntityPlayer entityplayer, World world, int x, int y, int z, int l,
			float a, float b, float c) {
		TileEntity tileentity = world.getBlockTileEntity(x, y, z);
		if (tileentity != null && tileentity instanceof TileEntityLittleChunk) {
			try {
				tileLock.writeLock(world);
				selectedLittleTiles.put(
						entityplayer,
						(TileEntityLittleChunk) tileentity);
				tileLock.writeUnlock();
			} catch (InterruptedException e) {
				LoggerLittleBlocks
						.getInstance("ItemLittleBlocksCopier")
							.writeStackTrace(e);
			}
			return true;
		} else if (tileentity == null) {
			try {
				tileLock.readLock(world);
				if (!selectedLittleTiles.containsKey(entityplayer)) {
					tileLock.readUnlock();
					return false;
				}
				TileEntityLittleChunk selectedLittleTile = selectedLittleTiles
						.get(entityplayer);
				tileLock.readUnlock();
				int xx = x, yy = y, zz = z;
				if (selectedLittleTile != null) {
					if (l == 0) {
						--yy;
					}

					if (l == 1) {
						++yy;
					}

					if (l == 2) {
						--zz;
					}

					if (l == 3) {
						++zz;
					}

					if (l == 4) {
						--xx;
					}

					if (l == 5) {
						++xx;
					}
					world.setBlock(xx, yy, zz, LBCore.littleChunkID);
					TileEntity newtile = world.getBlockTileEntity(
							xx,
							yy,
							zz);
					if (newtile != null && newtile instanceof TileEntityLittleChunk) {
						TileEntityLittleChunk newtilelb = (TileEntityLittleChunk) newtile;
						tileLock.readLock(world);
						TileEntityLittleChunk oldtile = selectedLittleTiles
								.get(entityplayer);
						tileLock.readUnlock();
						for (int x1 = 0; x1 < LBCore.littleBlocksSize; x1++) {
							for (int y1 = 0; y1 < LBCore.littleBlocksSize; y1++) {
								for (int z1 = 0; z1 < LBCore.littleBlocksSize; z1++) {
									if (oldtile.getBlockID(x1, y1, z1) > 0) {
										newtilelb.setBlockIDWithMetadata(
												x1,
												y1,
												z1,
												oldtile.getBlockID(
														x1,
														y1,
														z1),
												oldtile.getBlockMetadata(
														x1,
														y1,
														z1));
									}
								}
							}
						}
						newtilelb.onInventoryChanged();
						world.markBlockForUpdate(xx, yy, zz);
					}
				}
				return true;
			} catch (InterruptedException e) {
				LoggerLittleBlocks
						.getInstance("ItemLittleBlocksCopier")
							.writeStackTrace(e);
			}
		}
		PacketDispatcher.sendPacketToPlayer(
				new PacketLittleNotify(
						CommandLib.COPIER_MESSAGE).getPacket(),
						(Player) entityplayer);
		return false;
	}
}
