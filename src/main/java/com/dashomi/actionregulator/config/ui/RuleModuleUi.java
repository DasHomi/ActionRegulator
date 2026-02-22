package com.dashomi.actionregulator.config.ui;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.NotificationType;
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

    private static final List<String> ALL_BLOCKS = BuiltInRegistries.BLOCK.keySet()
            .stream().map(Object::toString).sorted().toList();
    private static final List<String> ALL_ITEMS = BuiltInRegistries.ITEM.keySet()
            .stream().map(Object::toString).sorted().toList();

    /** Overlay tinted surface to signal a disabled module */
    private static final Surface DISABLED_SURFACE =
            Surface.flat(0x55000000).and(Surface.PANEL);

    private static final ButtonComponent.Renderer NOTIF_ON  =
            ButtonComponent.Renderer.flat(0xFF2255AA, 0xFF3366CC, 0xFF1A4488);
    private static final ButtonComponent.Renderer NOTIF_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

    private final RuleModule rule;

    public RuleModuleUi(RuleModule rule) {
        this.rule = rule;
    }

    public FlowLayout build(ActionRegulatorConfig config, FlowLayout moduleList) {
        FlowLayout card = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        card.surface(rule.enabled ? Surface.PANEL : DISABLED_SURFACE);
        // extra bottom padding to prevent clipping
        card.padding(Insets.of(6, 6, 10, 6));
        card.margins(Insets.bottom(6));

        boolean[] collapsed = {false};
        FlowLayout body = buildBody(config, moduleList, card);

        // ── Collapse button (fixed width) ────────────────────────────────────
        ButtonComponent collapseBtn = UIComponents.button(Component.literal("▼"), b -> {
            collapsed[0] = !collapsed[0];
            b.setMessage(Component.literal(collapsed[0] ? "▶" : "▼"));
            body.sizing(Sizing.fill(100), collapsed[0] ? Sizing.fixed(0) : Sizing.content());
        });
        collapseBtn.sizing(Sizing.fixed(20), Sizing.fixed(16));

        // ── Name box (fill remaining space) ──────────────────────────────────
        // Sizing.fill(100) inside a horizontal flow = 100% of PARENT width → too wide.
        // Use Sizing.expand() so it takes only the leftover space after fixed siblings.
        TextBoxComponent nameBox = UIComponents.textBox(Sizing.expand(), rule.name);
        nameBox.sizing(Sizing.expand(), Sizing.fixed(16));
        nameBox.onChanged().subscribe(v -> rule.name = v);

        // ── Enable toggle (fixed, rightmost) ─────────────────────────────────
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

        body.child(sectionLabel("Target Blocks"));
        body.child(new RegistryPickerComponent(rule.targetBlocks, ALL_BLOCKS).build());

        body.child(sectionLabel("Hand Items").margins(Insets.top(4)));
        body.child(new RegistryPickerComponent(rule.handItems, ALL_ITEMS).build());

        body.child(sectionLabel("Dimensions").margins(Insets.top(4)));
        body.child(new DimensionPickerComponent(rule.activeDimensions).build());

        body.child(sectionLabel("Notification").margins(Insets.top(4)));

        TextBoxComponent notifText = UIComponents.textBox(Sizing.fill(100), rule.notificationMessage);
        notifText.onChanged().subscribe(v -> rule.notificationMessage = v);
        notifText.margins(Insets.bottom(4));
        notifText.sizing(Sizing.fill(100),
                rule.notificationType == NotificationType.TEXT ? Sizing.content() : Sizing.fixed(0));

        body.child(buildNotificationButtons(notifText));
        body.child(notifText);

        // ── Delete button, right-aligned, red ────────────────────────────────
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

    private FlowLayout buildNotificationButtons(TextBoxComponent notifText) {
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
                notifText.sizing(Sizing.fill(100),
                        type == NotificationType.TEXT ? Sizing.content() : Sizing.fixed(0));
            });
            btns[i].renderer(isCurrent ? NOTIF_ON : NOTIF_OFF);
            row.child(btns[i]);
        }

        return row;
    }


    private LabelComponent sectionLabel(String text) {
        return UIComponents.label(Component.literal(text))
                .color(Color.ofArgb(0xFF4EA8DE));
    }
}
