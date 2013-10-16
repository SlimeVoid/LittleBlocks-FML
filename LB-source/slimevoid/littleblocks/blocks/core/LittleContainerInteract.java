package slimevoid.littleblocks.blocks.core;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import slimevoid.littleblocks.api.ILittleWorld;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.lib.CoreLib;

public class LittleContainerInteract {

	@ForgeSubscribe
	public void onInteractEvent(PlayerOpenContainerEvent event) {
		if (!event.canInteractWith) {
			Field fields[] = event.entityPlayer.openContainer.getClass().getDeclaredFields();
			if (event.entityPlayer.openContainer instanceof ContainerWorkbench) {
				WorkBenchCanInteract(	event,
										fields);
			} else if (event.entityPlayer.openContainer instanceof ContainerRepair) {
				AnvilCanInteract(	event,
									fields);
			} else {
				GenericCanInteract(	event,
									fields);
			}
		}

	}

	private void AnvilCanInteract(PlayerOpenContainerEvent event, Field[] fields) {
		try {
			Field worldObjF = fields[2];
			Field xPosF = fields[3];
			Field yPosF = fields[4];
			Field zPosF = fields[5];
			worldObjF.setAccessible(true);
			xPosF.setAccessible(true);
			yPosF.setAccessible(true);
			zPosF.setAccessible(true);
			IBlockAccess holdWorld = (IBlockAccess) worldObjF.get(event.entityPlayer.openContainer);
			int xPos = xPosF.getInt(event.entityPlayer.openContainer), yPos = yPosF.getInt(event.entityPlayer.openContainer), zPos = zPosF.getInt(event.entityPlayer.openContainer);
			ILittleWorld littleWorld = LittleBlocks.proxy.getLittleWorld((IBlockAccess) worldObjF.get(event.entityPlayer.openContainer),
																		false);
			if (!(worldObjF.get(event.entityPlayer.openContainer) instanceof ILittleWorld)
				&& littleWorld.getBlockId(	xPos,
											yPos,
											zPos) == Block.anvil.blockID) {
				worldObjF.set(	event.entityPlayer.openContainer,
								littleWorld);
			}
			if (Math.pow(	Math.pow(	(xPos >> 3) - event.entityPlayer.posX,
										2)
									+ Math.pow(	(yPos >> 3)
														- event.entityPlayer.posY,
												2)
									+ Math.pow(	(zPos >> 3)
														- event.entityPlayer.posZ,
												2),
							.5) <= 4) {
				FakePlayer fakePlayer = new FakePlayer(event.entityPlayer.worldObj, CoreLib.MOD_CHANNEL);

				fakePlayer.posX = xPos;
				fakePlayer.posY = yPos;
				fakePlayer.posZ = zPos;

				fakePlayer.openContainer = event.entityPlayer.openContainer;
				if (fakePlayer.openContainer.canInteractWith(fakePlayer)) {
					event.setResult(Result.ALLOW);
				}
				// this is just in case we accidently changed the world OBJ for
				// a realworld container
				worldObjF.set(	event.entityPlayer.openContainer,
								holdWorld);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void WorkBenchCanInteract(PlayerOpenContainerEvent event, Field[] fields) {

		try {
			Field worldObjF = fields[2];
			Field xPosF = fields[3];
			Field yPosF = fields[4];
			Field zPosF = fields[5];
			worldObjF.setAccessible(true);
			xPosF.setAccessible(true);
			yPosF.setAccessible(true);
			zPosF.setAccessible(true);
			IBlockAccess holdWorld = (IBlockAccess) worldObjF.get(event.entityPlayer.openContainer);
			int xPos = xPosF.getInt(event.entityPlayer.openContainer), yPos = yPosF.getInt(event.entityPlayer.openContainer), zPos = zPosF.getInt(event.entityPlayer.openContainer);
			ILittleWorld littleWorld = LittleBlocks.proxy.getLittleWorld((IBlockAccess) worldObjF.get(event.entityPlayer.openContainer),
																		false);
			if (!(worldObjF.get(event.entityPlayer.openContainer) instanceof ILittleWorld)
				&& littleWorld.getBlockId(	xPos,
											yPos,
											zPos) == Block.workbench.blockID) {
				worldObjF.set(	event.entityPlayer.openContainer,
								littleWorld);
			}
			if (Math.pow(	Math.pow(	(xPos >> 3) - event.entityPlayer.posX,
										2)
									+ Math.pow(	(yPos >> 3)
														- event.entityPlayer.posY,
												2)
									+ Math.pow(	(zPos >> 3)
														- event.entityPlayer.posZ,
												2),
							.5) <= 4) {
				FakePlayer fakePlayer = new FakePlayer(event.entityPlayer.worldObj, CoreLib.MOD_CHANNEL);

				fakePlayer.posX = xPos;
				fakePlayer.posY = yPos;
				fakePlayer.posZ = zPos;

				fakePlayer.openContainer = event.entityPlayer.openContainer;
				if (fakePlayer.openContainer.canInteractWith(fakePlayer)) {
					event.setResult(Result.ALLOW);
				}
				worldObjF.set(	event.entityPlayer.openContainer,
								holdWorld);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void GenericCanInteract(PlayerOpenContainerEvent event, Field fields[]) {
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				if (field.get(event.entityPlayer.openContainer) instanceof TileEntity) {
					TileEntity tile = (TileEntity) field.get(event.entityPlayer.openContainer);
					if (tile.hasWorldObj()
						&& tile.worldObj instanceof ILittleWorld) {
						TileEntityCanInteract(	event,
												tile);
						break;
					}
				} else if (field.get(event.entityPlayer.openContainer) instanceof IBlockAccess) {
					IBlockAccess world = (IBlockAccess) field.get(event.entityPlayer.openContainer);
					WorldCanInteract(	event,
										field,
										world);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void TileEntityCanInteract(PlayerOpenContainerEvent event, TileEntity tile) {
		if (Math.pow(	Math.pow(	(tile.xCoord >> 3)
											- event.entityPlayer.posX,
									2)
								+ Math.pow(	(tile.yCoord >> 3)
													- event.entityPlayer.posY,
											2)
								+ Math.pow(	(tile.zCoord >> 3)
													- event.entityPlayer.posZ,
											2),
						.5) <= 4) {
			FakePlayer fakePlayer = new FakePlayer(event.entityPlayer.worldObj, CoreLib.MOD_CHANNEL);
			fakePlayer.posX = (tile.xCoord);
			fakePlayer.posY = (tile.yCoord);
			fakePlayer.posZ = (tile.zCoord);

			fakePlayer.openContainer = event.entityPlayer.openContainer;
			if (fakePlayer.openContainer.canInteractWith(fakePlayer)) {
				event.setResult(Result.ALLOW);
			}
		}
	}

	private void WorldCanInteract(PlayerOpenContainerEvent event, Field worldField, IBlockAccess world) {
		try {
			worldField.set(	event.entityPlayer.openContainer,
							LittleBlocks.proxy.getLittleWorld(	world,
																false));
			FakePlayer fakePlayer = new FakePlayer(event.entityPlayer.worldObj, CoreLib.MOD_CHANNEL);
			fakePlayer.posX = event.entityPlayer.posX * 8;
			fakePlayer.posY = (event.entityPlayer.posY + (event.entityPlayer.height * 0.9)) * 8;
			fakePlayer.posZ = event.entityPlayer.posZ * 8;
			fakePlayer.openContainer = event.entityPlayer.openContainer;
			if (fakePlayer.openContainer.canInteractWith(fakePlayer)) {
				event.setResult(Result.ALLOW);
			}
			worldField.set(	event.entityPlayer.openContainer,
							world);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}