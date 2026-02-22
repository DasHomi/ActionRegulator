package com.dashomi.actionregulator.config.ui;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.network.chat.Component;
import java.util.ArrayList;
import java.util.List;

public class RegistryPickerComponent {

    private final List<String> selected;
    private final List<String> allEntries;
    private FlowLayout chips;

    public RegistryPickerComponent(List<String> selected, List<String> allEntries) {
        this.selected = selected;
        this.allEntries = allEntries;
    }

    public FlowLayout build() {
        FlowLayout container = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        container.surface(Surface.flat(0x33000000));
        container.padding(Insets.of(4));
        container.gap(3);

        chips = UIContainers.ltrTextFlow(Sizing.fill(100), Sizing.content());
        chips.gap(3);
        refreshChips();

        TextBoxComponent search = UIComponents.textBox(Sizing.fill(100));
        search.setMaxLength(64);

        FlowLayout suggestions = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        suggestions.surface(Surface.flat(0xDD101010));
        suggestions.padding(Insets.of(2));
        suggestions.sizing(Sizing.fill(100), Sizing.fixed(0));

        search.onChanged().subscribe(query -> {
            suggestions.clearChildren();
            if (query.isBlank()) {
                suggestions.sizing(Sizing.fill(100), Sizing.fixed(0));
                return;
            }
            String q = query.toLowerCase();
            List<String> matches = allEntries.stream()
                    .filter(e -> e.contains(q))
                    .limit(6)
                    .toList();

            if (matches.isEmpty()) {
                suggestions.sizing(Sizing.fill(100), Sizing.fixed(0));
                return;
            }

            matches.forEach(entry -> {
                ButtonComponent btn = UIComponents.button(
                        Component.literal(shortName(entry)),
                        b -> {
                            if (!selected.contains(entry)) {
                                selected.add(entry);
                                refreshChips();
                            }
                            search.text("");
                            suggestions.clearChildren();
                            suggestions.sizing(Sizing.fill(100), Sizing.fixed(0));
                        });
                btn.sizing(Sizing.fill(100), Sizing.fixed(16));
                btn.margins(Insets.bottom(1));
                suggestions.child(btn);
            });
            suggestions.sizing(Sizing.fill(100), Sizing.content());
        });

        container.child(chips);
        container.child(search);
        container.child(suggestions);
        return container;
    }

    private void refreshChips() {
        chips.clearChildren();
        for (String entry : new ArrayList<>(selected)) {
            ButtonComponent chip = UIComponents.button(
                    Component.literal(shortName(entry) + " ✕"),
                    b -> {
                        selected.remove(entry);
                        refreshChips();
                    });
            chip.margins(Insets.of(0, 3, 3, 0));
            chips.child(chip);
        }
    }

    private static String shortName(String id) {
        return id.startsWith("minecraft:") ? id.substring(10) : id;
    }
}
