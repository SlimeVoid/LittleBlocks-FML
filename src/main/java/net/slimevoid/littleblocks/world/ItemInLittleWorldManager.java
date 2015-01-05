package net.slimevoid.littleblocks.world;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.PacketLib;
import net.slimevoid.littleblocks.items.ItemLittleBlocksWand;
import net.slimevoid.littleblocks.items.wand.EnumWandAction;
import cpw.mods.fml.common.eventhandler.Event;

public class ItemInLittleWorldManager extends ItemInWorldManager {

    public ItemInLittleWorldManager(World world, EntityPlayerMP entityplayer) {
        super((World) LittleBlocks.proxy.getLittleWorld(world,
                                                        false));
        this.thisPlayerMP = entityplayer;
    }

    @Override
    public void onBlockClicked(int x, int y, int z, int side) {
        PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(thisPlayerMP, Action.LEFT_CLICK_BLOCK, x, y, z, side, theWorld);
        if (event.isCanceled()) {
            PacketLib.sendBlockChange(this.theWorld,
                                      this.thisPlayerMP,
                                      x,
                                      y,
                                      z);
            return;
        }
        if (this.isCreative()) {
            if (!this.theWorld.extinguishFire(this.thisPlayerMP,
                                              x,
                                              y,
                                              z,
                                              side)) {
                this.tryHarvestBlock(x,
                                     y,
                                     z);
            }
        } else {
            Block block = this.theWorld.getBlock(x,
                                                 y,
                                                 z);
            
            if (!block.isAir(this.theWorld, x, y, z)) {
                if (event.useBlock != Event.Result.DENY) {
	                block.onBlockClicked(this.theWorld,
				                        x,
				                        y,
				                        z,
				                        this.thisPlayerMP);
	                this.theWorld.extinguishFire(this.thisPlayerMP,
					                            x,
					                            y,
					                            z,
					                            side);
                } else {
                	PacketLib.sendBlockChange(this.theWorld,
				                            this.thisPlayerMP,
				                            x,
				                            y,
				                            z);
                }
            }

            if (event.useItem == Event.Result.DENY) {
                PacketLib.sendBlockChange(this.theWorld,
                                          this.thisPlayerMP,
                                          x,
                                          y,
                                          z);
                return;
            }

            if (!block.isAir(this.theWorld, x, y, z)) {
                this.tryHarvestBlock(x,
                                     y,
                                     z);
            }
        }
    }

    @Override
    public boolean isCreative() {
        return this.thisPlayerMP.capabilities.isCreativeMode;
    }

