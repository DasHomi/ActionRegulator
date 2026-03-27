package com.dashomi.actionregulator.utils;

import com.dashomi.actionregulator.config.RuleModule;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

public class RuleAppliedNotification {
    public static void sendNotification(Player player, RuleModule rule) {
        switch (rule.notificationType) {
            case SOUND:
                player.playSound(SoundEvents.NOTE_BLOCK_BASEDRUM.value(), 1.0f, 1.1f);
                break;
            case SYMBOL:
                player.sendOverlayMessage(Component.literal("❌").withStyle(ChatFormatting.RED));
                break;
            case TEXT:
                player.sendOverlayMessage(Component.literal(rule.notificationMessage).withStyle(ChatFormatting.WHITE));
                break;
        }
    }
}
