package com.dashomi.actionregulator.config.ui;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.NotificationType;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.CheckboxComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.CollapsibleContainer;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import java.util.List;

public class RuleModuleUi {

    private static final String[] NOTIF_LABELS = {"Off", "Sound", "Symbol", "Text"};

    private static final List<String> ALL_BLOCKS = BuiltInRegistries.BLOCK.keySet()
            .stream().map(Object::toString).sorted().toList();
    private static final List<String> ALL_ITEMS  = BuiltInRegistries.ITEM.keySet()
            .stream().map(Object::toString).sorted().toList();

    private final RuleModule rule;

    public RuleModuleUi(RuleModule rule) {
        this.rule = rule;
    }

    public CollapsibleContainer build(ActionRegulatorConfig config, FlowLayout moduleList) {
        CollapsibleContainer card = UIContainers.collapsible(
                Sizing.fill(100), Sizing.content(),
                Component.literal(rule.name), true
        );
        card.margins(Insets.bottom(4));

        FlowLayout header = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        header.verticalAlignment(VerticalAlignment.CENTER);
        header.margins(Insets.bottom(6));
        header.gap(4);

        TextBoxComponent nameBox = UIComponents.textBox(Sizing.fill(75), rule.name);
        nameBox.onChanged().subscribe(v -> {
            rule.name = v;

            card.titleLayout().children().stream()
                    .filter(c -> c instanceof LabelComponent)
                    .map(c -> (LabelComponent) c)
                    .findFirst()
                    .ifPresent(l -> l.text(Component.literal(v)));
        });

        CheckboxComponent enabledToggle = UIComponents.checkbox(Component.literal("Enabled"));
        enabledToggle.checked(rule.enabled);
        enabledToggle.onChanged(checked -> rule.enabled = checked);

        ButtonComponent removeBtn = UIComponents.button(Component.literal("✕ Remove"), btn -> {
            config.rules.remove(rule);
            moduleList.removeChild(card);
        });

        header.child(nameBox).child(enabledToggle).child(removeBtn);
        card.child(header);

        card.child(sectionLabel("Trigger"));
        FlowLayout triggerBox = panelBox();

        triggerBox.child(colorLabel("Target Blocks"));
        triggerBox.child(new RegistryPickerComponent(rule.targetBlocks, ALL_BLOCKS).build());

        triggerBox.child(colorLabel("Hand Items").margins(Insets.top(5)));
        triggerBox.child(new RegistryPickerComponent(rule.handItems, ALL_ITEMS).build());

        triggerBox.child(colorLabel("Dimension(s)").margins(Insets.top(5)));
        triggerBox.child(new DimensionPickerComponent(rule.activeDimensions).build());

        card.child(triggerBox);

        card.child(sectionLabel("Notification").margins(Insets.top(8)));
        FlowLayout notifBox = panelBox();
        notifBox.child(buildNotificationButtons());

        TextBoxComponent notifText = UIComponents.textBox(Sizing.fill(100), rule.notificationMessage);
        notifText.margins(Insets.top(4));
        notifText.onChanged().subscribe(v -> rule.notificationMessage = v);
        notifBox.child(notifText);

        card.child(notifBox);
        return card;
    }

    private FlowLayout buildNotificationButtons() {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        row.gap(4);

        NotificationType[] types = NotificationType.values();
        ButtonComponent[] btns = new ButtonComponent[types.length];

        for (int i = 0; i < types.length; i++) {
            final NotificationType type = types[i];
            btns[i] = UIComponents.button(Component.literal(NOTIF_LABELS[i]), b -> {
                rule.notificationType = type;
                for (ButtonComponent btn : btns) btn.active(true);
                b.active(false); // "pressed" look
            });
            row.child(btns[i]);
        }

        for (int i = 0; i < btns.length; i++) {
            btns[i].active(i != rule.notificationType.ordinal());
        }

        return row;
    }

    private LabelComponent sectionLabel(String text) {
        return UIComponents.label(Component.literal(text))
                .color(Color.ofArgb(0xFF22BB22));
    }

    private LabelComponent colorLabel(String text) {
        return UIComponents.label(Component.literal(text))
                .color(Color.ofArgb(0xFF4EA8DE));
    }

    private FlowLayout panelBox() {
        FlowLayout box = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        box.surface(Surface.PANEL_INSET);
        box.padding(Insets.of(6));
        box.margins(Insets.bottom(4));
        return box;
    }
}

