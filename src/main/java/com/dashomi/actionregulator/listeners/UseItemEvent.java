package com.dashomi.actionregulator.listeners;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.TargetMode;
import com.dashomi.actionregulator.enums.TriggerType;
import com.dashomi.actionregulator.utils.FilterUtils;
import com.dashomi.actionregulator.utils.RegistryStringCreator;
import com.dashomi.actionregulator.utils.RuleAppliedNotification;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class UseItemEvent {
    public static InteractionResult useItemListener(Player player, Level world, InteractionHand hand) {
        if (!player.isSpectator() && FilterUtils.actionRegulatorIsNotDisabled()) {
            String currentDimension = RegistryStringCreator.getDimensionId(world);

            for (RuleModule rule : ActionRegulatorConfig.get().rules) {
                if (!rule.enabled || rule.triggerType != TriggerType.ON_USE) continue;

                if (rule.activeDimensions.isEmpty() || !rule.activeDimensions.contains(currentDimension)) continue;

                if (FilterUtils.doesNotMatchHandItemFilter(rule, player, hand)) continue;

                boolean activeTargetSet = rule.targetMode == TargetMode.BLOCKS
                        ? !rule.targetBlocks.isEmpty() || !rule.targetBlockTags.isEmpty()
                        : !rule.targetEntities.isEmpty() || !rule.targetEntityTypeTags.isEmpty();
                if (activeTargetSet) continue;

                ItemStack stack = player.getItemInHand(hand);
                if (!stack.has(DataComponents.CONSUMABLE)) continue;

                RuleAppliedNotification.sendNotification(player, rule);

                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.PASS;
    }
}
