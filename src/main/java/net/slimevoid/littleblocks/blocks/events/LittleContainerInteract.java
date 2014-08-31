package net.slimevoid.littleblocks.blocks.events;

import java.lang.reflect.Field;
import java.util.UUID;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.slimevoid.littleblocks.api.ILittleWorld;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.CoreLib;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

// import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

public class LittleContainerInteract {

    @SubscribeEvent
    public void onInteractEvent(PlayerOpenContainerEvent event) {
        if (!event.canInteractWith) {
            try {
                Field fields[] = event.entityPlayer.openContainer.getClass().getDeclaredFields();
                GenericCanInteract(event,
                                   fields,
                                   event.entityPlayer.openContainer);
            } catch (StackOverflowError s) {
                s.printStackTrace();
            }
        }
    }

    private void GenericCanInteract(PlayerOpenContainerEvent event, Field fields[], Object datasource) {
        for (int i = 0; i < fields.length && event.getResult() != Result.ALLOW; i++) {
            try {
                fields[i].setAccessible(true);
                if (fields[i].get(datasource) instanceof TileEntity) {
                    TileEntity tile = (TileEntity) fields[i].get(datasource);
                    if (tile.hasWorldObj()
                        && tile.getWorldObj() instanceof ILittleWorld) {
                        TileEntityCanInteract(event,
                                              tile);
                        break;

                    }
                } else if (fields[i].get(datasource) instanceof IBlockAccess) {
                    IBlockAccess world = (IBlockAccess) fields[i].get(datasource);
                    WorldCanInteract(event,
                                     fields[i],
                                     world,
                                     fields,
                                     i,
                                     datasource);
                    break;

                } else if (fields[i].get(datasource) instanceof IInventory) {
                    GenericCanInteract(event,
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
            FakePlayer fakePlayer = new FakePlayer((WorldServer) event.entityPlayer.worldObj, new GameProfile((UUID)null, CoreLib.MOD_CHANNEL));
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
        if (Math.pow(Math.pow((tile.xCoord >> 3) - event.entityPlayer.posX,
                              2)
                             + Math.pow((tile.yCoord >> 3)
                                                - event.entityPlayer.posY,
                                        2)
                             + Math.pow((tile.zCoord >> 3)
                                                - event.entityPlayer.posZ,
                                        2),
                     .5) <= 4) {
            FakePlayer fakePlayer = new FakePlayer((WorldServer) event.entityPlayer.worldObj, new GameProfile((UUID)null, CoreLib.MOD_CHANNEL));
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
            boolean checked = false;
            worldField.set(datasource,
                           LittleBlocks.proxy.getLittleWorld(world,
                                                             false));
            FakePlayer fakePlayer = new FakePlayer((WorldServer) event.entityPlayer.worldObj, new GameProfile((UUID)null, CoreLib.MOD_CHANNEL));
            if (fields.length > worldFieldIndex + 3) {
                fields[worldFieldIndex + 1].setAccessible(true);
                fields[worldFieldIndex + 2].setAccessible(true);
                fields[worldFieldIndex + 3].setAccessible(true);

                fakePlayer.openContainer = event.entityPlayer.openContainer;
                if (fields[worldFieldIndex + 1].get(datasource) instanceof Integer
                    && fields[worldFieldIndex + 2].get(datasource) instanceof Integer
                    && fields[worldFieldIndex + 3].get(datasource) instanceof Integer) {
                    if (Math.pow(Math.pow((fields[worldFieldIndex + 1].getInt(datasource) >> 3)
                                                  - event.entityPlayer.posX,
                                          2)
                                         + Math.pow((fields[worldFieldIndex + 2].getInt(datasource) >> 3)
                                                            - event.entityPlayer.posY,
                                                    2)
                                         + Math.pow((fields[worldFieldIndex + 3].getInt(datasource) >> 3)
                                                            - event.entityPlayer.posZ,
                                                    2),
                                 .5) <= 4) {
                        fakePlayer.posX = fields[worldFieldIndex + 1].getInt(datasource);
                        fakePlayer.posY = fields[worldFieldIndex + 2].getInt(datasource);
                        fakePlayer.posZ = fields[worldFieldIndex + 3].getInt(datasource);
                        checked = true;
                    }
                }
            }
            if (!checked) {
                // do this in case the worldobj is the only thing keeping us
                // from interacting
                fakePlayer.posX = event.entityPlayer.posX * 8;
                fakePlayer.posY = (event.entityPlayer.posY + (event.entityPlayer.height * 0.9)) * 8;
                fakePlayer.posZ = event.entityPlayer.posZ * 8;
            }

            if (fakePlayer.openContainer.canInteractWith(fakePlayer)) {
                event.setResult(Result.ALLOW);
            }
            worldField.set(datasource,
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