package net.slimevoid.littleblocks.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.PacketLib;
import net.slimevoid.littleblocks.items.ItemLittleBlocksWand;
import net.slimevoid.littleblocks.items.wand.EnumWandAction;

public class ItemInLittleWorldManager extends ItemInWorldManager {

    public ItemInLittleWorldManager(World world, EntityPlayerMP entityplayer) {
        super((World) LittleBlocks.proxy.getLittleWorld(world,
                false));
        this.thisPlayerMP = entityplayer;
    }

    @Override
    public void onBlockClicked(BlockPos pos, EnumFacing side) {
        PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(thisPlayerMP, Action.LEFT_CLICK_BLOCK, theWorld, pos, side);
        if (event.isCanceled()) {
            PacketLib.sendBlockChange(this.theWorld,
                    this.thisPlayerMP,
                    pos);
            return;
        }
        if (this.isCreative()) {
            if (!this.theWorld.extinguishFire(this.thisPlayerMP,
                    pos,
                    side)) {
                this.tryHarvestBlock(pos);
            }
        } else {
            Block block = this.theWorld.getBlockState(pos).getBlock();

            if (!block.isAir(this.theWorld, pos)) {
                if (event.useBlock != Event.Result.DENY) {
                    block.onBlockClicked(this.theWorld,
                            pos,
                            this.thisPlayerMP);
                    this.theWorld.extinguishFire(this.thisPlayerMP,
                            pos,
                            side);
                } else {
                    PacketLib.sendBlockChange(this.theWorld,
                            this.thisPlayerMP,
                            pos);
                }
            }

            if (event.useItem == Event.Result.DENY) {
                PacketLib.sendBlockChange(this.theWorld,
                        this.thisPlayerMP,
                        pos);
                return;
            }

            if (!block.isAir(this.theWorld, pos)) {
                this.tryHarvestBlock(pos);
            }
        }
    }

    @Override
    public boolean isCreative() {
        return this.thisPlayerMP.capabilities.isCreativeMode;
    }

    @Override
    public boolean tryHarvestBlock(BlockPos pos) {
        int exp = ForgeHooks.onBlockBreakEvent(theWorld, getGameType(), thisPlayerMP, pos);
        if (exp == -1) {
            return false;
        } else {
            ItemStack stack = thisPlayerMP.getCurrentEquippedItem();
            if (stack != null && stack.getItem().onBlockStartBreak(stack, pos, thisPlayerMP)) {
                return false;
            }
            IBlockState state = this.theWorld.getBlockState(pos);
            TileEntity tileentity = this.theWorld.getTileEntity(pos);

            this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP,
                    2001,
                    pos,
                    Block.getStateId(state));
            boolean blockHarvested = false;

            if (this.isCreative()) {
                blockHarvested = this.removeBlock(pos);
                //if (blockHarvested) {
                    PacketLib.sendBlockChange(
                            this.theWorld,
                            this.thisPlayerMP,
                            pos);
                //}
            } else {
                ItemStack playerHeldItem = this.thisPlayerMP.getCurrentEquippedItem();
                boolean canHarvest = true;

                if (playerHeldItem != null) {
                    playerHeldItem.onBlockDestroyed(
                            this.theWorld,
                            state.getBlock(),
                            pos,
                            this.thisPlayerMP);
                }

                blockHarvested = this.removeBlock(pos, blockHarvested);
                if (blockHarvested && canHarvest) {
                    state.getBlock().harvestBlock(
                            this.theWorld,
                            this.thisPlayerMP,
                            pos,
                            state,
                            tileentity);
                }
            }
            return blockHarvested;
        }
    }

    private boolean removeBlock(BlockPos pos)
    {
        return removeBlock(pos, false);
    }
    private boolean removeBlock(BlockPos pos, boolean canHarvest)
    {
        IBlockState state = this.theWorld.getBlockState(pos);
        state.getBlock().onBlockHarvested(this.theWorld, pos, state, this.thisPlayerMP);
        boolean blockIsRemoved = state.getBlock().removedByPlayer(theWorld, pos, thisPlayerMP, canHarvest);

        if (this.thisPlayerMP.getHeldItem() != null
                && this.thisPlayerMP.getHeldItem().getItem() instanceof ItemLittleBlocksWand) {
            if (EnumWandAction.forceDestroy(this.thisPlayerMP)) {
                this.theWorld.setBlockToAir(pos);
                if (!blockIsRemoved) {
                    int fortune = EnchantmentHelper.getFortuneModifier(this.thisPlayerMP);
                    state.getBlock().dropBlockAsItem(this.theWorld,
                            pos,
                            state,
                            fortune);
                }
                blockIsRemoved = true;
            }
        }

        if (blockIsRemoved) {
            state.getBlock().onBlockDestroyedByPlayer(this.theWorld, pos, state);
        }

        return blockIsRemoved;
    }

    @Override
    public boolean activateBlockOrUseItem(EntityPlayer entityplayer, World world, ItemStack itemstack, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        PlayerInteractEvent event = ForgeEventFactory.onPlayerInteract(entityplayer,
                Action.RIGHT_CLICK_BLOCK,
                world,
                pos,
                side);
        if (event.isCanceled()) {
            PacketLib.sendBlockChange(this.theWorld,
                    this.thisPlayerMP,
                    pos);
            return false;
        }

        Item item = (itemstack != null ? itemstack.getItem() : null);
        if (item != null && item.onItemUseFirst(itemstack,
                entityplayer,
                world,
                pos,
                side,
                hitX,
                hitY,
                hitZ)) {
            if (itemstack.stackSize <= 0) ForgeEventFactory.onPlayerDestroyItem(thisPlayerMP,
                    itemstack);
            return true;
        }

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        boolean isAir = block.isAir(world, pos);
        boolean useBlock = !entityplayer.isSneaking() || entityplayer.getHeldItem() == null;
        if (!useBlock) {
            useBlock = entityplayer.getHeldItem().getItem().doesSneakBypassUse(world,
                    pos,
                    entityplayer);
        }
        boolean result = false;

        if (useBlock) {
            if (event.useBlock != Event.Result.DENY) {
                result = block.onBlockActivated(world,
                        pos,
                        state,
                        entityplayer,
                        side,
                        hitX,
                        hitY,
                        hitZ);
            } else {
                PacketLib.sendBlockChange(this.theWorld,
                        this.thisPlayerMP,
                        pos);
                result = event.useItem != Event.Result.ALLOW;
            }
        }

        if (itemstack != null && !result && event.useItem != Event.Result.DENY) {
            int meta = itemstack.getItemDamage();
            int size = itemstack.stackSize;
            result = itemstack.onItemUse(entityplayer,
                    world,
                    pos,
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
