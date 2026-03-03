package com.dashomi.actionregulator.config.ui;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.config.ConfigManager;
import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.NotificationType;
import com.dashomi.actionregulator.enums.TargetMode;
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
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class RuleModuleUi {
    private static final String[] NOTIF_LABELS = {
            "actionregulator.ui.notification.off",
            "actionregulator.ui.notification.sound",
            "actionregulator.ui.notification.symbol",
            "actionregulator.ui.notification.text"
    };

    private static final String[] TRIGGER_LABELS = new String[]{
            "actionregulator.ui.trigger.attack",
            "actionregulator.ui.trigger.use"
    };

    private static final List<String> ALL_BLOCKS = BuiltInRegistries.BLOCK.keySet()
            .stream().map(Object::toString).sorted().toList();
    private static final List<String> ALL_ITEMS = BuiltInRegistries.ITEM.keySet()
            .stream().map(Object::toString).sorted().toList();
    private static final List<String> ALL_ENTITIES = BuiltInRegistries.ENTITY_TYPE.keySet()
            .stream().map(Object::toString).sorted().toList();

    private static final Surface DISABLED_SURFACE = Surface.PANEL;

    private static final ButtonComponent.Renderer NOTIF_ON  =
            ButtonComponent.Renderer.flat(0xFF2255AA, 0xFF3366CC, 0xFF1A4488);
    private static final ButtonComponent.Renderer NOTIF_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

    private static final ButtonComponent.Renderer TRIGGER_ON  =
            ButtonComponent.Renderer.flat(0xFFAA7700, 0xFFCC9900, 0xFF885500);
    private static final ButtonComponent.Renderer TRIGGER_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

    private static final ButtonComponent.Renderer INVERT_ON =
            ButtonComponent.Renderer.flat(0xFF226622, 0xFF338833, 0xFF114411);
    private static final ButtonComponent.Renderer INVERT_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

    private static final ButtonComponent.Renderer ENABLED_BTN =
            ButtonComponent.Renderer.flat(0xFF227722, 0xFF339933, 0xFF115511);
    private static final ButtonComponent.Renderer DISABLED_BTN =
            ButtonComponent.Renderer.flat(0xFF882222, 0xFFAA3333, 0xFF661111);

    private static final ButtonComponent.Renderer TARGET_MODE_ON =
            ButtonComponent.Renderer.flat(0xFF7A3A9A, 0xFF9B4DBF, 0xFF5A1A7A);
    private static final ButtonComponent.Renderer TARGET_MODE_OFF =
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

        FlowLayout body = buildBody(config, moduleList, card);

        body.sizing(Sizing.fill(100), rule.expanded ? Sizing.content() : Sizing.fixed(0));

        ButtonComponent collapseBtn = UIComponents.button(
                Component.translatable(rule.expanded ? "actionregulator.ui.module.collapse" : "actionregulator.ui.module.expand"), b -> {
            rule.expanded = !rule.expanded;
            b.setMessage(Component.translatable(rule.expanded ? "actionregulator.ui.module.collapse" : "actionregulator.ui.module.expand"));
            body.sizing(Sizing.fill(100), rule.expanded ? Sizing.content() : Sizing.fixed(0));
        });
        collapseBtn.sizing(Sizing.fixed(20), Sizing.fixed(16));

        TextBoxComponent nameBox = UIComponents.textBox(Sizing.expand(), rule.name);
        nameBox.sizing(Sizing.expand(), Sizing.fixed(16));
        nameBox.onChanged().subscribe(v -> rule.name = v);

        ButtonComponent enableBtn = UIComponents.button(
                Component.translatable(rule.enabled ? "actionregulator.ui.module.enabled" : "actionregulator.ui.module.disabled"), b -> {
                    rule.enabled = !rule.enabled;
                    b.setMessage(Component.translatable(rule.enabled ? "actionregulator.ui.module.enabled" : "actionregulator.ui.module.disabled"));
                    b.renderer(rule.enabled ? ENABLED_BTN : DISABLED_BTN);
                    card.surface(rule.enabled ? Surface.PANEL : DISABLED_SURFACE);
                });
        enableBtn.sizing(Sizing.fixed(32), Sizing.fixed(16));
        enableBtn.renderer(rule.enabled ? ENABLED_BTN : DISABLED_BTN);

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

        body.child(sectionLabel("actionregulator.ui.section.trigger", 0xFF4EA8DE));
        body.child(buildTriggerButtons());

        body.child(sectionLabel("actionregulator.ui.section.dimensions", 0xFF4EA8DE).margins(Insets.top(4)));
        body.child(new DimensionPickerComponent(rule.activeDimensions).build());

        body.child(sectionLabel("actionregulator.ui.section.notification", 0xFF4EA8DE).margins(Insets.top(4)));

        TextBoxComponent notifText = UIComponents.textBox(Sizing.fill(100), rule.notificationMessage);
        notifText.onChanged().subscribe(v -> rule.notificationMessage = v);
        notifText.margins(Insets.bottom(4));

        body.child(buildNotificationButtons(notifText, body));

        if (rule.notificationType == NotificationType.TEXT) {
            body.child(notifText);
        }

        ButtonComponent removeBtn = UIComponents.button(Component.translatable("actionregulator.ui.module.deleteRule"), btn -> {
            config.rules.remove(rule);
            moduleList.removeChild(card);
        });
        removeBtn.margins(Insets.top(6));
        removeBtn.renderer(ButtonComponent.Renderer.flat(0xFFAA2222, 0xFFCC3333, 0xFF881111));

        LabelComponent exportFeedback = UIComponents.label(Component.empty());
        exportFeedback.sizing(Sizing.fill(100), Sizing.content());
        exportFeedback.margins(Insets.top(2));

        Path[] lastExported = {null};

        ButtonComponent exportBtn = UIComponents.button(
                Component.translatable("actionregulator.ui.module.exportRule"), btn -> {
                    try {
                        Path exported = ConfigManager.exportRule(rule);
                        lastExported[0] = exported;
                        String display = exported.getParent().getFileName() + "/" + exported.getFileName();
                        exportFeedback.text(
                                Component.literal("✔ " + display + " ↗").withColor(0xFF55FF55));
                    } catch (IOException e) {
                        exportFeedback.text(
                                Component.literal("✘ " + e.getMessage()).withColor(0xFFFF5555));
                    }
                });
        exportBtn.margins(Insets.top(6));

        exportFeedback.mouseDown().subscribe((mouseX, mouseY) -> {
            if (lastExported[0] != null) {
                try {
                    String folder = lastExported[0].getParent().toAbsolutePath().toString();
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        new ProcessBuilder("explorer.exe", folder).start();
                    } else if (os.contains("mac")) {
                        new ProcessBuilder("open", folder).start();
                    } else {
                        new ProcessBuilder("xdg-open", folder).start();
                    }
                } catch (IOException ignored) {}
                return true;
            }
            return false;
        });

        FlowLayout actionRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        actionRow.horizontalAlignment(HorizontalAlignment.RIGHT);
        actionRow.verticalAlignment(VerticalAlignment.CENTER);
        actionRow.gap(4);
        actionRow.child(exportBtn);
        actionRow.child(removeBtn);

        body.child(actionRow);
        body.child(exportFeedback);

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
            btns[i] = UIComponents.button(Component.translatable(TRIGGER_LABELS[i]), b -> {
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

        pickerArea.child(registrySectionHeader("actionregulator.ui.section.handItems", 0,
                () -> rule.invertHandItems, v -> rule.invertHandItems = v));
        pickerArea.child(new RegistryPickerComponent(rule.handItems, ALL_ITEMS, "item").build());

        FlowLayout targetModeRow = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        targetModeRow.gap(4);
        targetModeRow.margins(Insets.top(4));

        ButtonComponent[] modeBtns = new ButtonComponent[2];

        modeBtns[0] = UIComponents.button(
                Component.translatable("actionregulator.ui.targetmode.blocks"), b -> {
                    if (rule.targetMode != TargetMode.BLOCKS) {
                        rule.targetMode = TargetMode.BLOCKS;
                        modeBtns[0].renderer(TARGET_MODE_ON);
                        modeBtns[1].renderer(TARGET_MODE_OFF);
                        rebuildPickerArea(pickerArea);
                    }
                });
        modeBtns[0].renderer(rule.targetMode == TargetMode.BLOCKS ? TARGET_MODE_ON : TARGET_MODE_OFF);

        modeBtns[1] = UIComponents.button(
                Component.translatable("actionregulator.ui.targetmode.entities"), b -> {
                    if (rule.targetMode != TargetMode.ENTITIES) {
                        rule.targetMode = TargetMode.ENTITIES;
                        modeBtns[0].renderer(TARGET_MODE_OFF);
                        modeBtns[1].renderer(TARGET_MODE_ON);
                        rebuildPickerArea(pickerArea);
                    }
                });
        modeBtns[1].renderer(rule.targetMode == TargetMode.ENTITIES ? TARGET_MODE_ON : TARGET_MODE_OFF);

        targetModeRow.child(modeBtns[0]);
        targetModeRow.child(modeBtns[1]);
        pickerArea.child(sectionLabel("actionregulator.ui.section.target", 0xFF4EA8DE).margins(Insets.top(4)));
        pickerArea.child(targetModeRow);

        if (rule.targetMode == TargetMode.BLOCKS) {
            pickerArea.child(registrySectionHeader("actionregulator.ui.section.targetBlocks", 4,
                    () -> rule.invertTargetBlocks, v -> rule.invertTargetBlocks = v));
            pickerArea.child(new RegistryPickerComponent(rule.targetBlocks, ALL_BLOCKS, "block").build());
        } else {
            pickerArea.child(registrySectionHeader("actionregulator.ui.section.targetEntities", 4,
                    () -> rule.invertTargetEntities, v -> rule.invertTargetEntities = v));
            pickerArea.child(new RegistryPickerComponent(rule.targetEntities, ALL_ENTITIES, "entity").build());
        }
    }

    private FlowLayout registrySectionHeader(String labelKey, int topMargin,
            java.util.function.BooleanSupplier getter, java.util.function.Consumer<Boolean> setter) {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        row.verticalAlignment(VerticalAlignment.CENTER);
        row.gap(4);
        if (topMargin > 0) row.margins(Insets.top(topMargin));

        LabelComponent label = sectionLabel(labelKey, 0xFF1E648D);
        label.sizing(Sizing.expand(), Sizing.content());

        boolean[] state = {getter.getAsBoolean()};
        ButtonComponent invertBtn = UIComponents.button(
                Component.translatable(state[0] ? "actionregulator.ui.registryinvert.on" : "actionregulator.ui.registryinvert.off"),
                b -> {
                    state[0] = !state[0];
                    setter.accept(state[0]);
                    b.setMessage(Component.translatable(state[0] ? "actionregulator.ui.registryinvert.on" : "actionregulator.ui.registryinvert.off"));
                    b.renderer(state[0] ? INVERT_ON : INVERT_OFF);
                });
        invertBtn.sizing(Sizing.fixed(68), Sizing.fixed(14));
        invertBtn.renderer(state[0] ? INVERT_ON : INVERT_OFF);

        row.child(label);
        row.child(invertBtn);
        return row;
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
            btns[i] = UIComponents.button(Component.translatable(NOTIF_LABELS[i]), b -> {
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

    private LabelComponent sectionLabel(String key, int color) {
        return UIComponents.label(Component.translatable(key))
                .color(Color.ofArgb(color));
    }
}
