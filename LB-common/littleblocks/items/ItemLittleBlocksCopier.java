package littleblocks.items;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import eurysmods.data.ReadWriteLock;

import littleblocks.core.LBCore;
import littleblocks.core.LBInit;
import littleblocks.core.LoggerLittleBlocks;
import littleblocks.tileentities.TileEntityLittleBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ItemLittleBlocksCopier extends Item {
	
	private HashMap<EntityPlayer, TileEntityLittleBlocks> selectedLittleTiles = new HashMap();
	private ReadWriteLock tileLock = new ReadWriteLock();

	public ItemLittleBlocksCopier(int id) {
		super(id);
		this.setNoRepair();
		this.setCreativeTab(CreativeTabs.tabTools);
	}
	
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l, float a, float b, float c) {
		if (!world.isRemote) {
			TileEntity tileentity = world.getBlockTileEntity(x, y, z);
			if (tileentity != null && tileentity instanceof TileEntityLittleBlocks) {
				try {
					tileLock.writeLock(world);
					selectedLittleTiles.put(entityplayer, (TileEntityLittleBlocks)tileentity);
					tileLock.writeUnlock();
				} catch (InterruptedException e) {
					LoggerLittleBlocks.getInstance("ItemLittleBlocksCopier").writeStackTrace(e);
				}
				return true;
			} else if (tileentity == null) {
				try {
					tileLock.readLock(world);
					if (!selectedLittleTiles.containsKey(entityplayer)) {
						tileLock.readUnlock();
						return false;
					}
					TileEntityLittleBlocks selectedLittleTile = selectedLittleTiles.get(entityplayer);
					tileLock.readUnlock();
					if (selectedLittleTile != null) {
			            if (l == 0)
			            {
			                --y;
			            }
	
			            if (l == 1)
			            {
			                ++y;
			            }
	
			            if (l == 2)
			            {
			                --z;
			            }
	
			            if (l == 3)
			            {
			                ++z;
			            }
	
			            if (l == 4)
			            {
			                --x;
			            }
	
			            if (l == 5)
			            {
			                ++x;
			            }
			            world.setBlock(x, y, z, LBCore.littleBlocksID);
			            TileEntity newtile = world.getBlockTileEntity(x, y, z);
			            if (newtile != null && newtile instanceof TileEntityLittleBlocks) {
			            	TileEntityLittleBlocks newtilelb = (TileEntityLittleBlocks)newtile;
			            	newtilelb.setContent(selectedLittleTiles.get(entityplayer).getContent());
			            	newtilelb.setMetadata(selectedLittleTiles.get(entityplayer).getMetadata());
			            	newtilelb.onInventoryChanged();
			            }
			            world.markBlockForUpdate(x, y, z);
					}
					return true;
				} catch (InterruptedException e) {
					LoggerLittleBlocks.getInstance("ItemLittleBlocksCopier").writeStackTrace(e);
				}
			}
		}
		return false;
	}

	@Override
	public String getTextureFile() {
		return LBInit.LBM.getItemSheet();
	}
}
