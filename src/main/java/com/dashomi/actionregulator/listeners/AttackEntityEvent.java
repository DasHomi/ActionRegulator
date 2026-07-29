package com.dashomi.actionregulator.listeners;

import com.dashomi.actionregulator.enums.TargetMode;
import com.dashomi.actionregulator.enums.TriggerType;
import com.dashomi.actionregulator.utils.FilterUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.Nullable;

public class AttackEntityEvent {
    public static InteractionResult attackEntityListener(Player player, Level world, InteractionHand hand, Entity entity, @Nullable EntityHitResult result) {
        return RuleEngine.evaluate(player, world, hand, TriggerType.ON_ATTACK, TargetMode.ENTITIES,
                rule -> FilterUtils.doesNotMatchTargetEntityFilter(rule, entity));
    }
}