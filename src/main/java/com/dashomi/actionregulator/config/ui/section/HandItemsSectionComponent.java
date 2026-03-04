package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import io.wispforest.owo.ui.component.LabelComponent;
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
    protected void buildExtraOptions(FlowLayout container) {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        row.verticalAlignment(VerticalAlignment.CENTER);
        row.gap(6);

        LabelComponent label = UIComponents.label(
                Component.translatable("actionregulator.ui.handItems.minDurability.label"));
        label.sizing(Sizing.content(), Sizing.content());

        String initial = rule.handItemsMinDurability < 0 ? "" : String.valueOf(rule.handItemsMinDurability);
        TextBoxComponent input = UIComponents.textBox(Sizing.fixed(48), initial);
        input.setMaxLength(6);
        input.onChanged().subscribe(text -> {
            if (text.isBlank()) {
                rule.handItemsMinDurability = -1;
                return;
            }
            try {
                int val = Integer.parseInt(text);
                if (val >= 0) rule.handItemsMinDurability = val;
            } catch (NumberFormatException ignored) {}
        });

        row.child(label);
        row.child(input);
        container.child(row);
    }
}
