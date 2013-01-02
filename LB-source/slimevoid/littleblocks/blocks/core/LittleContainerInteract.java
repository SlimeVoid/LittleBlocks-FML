package slimevoid.littleblocks.blocks.core;

public class LittleContainerInteract {
	
/*	@ForgeSubscribe
	public void onInteractEvent(ContainerInteractEvent event) {
		LittleWorld littleWorld = ((ILBCommonProxy)LBInit.LBM.getProxy()).getLittleWorld(event.entityPlayer.worldObj, false);
		if (event.container instanceof ContainerChest) {
			IInventory chest = ((ContainerChest)event.container).getLowerChestInventory();
			if (chest instanceof TileEntityChest) {
				TileEntityChest chestentity = (TileEntityChest)chest;
				if (LittleBlockDataHandler.isUseableByPlayer(chestentity, event.entityPlayer)) {
					event.setResult(Result.ALLOW);
				}
			}
		}
		if (event.container instanceof ContainerDispenser) {
			TileEntityDispenser dispenser = ((ContainerDispenser)event.container).getDispenser();
			if (dispenser.getWorldObj() == littleWorld) {
				if (LittleBlockDataHandler.isUseableByPlayer(dispenser, event.entityPlayer)) {
					event.setResult(Result.ALLOW);
				}
			}
		}
		if (event.container instanceof ContainerWorkbench) {
			ContainerWorkbench workbench = ((ContainerWorkbench)event.container);
			World worldobj = ((ContainerWorkbench)event.container).getWorldObj();
			if (worldobj == event.entityPlayer.worldObj) {
				boolean flag = LittleBlockDataHandler.isUseableByPlayer(
						event.entityPlayer,
						littleWorld,
						Block.workbench.blockID,
						workbench.getPosX(),
						workbench.getPosY(),
						workbench.getPosZ());
				if (flag) {
					event.setResult(Result.ALLOW);
				} else {
					event.setResult(Result.DEFAULT); 
				}
			}
		}
	}*/
	
}
