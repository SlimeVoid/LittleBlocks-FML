package net.slimevoid.littleblocks.core.lib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.slimevoid.library.network.PacketIds;
import net.slimevoid.library.network.handlers.PacketPipeline;
import net.slimevoid.library.util.helpers.PacketHelper;
import net.slimevoid.littleblocks.client.network.ClientNetworkEvent;
import net.slimevoid.littleblocks.client.network.packets.executors.ClientBlockChangeExecutor;
import net.slimevoid.littleblocks.client.network.packets.executors.ClientBlockEventExecutor;
import net.slimevoid.littleblocks.client.network.packets.executors.ClientCopierNotifyExecutor;
import net.slimevoid.littleblocks.client.network.packets.executors.ClientLittleCollectionExecutor;
import net.slimevoid.littleblocks.client.network.packets.executors.ClientPacketLittleBlocksLoginExecutor;
import net.slimevoid.littleblocks.network.NetworkEvent;
import net.slimevoid.littleblocks.network.handlers.PacketLittleBlockCollectionHandler;
import net.slimevoid.littleblocks.network.handlers.PacketLittleBlockEventHandler;
import net.slimevoid.littleblocks.network.handlers.PacketLittleBlockHandler;
import net.slimevoid.littleblocks.network.handlers.PacketLittleBlocksHandler;
import net.slimevoid.littleblocks.network.handlers.PacketLittleNotifyHandler;
import net.slimevoid.littleblocks.network.handlers.PacketLoginHandler;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlock;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlockChange;
import net.slimevoid.littleblocks.network.packets.PacketLittleBlocksEvents;
import net.slimevoid.littleblocks.network.packets.PacketLittleNotify;
import net.slimevoid.littleblocks.network.packets.executors.PacketLittleBlockActivatedExecutor;
import net.slimevoid.littleblocks.network.packets.executors.PacketLittleBlockClickedExecutor;
import net.slimevoid.littleblocks.network.packets.executors.PacketLittleWandSwitchExecutor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketLib {

    public final static String   CHANNEL_COMMAND_BLOCK = "MC|AdvCdm";
    public final static String   LITTLEWORLD           = "LW:";
    public final static int      PACKETID_EVENT        = PacketIds.PLAYER + 100;
    public final static int      BLOCK_CLICK           = 0;
    public final static int      DIG_ONGOING           = 1;
    public final static int      DIG_BROKEN            = 2;

    public static PacketPipeline handler               = new PacketPipeline();

    @SideOnly(Side.CLIENT)
    public static void registerClientPacketHandlers() {
        MinecraftForge.EVENT_BUS.register(new ClientNetworkEvent());

        handler.getPacketHandler(PacketIds.LOGIN).registerClientExecutor(CommandLib.SETTINGS,
                                                                         new ClientPacketLittleBlocksLoginExecutor());

        handler.getPacketHandler(PacketIds.UPDATE).registerClientExecutor(CommandLib.UPDATE_CLIENT,
                                                                          new ClientBlockChangeExecutor());

        handler.getPacketHandler(PacketIds.ENTITY).registerClientExecutor(CommandLib.ENTITY_COLLECTION,
                                                                          new ClientLittleCollectionExecutor());

        handler.getPacketHandler(PacketIds.PLAYER).registerClientExecutor(CommandLib.COPIER_MESSAGE,
                                                                          new ClientCopierNotifyExecutor());

        handler.getPacketHandler(PACKETID_EVENT).registerClientExecutor(CommandLib.BLOCK_EVENT,
                                                                        new ClientBlockEventExecutor());
    }

    public static void registerPacketHandlers() {
        MinecraftForge.EVENT_BUS.register(new NetworkEvent());

        PacketLittleNotifyHandler playerHandler = new PacketLittleNotifyHandler();
        playerHandler.registerServerExecutor(CommandLib.WAND_SWITCH,
                                             new PacketLittleWandSwitchExecutor());

        handler.registerPacketHandler(PacketIds.PLAYER,
                                      playerHandler);

        PacketLittleBlockHandler littleBlockHandler = new PacketLittleBlockHandler();
        littleBlockHandler.registerServerExecutor(CommandLib.BLOCK_ACTIVATED,
                                                  new PacketLittleBlockActivatedExecutor());
        littleBlockHandler.registerServerExecutor(CommandLib.BLOCK_CLICKED,
                                                  new PacketLittleBlockClickedExecutor());

        handler.registerPacketHandler(PacketIds.LOGIN,
                                      new PacketLoginHandler());

        handler.registerPacketHandler(PacketIds.TILE,
                                      littleBlockHandler);

        handler.registerPacketHandler(PacketIds.ENTITY,
                                      new PacketLittleBlockCollectionHandler());

        handler.registerPacketHandler(PACKETID_EVENT,
                                      new PacketLittleBlockEventHandler());

        handler.registerPacketHandler(PacketIds.UPDATE,
                                      new PacketLittleBlocksHandler());
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
        PacketLittleBlockChange changePacket = new PacketLittleBlockChange(x, y, z, world);
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