    public boolean tryHarvestBlock(int x, int y, int z) {
        BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(theWorld, getGameType(), thisPlayerMP, x, y, z);
        if (event.isCanceled()) {
            return false;
        } else {
        	ItemStack stack = thisPlayerMP.getCurrentEquippedItem();
            if (stack != null && stack.getItem().onBlockStartBreak(stack, x, y, z, thisPlayerMP)) {
                return false;
            }
	        Block block = this.theWorld.getBlock(x,
	                                             y,
	                                             z);
	        int blockId = Block.getIdFromBlock(block);
	        int metadata = this.theWorld.getBlockMetadata(x,
	                                                      y,
	                                                      z);
	        this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP,
	                                         2001,
	                                         x,
	                                         y,
	                                         z,
	                                         blockId
	                                                 + (this.theWorld.getBlockMetadata(x,
	                                                                                   y,
	                                                                                   z) << 12));
	        boolean blockHarvested = false;
	
	        if (this.isCreative()) {
	            blockHarvested = this.removeBlock(x,
	                                              y,
	                                              z);
	            if (blockHarvested) {
	            	PacketLib.sendBlockChange(this.theWorld,
                                              this.thisPlayerMP,
                                              x,
                                              y,
                                              z);
	            }
	        } else {
	            ItemStack playerHeldItem = this.thisPlayerMP.getCurrentEquippedItem();
	            boolean canHarvest = false;
	            if (block != null) {
	                canHarvest = true;
	            }
	
	            if (playerHeldItem != null) {
	                playerHeldItem.func_150999_a/* onBlockDestroyed */(this.theWorld,
	                                                                   block,
	                                                                   x,
	                                                                   y,
	                                                                   z,
	                                                                   this.thisPlayerMP);
	            }
	
	            blockHarvested = this.removeBlock(x,
	                                              y,
	                                              z);
	            if (blockHarvested && canHarvest) {
	                block.harvestBlock(this.theWorld,
	                                   this.thisPlayerMP,
	                                   x,
	                                   y,
	                                   z,
	                                   metadata);
	            }
	        }
	        return blockHarvested;
        }
    }

    private boolean removeBlock(int x, int y, int z) {
    	return this.removeBlock(x, y, z, false);
    }
    
    private boolean removeBlock(int x, int y, int z, boolean canHarvest) {

        Block littleBlock = this.theWorld.getBlock(x,
                                                   y,
                                                   z);
        int metadata = this.theWorld.getBlockMetadata(x,
                                                      y,
                                                      z);

        littleBlock.onBlockHarvested(this.theWorld,
                                     x,
                                     y,
                                     z,
                                     metadata,
                                     this.thisPlayerMP);

        boolean blockIsRemoved = littleBlock.removedByPlayer(theWorld,
                                                             thisPlayerMP,
                                                             x,
                                                             y,
                                                             z,
                                                             canHarvest);

        if (this.thisPlayerMP.getHeldItem() != null
            && this.thisPlayerMP.getHeldItem().getItem() instanceof ItemLittleBlocksWand) {
            if (EnumWandAction.forceDestroy(this.thisPlayerMP)) {
                this.theWorld.setBlockToAir(x,
                                            y,
                                            z);
                if (!blockIsRemoved) {
                    int fortune = EnchantmentHelper.getFortuneModifier(this.thisPlayerMP);
                    littleBlock.dropBlockAsItem(this.theWorld,
                                                x,
                                                y,
                                                z,
                                                metadata,
                                                fortune);
                }
                blockIsRemoved = true;
            }
        }

        if (blockIsRemoved) {
            littleBlock.onBlockDestroyedByPlayer(this.theWorld,
                                                 x,
                                                 y,
                                                 z,
                                                 metadata);
        }

        return blockIsRemoved;	
    }

    public boolean activateBlockOrUseItem(EntityPlayer entityplayer, World world, ItemStack itemstack, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(entityplayer,
                                                                       Action.RIGHT_CLICK_BLOCK,
                                                                       x,
                                                                       y,
                                                                       z,
                                                                       side,
                                                                       world);
        if (event.isCanceled()) {
            PacketLib.sendBlockChange(this.theWorld,
                                      this.thisPlayerMP,
                                      x,
                                      y,
                                      z);
            return false;
        }

        Item item = (itemstack != null ? itemstack.getItem() : null);
        if (item != null && item.onItemUseFirst(itemstack,
                                                entityplayer,
                                                world,
                                                x,
                                                y,
                                                z,
                                                side,
                                                hitX,
                                                hitY,
                                                hitZ)) {
            if (itemstack.stackSize <= 0) ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP,
                                                                                itemstack);
            return true;
        }

        Block block = world.getBlock(x,
                                     y,
                                     z);
        boolean isAir = block.isAir(world, x, y, z);
        boolean useBlock = !entityplayer.isSneaking() || entityplayer.getHeldItem() == null;
        if (!useBlock) {
        	useBlock = entityplayer.getHeldItem().getItem().doesSneakBypassUse(world,
                    x,
                    y,
                    z,
                    entityplayer);
        }
        boolean result = false;

        if (useBlock) {
            if (event.useBlock != Event.Result.DENY) {
                result = block.onBlockActivated(world,
                                                x,
                                                y,
                                                z,
                                                entityplayer,
                                                side,
                                                hitX,
                                                hitY,
                                                hitZ);
            } else {
                PacketLib.sendBlockChange(this.theWorld,
                                          this.thisPlayerMP,
                                          x,
                                          y,
                                          z);
                result = event.useItem != Event.Result.ALLOW;
            }
        }

        if (itemstack != null && !result && event.useItem != Event.Result.DENY) {
            int meta = itemstack.getItemDamage();
            int size = itemstack.stackSize;
            result = itemstack.tryPlaceItemIntoWorld(entityplayer,
                                                     world,
                                                     x,
                                                     y,
                                                     z,
                                                     side,
                                                     hitX,
                                                     hitY,
                                                     hitZ);
            if (isCreative()) {
                itemstack.setItemDamage(meta);
                itemstack.stackSize = size;
            }
            if (itemstack.stackSize <= 0) ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP,
                                                                                itemstack);
        }

        return result;
    }
}
