package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.PlayerConditionMode;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public class PlayerConditionsSectionComponent {

    private static final ButtonComponent.Renderer MODE_ON =
            ButtonComponent.Renderer.flat(0xFF2255AA, 0xFF3366CC, 0xFF1A4488);
    private static final ButtonComponent.Renderer MODE_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

    private final RuleModule rule;

    public PlayerConditionsSectionComponent(RuleModule rule) {
        this.rule = rule;
    }

    public FlowLayout build() {
        FlowLayout section = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        section.gap(4);

        section.child(buildConditionRow(
                "actionregulator.ui.playerConditions.elytra",
                () -> rule.elytraFlyingCondition,
                v -> rule.elytraFlyingCondition = v));

        section.child(buildConditionRow(
                "actionregulator.ui.playerConditions.swimming",
                () -> rule.swimmingCondition,
                v -> rule.swimmingCondition = v));

        return section;
    }

    private FlowLayout buildConditionRow(
            String labelKey,
            Supplier<PlayerConditionMode> getter,
            Consumer<PlayerConditionMode> setter
    ) {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        row.verticalAlignment(VerticalAlignment.CENTER);
        row.gap(4);
        row.child(UIComponents.label(Component.translatable(labelKey))
                .sizing(Sizing.content(), Sizing.content()));

        PlayerConditionMode[] modes = PlayerConditionMode.values();
        ButtonComponent[] btns = new ButtonComponent[modes.length];

        for (int i = 0; i < modes.length; i++) {
            final PlayerConditionMode mode = modes[i];
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

    private String getModeLabelKey(PlayerConditionMode mode) {
        return switch (mode) {
            case IGNORED -> "actionregulator.ui.playerConditions.mode.ignored";
            case REQUIRED -> "actionregulator.ui.playerConditions.mode.required";
            case FORBIDDEN -> "actionregulator.ui.playerConditions.mode.forbidden";
        };
    }
}
