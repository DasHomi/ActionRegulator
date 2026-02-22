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

    private static final ButtonComponent.Renderer RENDERER_ON  =
            ButtonComponent.Renderer.flat(0xFF2255AA, 0xFF3366CC, 0xFF1A4488);

    private static final ButtonComponent.Renderer RENDERER_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

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
            boolean initiallySelected = activeDimensions.contains(key);

            final ButtonComponent[] ref = new ButtonComponent[1];

            ButtonComponent btn = UIComponents.button(Component.literal(label), b -> {
                boolean nowSelected = activeDimensions.contains(key);
                if (nowSelected) {
                    activeDimensions.remove(key);
                    ref[0].renderer(RENDERER_OFF);
                } else {
                    activeDimensions.add(key);
                    ref[0].renderer(RENDERER_ON);
                }
            });

            btn.renderer(initiallySelected ? RENDERER_ON : RENDERER_OFF);
            ref[0] = btn;
            row.child(btn);
        }

        return row;
    }
}
