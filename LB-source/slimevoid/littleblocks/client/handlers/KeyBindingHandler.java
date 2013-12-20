package slimevoid.littleblocks.client.handlers;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import slimevoid.littleblocks.core.lib.PacketLib;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyBindingHandler extends KeyHandler {

	private Minecraft			mc;
	private static KeyBinding	switchAction	= new KeyBinding("LittleBlocks Wand-Action Change", Keyboard.KEY_O);

	public KeyBindingHandler() {
		super(new KeyBinding[] { switchAction }, new boolean[] { false });
		this.mc = FMLClientHandler.instance().getClient();
	}

	@Override
	public String getLabel() {
		return "LittleBlocks Wand-Action Change";
	}

	private static boolean	keyHasBeenPressed	= false;

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if (this.mc.currentScreen == null) {
			keyHasBeenPressed = true;
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if (keyHasBeenPressed) {
			keyHasBeenPressed = false;
			if (kb.equals(switchAction)) {
				PacketLib.wandModeSwitched();
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
