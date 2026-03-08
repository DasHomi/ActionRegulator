package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.CustomNameMode;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import java.util.List;

public class HandItemsSectionComponent extends RegistrySectionComponent {

    private static final List<String> ALL_ITEMS = BuiltInRegistries.ITEM.keySet()
            .stream().map(Object::toString).sorted().toList();

    private static final ButtonComponent.Renderer MODE_ON =
            ButtonComponent.Renderer.flat(0xFF2255AA, 0xFF3366CC, 0xFF1A4488);
    private static final ButtonComponent.Renderer MODE_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

    private final RuleModule rule;

    public HandItemsSectionComponent(RuleModule rule) {
        super(
                "actionregulator.ui.section.handItems",
                0,
                () -> rule.invertHandItems,
                v -> rule.invertHandItems = v,
                rule.handItems,
                ALL_ITEMS,
                "item"
        );
        this.rule = rule;
    }

    @Override
    protected void buildExtraDropdowns(FlowLayout section) {
        FlowLayout dropdown = buildDropdown("Extra Options", container -> {

            FlowLayout durRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
            durRow.verticalAlignment(VerticalAlignment.CENTER);
            durRow.gap(6);
            durRow.child(UIComponents.label(
                    Component.translatable("actionregulator.ui.handItems.durabilityThreshold.label"))
                    .sizing(Sizing.content(), Sizing.content()));
            String initialDur = rule.handItemDurabilityThreshold < 0 ? "" : String.valueOf(rule.handItemDurabilityThreshold);
            TextBoxComponent durInput = UIComponents.textBox(Sizing.fixed(48), initialDur);
            durInput.setMaxLength(6);
            durInput.onChanged().subscribe(text -> {
                String digitsOnly = text.replaceAll("\\D", "");
                if (!digitsOnly.equals(text)) { durInput.text(digitsOnly); return; }
                if (text.isBlank()) { rule.handItemDurabilityThreshold = -1; return; }
                try { int val = Integer.parseInt(text); if (val >= 0) rule.handItemDurabilityThreshold = val; }
                catch (NumberFormatException ignored) {}
            });
            durRow.child(durInput);
            container.child(durRow);

            FlowLayout modeRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
            modeRow.verticalAlignment(VerticalAlignment.CENTER);
            modeRow.gap(4);
            modeRow.child(UIComponents.label(
                    Component.translatable("actionregulator.ui.handItems.customName.label"))
                    .sizing(Sizing.content(), Sizing.content()));

            CustomNameMode[] modes = CustomNameMode.values();
            String[] labelKeys = {
                    "actionregulator.ui.handItems.customName.any",
                    "actionregulator.ui.handItems.customName.named",
                    "actionregulator.ui.handItems.customName.unnamed"
            };
            ButtonComponent[] btns = new ButtonComponent[modes.length];
            for (int i = 0; i < modes.length; i++) {
                final CustomNameMode mode = modes[i];
                final int idx = i;
                btns[i] = UIComponents.button(Component.translatable(labelKeys[i]), b -> {
                    rule.handItemCustomNameMode = mode;
                    for (int j = 0; j < btns.length; j++)
                        btns[j].renderer(j == idx ? MODE_ON : MODE_OFF);
                });
                btns[i].sizing(Sizing.content(), Sizing.fixed(14));
                btns[i].renderer(rule.handItemCustomNameMode == modes[i] ? MODE_ON : MODE_OFF);
                modeRow.child(btns[i]);
            }
            container.child(modeRow);

            FlowLayout nameRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
            nameRow.verticalAlignment(VerticalAlignment.CENTER);
            nameRow.gap(6);
            nameRow.child(UIComponents.label(
                    Component.translatable("actionregulator.ui.handItems.customName.input.label"))
                    .sizing(Sizing.content(), Sizing.content()));
            TextBoxComponent nameInput = UIComponents.textBox(Sizing.expand(), rule.handItemCustomNameFilter);
            nameInput.setMaxLength(64);
            nameInput.onChanged().subscribe(text -> rule.handItemCustomNameFilter = text);
            nameRow.child(nameInput);
            container.child(nameRow);
        });

        if (dropdown != null) section.child(dropdown);
    }
}
