package com.dashomi.actionregulator.listeners;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.TriggerType;
import com.dashomi.actionregulator.utils.RegistryStringCreator;
import com.dashomi.actionregulator.utils.RuleAppliedNotification;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class UseBlockEvent {
    public static InteractionResult useBlockListener(Player player, Level world, InteractionHand hand, BlockHitResult blockHitResult) {
        if (!player.isSpectator()) {
            String currentDimension = RegistryStringCreator.getDimensionId(world);
            String heldItemId = RegistryStringCreator.getItemId(player, hand);
            String targetBlockId = RegistryStringCreator.getBlockId(world, blockHitResult.getBlockPos());

            for (RuleModule rule : ActionRegulatorConfig.get().rules) {
                if (!rule.enabled || rule.triggerType != TriggerType.ON_USE) continue;

                if (rule.activeDimensions.isEmpty() || !rule.activeDimensions.contains(currentDimension)) continue;

                if (!rule.handItems.isEmpty() && !rule.handItems.contains(heldItemId)) continue;

                if (!rule.targetBlocks.isEmpty() && !rule.targetBlocks.contains(targetBlockId)) continue;

                RuleAppliedNotification.sendNotification(player, rule);

                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.PASS;
    }
}
