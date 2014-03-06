package com.slimevoid.littleblocks.core.lib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.slimevoid.library.network.PacketIds;
import com.slimevoid.library.network.handlers.ClientPacketHandler;
import com.slimevoid.library.network.handlers.ServerPacketHandler;
import com.slimevoid.library.util.helpers.PacketHelper;
import com.slimevoid.littleblocks.client.network.ClientNetworkEvent;
import com.slimevoid.littleblocks.client.network.packets.executors.ClientBlockChangeExecutor;
import com.slimevoid.littleblocks.client.network.packets.executors.ClientBlockEventExecutor;
import com.slimevoid.littleblocks.client.network.packets.executors.ClientCopierNotifyExecutor;
import com.slimevoid.littleblocks.client.network.packets.executors.ClientLittleCollectionExecutor;
import com.slimevoid.littleblocks.client.network.packets.executors.ClientPacketLittleBlocksLoginExecutor;
import com.slimevoid.littleblocks.network.NetworkEvent;
import com.slimevoid.littleblocks.network.handlers.PacketLittleBlockCollectionHandler;
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
import com.slimevoid.littleblocks.network.packets.executors.PacketLittleWandSwitchExecutor;

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
        MinecraftForge.EVENT_BUS.register(new ClientNetworkEvent());

        ClientPacketHandler handler = new ClientPacketHandler();

        PacketLoginHandler clientLoginHandler = new PacketLoginHandler();
        clientLoginHandler.registerPacketHandler(CommandLib.SETTINGS,
                                                 new ClientPacketLittleBlocksLoginExecutor());

        handler.registerPacketHandler(PacketIds.LOGIN,
                                      clientLoginHandler);

        PacketLittleBlocksHandler clientLittleBlocksHandler = new PacketLittleBlocksHandler();
        clientLittleBlocksHandler.registerPacketHandler(CommandLib.UPDATE_CLIENT,
                                                        new ClientBlockChangeExecutor());

        handler.registerPacketHandler(PacketIds.UPDATE,
                                      clientLittleBlocksHandler);

        PacketLittleBlockCollectionHandler clientCollectionHandler = new PacketLittleBlockCollectionHandler();
        clientCollectionHandler.registerPacketHandler(CommandLib.ENTITY_COLLECTION,
                                                      new ClientLittleCollectionExecutor());

        handler.registerPacketHandler(PacketIds.ENTITY,
                                      clientCollectionHandler);

        PacketLittleNotifyHandler clientLittleNotifyHandler = new PacketLittleNotifyHandler();
        clientLittleNotifyHandler.registerPacketHandler(CommandLib.COPIER_MESSAGE,
                                                        new ClientCopierNotifyExecutor());

        handler.registerPacketHandler(PacketIds.PLAYER,
                                      clientLittleNotifyHandler);

        PacketLittleBlockEventHandler clientLittleBlockEventHandler = new PacketLittleBlockEventHandler();
        clientLittleBlockEventHandler.registerPacketHandler(CommandLib.BLOCK_EVENT,
                                                            new ClientBlockEventExecutor());

        handler.registerPacketHandler(PacketLib.PACKETID_EVENT,
                                      clientLittleBlockEventHandler);

        PacketHelper.registerClientHandler(CoreLib.MOD_CHANNEL,
                                            handler);
    }

    public static void registerPacketHandlers() {
        MinecraftForge.EVENT_BUS.register(new NetworkEvent());

        ServerPacketHandler handler = new ServerPacketHandler();
        PacketLittleNotifyHandler playerHandler = new PacketLittleNotifyHandler();
        playerHandler.registerPacketHandler(CommandLib.WAND_SWITCH,
                                            new PacketLittleWandSwitchExecutor());

        handler.registerPacketHandler(PacketIds.PLAYER,
                                      playerHandler);

        PacketLittleBlockHandler littleBlockHandler = new PacketLittleBlockHandler();
        littleBlockHandler.registerPacketHandler(CommandLib.BLOCK_ACTIVATED,
                                                 new PacketLittleBlockActivatedExecutor());
        littleBlockHandler.registerPacketHandler(CommandLib.BLOCK_CLICKED,
                                                 new PacketLittleBlockClickedExecutor());

        handler.registerPacketHandler(PacketIds.TILE,
                                      littleBlockHandler);

        PacketHelper.registerServerHandler(CoreLib.MOD_CHANNEL,
                                      handler);
    }

    @SideOnly(Side.CLIENT)
    public static void blockActivated(World world, EntityPlayer entityplayer, int x, int y, int z, int q, float a, float b, float c) {
        PacketLittleBlock packetLB = new PacketLittleBlock(x, y, z, q, entityplayer.getCurrentEquippedItem(), a, b, c);
        PacketHelper.sendToServer(packetLB);
    }

    public static void sendBlockEvent(int x, int y, int z, int blockID, int eventID, int eventParameter) {
        PacketLittleBlocksEvents eventPacket = new PacketLittleBlocksEvents(x, y, z, blockID, eventID, eventParameter);
        PacketHelper.broadcastPacket(eventPacket);
    }

    public static void sendBlockClick(int x, int y, int z, int side) {
        PacketLittleBlock clickPacket = new PacketLittleBlock(x, y, z, side);
        PacketHelper.sendToServer(clickPacket);
    }

    public static void sendBlockPlace(World world, EntityPlayer entityplayer, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        PacketLittleBlock placePacket = new PacketLittleBlock(x, y, z, side, entityplayer.inventory.getCurrentItem(), hitX, hitY, hitZ);
        PacketHelper.sendToServer(placePacket);
    }

    public static void sendItemUse(World world, EntityPlayer entityplayer, int x, int y, int z, int side, ItemStack itemstack) {
        PacketLittleBlock usePacket = new PacketLittleBlock(x, y, z, side, itemstack, 0.0f, 0.0f, 0.0f);
        PacketHelper.sendToServer(usePacket);
    }

    public static void sendBlockChange(World world, EntityPlayer entityplayer, int x, int y, int z) {
        PacketLittleBlocks changePacket = new PacketLittleBlocks(x, y, z, world);
        PacketHelper.sendToPlayer(changePacket,
                                  (EntityPlayerMP) entityplayer);
    }

    @SideOnly(Side.CLIENT)
    public static void wandModeSwitched() {
        PacketLittleNotify packet = new PacketLittleNotify(CommandLib.WAND_SWITCH);
        PacketHelper.sendToServer(packet);
    }

    public static void sendWandChange(World world, EntityPlayer entityplayer, int actionID) {
        PacketLittleNotify wandPacket = new PacketLittleNotify(CommandLib.WAND_SWITCH);
        wandPacket.side = actionID;
        PacketHelper.sendToPlayer(wandPacket,
                                  (EntityPlayerMP) entityplayer);
    }

    // public static void tryMinecraftPacket(INetworkManager manager,
    // Packet250CustomPayload packet, Player player) {
    // if (packet.channel.equals(CHANNEL_COMMAND_BLOCK)) {
    // if
    // (!FMLCommonHandler.instance().getMinecraftServerInstance().isCommandBlockEnabled())
    // {
    // ((EntityPlayer)
    // player).sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(MessageLib.COMMAND_BLOCK_DISABLED));
    // } else if (((EntityPlayer) player).canCommandSenderUseCommand(2,
    // "")
    // && ((EntityPlayer) player).capabilities.isCreativeMode) {
    // try {
    // DataInputStream data = new DataInputStream(new
    // ByteArrayInputStream(packet.data));
    // int i = data.readInt();
    // int j = data.readInt();
    // int k = data.readInt();
    // String s = Packet.readString(data,
    // 256);
    // TileEntity realTileEntity = ((EntityPlayer)
    // player).worldObj.getBlockTileEntity(i,
    // j,
    // k);
    // boolean littleCommand = s.startsWith(LITTLEWORLD);
    // if (realTileEntity == null || littleCommand) {
    // TileEntity tileentity =
    // SlimevoidHelper.getBlockTileEntity(((EntityPlayer) player).worldObj,
    // i,
    // j,
    // k);
    //
    // if (tileentity != null
    // && tileentity instanceof TileEntityCommandBlock
    // && tileentity.worldObj instanceof ILittleWorld) {
    // if (littleCommand) {
    // s = s.substring(LITTLEWORLD.length());
    // }
    // ((TileEntityCommandBlock) tileentity).setCommand(s);
    // tileentity.worldObj.markBlockForUpdate(i,
    // j,
    // k);
    // ((EntityPlayer)
    // player).sendChatToPlayer(ChatMessageComponent.createFromTranslationWithSubstitutions(MessageLib.COMMAND_BLOCK_SUCCESS,
    // new Object[] { s }));
    // }
    // }
    // } catch (Exception exception3) {
    // exception3.printStackTrace();
    // }
    // } else {
    // ((EntityPlayer)
    // player).sendChatToPlayer(ChatMessageComponent.createFromTranslationKey(MessageLib.COMMAND_BLOCK_NOT_ALLOWED));
    // }
    // }
    // }
}
