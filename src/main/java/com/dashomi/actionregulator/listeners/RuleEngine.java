package com.dashomi.actionregulator.listeners;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.TargetMode;
import com.dashomi.actionregulator.enums.TriggerType;
import com.dashomi.actionregulator.utils.FilterUtils;
import com.dashomi.actionregulator.utils.RegistryStringCreator;
import com.dashomi.actionregulator.utils.RuleAppliedNotification;
import java.util.function.Predicate;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class RuleEngine {

    /**
     * Returns {@link InteractionResult#FAIL} for the first enabled rule that matches every condition
     * (after firing its notification), or {@link InteractionResult#PASS} if no rule applies.
     *
     * @param doesNotMatchTarget target-specific check; returns {@code true} when the rule does not
     *                           apply to the current target (mirrors the {@code FilterUtils.doesNotMatch*}
     *                           convention), causing the rule to be skipped.
     */
    public static InteractionResult evaluate(
            Player player,
            Level world,
            InteractionHand hand,
            TriggerType trigger,
            TargetMode targetMode,
            Predicate<RuleModule> doesNotMatchTarget
    ) {
        if (!FilterUtils.actionRegulatorIsNotDisabled()) {
            return InteractionResult.PASS;
        }

        String currentDimension = RegistryStringCreator.getDimensionId(world);

        for (RuleModule rule : ActionRegulatorConfig.get().rules) {
            if (!rule.enabled || rule.triggerType != trigger) continue;

            if (rule.targetMode != targetMode) continue;

            if (rule.activeDimensions.isEmpty() || !rule.activeDimensions.contains(currentDimension)) continue;

            if (FilterUtils.doesNotMatchPlayerConditions(rule, player)) continue;

            if (FilterUtils.doesNotMatchHandItemFilter(rule, player, hand)) continue;

            if (doesNotMatchTarget.test(rule)) continue;

            RuleAppliedNotification.sendNotification(player, rule);

            return InteractionResult.FAIL;
        }

        return InteractionResult.PASS;
    }
}