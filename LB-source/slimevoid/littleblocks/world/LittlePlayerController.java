package slimevoid.littleblocks.world;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import slimevoid.littleblocks.core.LittleBlocks;
import slimevoid.littleblocks.core.lib.BlockUtil;
import slimevoid.littleblocks.core.lib.PacketLib;
import slimevoid.littleblocks.items.ItemLittleBlocksWand;
import slimevoid.littleblocks.items.wand.EnumWandAction;
import cpw.mods.fml.client.FMLClientHandler;

public class LittlePlayerController extends PlayerControllerMP {

    private Minecraft    mc;
    private EnumGameType currentGameType;

    public LittlePlayerController(Minecraft client, NetClientHandler clientHandler) {
        super(client, clientHandler);
        this.currentGameType = EnumGameType.SURVIVAL;
        this.mc = client;
    }

    @Override
    public void setGameType(EnumGameType gameType) {
        this.currentGameType = gameType;
        this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
    }

    public static void clickBlockCreative(Minecraft client, LittlePlayerController controller, int x, int y, int z, int side) {
        if (!((World) LittleBlocks.proxy.getLittleWorld(client.theWorld,
                                                        false)).extinguishFire(client.thePlayer,
                                                                               x,
                                                                               y,
                                                                               z,
                                                                               side)) {
            controller.onPlayerDestroyBlock(x,
                                            y,
                                            z,
                                            side);
        }
    }

    public void onPlayerRightClickFirst(EntityPlayer entityplayer, World world, ItemStack itemstack, int x, int y, int z, int sumside, float hitX, float hitY, float hitZ) {
        boolean flag = true;
        int stackSize = itemstack != null ? itemstack.stackSize : 0;
        if (this.onPlayerRightClick(entityplayer,
                                    world,
                                    itemstack,
                                    x,
                                    y,
                                    z,
                                    sumside,
                                    Vec3.createVectorHelper(hitX,
                                                            hitY,
                                                            hitZ))) {
            flag = false;
            entityplayer.swingItem();
        }
        if (itemstack == null) {
            return;
        }

        if (itemstack.stackSize == 0) {
            entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
        } else if (itemstack.stackSize != stackSize || this.isInCreativeMode()) {
            FMLClientHandler.instance().getClient().entityRenderer.itemRenderer.resetEquippedProgress();
        }

        if (flag) {
            ItemStack itemstack1 = entityplayer.inventory.getCurrentItem();
            if (itemstack1 != null && this.sendUseItem(entityplayer,
                                                       world,
                                                       itemstack1)) {
                FMLClientHandler.instance().getClient().entityRenderer.itemRenderer.resetEquippedProgress2();
            }
        }
    }

