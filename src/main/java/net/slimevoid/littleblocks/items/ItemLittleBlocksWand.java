package net.slimevoid.littleblocks.items;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidBlock;
import net.slimevoid.library.data.ReadWriteLock;
import net.slimevoid.library.util.helpers.PacketHelper;
import net.slimevoid.littleblocks.blocks.BlockLittleChunk;
import net.slimevoid.littleblocks.core.LoggerLittleBlocks;
import net.slimevoid.littleblocks.core.lib.CommandLib;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.IconLib;
import net.slimevoid.littleblocks.items.wand.EnumWandAction;
import net.slimevoid.littleblocks.network.packets.PacketLittleNotify;
import net.slimevoid.littleblocks.tileentities.TileEntityLittleChunk;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLittleBlocksWand extends Item {

    protected IIcon[] iconList;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        iconList = new IIcon[1];
        iconList[0] = iconRegister.registerIcon(IconLib.LB_WAND);
    }

    @Override
    public IIcon getIconFromDamage(int i) {
        return iconList[0];
    }

    public static HashMap<EntityPlayer, TileEntityLittleChunk> selectedLittleTiles = new HashMap<EntityPlayer, TileEntityLittleChunk>();
    public static ReadWriteLock                                tileLock            = new ReadWriteLock();

    public ItemLittleBlocksWand(int id) {
        super();
        this.setNoRepair();
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    public boolean isFull3D() {
        return true;
    }

    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        return true;
    }

    @Override
    public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l, float a, float b, float c) {
        if (entityplayer.canPlayerEdit(x,
                                       y,
                                       z,
                                       l,
                                       itemstack)) {
            if (!world.isRemote) {
                EnumWandAction playerWandAction = EnumWandAction.getWandActionForPlayer(entityplayer);
                if (playerWandAction.equals(EnumWandAction.COPY_LB)) {
                    return this.doCopyLB(itemstack,
                                         entityplayer,
                                         world,
                                         x,
                                         y,
                                         z,
                                         l,
                                         a,
                                         b,
                                         c);
                }
                if (playerWandAction.equals(EnumWandAction.ROTATE_LB)) {
                    return this.doRotateLB(itemstack,
                                           entityplayer,
                                           world,
                                           x,
                                           y,
                                           z,
                                           l,
                                           a,
                                           b,
                                           c);
                }
                if (playerWandAction.equals(EnumWandAction.PLACE_LB)) {
                    return this.doPlaceLB(itemstack,
                                          entityplayer,
                                          world,
                                          x,
                                          y,
                                          z,
                                          l,
                                          a,
                                          b,
                                          c);
                }
            }
        }
        return false;
    }

    private boolean doRotateLB(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l, float a, float b, float c) {
        if (world.getBlock(x,
                           y,
                           z) == ConfigurationLib.littleChunk) {
            ((BlockLittleChunk) ConfigurationLib.littleChunk).rotateLittleChunk(world,
                                                                                x,
                                                                                y,
                                                                                z,
                                                                                ForgeDirection.UP);
            return true;
        }
        return false;
    }

    private boolean doPlaceLB(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l, float a, float b, float c) {
        Block blockID = world.getBlock(x,
                                       y,
                                       z);
        if (blockID != ConfigurationLib.littleChunk) {
            if (!blockID.isReplaceable(world,
                                       x,
                                       y,
                                       z) || blockID instanceof IFluidBlock) {
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
            }
            blockID = world.getBlock(x,
                                     y,
                                     z);
            if (blockID.getMaterial() == Material.air || blockID.isAir(world,
                                                                       x,
                                                                       y,
                                                                       z)
                || blockID.isReplaceable(world,
                                         x,
                                         y,
                                         z)
                && !(world.getBlock(x,
                                    y,
                                    z) instanceof IFluidBlock)) {
                world.setBlock(x,
                               y,
                               z,
                               ConfigurationLib.littleChunk);
                TileEntity newtile = world.getTileEntity(x,
                                                         y,
                                                         z);
                if (newtile != null) {
                    newtile.markDirty();
                    world.markBlockForUpdate(x,
                                             y,
                                             z);
                } else {
                    FMLCommonHandler.instance().getFMLLogger().warn("Could not initialize LittleChunk");
                }
            }
            return true;
        }
        return false;
    }

    private boolean doCopyLB(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int l, float a, float b, float c) {
        if (((EntityPlayerMP) entityplayer).capabilities.isCreativeMode) {
            TileEntity tileentity = world.getTileEntity(x,
                                                        y,
                                                        z);
            if (tileentity != null
                && tileentity instanceof TileEntityLittleChunk) {
                try {
                    tileLock.writeLock(world);
                    selectedLittleTiles.put(entityplayer,
                                            (TileEntityLittleChunk) tileentity);
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
                    TileEntityLittleChunk selectedLittleTile = selectedLittleTiles.get(entityplayer);
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
                        world.setBlock(xx,
                                       yy,
                                       zz,
                                       ConfigurationLib.littleChunk);
                        TileEntity newtile = world.getTileEntity(xx,
                                                                 yy,
                                                                 zz);
                        if (newtile != null
                            && newtile instanceof TileEntityLittleChunk) {
                            TileEntityLittleChunk newtilelb = (TileEntityLittleChunk) newtile;
                            tileLock.readLock(world);
                            TileEntityLittleChunk oldtile = selectedLittleTiles.get(entityplayer);
                            tileLock.readUnlock();
                            for (int x1 = 0; x1 < ConfigurationLib.littleBlocksSize; x1++) {
                                for (int y1 = 0; y1 < ConfigurationLib.littleBlocksSize; y1++) {
                                    for (int z1 = 0; z1 < ConfigurationLib.littleBlocksSize; z1++) {
                                        if (oldtile.getBlock(x1,
                                                             y1,
                                                             z1) != Blocks.air) {
                                            Block blockId = oldtile.getBlock(x1,
                                                                             y1,
                                                                             z1);
                                            int metadata = oldtile.getBlockMetadata(x1,
                                                                                    y1,
                                                                                    z1);
                                            newtilelb.setBlockIDWithMetadata(x1,
                                                                             y1,
                                                                             z1,
                                                                             blockId,
                                                                             metadata);
                                            TileEntity oldLittleTile = oldtile.getChunkTileEntity(x1,
                                                                                                  y1,
                                                                                                  z1);
                                            if (oldLittleTile != null) {
                                                newtilelb.setChunkBlockTileEntity(x1,
                                                                                  y1,
                                                                                  z1,
                                                                                  oldLittleTile);
                                            }
                                        }
                                    }
                                }
                            }
                            newtilelb.markDirty();
                            world.markBlockForUpdate(xx,
                                                     yy,
                                                     zz);
                        }
                    }
                    return true;
                } catch (InterruptedException e) {
                    LoggerLittleBlocks.getInstance("ItemLittleBlocksCopier").writeStackTrace(e);
                }
            }
        }
        PacketHelper.sendToPlayer(new PacketLittleNotify(CommandLib.COPIER_MESSAGE),
                                  (EntityPlayerMP) entityplayer);
        return false;
    }
}
