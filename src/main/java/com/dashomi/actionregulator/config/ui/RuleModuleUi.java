package com.dashomi.actionregulator.config.ui;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.NotificationType;
import com.dashomi.actionregulator.enums.TriggerType;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import java.util.List;

public class RuleModuleUi {
    private static final String[] NOTIF_LABELS = {"Off", "Sound", "Symbol", "Text"};

    private static final String[] TRIGGER_LABELS = {"Use Item", "Attack Entity", "Block Break", "Block Place"};

    private static final List<String> ALL_BLOCKS = BuiltInRegistries.BLOCK.keySet()
            .stream().map(Object::toString).sorted().toList();
    private static final List<String> ALL_ITEMS = BuiltInRegistries.ITEM.keySet()
            .stream().map(Object::toString).sorted().toList();
    private static final List<String> ALL_ENTITIES = BuiltInRegistries.ENTITY_TYPE.keySet()
            .stream().map(Object::toString).sorted().toList();

    private static final Surface DISABLED_SURFACE =
            Surface.flat(0x55000000).and(Surface.PANEL);

    private static final ButtonComponent.Renderer NOTIF_ON  =
            ButtonComponent.Renderer.flat(0xFF2255AA, 0xFF3366CC, 0xFF1A4488);
    private static final ButtonComponent.Renderer NOTIF_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

    private static final ButtonComponent.Renderer TRIGGER_ON  =
            ButtonComponent.Renderer.flat(0xFFAA7700, 0xFFCC9900, 0xFF885500);
    private static final ButtonComponent.Renderer TRIGGER_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

    private final RuleModule rule;

    public RuleModuleUi(RuleModule rule) {
        this.rule = rule;
    }

    public FlowLayout build(ActionRegulatorConfig config, FlowLayout moduleList) {
        FlowLayout card = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        card.surface(rule.enabled ? Surface.PANEL : DISABLED_SURFACE);

        card.padding(Insets.of(6, 6, 10, 6));
        card.margins(Insets.bottom(6));

        boolean[] collapsed = {false};
        FlowLayout body = buildBody(config, moduleList, card);

        ButtonComponent collapseBtn = UIComponents.button(Component.literal("▼"), b -> {
            collapsed[0] = !collapsed[0];
            b.setMessage(Component.literal(collapsed[0] ? "▶" : "▼"));
            body.sizing(Sizing.fill(100), collapsed[0] ? Sizing.fixed(0) : Sizing.content());
        });
        collapseBtn.sizing(Sizing.fixed(20), Sizing.fixed(16));

        TextBoxComponent nameBox = UIComponents.textBox(Sizing.expand(), rule.name);
        nameBox.sizing(Sizing.expand(), Sizing.fixed(16));
        nameBox.onChanged().subscribe(v -> rule.name = v);

        ButtonComponent enableBtn = UIComponents.button(
                Component.literal(rule.enabled ? "ON" : "OFF"), b -> {
                    rule.enabled = !rule.enabled;
                    b.setMessage(Component.literal(rule.enabled ? "ON" : "OFF"));
                    card.surface(rule.enabled ? Surface.PANEL : DISABLED_SURFACE);
                });
        enableBtn.sizing(Sizing.fixed(32), Sizing.fixed(16));

        FlowLayout header = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        header.verticalAlignment(VerticalAlignment.CENTER);
        header.gap(4);
        header.margins(Insets.bottom(4));
        header.child(collapseBtn);
        header.child(nameBox);
        header.child(enableBtn);

        card.child(header);
        card.child(body);
        return card;
    }

    private FlowLayout buildBody(ActionRegulatorConfig config, FlowLayout moduleList, FlowLayout card) {
        FlowLayout body = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        body.gap(4);

        body.child(sectionLabel("Trigger", 0xFF4EA8DE));
        body.child(buildTriggerButtons());

        body.child(sectionLabel("Dimensions", 0xFF4EA8DE).margins(Insets.top(4)));
        body.child(new DimensionPickerComponent(rule.activeDimensions).build());

        body.child(sectionLabel("Notification", 0xFF4EA8DE).margins(Insets.top(4)));

        TextBoxComponent notifText = UIComponents.textBox(Sizing.fill(100), rule.notificationMessage);
        notifText.onChanged().subscribe(v -> rule.notificationMessage = v);
        notifText.margins(Insets.bottom(4));

        body.child(buildNotificationButtons(notifText, body));

        if (rule.notificationType == NotificationType.TEXT) {
            body.child(notifText);
        }

        ButtonComponent removeBtn = UIComponents.button(Component.literal("Delete Rule"), btn -> {
            config.rules.remove(rule);
            moduleList.removeChild(card);
        });
        removeBtn.margins(Insets.top(6));
        removeBtn.renderer(ButtonComponent.Renderer.flat(0xFFAA2222, 0xFFCC3333, 0xFF881111));

        FlowLayout deleteRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        deleteRow.horizontalAlignment(HorizontalAlignment.RIGHT);
        deleteRow.child(removeBtn);
        body.child(deleteRow);

        return body;
    }

