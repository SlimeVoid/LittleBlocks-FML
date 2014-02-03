package slimevoid.littleblocks.world;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.items.ItemLittleBlocksWand;
import slimevoid.littleblocks.items.wand.EnumWandAction;

public class ItemInLittleWorldManager extends ItemInWorldManager {

    public ItemInLittleWorldManager(World world, EntityPlayerMP entityplayer) {
        super((World) LittleBlocks.proxy.getLittleWorld(world,
                                                        false));
        this.thisPlayerMP = entityplayer;
    }

    @Override
    public void onBlockClicked(int x, int y, int z, int side) {
        PlayerInteractEvent event = new PlayerInteractEvent(this.thisPlayerMP, Action.LEFT_CLICK_BLOCK, x, y, z, side);
        MinecraftForge.EVENT_BUS.post(event);
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
            int blockId = this.theWorld.getBlockId(x,
                                                   y,
                                                   z);

            Block block = Block.blocksList[blockId];
            if (block == null) return;

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

            if (event.useItem == Event.Result.DENY) {
                PacketLib.sendBlockChange(this.theWorld,
                                          this.thisPlayerMP,
                                          x,
                                          y,
                                          z);
                return;
            }

            if (blockId > 0) {
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
        int blockId = this.theWorld.getBlockId(x,
                                               y,
                                               z);
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
            if (blockHarvested) PacketLib.sendBlockChange(this.theWorld,
                                                          this.thisPlayerMP,
                                                          x,
                                                          y,
                                                          z);
        } else {
            ItemStack playerHeldItem = this.thisPlayerMP.getCurrentEquippedItem();
            boolean canHarvest = false;
            Block block = Block.blocksList[blockId];
            if (block != null) {
                canHarvest = true;
            }

            if (playerHeldItem != null) {
                playerHeldItem.onBlockDestroyed(this.theWorld,
                                                blockId,
                                                x,
                                                y,
                                                z,
                                                this.thisPlayerMP);
            }

            blockHarvested = this.removeBlock(x,
                                              y,
                                              z);
            if (blockHarvested && canHarvest) {
                Block.blocksList[blockId].harvestBlock(this.theWorld,
                                                       this.thisPlayerMP,
                                                       x,
                                                       y,
                                                       z,
                                                       metadata);
            }
        }
        return blockHarvested;
    }

    private boolean removeBlock(int x, int y, int z) {
        Block littleBlock = Block.blocksList[this.theWorld.getBlockId(x,
                                                                      y,
                                                                      z)];
        int metadata = this.theWorld.getBlockMetadata(x,
                                                      y,
                                                      z);

        if (littleBlock != null) {
            littleBlock.onBlockHarvested(this.theWorld,
                                         x,
                                         y,
                                         z,
                                         metadata,
                                         this.thisPlayerMP);
        }

        boolean blockIsRemoved = (littleBlock != null && littleBlock.removeBlockByPlayer(theWorld,
                                                                                         thisPlayerMP,
                                                                                         x,
                                                                                         y,
                                                                                         z));
        ;

        if (this.thisPlayerMP.getHeldItem() != null
            && this.thisPlayerMP.getHeldItem().getItem() instanceof ItemLittleBlocksWand) {
            if (EnumWandAction.getWandActionForPlayer(this.thisPlayerMP).equals(EnumWandAction.DESTROY_LB)) {
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

        if (littleBlock != null && blockIsRemoved) {
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
                                                                       side);
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

        int i1 = world.getBlockId(x,
                                  y,
                                  z);
        Block block = Block.blocksList[i1];
        boolean result = false;

        if (block != null
            && (!entityplayer.isSneaking() || (entityplayer.getHeldItem() == null || entityplayer.getHeldItem().getItem().shouldPassSneakingClickToBlock(world,
                                                                                                                                                         x,
                                                                                                                                                         y,
                                                                                                                                                         z)))) {
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
