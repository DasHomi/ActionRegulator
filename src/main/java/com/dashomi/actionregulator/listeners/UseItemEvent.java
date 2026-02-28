package com.dashomi.actionregulator.listeners;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.TriggerType;
import com.dashomi.actionregulator.utils.FilterUtils;
import com.dashomi.actionregulator.utils.RegistryStringCreator;
import com.dashomi.actionregulator.utils.RuleAppliedNotification;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class UseItemEvent {
    public static InteractionResult useItemListener(Player player, Level world, InteractionHand hand) {
        if (!player.isSpectator()) {
            String currentDimension = RegistryStringCreator.getDimensionId(world);
            String heldItemId = RegistryStringCreator.getItemId(player, hand);

            for (RuleModule rule : ActionRegulatorConfig.get().rules) {
                if (!rule.enabled || rule.triggerType != TriggerType.ON_USE) continue;

                if (rule.activeDimensions.isEmpty() || !rule.activeDimensions.contains(currentDimension)) continue;

                if (!FilterUtils.matchesFilter(rule.handItems, rule.invertHandItems, heldItemId)) continue;

                if (!rule.targetBlocks.isEmpty() || !rule.targetEntities.isEmpty()) continue;

                RuleAppliedNotification.sendNotification(player, rule);

                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.PASS;
    }
}
