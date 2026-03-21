package com.dashomi.actionregulator.utils;

import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.CustomNameMode;
import com.dashomi.actionregulator.enums.HandItemMode;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FilterUtils {
    public static boolean doesNotMatchFilter(List<String> list, boolean invert, String id) {
        if (!invert) {
            return !list.isEmpty() && !list.contains(id);
        } else {
            return list.isEmpty() || list.contains(id);
        }
    }

    public static boolean doesNotMatchHandItemFilter(RuleModule rule, Player player, InteractionHand hand) {
        if (rule.handItemMode == HandItemMode.MAINHAND && hand != InteractionHand.MAIN_HAND) {
            return true;
        }
        if (rule.handItemMode == HandItemMode.OFFHAND && hand != InteractionHand.OFF_HAND) {
            return true;
        }

        String handItemId = RegistryStringCreator.getItemId(player, hand);
        ItemStack handItem = player.getItemInHand(hand);

        if (doesNotMatchFilter(rule.handItems, rule.invertHandItems, handItemId)) {
            return true;
        }

        boolean hasCustomName = handItem.has(DataComponents.CUSTOM_NAME);
        if (rule.handItemCustomNameMode == CustomNameMode.NAMED && !hasCustomName) {
            return true;
        }
        if (rule.handItemCustomNameMode == CustomNameMode.UNNAMED && hasCustomName) {
            return true;
        }

        if (rule.handItemDurabilityThreshold >= 0 && handItem.isDamageableItem()) {
            int remainingDurability = handItem.getMaxDamage() - handItem.getDamageValue();
            if (remainingDurability > rule.handItemDurabilityThreshold) {
                return true;
            }
        }

        String customNameFilter = rule.handItemCustomNameFilter == null ? "" : rule.handItemCustomNameFilter.trim();
        if (!customNameFilter.isEmpty()) {
            if (!hasCustomName) {
                return false;
            }
            String itemName = handItem.getHoverName().getString();
            return !itemName.toLowerCase(Locale.ROOT).contains(customNameFilter.toLowerCase(Locale.ROOT));
        }

        return false;
    }

    public static boolean doesNotMatchTargetEntityFilter(RuleModule rule, Entity entity) {
        String targetEntityId = RegistryStringCreator.getEntityId(entity);
        if (doesNotMatchFilter(rule.targetEntities, rule.invertTargetEntities, targetEntityId)) {
            return true;
        }

        boolean hasCustomName = entity.hasCustomName();
        if (rule.targetEntityCustomNameMode == CustomNameMode.NAMED && !hasCustomName) {
            return true;
        }
        if (rule.targetEntityCustomNameMode == CustomNameMode.UNNAMED && hasCustomName) {
            return true;
        }

        String customNameFilter = rule.targetEntityCustomNameFilter == null ? "" : rule.targetEntityCustomNameFilter.trim();
        if (!customNameFilter.isEmpty()) {
            if (!hasCustomName) {
                return false;
            }
            String entityName = entity.getCustomName() == null ? "" : entity.getCustomName().getString();
            return !entityName.toLowerCase(Locale.ROOT).contains(customNameFilter.toLowerCase(Locale.ROOT));
        }

        return false;
    }
}
