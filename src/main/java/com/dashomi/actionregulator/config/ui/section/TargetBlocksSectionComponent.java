package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.config.ui.ConditionRowComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import java.util.List;

public class TargetBlocksSectionComponent extends RegistrySectionComponent {

    private static final List<String> ALL_BLOCKS = BuiltInRegistries.BLOCK.keySet()
            .stream().map(Object::toString).sorted().toList();
    private static final List<String> ALL_BLOCK_TAGS = BuiltInRegistries.BLOCK.getTags()
            .map(tag -> toTagSelector(tag.key().location().toString())).distinct().sorted().toList();

    private final RuleModule rule;

    public TargetBlocksSectionComponent(RuleModule rule) {
        super(
                "actionregulator.ui.section.targetBlocks",
                4,
                () -> rule.invertTargetBlocks,
                v -> rule.invertTargetBlocks = v,
                rule.targetBlocks,
                ALL_BLOCKS,
                "block",
                rule.targetBlockTags,
                ALL_BLOCK_TAGS,
                "block_tag"
        );
        this.rule = rule;
    }

    @Override
    protected void buildExtraDropdowns(FlowLayout section) {
        FlowLayout dropdown = buildDropdown(
                Component.translatable("actionregulator.ui.section.extraOptions"),
                container -> container.child(new ConditionRowComponent(
                        "actionregulator.ui.blockConditions.waterlogged",
                        () -> rule.waterloggedCondition,
                        v -> rule.waterloggedCondition = v).build()));

        if (dropdown != null) section.child(dropdown);
    }

    private static String toTagSelector(String rawTag) {
        return rawTag.startsWith("minecraft:") ? rawTag.substring("minecraft:".length()) : rawTag;
    }
}