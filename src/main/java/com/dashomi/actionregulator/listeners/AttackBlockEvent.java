package com.dashomi.actionregulator.listeners;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.TriggerType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AttackBlockEvent {
    public static InteractionResult attackBlockListener(Player player, Level world, InteractionHand hand, BlockPos blockPos, Direction direction) {
        if (!player.isSpectator()) {
            String currentDimension = world.dimension().toString();
            currentDimension = currentDimension.substring(currentDimension.indexOf(" / ") + 3, currentDimension.length() - 1);

            BlockState blockState = world.getBlockState(blockPos);
            String currentBlock = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();

            ItemStack heldItem = player.getItemInHand(hand);
            String heldItemId = BuiltInRegistries.ITEM.getKey(heldItem.getItem()).toString();

            for (RuleModule rule : ActionRegulatorConfig.get().rules) {
                if (!rule.enabled || rule.triggerType != TriggerType.ON_BLOCK_BREAK) continue;

                if (rule.activeDimensions.isEmpty() || !rule.activeDimensions.contains(currentDimension)) continue;

                if (!rule.targetBlocks.isEmpty() && !rule.targetBlocks.contains(currentBlock)) continue;

                if (!rule.handItems.isEmpty() && !rule.handItems.contains(heldItemId)) continue;

                switch (rule.notificationType) {
                    case SOUND:
                        player.playSound(SoundEvents.NOTE_BLOCK_BASEDRUM.value(), 1.0f, 1.1f);
                        break;
                    case SYMBOL:
                        player.displayClientMessage(Component.literal("❌").withStyle(ChatFormatting.RED), true);
                        break;
                    case TEXT:
                        player.displayClientMessage(Component.literal(rule.notificationMessage).withStyle(ChatFormatting.WHITE), true);
                        break;
                }

                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.PASS;
    }
}
