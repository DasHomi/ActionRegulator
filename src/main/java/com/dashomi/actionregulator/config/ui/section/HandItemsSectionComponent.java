package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.HandItemDurabilityMode;
import com.dashomi.actionregulator.enums.HandItemMode;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

public class HandItemsSectionComponent extends RegistrySectionComponent {

    private static final List<String> ALL_ITEMS = BuiltInRegistries.ITEM.keySet()
            .stream().map(Object::toString).sorted().toList();
    private static final List<String> ALL_ITEM_TAGS = BuiltInRegistries.ITEM.getTags()
            .map(tag -> toTagSelector(tag.key().location().toString())).distinct().sorted().toList();

    private final RuleModule rule;

    public HandItemsSectionComponent(RuleModule rule) {
        super(
                "actionregulator.ui.section.handItems",
                0,
                () -> rule.invertHandItems,
                v -> rule.invertHandItems = v,
                rule.handItems,
                ALL_ITEMS,
                "item",
                rule.handItemTags,
                ALL_ITEM_TAGS,
                "item_tag"
        );
        this.rule = rule;
    }

    @Override
    protected void buildExtraDropdowns(FlowLayout section) {
        FlowLayout dropdown = buildDropdown("Extra Options", container -> {

            FlowLayout handModeRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
            handModeRow.verticalAlignment(VerticalAlignment.CENTER);
            handModeRow.gap(4);
            handModeRow.child(UIComponents.label(
                    Component.translatable("actionregulator.ui.handItems.mode.label"))
                    .sizing(Sizing.content(), Sizing.content()));

            HandItemMode[] handModes = HandItemMode.values();
            String[] handModeLabelKeys = {
                    "actionregulator.ui.handItems.mode.both",
                    "actionregulator.ui.handItems.mode.mainhand",
                    "actionregulator.ui.handItems.mode.offhand"
            };
            ButtonComponent[] handModeBtns = new ButtonComponent[handModes.length];
            for (int i = 0; i < handModes.length; i++) {
                final HandItemMode mode = handModes[i];
                final int idx = i;
                handModeBtns[i] = UIComponents.button(Component.translatable(handModeLabelKeys[i]), b -> {
                    rule.handItemMode = mode;
                    for (int j = 0; j < handModeBtns.length; j++) {
                        handModeBtns[j].renderer(j == idx ? MODE_ON : MODE_OFF);
                    }
                });
                handModeBtns[i].sizing(Sizing.content(), Sizing.fixed(14));
                handModeBtns[i].renderer(rule.handItemMode == mode ? MODE_ON : MODE_OFF);
                handModeRow.child(handModeBtns[i]);
            }
            container.child(handModeRow);

            FlowLayout durRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
            durRow.verticalAlignment(VerticalAlignment.CENTER);
            durRow.gap(4);
            durRow.child(UIComponents.label(
                    Component.translatable("actionregulator.ui.handItems.durability.label"))
                    .sizing(Sizing.content(), Sizing.content()));

            String initialDur = rule.handItemDurabilityThreshold < 0 ? "" : String.valueOf(rule.handItemDurabilityThreshold);
            TextBoxComponent durInput = UIComponents.textBox(Sizing.fixed(48), initialDur);
            durInput.setMaxLength(6);
            durInput.onChanged().subscribe(text -> {
                String digitsOnly = text.replaceAll("\\D", "");
                if (!digitsOnly.equals(text)) {
                    durInput.text(digitsOnly);
                    return;
                }
                if (text.isBlank()) {
                    rule.handItemDurabilityThreshold = -1;
                    return;
                }
                try {
                    int val = Integer.parseInt(text);
                    if (val >= 0) rule.handItemDurabilityThreshold = val;
                } catch (NumberFormatException ignored) {}
            });

            HandItemDurabilityMode[] durabilityModes = {
                    HandItemDurabilityMode.IGNORED,
                    HandItemDurabilityMode.ABOVE,
                    HandItemDurabilityMode.BELOW
            };
            String[] durabilityModeLabelKeys = {
                    "actionregulator.ui.handItems.durability.ignored",
                    "actionregulator.ui.handItems.durability.above",
                    "actionregulator.ui.handItems.durability.below"
            };
            ButtonComponent[] durabilityModeButtons = new ButtonComponent[durabilityModes.length];
            for (int i = 0; i < durabilityModes.length; i++) {
                final HandItemDurabilityMode mode = durabilityModes[i];
                final int idx = i;
                durabilityModeButtons[i] = UIComponents.button(Component.translatable(durabilityModeLabelKeys[i]), b -> {
                    rule.handItemDurabilityMode = mode;
                    durInput.setEditable(mode != HandItemDurabilityMode.IGNORED);
                    for (int j = 0; j < durabilityModeButtons.length; j++) {
                        durabilityModeButtons[j].renderer(j == idx ? MODE_ON : MODE_OFF);
                    }
                });
                durabilityModeButtons[i].sizing(Sizing.content(), Sizing.fixed(14));
                durabilityModeButtons[i].renderer(rule.handItemDurabilityMode == mode ? MODE_ON : MODE_OFF);
                durRow.child(durabilityModeButtons[i]);
            }
            durInput.setEditable(rule.handItemDurabilityMode != HandItemDurabilityMode.IGNORED);
            durRow.child(durInput);
            container.child(durRow);

            buildCustomNameOptions(
                    container,
                    () -> rule.handItemCustomNameMode,
                    v -> rule.handItemCustomNameMode = v,
                    () -> rule.handItemCustomNameFilter,
                    v -> rule.handItemCustomNameFilter = v
            );
        });

        if (dropdown != null) section.child(dropdown);
    }

    private static String toTagSelector(String rawTag) {
        return rawTag.startsWith("minecraft:") ? rawTag.substring("minecraft:".length()) : rawTag;
    }
}
