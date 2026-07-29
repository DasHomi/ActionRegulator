package com.dashomi.actionregulator.config.ui;

import com.dashomi.actionregulator.enums.ConditionMode;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public class ConditionRowComponent {

    public static final ButtonComponent.Renderer MODE_ON =
            ButtonComponent.Renderer.flat(0xFF2255AA, 0xFF3366CC, 0xFF1A4488);
    public static final ButtonComponent.Renderer MODE_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

    private final String labelKey;
    private final Supplier<ConditionMode> getter;
    private final Consumer<ConditionMode> setter;

    public ConditionRowComponent(String labelKey, Supplier<ConditionMode> getter, Consumer<ConditionMode> setter) {
        this.labelKey = labelKey;
        this.getter = getter;
        this.setter = setter;
    }

    public FlowLayout build() {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        row.verticalAlignment(VerticalAlignment.CENTER);
        row.gap(4);
        row.child(UIComponents.label(Component.translatable(labelKey))
                .sizing(Sizing.content(), Sizing.content()));

        ConditionMode[] modes = ConditionMode.values();
        ButtonComponent[] btns = new ButtonComponent[modes.length];

        for (int i = 0; i < modes.length; i++) {
            final ConditionMode mode = modes[i];
            final int idx = i;
            btns[i] = UIComponents.button(Component.translatable(getModeLabelKey(mode)), b -> {
                setter.accept(mode);
                for (int j = 0; j < btns.length; j++) {
                    btns[j].renderer(j == idx ? MODE_ON : MODE_OFF);
                }
            });
            btns[i].sizing(Sizing.content(), Sizing.fixed(14));
            btns[i].renderer(getter.get() == mode ? MODE_ON : MODE_OFF);
            row.child(btns[i]);
        }

        return row;
    }

    private static String getModeLabelKey(ConditionMode mode) {
        return switch (mode) {
            case IGNORED -> "actionregulator.ui.conditionMode.ignored";
            case REQUIRED -> "actionregulator.ui.conditionMode.required";
            case FORBIDDEN -> "actionregulator.ui.conditionMode.forbidden";
        };
    }
}