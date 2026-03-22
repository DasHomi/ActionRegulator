package com.dashomi.actionregulator.listeners;

import com.dashomi.actionregulator.ActionregulatorClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class EndClientTickEvent {
    private static boolean wasTemporaryOverrideActive;

    public static void endClientTickListener(Minecraft client) {
        if (client.player == null) return;

        boolean isTemporaryOverrideActive = ActionregulatorClient.isTemporaryOverrideActive();

        if (isTemporaryOverrideActive) {
            client.player.displayClientMessage(Component.translatable("actionregulator.temporary_override.active"), true);
        } else if (wasTemporaryOverrideActive) {
            client.player.displayClientMessage(Component.empty(), true);
        }

        wasTemporaryOverrideActive = isTemporaryOverrideActive;
    }
}
