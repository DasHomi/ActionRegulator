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

public class TargetEntitiesSectionComponent extends RegistrySectionComponent {

    private static final List<String> ALL_ENTITIES = BuiltInRegistries.ENTITY_TYPE.keySet()
            .stream().map(Object::toString).sorted().toList();

    private static final ButtonComponent.Renderer MODE_ON =
            ButtonComponent.Renderer.flat(0xFF2255AA, 0xFF3366CC, 0xFF1A4488);
    private static final ButtonComponent.Renderer MODE_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

    private final RuleModule rule;

    public TargetEntitiesSectionComponent(RuleModule rule) {
        super(
                "actionregulator.ui.section.targetEntities",
                4,
                () -> rule.invertTargetEntities,
                v -> rule.invertTargetEntities = v,
                rule.targetEntities,
                ALL_ENTITIES,
                "entity"
        );
        this.rule = rule;
    }

    @Override
    protected void buildExtraDropdowns(FlowLayout section) {
        FlowLayout dropdown = buildDropdown("Extra Options", container -> {

            FlowLayout modeRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
            modeRow.verticalAlignment(VerticalAlignment.CENTER);
            modeRow.gap(4);
            modeRow.child(UIComponents.label(
                    Component.translatable("actionregulator.ui.targetEntities.customName.label"))
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
                    rule.targetEntityCustomNameMode = mode;
                    for (int j = 0; j < btns.length; j++)
                        btns[j].renderer(j == idx ? MODE_ON : MODE_OFF);
                });
                btns[i].sizing(Sizing.content(), Sizing.fixed(14));
                btns[i].renderer(rule.targetEntityCustomNameMode == modes[i] ? MODE_ON : MODE_OFF);
                modeRow.child(btns[i]);
            }
            container.child(modeRow);

            FlowLayout nameRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
            nameRow.verticalAlignment(VerticalAlignment.CENTER);
            nameRow.gap(6);
            nameRow.child(UIComponents.label(
                    Component.translatable("actionregulator.ui.targetEntities.customName.input.label"))
                    .sizing(Sizing.content(), Sizing.content()));
            TextBoxComponent nameInput = UIComponents.textBox(Sizing.expand(), rule.targetEntityCustomNameFilter);
            nameInput.setMaxLength(64);
            nameInput.onChanged().subscribe(text -> rule.targetEntityCustomNameFilter = text);
            nameRow.child(nameInput);
            container.child(nameRow);
        });

        if (dropdown != null) section.child(dropdown);
    }
}
