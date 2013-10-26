package slimevoid.littleblocks.client.handlers;

import java.util.EnumSet;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import slimevoid.littleblocks.core.lib.CommandLib;
import slimevoid.littleblocks.items.wand.EnumWandAction;
import slimevoid.littleblocks.network.packets.PacketLittleNotify;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class KeyBindingHandler extends KeyHandler {

	private static KeyBinding	switchAction	= new KeyBinding("LittleBlocks Wand-Action Change", Keyboard.KEY_O);

	public KeyBindingHandler() {
		super(new KeyBinding[] { switchAction }, new boolean[] { false });
	}

	@Override
	public String getLabel() {
		return "LittleBlocks Wand-Action Change";
	}

	private static boolean	keyHasBeenPressed	= false;

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		keyHasBeenPressed = true;
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if (keyHasBeenPressed) {
			keyHasBeenPressed = false;
			if (kb.equals(switchAction)) {
				PacketLittleNotify packet = new PacketLittleNotify(CommandLib.WAND_SWITCH);
				PacketDispatcher.sendPacketToServer(packet.getPacket());
				EnumWandAction.setNextWandAction();
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
