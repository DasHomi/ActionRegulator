package com.dashomi.actionregulator.config.ui;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.network.chat.Component;
import java.util.List;

public class DimensionPickerComponent {

    private static final String[] KEYS   = {
            "minecraft:overworld",
            "minecraft:the_nether",
            "minecraft:the_end"
    };
    private static final String[] LABELS = { "Overworld", "Nether", "End" };

    private final List<String> activeDimensions;

    public DimensionPickerComponent(List<String> activeDimensions) {
        this.activeDimensions = activeDimensions;
    }

    public FlowLayout build() {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        row.gap(4);

        for (int i = 0; i < KEYS.length; i++) {
            final String key   = KEYS[i];
            final String label = LABELS[i];
            boolean isActive   = activeDimensions.contains(key);

            ButtonComponent btn = UIComponents.button(Component.literal(label), b -> {
                boolean nowSelected = activeDimensions.contains(key);
                if (nowSelected) {
                    activeDimensions.remove(key);
                    b.active(true);
                } else {
                    activeDimensions.add(key);
                    b.active(false);
                }
            });

            btn.active(!isActive);
            row.child(btn);
        }

        return row;
    }
}

