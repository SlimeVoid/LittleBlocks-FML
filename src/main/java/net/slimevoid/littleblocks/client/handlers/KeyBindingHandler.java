package net.slimevoid.littleblocks.client.handlers;

import java.util.EnumSet;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.slimevoid.littleblocks.core.lib.PacketLib;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class KeyBindingHandler {

    public static KeyBinding LITTLEWAND_ACTION_KEY = new KeyBinding("LittleBlocks Wand-Action Change", Keyboard.KEY_O, "key.categories.misc");

    public KeyBindingHandler() {
        ClientRegistry.registerKeyBinding(LITTLEWAND_ACTION_KEY);
    }

    @SubscribeEvent
    public void keyInput(LivingUpdateEvent event) {
        if (event.entityLiving instanceof EntityPlayerSP) {
            if (LITTLEWAND_ACTION_KEY.isPressed()) {
                PacketLib.wandModeSwitched();
            }
        }
    }

}
