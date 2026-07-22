package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.enums.PlayerConditionMode;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;

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
        FlowLayout content = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        content.gap(4);
        content.padding(Insets.of(4, 4, 6, 4));
        content.surface(Surface.flat(0x22FFFFFF));

        content.child(buildConditionRow(
                "actionregulator.ui.playerConditions.elytra",
                () -> rule.elytraFlyingCondition,
                v -> rule.elytraFlyingCondition = v));

        content.child(buildConditionRow(
                "actionregulator.ui.playerConditions.swimming",
                () -> rule.swimmingCondition,
                v -> rule.swimmingCondition = v));

        content.child(buildGameModeRow());

        boolean[] open = { false };
        content.sizing(Sizing.fill(100), Sizing.fixed(0));

        Component title = Component.translatable("actionregulator.ui.section.playerConditions");
        ButtonComponent toggle = UIComponents.button(
                Component.literal("▶ ").append(title),
                b -> {
                    open[0] = !open[0];
                    b.setMessage(Component.literal(open[0] ? "▼ " : "▶ ").append(title));
                    content.sizing(Sizing.fill(100), open[0] ? Sizing.content() : Sizing.fixed(0));
                });
        toggle.sizing(Sizing.fill(100), Sizing.fixed(14));
        toggle.renderer(ButtonComponent.Renderer.flat(0xFF333333, 0xFF444444, 0xFF222222));

        FlowLayout wrapper = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        wrapper.gap(2);
        wrapper.child(toggle);
        wrapper.child(content);
        return wrapper;
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

    private FlowLayout buildGameModeRow() {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        row.verticalAlignment(VerticalAlignment.CENTER);
        row.gap(4);
        row.child(UIComponents.label(Component.translatable("actionregulator.ui.playerConditions.gamemode"))
                .sizing(Sizing.content(), Sizing.content()));

        for (GameType mode : GameType.values()) {
            final String id = mode.name();
            ButtonComponent btn = UIComponents.button(Component.translatable(getGameModeLabelKey(mode)), b -> {
                if (rule.activeGameModes.contains(id)) {
                    rule.activeGameModes.remove(id);
                } else {
                    rule.activeGameModes.add(id);
                }
                b.renderer(rule.activeGameModes.contains(id) ? MODE_ON : MODE_OFF);
            });
            btn.sizing(Sizing.content(), Sizing.fixed(14));
            btn.renderer(rule.activeGameModes.contains(id) ? MODE_ON : MODE_OFF);
            row.child(btn);
        }

        return row;
    }

    private String getGameModeLabelKey(GameType mode) {
        return switch (mode) {
            case SURVIVAL -> "actionregulator.ui.playerConditions.gamemode.survival";
            case CREATIVE -> "actionregulator.ui.playerConditions.gamemode.creative";
            case ADVENTURE -> "actionregulator.ui.playerConditions.gamemode.adventure";
            case SPECTATOR -> "actionregulator.ui.playerConditions.gamemode.spectator";
        };
    }

    private String getModeLabelKey(PlayerConditionMode mode) {
        return switch (mode) {
            case IGNORED -> "actionregulator.ui.playerConditions.mode.ignored";
            case REQUIRED -> "actionregulator.ui.playerConditions.mode.required";
            case FORBIDDEN -> "actionregulator.ui.playerConditions.mode.forbidden";
        };
    }
}
