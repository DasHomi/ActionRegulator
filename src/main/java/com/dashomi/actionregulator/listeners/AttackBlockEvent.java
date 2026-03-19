package com.dashomi.actionregulator.listeners;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.TargetMode;
import com.dashomi.actionregulator.enums.TriggerType;
import com.dashomi.actionregulator.utils.FilterUtils;
import com.dashomi.actionregulator.utils.RegistryStringCreator;
import com.dashomi.actionregulator.utils.RuleAppliedNotification;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AttackBlockEvent {
    public static InteractionResult attackBlockListener(Player player, Level world, InteractionHand hand, BlockPos blockPos, Direction direction) {
        if (!player.isSpectator()) {
            String currentDimension = RegistryStringCreator.getDimensionId(world);
            String targetBlockId = RegistryStringCreator.getBlockId(world, blockPos);

            for (RuleModule rule : ActionRegulatorConfig.get().rules) {
                if (!rule.enabled || rule.triggerType != TriggerType.ON_ATTACK) continue;

                if (rule.targetMode != TargetMode.BLOCKS) continue;

                if (rule.activeDimensions.isEmpty() || !rule.activeDimensions.contains(currentDimension)) continue;

                if (FilterUtils.doesNotMatchFilter(rule.targetBlocks, rule.invertTargetBlocks, targetBlockId)) continue;

                if (FilterUtils.doesNotMatchHandItemFilter(rule, player, hand)) continue;

                RuleAppliedNotification.sendNotification(player, rule);

                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.PASS;
    }
}
