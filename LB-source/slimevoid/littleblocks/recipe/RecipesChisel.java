/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * Lesser General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package slimevoid.littleblocks.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import slimevoid.littleblocks.core.LBChiselCore;
import slimevoid.littleblocks.core.LBCore;
import slimevoid.littleblocks.items.ItemLittleBlocksChisel;

public class RecipesChisel implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting craftMatrix, World world) {
		ItemStack a = null;
		ItemStack b = null;
		boolean itemblockFound = false;
		for ( int i = 0; i < craftMatrix.getInventoryStackLimit(); i++ ) {
			if ( craftMatrix.getStackInSlot(i) != null ) {
				if ( craftMatrix.getStackInSlot(i).getItem() instanceof ItemLittleBlocksChisel ) {
					if ( a == null ) {
						a = craftMatrix.getStackInSlot(i);
					} else if ( b == null  ) {
						b = craftMatrix.getStackInSlot(i);
					} else {
						return false;
					}
				}
				if ( craftMatrix.getStackInSlot(i).getItem() instanceof ItemBlock ) {
					if ( itemblockFound ) return false;
					if ( a == null ) {
						a = craftMatrix.getStackInSlot(i);
						itemblockFound = true;
					} else if ( b == null ) {
						b = craftMatrix.getStackInSlot(i);
						itemblockFound = true;
					} else {
						return false;
					}
				}
			}
		}
		return ( 
				a != null && b != null
		);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix) {
		ItemStack chiseledBlocks = null;
		ItemStack blockStack = null;
		for ( int i = 0; i < craftMatrix.getInventoryStackLimit(); i++ ) {
			if ( craftMatrix.getStackInSlot(i) != null ) {
				if ( craftMatrix.getStackInSlot(i).getItem() instanceof ItemBlock ) {
					if ( blockStack == null ) {
						blockStack = craftMatrix.getStackInSlot(i);
					}
				}
			}
		}
		
		if ( blockStack != null ) {
			chiseledBlocks = LBChiselCore.getChiseledStack(blockStack);
		}
		
		
		return chiseledBlocks;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(LBCore.littleBlock,1);
	}
}

