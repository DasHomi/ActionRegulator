package com.dashomi.actionregulator.utils;

import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.CustomNameMode;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
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
}
