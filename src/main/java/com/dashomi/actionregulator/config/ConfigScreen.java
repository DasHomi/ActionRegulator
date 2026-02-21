package com.dashomi.actionregulator.config;

import com.dashomi.actionregulator.enums.NotificationType;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.CheckboxComponent;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.CollapsibleContainer;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import java.util.ArrayList;
import java.util.List;

import static com.dashomi.actionregulator.ActionregulatorClient.MOD_ID;

public class ConfigScreen extends BaseUIModelScreen<FlowLayout> {

    // Known dimensions – extend as needed
    private static final String[] DIMENSION_KEYS = {
            "minecraft:overworld",
            "minecraft:the_nether",
            "minecraft:the_end"
    };
    private static final String[] DIMENSION_LABELS = { "Overworld", "Nether", "End" };

    private static final String[] NOTIF_LABELS = { "Off", "Sound", "Symbol", "Text" };

    private final ActionRegulatorConfig config;
    private FlowLayout moduleList;

    public ConfigScreen(Screen parent) {
        super(FlowLayout.class, DataSource.asset(Identifier.fromNamespaceAndPath(MOD_ID, "config_screen")));
        // Use singleton config; create a default one if none exists yet
        this.config = ActionRegulatorConfig.getOrCreate();
    }

    @Override
    protected void build(FlowLayout root) {
        moduleList = root.childById(FlowLayout.class, "module-list");

        // Render existing modules
        for (RuleModule rule : config.rules) {
            addModuleCard(rule);
        }

        // "Add Module" button
        root.childById(ButtonComponent.class, "add-module-button").onPress(btn -> {
            RuleModule newRule = new RuleModule();
            config.rules.add(newRule);
            addModuleCard(newRule);
        });
    }

    // Module card builder

    private void addModuleCard(RuleModule rule) {
        CollapsibleContainer card = UIContainers.collapsible(
                Sizing.fill(100), Sizing.content(),
                Component.literal(rule.name), true
        );
        card.margins(Insets.bottom(4));

        // ── Header: name text-box + enabled checkbox ──────────────────────
        FlowLayout header = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        header.verticalAlignment(io.wispforest.owo.ui.core.VerticalAlignment.CENTER);
        header.margins(Insets.bottom(6));

        TextBoxComponent nameBox = UIComponents.textBox(Sizing.fill(75), rule.name);
        nameBox.onChanged().subscribe(v -> {
            rule.name = v;
            // Update collapsible title
            card.titleLayout().children().stream()
                    .filter(c -> c instanceof io.wispforest.owo.ui.component.LabelComponent)
                    .map(c -> (io.wispforest.owo.ui.component.LabelComponent) c)
                    .findFirst()
                    .ifPresent(l -> l.text(Component.literal(v)));
        });

        CheckboxComponent enabledToggle = UIComponents.checkbox(Component.empty());
        enabledToggle.checked(rule.enabled);
        enabledToggle.onChanged(checked -> rule.enabled = checked);
        enabledToggle.margins(Insets.left(4));

        ButtonComponent removeBtn = UIComponents.button(Component.literal("✕"), btn -> {
            config.rules.remove(rule);
            moduleList.removeChild(card);
        });
        removeBtn.margins(Insets.left(4));

        header.child(nameBox).child(enabledToggle).child(removeBtn);
        card.child(header);

        // ── Trigger section ───────────────────────────────────────────────
        card.child(sectionLabel("Trigger"));

        FlowLayout triggerBox = panelBox();

        // "Block breaking" sub-label
        triggerBox.child(colorLabel("Block breaking"));

        // Target block(s) with search
        triggerBox.child(colorLabel("Target block(s)"));
        triggerBox.child(buildRegistryPicker(rule.targetBlocks,
                BuiltInRegistries.BLOCK.keySet().stream()
                        .map(Object::toString).sorted().toList()));

        // Hand item(s) with search
        triggerBox.child(colorLabel("Hand item(s)").margins(Insets.top(5)));
        triggerBox.child(buildRegistryPicker(rule.handItems,
                BuiltInRegistries.ITEM.keySet().stream()
                        .map(Object::toString).sorted().toList()));

        // Dimension(s) toggles
        triggerBox.child(colorLabel("Dimension(s)").margins(Insets.top(5)));
        triggerBox.child(buildDimensionToggles(rule));

        card.child(triggerBox);

        // ── Notification section ──────────────────────────────────────────
        card.child(sectionLabel("Notification").margins(Insets.top(8)));
        FlowLayout notifBox = panelBox();
        notifBox.child(buildNotificationButtons(rule));

        TextBoxComponent notifText = UIComponents.textBox(Sizing.fill(100), rule.notificationMessage);
        notifText.margins(Insets.top(4));
        notifText.onChanged().subscribe(v -> rule.notificationMessage = v);
        notifBox.child(notifText);

        card.child(notifBox);

        moduleList.child(card);
    }

