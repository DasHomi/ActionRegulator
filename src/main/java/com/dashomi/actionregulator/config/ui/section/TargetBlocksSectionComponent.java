package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.BlockConditionMode;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
        FlowLayout dropdown = buildDropdown("Extra Options", container -> {
            container.child(buildConditionRow(
                    "actionregulator.ui.blockConditions.waterlogged",
                    () -> rule.waterloggedCondition,
                    v -> rule.waterloggedCondition = v));
        });

        if (dropdown != null) section.child(dropdown);
    }

    private FlowLayout buildConditionRow(
            String labelKey,
            Supplier<BlockConditionMode> getter,
            Consumer<BlockConditionMode> setter
    ) {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        row.verticalAlignment(VerticalAlignment.CENTER);
        row.gap(4);
        row.child(UIComponents.label(Component.translatable(labelKey))
                .sizing(Sizing.content(), Sizing.content()));

        BlockConditionMode[] modes = BlockConditionMode.values();
        ButtonComponent[] btns = new ButtonComponent[modes.length];

        for (int i = 0; i < modes.length; i++) {
            final BlockConditionMode mode = modes[i];
            final int idx = i;
            btns[i] = UIComponents.button(Component.translatable(getModeLabelKey(mode)), b -> {
                setter.accept(mode);
                for (int j = 0; j < btns.length; j++) {
                    btns[j].renderer(j == idx ? MODE_ON : MODE_OFF);
                }
            });
            btns[i].sizing(Sizing.content(), Sizing.fixed(14));
            btns[i].renderer(getter.get() == mode ? MODE_ON : MODE_OFF);
            row.child(btns[i]);
        }

        return row;
    }

    private String getModeLabelKey(BlockConditionMode mode) {
        return switch (mode) {
            case IGNORED -> "actionregulator.ui.blockConditions.mode.ignored";
            case REQUIRED -> "actionregulator.ui.blockConditions.mode.required";
            case FORBIDDEN -> "actionregulator.ui.blockConditions.mode.forbidden";
        };
    }

    private static String toTagSelector(String rawTag) {
        return rawTag.startsWith("minecraft:") ? rawTag.substring("minecraft:".length()) : rawTag;
    }
}