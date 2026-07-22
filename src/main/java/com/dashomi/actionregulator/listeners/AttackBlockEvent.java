package com.dashomi.actionregulator.listeners;

import com.dashomi.actionregulator.enums.TargetMode;
import com.dashomi.actionregulator.enums.TriggerType;
import com.dashomi.actionregulator.utils.FilterUtils;
import com.dashomi.actionregulator.utils.RegistryStringCreator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AttackBlockEvent {
    public static InteractionResult attackBlockListener(Player player, Level world, InteractionHand hand, BlockPos blockPos, Direction direction) {
        BlockState blockState = world.getBlockState(blockPos);
        String targetBlockId = RegistryStringCreator.getBlockId(blockState);

        return RuleEngine.evaluate(player, world, hand, TriggerType.ON_ATTACK, TargetMode.BLOCKS,
                rule -> FilterUtils.doesNotMatchBlockFilter(rule, targetBlockId, blockState)
                        || FilterUtils.doesNotMatchBlockConditions(rule, blockState));
    }
}