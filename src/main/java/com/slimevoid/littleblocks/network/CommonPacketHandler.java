package com.slimevoid.littleblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.slimevoid.library.data.Logger;
import com.slimevoid.library.network.handlers.SubPacketHandler;
import com.slimevoid.littleblocks.core.LoggerLittleBlocks;
import com.slimevoid.littleblocks.core.lib.CoreLib;
import com.slimevoid.littleblocks.core.lib.PacketLib;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class CommonPacketHandler implements IPacketHandler {

    private static Map<Integer, SubPacketHandler> commonHandlers;

    /**
     * Initializes the commonHandler Map
     */
    public static void init() {
        commonHandlers = new HashMap<Integer, SubPacketHandler>();
    }

    /**
     * Register a sub-handler with the server-side packet handler.
     * 
     * @param packetID
     *            Packet ID for the sub-handler to handle.
     * @param handler
     *            The sub-handler.
     */
    public static void registerPacketHandler(int packetID, SubPacketHandler handler) {
        if (commonHandlers.containsKey(packetID)) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(CommonPacketHandler.class.toString())).write(false,
                                                                                                               "PacketID ["
                                                                                                                       + packetID
                                                                                                                       + "] already registered.",
                                                                                                               Logger.LogLevel.ERROR);
            throw new RuntimeException("PacketID [" + packetID
                                       + "] already registered.");
        }
        commonHandlers.put(packetID,
                           handler);
    }

    /**
     * Retrieves the registered sub-handler from the server side list
     * 
     * @param packetID
     * @return the sub-handler
     */
    public static SubPacketHandler getPacketHandler(int packetID) {
        if (!commonHandlers.containsKey(packetID)) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(CommonPacketHandler.class.toString())).write(false,
                                                                                                               "Tried to get a Packet Handler for ID: "
                                                                                                                       + packetID
                                                                                                                       + " that has not been registered.",
                                                                                                               Logger.LogLevel.WARNING);
            throw new RuntimeException("Tried to get a Packet Handler for ID: "
                                       + packetID
                                       + " that has not been registered.");
        }
        return commonHandlers.get(packetID);
    }

    /**
     * The server-side packet handler receives a packet.<br>
     * Fetches the packet ID and routes it on to sub-handlers.
     */
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
        try {
            if (packet.channel.equals(CoreLib.MOD_CHANNEL)) {
                int packetID = data.read();
                getPacketHandler(packetID).onPacketData(manager,
                                                        packet,
                                                        player);
            } else {
                PacketLib.tryMinecraftPacket(manager,
                                             packet,
                                             player);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
