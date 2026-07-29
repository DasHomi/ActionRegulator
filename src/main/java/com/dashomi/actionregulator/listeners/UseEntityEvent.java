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
import org.jetbrains.annotations.Nullable;

public class UseEntityEvent {
    public static InteractionResult useEntityListener(Player player, Level world, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        return RuleEngine.evaluate(player, world, hand, TriggerType.ON_USE, TargetMode.ENTITIES,
                rule -> FilterUtils.doesNotMatchTargetEntityFilter(rule, entity));
    }
}