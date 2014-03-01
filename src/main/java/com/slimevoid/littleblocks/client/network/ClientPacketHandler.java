package com.slimevoid.littleblocks.client.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;

import com.slimevoid.library.data.Logger;
import com.slimevoid.library.network.handlers.SubPacketHandler;
import com.slimevoid.littleblocks.core.LoggerLittleBlocks;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler implements IPacketHandler {

    private static Map<Integer, SubPacketHandler> clientHandlers;

    public static void init() {
        clientHandlers = new HashMap<Integer, SubPacketHandler>();
    }

    public static void registerPacketHandler(int packetID, SubPacketHandler handler) {
        if (clientHandlers.containsKey(packetID)) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(ClientPacketHandler.class.toString())).write(false,
                                                                                                               "PacketID ["
                                                                                                                       + packetID
                                                                                                                       + "] already registered.",
                                                                                                               Logger.LogLevel.ERROR);
            throw new RuntimeException("PacketID [" + packetID
                                       + "] already registered.");
        }
        clientHandlers.put(packetID,
                           handler);
    }

    /**
     * Retrieves the registered sub-handler from the server side list
     * 
     * @param packetID
     * @return the sub-handler
     */
    public static SubPacketHandler getPacketHandler(int packetID) {
        if (!clientHandlers.containsKey(packetID)) {
            LoggerLittleBlocks.getInstance(Logger.filterClassName(ClientPacketHandler.class.toString())).write(false,
                                                                                                               "Tried to get a Packet Handler for ID: "
                                                                                                                       + packetID
                                                                                                                       + " that has not been registered.",
                                                                                                               Logger.LogLevel.WARNING);
            throw new RuntimeException("Tried to get a Packet Handler for ID: "
                                       + packetID
                                       + " that has not been registered.");
        }
        return clientHandlers.get(packetID);
    }

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
        try {
            int packetID = data.read();
            getPacketHandler(packetID).onPacketData(manager,
                                                    packet,
                                                    player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
