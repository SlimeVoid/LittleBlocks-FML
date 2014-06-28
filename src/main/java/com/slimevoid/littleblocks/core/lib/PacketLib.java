package com.slimevoid.littleblocks.core.lib;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;

import com.slimevoid.library.network.PacketIds;
import com.slimevoid.library.util.helpers.SlimevoidHelper;
import com.slimevoid.littleblocks.api.ILittleWorld;
import com.slimevoid.littleblocks.client.network.ClientPacketHandler;
import com.slimevoid.littleblocks.client.network.packets.executors.ClientBlockChangeExecutor;
import com.slimevoid.littleblocks.client.network.packets.executors.ClientBlockEventExecutor;
import com.slimevoid.littleblocks.client.network.packets.executors.ClientCopierNotifyExecutor;
import com.slimevoid.littleblocks.client.network.packets.executors.ClientPacketLittleBlocksLoginExecutor;
import com.slimevoid.littleblocks.client.network.packets.executors.ClientPacketLittleBlocksSoundExecutor;
import com.slimevoid.littleblocks.network.CommonPacketHandler;
import com.slimevoid.littleblocks.network.handlers.PacketLittleBlockEventHandler;
import com.slimevoid.littleblocks.network.handlers.PacketLittleBlockHandler;
import com.slimevoid.littleblocks.network.handlers.PacketLittleBlocksHandler;
import com.slimevoid.littleblocks.network.handlers.PacketLittleNotifyHandler;
import com.slimevoid.littleblocks.network.handlers.PacketLoginHandler;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlock;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocks;
import com.slimevoid.littleblocks.network.packets.PacketLittleBlocksEvents;
import com.slimevoid.littleblocks.network.packets.PacketLittleNotify;
import com.slimevoid.littleblocks.network.packets.executors.PacketLittleBlockActivatedExecutor;
import com.slimevoid.littleblocks.network.packets.executors.PacketLittleBlockClickedExecutor;
import com.slimevoid.littleblocks.network.packets.executors.PacketLittleBlocksLoginExecutor;
import com.slimevoid.littleblocks.network.packets.executors.PacketLittleWandSwitchExecutor;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketLib {

    public final static String CHANNEL_COMMAND_BLOCK = "MC|AdvCdm";
    public final static String LITTLEWORLD           = "LW:";
    public final static int    PACKETID_EVENT        = PacketIds.PLAYER + 100;
    public final static int    BLOCK_CLICK           = 0;
    public final static int    DIG_ONGOING           = 1;
    public final static int    DIG_BROKEN            = 2;

    @SideOnly(Side.CLIENT)
    public static void registerClientPacketHandlers() {
        PacketLoginHandler clientLoginHandler = new PacketLoginHandler();
        clientLoginHandler.registerPacketHandler(CommandLib.SETTINGS,
                                                 new ClientPacketLittleBlocksLoginExecutor());

        ClientPacketHandler.registerPacketHandler(PacketIds.LOGIN,
                                                  clientLoginHandler);

        PacketLittleBlocksHandler clientLittleBlocksHandler = new PacketLittleBlocksHandler();
        clientLittleBlocksHandler.registerPacketHandler(CommandLib.UPDATE_CLIENT,
                                                        new ClientBlockChangeExecutor());

        ClientPacketHandler.registerPacketHandler(PacketIds.UPDATE,
                                                  clientLittleBlocksHandler);

        PacketLittleNotifyHandler clientLittleNotifyHandler = new PacketLittleNotifyHandler();
        clientLittleNotifyHandler.registerPacketHandler(CommandLib.COPIER_MESSAGE,
                                                        new ClientCopierNotifyExecutor());

        ClientPacketHandler.registerPacketHandler(PacketIds.PLAYER,
                                                  clientLittleNotifyHandler);

        PacketLittleBlockEventHandler clientLittleBlockEventHandler = new PacketLittleBlockEventHandler();
        clientLittleBlockEventHandler.registerPacketHandler(CommandLib.BLOCK_EVENT,
                                                            new ClientBlockEventExecutor());
        clientLittleBlockEventHandler.registerPacketHandler(CommandLib.BLOCK_SOUND,
                                                            new ClientPacketLittleBlocksSoundExecutor());

        ClientPacketHandler.registerPacketHandler(PacketLib.PACKETID_EVENT,
                                                  clientLittleBlockEventHandler);
    }

    public static void registerPacketHandlers() {
        PacketLoginHandler loginHandler = new PacketLoginHandler();
        loginHandler.registerPacketHandler(CommandLib.FETCH,
                                           new PacketLittleBlocksLoginExecutor());

        CommonPacketHandler.registerPacketHandler(PacketIds.LOGIN,
                                                  loginHandler);

        PacketLittleNotifyHandler playerHandler = new PacketLittleNotifyHandler();
        playerHandler.registerPacketHandler(CommandLib.WAND_SWITCH,
                                            new PacketLittleWandSwitchExecutor());

        CommonPacketHandler.registerPacketHandler(PacketIds.PLAYER,
                                                  playerHandler);

        PacketLittleBlockHandler littleBlockHandler = new PacketLittleBlockHandler();
        littleBlockHandler.registerPacketHandler(CommandLib.BLOCK_ACTIVATED,
                                                 new PacketLittleBlockActivatedExecutor());
        littleBlockHandler.registerPacketHandler(CommandLib.BLOCK_CLICKED,
                                                 new PacketLittleBlockClickedExecutor());

        CommonPacketHandler.registerPacketHandler(PacketIds.TILE,
                                                  littleBlockHandler);
    }

    @SideOnly(Side.CLIENT)
    public static void blockActivated(World world, EntityPlayer entityplayer, int x, int y, int z, int q, float a, float b, float c) {
        PacketLittleBlock packetLB = new PacketLittleBlock(x, y, z, q, entityplayer.getCurrentEquippedItem(), a, b, c);
        PacketDispatcher.sendPacketToServer(packetLB.getPacket());
    }

    public static void sendBlockEvent(int x, int y, int z, int blockID, int eventID, int eventParameter) {
        PacketLittleBlocksEvents eventPacket = new PacketLittleBlocksEvents(x, y, z, blockID, eventID, eventParameter);
        PacketDispatcher.sendPacketToAllPlayers(eventPacket.getPacket());
    }

    public static void sendBlockClick(int x, int y, int z, int side) {
        PacketLittleBlock clickPacket = new PacketLittleBlock(x, y, z, side);
        PacketDispatcher.sendPacketToServer(clickPacket.getPacket());
    }

    public static void sendBlockPlace(World world, EntityPlayer entityplayer, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        PacketLittleBlock placePacket = new PacketLittleBlock(x, y, z, side, entityplayer.inventory.getCurrentItem(), hitX, hitY, hitZ);
        PacketDispatcher.sendPacketToServer(placePacket.getPacket());
    }

    public static void sendItemUse(World world, EntityPlayer entityplayer, int x, int y, int z, int side, ItemStack itemstack) {
        PacketLittleBlock usePacket = new PacketLittleBlock(x, y, z, side, itemstack, 0.0f, 0.0f, 0.0f);
        PacketDispatcher.sendPacketToServer(usePacket.getPacket());
    }

    public static void sendBlockChange(World world, EntityPlayer entityplayer, int x, int y, int z) {
        PacketLittleBlocks changePacket = new PacketLittleBlocks(x, y, z, world);
        PacketDispatcher.sendPacketToPlayer(changePacket.getPacket(),
                                            (Player) entityplayer);
    }

    @SideOnly(Side.CLIENT)
    public static void wandModeSwitched() {
        PacketLittleNotify packet = new PacketLittleNotify(CommandLib.WAND_SWITCH);
        PacketDispatcher.sendPacketToServer(packet.getPacket());
    }

    public static void sendWandChange(World world, EntityPlayer entityplayer, int actionID) {
        PacketLittleNotify wandPacket = new PacketLittleNotify(CommandLib.WAND_SWITCH);
        wandPacket.side = actionID;
        PacketDispatcher.sendPacketToPlayer(wandPacket.getPacket(),
                                            (Player) entityplayer);
    }

    public static void tryMinecraftPacket(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        if (packet.channel.equals(CHANNEL_COMMAND_BLOCK)) {
            if (!FMLCommonHandler.instance().getMinecraftServerInstance().isCommandBlockEnabled()) {
                ((EntityPlayer) player).sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(MessageLib.COMMAND_BLOCK_DISABLED));
            } else if (((EntityPlayer) player).canCommandSenderUseCommand(2,
                                                                          "")
                       && ((EntityPlayer) player).capabilities.isCreativeMode) {
                try {
                    DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
                    int i = data.readInt();
                    int j = data.readInt();
                    int k = data.readInt();
                    String s = Packet.readString(data,
                                                 256);
                    TileEntity realTileEntity = ((EntityPlayer) player).worldObj.getBlockTileEntity(i,
                                                                                                    j,
                                                                                                    k);
                    boolean littleCommand = s.startsWith(LITTLEWORLD);
                    if (realTileEntity == null || littleCommand) {
                        TileEntity tileentity = SlimevoidHelper.getBlockTileEntity(((EntityPlayer) player).worldObj,
                                                                                   i,
                                                                                   j,
                                                                                   k);

                        if (tileentity != null
                            && tileentity instanceof TileEntityCommandBlock
                            && tileentity.worldObj instanceof ILittleWorld) {
                            if (littleCommand) {
                                s = s.substring(LITTLEWORLD.length());
                            }
                            ((TileEntityCommandBlock) tileentity).setCommand(s);
                            tileentity.worldObj.markBlockForUpdate(i,
                                                                   j,
                                                                   k);
                            ((EntityPlayer) player).sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(MessageLib.COMMAND_BLOCK_SUCCESS,
                                                                                                                                 new Object[] { s }));
                        }
                    }
                } catch (Exception exception3) {
                    exception3.printStackTrace();
                }
            } else {
                ((EntityPlayer) player).sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(MessageLib.COMMAND_BLOCK_NOT_ALLOWED));
            }
        }
    }
}
