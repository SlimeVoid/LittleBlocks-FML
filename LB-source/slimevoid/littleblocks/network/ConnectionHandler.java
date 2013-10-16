package slimevoid.littleblocks.network;

import slimevoid.littleblocks.core.LittleBlocks;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class ConnectionHandler implements IConnectionHandler {

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
		LittleBlocks.proxy.playerLoggedIn(player, netHandler, manager);
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		return LittleBlocks.proxy.connectionReceived(netHandler, manager);
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
		LittleBlocks.proxy.connectionOpened(netClientHandler, server, port, manager);
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
		LittleBlocks.proxy.connectionOpened(netClientHandler, server, manager);
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
		LittleBlocks.proxy.connectionClosed(manager);
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
		LittleBlocks.proxy.clientLoggedIn(clientHandler, manager, login);
	}

}
