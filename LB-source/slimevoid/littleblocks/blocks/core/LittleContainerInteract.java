package slimevoid.littleblocks.blocks.core;

import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LittleBlocks;
import net.minecraft.block.Block;
import static net.minecraftforge.event.Event.Result.ALLOW;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.container.ContainerInteractEvent;

public class LittleContainerInteract {
	
	@ForgeSubscribe
	public void onInteractEvent(ContainerInteractEvent event) {
		ILittleWorld littleWorld = LittleBlocks.proxy.getLittleWorld(event.entityPlayer.worldObj, false);
		if (event.container instanceof ContainerChest) {
			IInventory chest = ((ContainerChest)event.container).getLowerChestInventory();
			if (chest instanceof TileEntityChest) {
				TileEntityChest chestentity = (TileEntityChest)chest;
				if (LittleBlockDataHandler.isUseableByPlayer(chestentity, event.entityPlayer)) {
					event.interact = ALLOW;
				}
			}
		}
		if (event.container instanceof ContainerDispenser) {
			TileEntityDispenser dispenser = ((ContainerDispenser)event.container).tileEntityDispenser;
			if (dispenser.getWorldObj() == littleWorld) {
				if (LittleBlockDataHandler.isUseableByPlayer(dispenser, event.entityPlayer)) {
                    event.interact = ALLOW;
				}
			}
		}
        if (event.container instanceof ContainerFurnace) {
            TileEntityFurnace furnace = ((ContainerFurnace)event.container).furnace;
            if (furnace.getWorldObj() == littleWorld) {
                if (LittleBlockDataHandler.isUseableByPlayer(furnace, event.entityPlayer)) {
                    event.interact = ALLOW;
                }
            }
        }
        if (event.container instanceof ContainerBrewingStand) {
            TileEntityBrewingStand brewingstand = ((ContainerBrewingStand)event.container).tileBrewingStand;
            if (brewingstand.getWorldObj() == littleWorld) {
                if (LittleBlockDataHandler.isUseableByPlayer(brewingstand, event.entityPlayer)) {
                    event.interact = ALLOW;
                }
            }
        }
		if (event.container instanceof ContainerWorkbench) {
			ContainerWorkbench workbench = ((ContainerWorkbench)event.container);
			World worldobj = ((ContainerWorkbench)event.container).worldObj;
			if (worldobj == event.entityPlayer.worldObj) {
				boolean flag = LittleBlockDataHandler.isUseableByPlayer(
						event.entityPlayer,
						(World) littleWorld,
						Block.workbench.blockID,
						workbench.posX,
						workbench.posY,
						workbench.posZ);
				if (flag) {
                    event.interact = ALLOW;
				}
			}
		}
        if (event.container instanceof ContainerEnchantment) {
            ContainerEnchantment enchanttable = ((ContainerEnchantment)event.container);
            World worldobj = ((ContainerEnchantment)event.container).worldPointer;
            if (worldobj == event.entityPlayer.worldObj) {
                boolean flag = LittleBlockDataHandler.isUseableByPlayer(
                        event.entityPlayer,
                        (World) littleWorld,
                        Block.enchantmentTable.blockID,
                        enchanttable.posX,
                        enchanttable.posY,
                        enchanttable.posZ);
                if (flag) {
                    event.interact = ALLOW;
                }
            }
        }
        if (event.container instanceof ContainerHopper) {
            TileEntityHopper brewingstand = (TileEntityHopper) ((ContainerHopper)event.container).field_94538_a;
            if (brewingstand.getWorldObj() == littleWorld) {
                if (LittleBlockDataHandler.isUseableByPlayer(brewingstand, event.entityPlayer)) {
                    event.interact = ALLOW;
                }
            }
        }
	}
	
}
