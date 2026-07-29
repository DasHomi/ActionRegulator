package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import com.dashomi.actionregulator.config.ui.ConditionRowComponent;
import com.dashomi.actionregulator.enums.ThresholdMode;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;

public class PlayerConditionsSectionComponent {

    private static final ButtonComponent.Renderer MODE_ON = ConditionRowComponent.MODE_ON;
    private static final ButtonComponent.Renderer MODE_OFF = ConditionRowComponent.MODE_OFF;

    private final RuleModule rule;

    public PlayerConditionsSectionComponent(RuleModule rule) {
        this.rule = rule;
    }

    public FlowLayout build() {
        FlowLayout content = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        content.gap(4);
        content.padding(Insets.of(4, 4, 6, 4));
        content.surface(Surface.flat(0x22FFFFFF));

        content.child(new ConditionRowComponent(
                "actionregulator.ui.playerConditions.elytra",
                () -> rule.elytraFlyingCondition,
                v -> rule.elytraFlyingCondition = v).build());

        content.child(new ConditionRowComponent(
                "actionregulator.ui.playerConditions.swimming",
                () -> rule.swimmingCondition,
                v -> rule.swimmingCondition = v).build());

        content.child(buildGameModeRow());

        content.child(buildThresholdRow(
                "actionregulator.ui.playerConditions.health",
                () -> rule.healthConditionMode,
                v -> rule.healthConditionMode = v,
                () -> rule.healthThreshold,
                v -> rule.healthThreshold = (float) v,
                true));

        content.child(buildThresholdRow(
                "actionregulator.ui.playerConditions.hunger",
                () -> rule.hungerConditionMode,
                v -> rule.hungerConditionMode = v,
                () -> rule.hungerThreshold,
                v -> rule.hungerThreshold = (int) Math.round(v),
                false));

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

    private FlowLayout buildThresholdRow(
            String labelKey,
            Supplier<ThresholdMode> modeGetter,
            Consumer<ThresholdMode> modeSetter,
            DoubleSupplier thresholdGetter,
            DoubleConsumer thresholdSetter,
            boolean allowDecimal
    ) {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        row.verticalAlignment(VerticalAlignment.CENTER);
        row.gap(4);
        row.child(UIComponents.label(Component.translatable(labelKey))
                .sizing(Sizing.content(), Sizing.content()));

        String initial = formatThreshold(thresholdGetter.getAsDouble(), allowDecimal);
        TextBoxComponent input = UIComponents.textBox(Sizing.fixed(48), initial);
        input.setMaxLength(6);
        input.onChanged().subscribe(text -> {
            String sanitized = allowDecimal ? sanitizeDecimal(text) : text.replaceAll("\\D", "");
            if (!sanitized.equals(text)) {
                input.text(sanitized);
                return;
            }
            if (text.isBlank()) {
                thresholdSetter.accept(-1);
                return;
            }
            try {
                double val = Double.parseDouble(text);
                if (val >= 0) thresholdSetter.accept(val);
            } catch (NumberFormatException ignored) {}
        });

        ThresholdMode[] modes = { ThresholdMode.IGNORED, ThresholdMode.ABOVE, ThresholdMode.BELOW };
        ButtonComponent[] btns = new ButtonComponent[modes.length];
        for (int i = 0; i < modes.length; i++) {
            final ThresholdMode mode = modes[i];
            final int idx = i;
            btns[i] = UIComponents.button(Component.translatable(getThresholdModeLabelKey(mode)), b -> {
                modeSetter.accept(mode);
                input.setEditable(mode != ThresholdMode.IGNORED);
                for (int j = 0; j < btns.length; j++) {
                    btns[j].renderer(j == idx ? MODE_ON : MODE_OFF);
                }
            });
            btns[i].sizing(Sizing.content(), Sizing.fixed(14));
            btns[i].renderer(modeGetter.get() == mode ? MODE_ON : MODE_OFF);
            row.child(btns[i]);
        }

        input.setEditable(modeGetter.get() != ThresholdMode.IGNORED);
        row.child(input);

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

    private static String formatThreshold(double value, boolean allowDecimal) {
        if (value < 0) return "";
        if (!allowDecimal) return String.valueOf((int) Math.round(value));
        if (value == Math.rint(value)) return String.valueOf((long) value);
        return Float.toString((float) value);
    }

    private static String sanitizeDecimal(String text) {
        StringBuilder sb = new StringBuilder();
        boolean dotSeen = false;
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            } else if (c == '.' && !dotSeen) {
                sb.append(c);
                dotSeen = true;
            }
        }
        return sb.toString();
    }

    private String getThresholdModeLabelKey(ThresholdMode mode) {
        return switch (mode) {
            case IGNORED -> "actionregulator.ui.playerConditions.threshold.ignored";
            case ABOVE -> "actionregulator.ui.playerConditions.threshold.above";
            case BELOW -> "actionregulator.ui.playerConditions.threshold.below";
        };
    }
}
