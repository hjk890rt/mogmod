package com.example.creativetoggle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerAbilities;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class CreativeToggleClient implements ClientModInitializer {

    private static final UUID ALLOWED_UUID = UUID.fromString("c623b03e-3e62-44b8-ba62-d017eaa10258");
    private static boolean enabled = false;

    @Override
    public void onInitializeClient() {

        KeyBinding toggleKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.creativetoggle.toggle",
                GLFW.GLFW_KEY_G,
                "category.creativetoggle"
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (!client.player.getUuid().equals(ALLOWED_UUID)) return;

            while (toggleKey.wasPressed()) {
                enabled = !enabled;

                if (enabled) {
                    openCreativeMenu(client);
                } else {
                    // just close screen if open
                    if (client.currentScreen instanceof CreativeInventoryScreen) {
                        client.setScreen(null);
                    }
                }
            }

            // FORCE survival-like abilities every tick
            PlayerAbilities a = client.player.getAbilities();
            a.allowFlying = false;
            a.flying = false;
            a.creativeMode = false;
            client.player.sendAbilitiesUpdate();
        });
    }

    private void openCreativeMenu(MinecraftClient client) {
        // temporarily enable creative flag JUST to open menu
        PlayerAbilities a = client.player.getAbilities();
        a.creativeMode = true;
        client.player.sendAbilitiesUpdate();

        client.setScreen(new CreativeInventoryScreen(client.player));

        // immediately revert so gameplay stays survival
        a.creativeMode = false;
        client.player.sendAbilitiesUpdate();
    }
}
