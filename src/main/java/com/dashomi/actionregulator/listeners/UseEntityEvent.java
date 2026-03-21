package com.dashomi.actionregulator.listeners;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.TargetMode;
import com.dashomi.actionregulator.enums.TriggerType;
import com.dashomi.actionregulator.utils.FilterUtils;
import com.dashomi.actionregulator.utils.RegistryStringCreator;
import com.dashomi.actionregulator.utils.RuleAppliedNotification;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public class UseEntityEvent {
    public static InteractionResult useEntityListener(Player player, Level world, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (!player.isSpectator()) {
            String currentDimension = RegistryStringCreator.getDimensionId(world);

            for (RuleModule rule : ActionRegulatorConfig.get().rules) {
                if (!rule.enabled || rule.triggerType != TriggerType.ON_USE) continue;

                if (rule.targetMode != TargetMode.ENTITIES) continue;

                if (rule.activeDimensions.isEmpty() || !rule.activeDimensions.contains(currentDimension)) continue;

                if (FilterUtils.doesNotMatchHandItemFilter(rule, player, hand)) continue;

                if (FilterUtils.doesNotMatchTargetEntityFilter(rule, entity)) continue;

                RuleAppliedNotification.sendNotification(player, rule);

                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.PASS;
    }
}