    @Override
    public boolean sendUseItem(EntityPlayer entityplayer, World world, ItemStack itemstack) {
        PacketLib.sendItemUse(world,
                              entityplayer,
                              -1,
                              -1,
                              -1,
                              255,
                              itemstack);
        int i = itemstack.stackSize;
        ItemStack itemstack1 = itemstack.useItemRightClick(world,
                                                           entityplayer);

        if (itemstack1 == itemstack
            && (itemstack1 == null || itemstack1.stackSize == i)) {
            return false;
        } else {
            entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = itemstack1;

            if (itemstack1.stackSize <= 0) {
                entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(entityplayer, itemstack1));
            }

            return true;
        }
    }

    @Override
    public boolean onPlayerRightClick(EntityPlayer entityplayer, World world, ItemStack itemstack, int x, int y, int z, int side, Vec3 hitAt) {

        float xOffset = (float) hitAt.xCoord;
        float yOffset = (float) hitAt.yCoord;
        float zOffset = (float) hitAt.zCoord;
        boolean flag = false;
        int blockId;
        if (itemstack != null && itemstack.getItem() != null
            && itemstack.getItem().onItemUseFirst(itemstack,
                                                  entityplayer,
                                                  world,
                                                  x,
                                                  y,
                                                  z,
                                                  side,
                                                  xOffset,
                                                  yOffset,
                                                  zOffset)) {
            return true;
        }

        if (!entityplayer.isSneaking()
            || (entityplayer.getHeldItem() == null || entityplayer.getHeldItem().getItem().shouldPassSneakingClickToBlock(world,
                                                                                                                          x,
                                                                                                                          y,
                                                                                                                          z))) {
            blockId = world.getBlockId(x,
                                       y,
                                       z);

            if (blockId > 0
                && Block.blocksList[blockId].onBlockActivated(world,
                                                              x,
                                                              y,
                                                              z,
                                                              entityplayer,
                                                              side,
                                                              xOffset,
                                                              yOffset,
                                                              zOffset)) {
                flag = true;
            }
        }

        if (!flag && itemstack != null
            && itemstack.getItem() instanceof ItemBlock) {
            ItemBlock itemblock = (ItemBlock) itemstack.getItem();

            if (!itemblock.canPlaceItemBlockOnSide(world,
                                                   x,
                                                   y,
                                                   z,
                                                   side,
                                                   entityplayer,
                                                   itemstack)) {
                return false;
            }
        }

        PacketLib.sendBlockPlace(world,
                                 entityplayer,
                                 x,
                                 y,
                                 z,
                                 side,
                                 xOffset,
                                 yOffset,
                                 zOffset);

        if (flag) {
            return true;
        } else if (itemstack == null) {
            return false;
        } else if (this.currentGameType.isCreative()) {
            blockId = itemstack.getItemDamage();
            int stackSize = itemstack.stackSize;
            boolean placedOrUsed = itemstack.tryPlaceItemIntoWorld(entityplayer,
                                                                   world,
                                                                   x,
                                                                   y,
                                                                   z,
                                                                   side,
                                                                   xOffset,
                                                                   yOffset,
                                                                   zOffset);
            itemstack.setItemDamage(blockId);
            itemstack.stackSize = stackSize;
            return placedOrUsed;
        } else {
            if (!itemstack.tryPlaceItemIntoWorld(entityplayer,
                                                 world,
                                                 x,
                                                 y,
                                                 z,
                                                 side,
                                                 xOffset,
                                                 yOffset,
                                                 zOffset)) {
                return false;
            }
            if (itemstack.stackSize <= 0) {
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(entityplayer, itemstack));
            }
            return true;
        }
    }

    @Override
    public void clickBlock(int x, int y, int z, int side) {
        World littleWorld = (World) LittleBlocks.proxy.getLittleWorld(mc.theWorld,
                                                                      false);
        if (!BlockUtil.isLittleChunk(littleWorld,
                                     x,
                                     y,
                                     z)) {
            return;
        }
        if (this.currentGameType.isCreative()) {
            PacketLib.sendBlockClick(x,
                                     y,
                                     z,
                                     side);
            clickBlockCreative(this.mc,
                               this,
                               x,
                               y,
                               z,
                               side);
        } else {
            PacketLib.sendBlockClick(x,
                                     y,
                                     z,
                                     side);
            int blockId = littleWorld.getBlockId(x,
                                                 y,
                                                 z);
            if (blockId > 0) {
                Block.blocksList[blockId].onBlockClicked(littleWorld,
                                                         x,
                                                         y,
                                                         z,
                                                         this.mc.thePlayer);
                this.onPlayerDestroyBlock(x,
                                          y,
                                          z,
                                          side);
            }
        }
    }

    @Override
    public boolean onPlayerDestroyBlock(int x, int y, int z, int side) {
        if (this.currentGameType.isCreative()
            && this.mc.thePlayer.getHeldItem() != null
            && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            return false;
        } else {
            World littleWorld = (World) LittleBlocks.proxy.getLittleWorld(this.mc.theWorld,
                                                                          false);
            Block littleBlock = Block.blocksList[littleWorld.getBlockId(x,
                                                                        y,
                                                                        z)];

            if (littleBlock == null) {
                return false;
            } else {
                littleWorld.playAuxSFX(2001,
                                       x,
                                       y,
                                       z,
                                       littleBlock.blockID
                                               + (littleWorld.getBlockMetadata(x,
                                                                               y,
                                                                               z) << 12));
                int i1 = littleWorld.getBlockMetadata(x,
                                                      y,
                                                      z);

                boolean blockIsRemoved = littleBlock.removeBlockByPlayer(littleWorld,
                                                                         mc.thePlayer,
                                                                         x,
                                                                         y,
                                                                         z);

                if (this.mc.thePlayer.getHeldItem() != null
                    && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemLittleBlocksWand) {
                    if (EnumWandAction.getWandAction().equals(EnumWandAction.DESTROY_LB)) {
                        littleWorld.setBlockToAir(x,
                                                  y,
                                                  z);
                        blockIsRemoved = true;
                    }
                }
                if (blockIsRemoved) {
                    littleBlock.onBlockDestroyedByPlayer(littleWorld,
                                                         x,
                                                         y,
                                                         z,
                                                         i1);
                }

                if (!this.currentGameType.isCreative()) {
                    ItemStack itemstack = this.mc.thePlayer.getCurrentEquippedItem();

                    if (itemstack != null) {
                        itemstack.onBlockDestroyed(littleWorld,
                                                   littleBlock.blockID,
                                                   x,
                                                   y,
                                                   z,
                                                   this.mc.thePlayer);

                        if (itemstack.stackSize == 0) {
                            this.mc.thePlayer.destroyCurrentEquippedItem();
                        }
                    }
                }

                return blockIsRemoved;
            }
        }
    }
}