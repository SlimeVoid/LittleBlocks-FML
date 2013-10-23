package slimevoid.littleblocks.blocks.core;

import java.lang.reflect.Field;

import net.minecraft.inventory.IInventory;
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
			GenericCanInteract(	event,
								fields,
								event.entityPlayer.openContainer);

		}

	}

	private void GenericCanInteract(PlayerOpenContainerEvent event, Field fields[], Object datasource) {
		for (int i = 0; i < fields.length && event.getResult() != Result.ALLOW; i++) {
			try {
				fields[i].setAccessible(true);
				if (fields[i].get(datasource) instanceof TileEntity) {
					TileEntity tile = (TileEntity) fields[i].get(datasource);
					if (tile.hasWorldObj()
						&& tile.worldObj instanceof ILittleWorld) {
						TileEntityCanInteract(	event,
												tile);
						break;

					}
				} else if (fields[i].get(datasource) instanceof IBlockAccess) {
					IBlockAccess world = (IBlockAccess) fields[i].get(datasource);
					WorldCanInteract(	event,
										fields[i],
										world,
										fields,
										i,
										datasource);
					break;

				} else if (fields[i].get(datasource) instanceof IInventory) {
					GenericCanInteract(	event,
										fields[i].get(datasource).getClass().getDeclaredFields(),
										fields[i].get(datasource));

				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// last ditch effort
		if (event.getResult() != Result.ALLOW
			&& event.getResult() != Result.DENY) {
			FakePlayer fakePlayer = new FakePlayer(event.entityPlayer.worldObj, CoreLib.MOD_CHANNEL);
			fakePlayer.posX = event.entityPlayer.posX * 8;
			fakePlayer.posY = (event.entityPlayer.posY + (event.entityPlayer.height * 0.9)) * 8;
			fakePlayer.posZ = event.entityPlayer.posZ * 8;
			fakePlayer.openContainer = event.entityPlayer.openContainer;
			if (fakePlayer.openContainer.canInteractWith(fakePlayer)) {
				event.setResult(Result.ALLOW);
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

	private void WorldCanInteract(PlayerOpenContainerEvent event, Field worldField, IBlockAccess world, Field[] fields, int worldFieldIndex, Object datasource) {
		try {
			worldField.set(	datasource,
							LittleBlocks.proxy.getLittleWorld(	world,
																false));

			fields[worldFieldIndex + 1].setAccessible(true);
			fields[worldFieldIndex + 2].setAccessible(true);
			fields[worldFieldIndex + 3].setAccessible(true);

			FakePlayer fakePlayer = new FakePlayer(event.entityPlayer.worldObj, CoreLib.MOD_CHANNEL);
			fakePlayer.openContainer = event.entityPlayer.openContainer;
			if (fields[worldFieldIndex + 1].get(datasource) instanceof Integer
				&& fields[worldFieldIndex + 2].get(datasource) instanceof Integer
				&& fields[worldFieldIndex + 3].get(datasource) instanceof Integer) {
				System.out.println("possible coords");
				if (Math.pow(	Math.pow(	(fields[worldFieldIndex + 1].getInt(datasource) >> 3)
													- event.entityPlayer.posX,
											2)
										+ Math.pow(	(fields[worldFieldIndex + 2].getInt(datasource) >> 3)
															- event.entityPlayer.posY,
													2)
										+ Math.pow(	(fields[worldFieldIndex + 3].getInt(datasource) >> 3)
															- event.entityPlayer.posZ,
													2),
								.5) <= 4) {
					fakePlayer.posX = fields[worldFieldIndex + 1].getInt(datasource);
					fakePlayer.posY = fields[worldFieldIndex + 2].getInt(datasource);
					fakePlayer.posZ = fields[worldFieldIndex + 3].getInt(datasource);
				}
			} else {
				// do this in case the worldobj is the only thing keeping us
				// from interacting
				fakePlayer.posX = event.entityPlayer.posX * 8;
				fakePlayer.posY = (event.entityPlayer.posY + (event.entityPlayer.height * 0.9)) * 8;
				fakePlayer.posZ = event.entityPlayer.posZ * 8;
			}

			if (fakePlayer.openContainer.canInteractWith(fakePlayer)) {
				event.setResult(Result.ALLOW);
			}
			worldField.set(	datasource,
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