    private FlowLayout buildTriggerButtons() {
        TriggerType[] types = TriggerType.values();
        ButtonComponent[] btns = new ButtonComponent[types.length];

        FlowLayout pickerArea = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        pickerArea.gap(4);
        pickerArea.margins(Insets.of(4));

        FlowLayout row = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        row.gap(4);

        for (int i = 0; i < types.length; i++) {
            final int idx = i;
            final TriggerType type = types[i];
            boolean isCurrent = rule.triggerType == type;
            btns[i] = UIComponents.button(Component.literal(TRIGGER_LABELS[i]), b -> {
                rule.triggerType = type;
                for (int j = 0; j < btns.length; j++) {
                    btns[j].renderer(j == idx ? TRIGGER_ON : TRIGGER_OFF);
                }
                rebuildPickerArea(pickerArea);
            });
            btns[i].renderer(isCurrent ? TRIGGER_ON : TRIGGER_OFF);
            row.child(btns[i]);
        }

        rebuildPickerArea(pickerArea);

        FlowLayout wrapper = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        wrapper.child(row);
        wrapper.child(pickerArea);
        return wrapper;
    }

    private void rebuildPickerArea(FlowLayout pickerArea) {
        pickerArea.clearChildren();

        switch (rule.triggerType) {
            case ON_BLOCK_BREAK, ON_BLOCK_PLACE -> {
                pickerArea.child(sectionLabel("Target Blocks", 0xFF1E648D));
                pickerArea.child(new RegistryPickerComponent(rule.targetBlocks, ALL_BLOCKS).build());
                pickerArea.child(sectionLabel("Hand Items", 0xFF1E648D).margins(Insets.top(4)));
                pickerArea.child(new RegistryPickerComponent(rule.handItems, ALL_ITEMS).build());
            }
            case ON_USE_ITEM -> {
                pickerArea.child(sectionLabel("Hand Items", 0xFF1E648D));
                pickerArea.child(new RegistryPickerComponent(rule.handItems, ALL_ITEMS).build());
                pickerArea.child(sectionLabel("Target Blocks", 0xFF1E648D).margins(Insets.top(4)));
                pickerArea.child(new RegistryPickerComponent(rule.targetBlocks, ALL_BLOCKS).build());
                pickerArea.child(sectionLabel("Target Entities", 0xFF1E648D).margins(Insets.top(4)));
                pickerArea.child(new RegistryPickerComponent(rule.targetEntities, ALL_ENTITIES).build());
            }
            case ON_ATTACK_ENTITY -> {
                pickerArea.child(sectionLabel("Hand Items", 0xFF1E648D));
                pickerArea.child(new RegistryPickerComponent(rule.handItems, ALL_ITEMS).build());
                pickerArea.child(sectionLabel("Target Entities", 0xFF1E648D).margins(Insets.top(4)));
                pickerArea.child(new RegistryPickerComponent(rule.targetEntities, ALL_ENTITIES).build());
            }
        }
    }

    private FlowLayout buildNotificationButtons(TextBoxComponent notifText, FlowLayout body) {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        row.gap(4);

        NotificationType[] types = NotificationType.values();
        ButtonComponent[] btns = new ButtonComponent[types.length];

        for (int i = 0; i < types.length; i++) {
            final int idx = i;
            final NotificationType type = types[i];
            boolean isCurrent = rule.notificationType == type;
            btns[i] = UIComponents.button(Component.literal(NOTIF_LABELS[i]), b -> {
                rule.notificationType = type;
                for (int j = 0; j < btns.length; j++) {
                    btns[j].renderer(j == idx ? NOTIF_ON : NOTIF_OFF);
                }
                if (type == NotificationType.TEXT) {
                    if (!body.children().contains(notifText)) {
                        int rowIndex = body.children().indexOf(row);
                        body.child(rowIndex + 1, notifText);
                    }
                } else {
                    body.removeChild(notifText);
                }
            });
            btns[i].renderer(isCurrent ? NOTIF_ON : NOTIF_OFF);
            row.child(btns[i]);
        }

        return row;
    }


    private LabelComponent sectionLabel(String text, int color) {
        return UIComponents.label(Component.literal(text))
                .color(Color.ofArgb(color));
    }
}