    // Registry picker (block / item search + tag list)

    private FlowLayout buildRegistryPicker(List<String> selectedList, List<String> allEntries) {
        FlowLayout container = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        container.surface(Surface.flat(0x22000000));
        container.padding(Insets.of(4));

        // Selected chips row
        FlowLayout chips = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        chips.gap(3);
        refreshChips(chips, selectedList);

        // Search box
        TextBoxComponent search = UIComponents.textBox(Sizing.fill(100));
        search.setMaxLength(64);

        // Suggestion list (shown while typing)
        FlowLayout suggestions = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        suggestions.surface(Surface.flat(0xCC101010));
        suggestions.padding(Insets.of(3));

        search.onChanged().subscribe(query -> {
            suggestions.clearChildren();
            if (query.isBlank()) return;
            allEntries.stream()
                    .filter(e -> e.contains(query.toLowerCase()))
                    .limit(8)
                    .forEach(entry -> {
                        ButtonComponent btn = UIComponents.button(
                                Component.literal(entry),
                                b -> {
                                    if (!selectedList.contains(entry)) {
                                        selectedList.add(entry);
                                        refreshChips(chips, selectedList);
                                    }
                                    search.text("");
                                    suggestions.clearChildren();
                                });
                        btn.sizing(Sizing.fill(100), Sizing.content());
                        btn.margins(Insets.bottom(1));
                        suggestions.child(btn);
                    });
        });

        container.child(chips).child(search).child(suggestions);
        return container;
    }

    /** Re-renders the chip row for the currently selected entries. */
    private void refreshChips(FlowLayout chips, List<String> selectedList) {
        chips.clearChildren();
        for (String entry : new ArrayList<>(selectedList)) {
            // short label: strip namespace if minecraft:
            String label = entry.startsWith("minecraft:") ? entry.substring(10) : entry;
            ButtonComponent chip = UIComponents.button(
                    Component.literal(label + " ✕"),
                    b -> {
                        selectedList.remove(entry);
                        refreshChips(chips, selectedList);
                    });
            chip.margins(Insets.right(2));
            chips.child(chip);
        }
    }

    // Dimension toggles  (true toggle – click again to deselect)

    private FlowLayout buildDimensionToggles(RuleModule rule) {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        row.gap(4);

        for (int i = 0; i < DIMENSION_KEYS.length; i++) {
            final String key = DIMENSION_KEYS[i];
            final String label = DIMENSION_LABELS[i];

            SmallCheckboxComponent cb = UIComponents.smallCheckbox(Component.literal(label));
            cb.checked(rule.activeDimensions.contains(key));
            cb.margins(Insets.right(4));
            cb.onChanged().subscribe(checked -> {
                if (checked) {
                    if (!rule.activeDimensions.contains(key)) rule.activeDimensions.add(key);
                } else {
                    rule.activeDimensions.remove(key);
                }
            });
            row.child(cb);
        }
        return row;
    }

    // Notification radio buttons

    private FlowLayout buildNotificationButtons(RuleModule rule) {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        row.gap(4);

        NotificationType[] types = NotificationType.values();
        ButtonComponent[] btns = new ButtonComponent[types.length];

        for (int i = 0; i < types.length; i++) {
            final NotificationType type = types[i];

            btns[i] = UIComponents.button(Component.literal(NOTIF_LABELS[i]), b -> {
                rule.notificationType = type;
                // deactivate all, then keep this one "active" (highlighted)
                for (ButtonComponent btn : btns) btn.active(true);
                b.active(false); // visually "pressed" = disabled = grey
            });
            row.child(btns[i]);
        }

        // Mark current selection
        btns[rule.notificationType.ordinal()].active(false);
        for (int i = 0; i < btns.length; i++) {
            if (i != rule.notificationType.ordinal()) btns[i].active(true);
        }

        return row;
    }

    // UI helpers

    private io.wispforest.owo.ui.component.LabelComponent sectionLabel(String text) {
        return UIComponents.label(Component.literal(text))
                .color(Color.ofArgb(0xFF22BB22));
    }

    private io.wispforest.owo.ui.component.LabelComponent colorLabel(String text) {
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
