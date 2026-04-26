package com.dashomi.actionregulator.utils;

import com.dashomi.actionregulator.ActionregulatorClient;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.CustomNameMode;
import com.dashomi.actionregulator.enums.HandItemDurabilityMode;
import com.dashomi.actionregulator.enums.HandItemMode;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class FilterUtils {
    public static boolean actionRegulatorIsNotDisabled() {
        return !ActionregulatorClient.isTemporaryOverrideActive();
    }

    public static boolean doesNotMatchBlockFilter(RuleModule rule, String blockId, BlockState blockState) {
        return doesNotMatchFilter(
                rule.targetBlocks,
                rule.targetBlockTags,
                rule.invertTargetBlocks,
                blockId,
                tagId -> {
                    Identifier parsed = parseTagId(tagId);
                    return parsed != null && blockState.is(TagKey.create(Registries.BLOCK, parsed));
                }
        );
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

        if (doesNotMatchFilter(
                rule.handItems,
                rule.handItemTags,
                rule.invertHandItems,
                handItemId,
                tagId -> {
                    Identifier parsed = parseTagId(tagId);
                    return parsed != null && handItem.is(TagKey.create(Registries.ITEM, parsed));
                })) {
            return true;
        }

        boolean hasCustomName = handItem.has(DataComponents.CUSTOM_NAME);
        if (rule.handItemCustomNameMode == CustomNameMode.CUSTOM_NAME && !hasCustomName) {
            return true;
        }
        if (rule.handItemCustomNameMode == CustomNameMode.DEFAULT_NAME && hasCustomName) {
            return true;
        }

        if (rule.handItemDurabilityMode != HandItemDurabilityMode.IGNORED
                && rule.handItemDurabilityThreshold >= 0
                && handItem.isDamageableItem()) {
            int remainingDurability = handItem.getMaxDamage() - handItem.getDamageValue();
            if (rule.handItemDurabilityMode == HandItemDurabilityMode.ABOVE) {
                if (remainingDurability < rule.handItemDurabilityThreshold) {
                    return true;
                }
            } else if (rule.handItemDurabilityMode == HandItemDurabilityMode.BELOW) {
                if (remainingDurability > rule.handItemDurabilityThreshold) {
                    return true;
                }
            }
        }

        if (rule.handItemCustomNameMode == CustomNameMode.FILTER) {
            String customNameFilter = rule.handItemCustomNameFilter == null ? "" : rule.handItemCustomNameFilter.trim();
            if (!customNameFilter.isEmpty()) {
                String itemName = handItem.getHoverName().getString();
                return !itemName.toLowerCase(Locale.ROOT).contains(customNameFilter.toLowerCase(Locale.ROOT));
            }
        }

        if (rule.handItemCustomNameMode == CustomNameMode.REGEX_FILTER) {
            String customNameFilter = rule.handItemCustomNameFilter == null ? "" : rule.handItemCustomNameFilter.trim();
            if (customNameFilter.isEmpty()) return true;
            String itemName = handItem.getHoverName().getString();
            return !matchesRegex(itemName, customNameFilter);
        }

        return false;
    }

    public static boolean doesNotMatchTargetEntityFilter(RuleModule rule, Entity entity) {
        String targetEntityId = RegistryStringCreator.getEntityId(entity);
        if (doesNotMatchFilter(
                rule.targetEntities,
                rule.targetEntityTypeTags,
                rule.invertTargetEntities,
                targetEntityId,
                tagId -> {
                    Identifier parsed = parseTagId(tagId);
                    return parsed != null && entity.getType().builtInRegistryHolder().is(TagKey.create(Registries.ENTITY_TYPE, parsed));
                })) {
            return true;
        }

        boolean hasCustomName = entity.hasCustomName();
        if (rule.targetEntityCustomNameMode == CustomNameMode.CUSTOM_NAME && !hasCustomName) {
            return true;
        }
        if (rule.targetEntityCustomNameMode == CustomNameMode.DEFAULT_NAME && hasCustomName) {
            return true;
        }

        if (rule.targetEntityCustomNameMode == CustomNameMode.FILTER) {
            String customNameFilter = rule.targetEntityCustomNameFilter == null ? "" : rule.targetEntityCustomNameFilter.trim();
            if (!customNameFilter.isEmpty()) {
                String entityName = entity.getName().getString();
                return !entityName.toLowerCase(Locale.ROOT).contains(customNameFilter.toLowerCase(Locale.ROOT));
            }
        }

        if (rule.targetEntityCustomNameMode == CustomNameMode.REGEX_FILTER) {
            String customNameFilter = rule.targetEntityCustomNameFilter == null ? "" : rule.targetEntityCustomNameFilter.trim();
            if (customNameFilter.isEmpty()) return true;
            String entityName = entity.getName().getString();
            return !matchesRegex(entityName, customNameFilter);
        }

        return false;
    }

    private static boolean matchesRegex(String value, String regex) {
        try {
            return Pattern.compile(regex).matcher(value).find();
        } catch (PatternSyntaxException ignored) {
            return false;
        }
    }

    private static boolean doesNotMatchFilter(
            List<String> idList,
            List<String> tagList,
            boolean invert,
            String id,
            Predicate<String> tagMatcher
    ) {
        List<String> ids = Objects.requireNonNullElse(idList, List.of());
        List<String> tags = Objects.requireNonNullElse(tagList, List.of());

        boolean hasAnySelector = !ids.isEmpty() || !tags.isEmpty();
        boolean idMatch = ids.contains(id);
        boolean tagMatch = tags.stream().anyMatch(tagMatcher);
        boolean anyMatch = idMatch || tagMatch;

        if (!invert) {
            return hasAnySelector && !anyMatch;
        } else {
            return !hasAnySelector || anyMatch;
        }
    }

    private static Identifier parseTagId(String rawTagId) {
        String tagId = rawTagId == null ? "" : rawTagId.trim();
        if (tagId.startsWith("#")) {
            tagId = tagId.substring(1);
        }

        if (tagId.isBlank()) {
            return null;
        }

        if (!tagId.contains(":")) {
            try {
                return Identifier.fromNamespaceAndPath("minecraft", tagId);
            } catch (Exception ignored) {
                return null;
            }
        }

        int split = tagId.indexOf(':');
        if (split <= 0 || split >= tagId.length() - 1) {
            return null;
        }

        try {
            return Identifier.fromNamespaceAndPath(tagId.substring(0, split), tagId.substring(split + 1));
        } catch (Exception ignored) {
            return null;
        }
    }
}
