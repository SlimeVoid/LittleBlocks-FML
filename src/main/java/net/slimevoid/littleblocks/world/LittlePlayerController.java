package net.slimevoid.littleblocks.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.slimevoid.littleblocks.core.LittleBlocks;
import net.slimevoid.littleblocks.core.lib.BlockUtil;
import net.slimevoid.littleblocks.core.lib.ConfigurationLib;
import net.slimevoid.littleblocks.core.lib.PacketLib;
import net.slimevoid.littleblocks.items.ItemLittleBlocksWand;
import net.slimevoid.littleblocks.items.wand.EnumWandAction;
import cpw.mods.fml.client.FMLClientHandler;

public class LittlePlayerController extends PlayerControllerMP {
	
    private Minecraft              mc;
    private WorldSettings.GameType currentGameType;

    public LittlePlayerController(Minecraft client, NetHandlerPlayClient clientHandler) {
        super(client, clientHandler);
        this.currentGameType = WorldSettings.GameType.SURVIVAL;
        this.mc = client;
    }

    @Override
    public void setGameType(WorldSettings.GameType gameType) {
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
            || (entityplayer.getHeldItem() == null || entityplayer.getHeldItem().getItem().doesSneakBypassUse/* shouldPassSneakingClickToBlock */(world,
                                                                                                                                                  x,
                                                                                                                                                  y,
                                                                                                                                                  z,
                                                                                                                                                  entityplayer))) {
            flag = world.getBlock(x,
                                  y,
                                  z).onBlockActivated(world,
                                                      x,
                                                      y,
                                                      z,
                                                      entityplayer,
                                                      side,
                                                      xOffset,
                                                      yOffset,
                                                      zOffset);
        }

        if (!flag && itemstack != null
            && itemstack.getItem() instanceof ItemBlock) {
            ItemBlock itemblock = (ItemBlock) itemstack.getItem();

            if (!itemblock.func_150936_a/* canPlaceItemBlockOnSide */(world,
                                                                      x,
                                                                      y,
                                                                      z,
                                                                      side,
                                                                      entityplayer,
                                                                      itemstack)) {
                return false;
            }
        }

        //System.out.println("Controller: " + world);
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
            int damage = itemstack.getItemDamage();
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
            itemstack.setItemDamage(damage);
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
            Block block = littleWorld.getBlock(x,
                                               y,
                                               z);
            boolean flag = block.getMaterial() != Material.air;
            if (flag) {
                block.onBlockClicked(littleWorld,
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
        ItemStack stack = this.mc.thePlayer.getHeldItem();
        if (stack != null && stack.getItem() != null
            && stack.getItem().onBlockStartBreak(stack,
                                                 x,
                                                 y,
                                                 z,
                                                 this.mc.thePlayer)) {
            return false;
        }
        if (this.currentGameType.isCreative()
            && this.mc.thePlayer.getHeldItem() != null
            && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            return false;
        } else {
            World littleWorld = (World) LittleBlocks.proxy.getLittleWorld(this.mc.theWorld,
                                                                          false);
            Block littleBlock = littleWorld.getBlock(x,
                                                     y,
                                                     z);

            if (littleBlock.getMaterial() == Material.air) {
                return false;
            } else {
                littleWorld.playAuxSFX(2001,
                                       x,
                                       y,
                                       z,
                                       Block.getIdFromBlock(ConfigurationLib.littleChunk/*littleBlock)
                                               + (littleWorld.getBlockMetadata(x,
                                                                               y,
                                                                               z) << 12*/));
                int metadata = littleWorld.getBlockMetadata(x,
                                                            y,
                                                            z);

                boolean blockIsRemoved = littleBlock.removedByPlayer(littleWorld,
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
                                                         metadata);
                }

                if (!this.currentGameType.isCreative()) {
                    ItemStack itemstack = this.mc.thePlayer.getCurrentEquippedItem();

                    if (itemstack != null) {
                        itemstack.func_150999_a/* onBlockDestroyed */(littleWorld,
                                                                      littleBlock,
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