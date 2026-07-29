package com.dashomi.actionregulator.listeners;

import com.dashomi.actionregulator.enums.TargetMode;
import com.dashomi.actionregulator.enums.TriggerType;
import com.dashomi.actionregulator.utils.FilterUtils;
import com.dashomi.actionregulator.utils.RegistryStringCreator;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class UseBlockEvent {
    public static InteractionResult useBlockListener(Player player, Level world, InteractionHand hand, BlockHitResult blockHitResult) {
        BlockState blockState = world.getBlockState(blockHitResult.getBlockPos());
        String targetBlockId = RegistryStringCreator.getBlockId(blockState);

        return RuleEngine.evaluate(player, world, hand, TriggerType.ON_USE, TargetMode.BLOCKS,
                rule -> FilterUtils.doesNotMatchBlockFilter(rule, targetBlockId, blockState)
                        || FilterUtils.doesNotMatchBlockConditions(rule, blockState));
    }
}