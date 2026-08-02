package com.dashomi.actionregulator.config.ui.widgets;

import com.dashomi.actionregulator.config.ui.PanelEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Enum selector by row of buttons of which only one may be selected.
 */
public class SelectorEntry<T> extends PanelEntry {
    private static final ChatFormatting SELECTED_STYLE = ChatFormatting.YELLOW;
    private static final int DEFAULT_HEIGHT = 20;
    private static final int BUTTON_GAP = 4;
    private static final int LABEL_PADDING = 6;

    private final List<T> options;
    private final List<Button> buttons = new ArrayList<>();
    private final Function<T, Component> labelProvider;
    private final int[] preferredWidths;

    private Consumer<T> responder;
    private T selected;
    private int buttonHeight = DEFAULT_HEIGHT;

    public static <T extends Enum<T>> SelectorEntry<T> of(Font font, Class<T> type, Function<T, Component> labelProvider) {
        return new SelectorEntry<>(font, List.of(type.getEnumConstants()), labelProvider);
    }

    public SelectorEntry(Font font, List<T> options, Function<T, Component> labelProvider) {
        if (options.isEmpty()) {
            throw new IllegalArgumentException("A selector needs at least one option");
        }
        this.options = List.copyOf(options);
        this.labelProvider = labelProvider;
        this.preferredWidths = new int[this.options.size()];
        this.selected = this.options.getFirst();

        for (int i = 0; i < this.options.size(); i++) {
            T option = this.options.get(i);
            Component label = labelProvider.apply(option);
            preferredWidths[i] = font.width(label) + 2 * LABEL_PADDING;

            Button button = Button.builder(label, b -> setSelected(option))
                    .bounds(0, 0, preferredWidths[i], buttonHeight)
                    .build();
            // The override replaces the hover check, so keep hovering visible for the other options.
            button.setOverrideRenderHighlightedSprite(() -> isSelected(option) || button.isHoveredOrFocused());
            buttons.add(button);
        }
        refreshLabels();
    }

    public List<T> getOptions() {
        return options;
    }

    public T getSelected() {
        return selected;
    }

    public boolean isSelected(T option) {
        return Objects.equals(selected, option);
    }

    public void setSelected(T option) {
        if (!options.contains(option)) {
            throw new IllegalArgumentException("Option is not part of this selector: " + option);
        }
        if (isSelected(option)) {
            return;
        }
        this.selected = option;
        refreshLabels();
        if (responder != null) {
            responder.accept(option);
        }
    }

    public void setResponder(Consumer<T> responder) {
        this.responder = responder;
    }

    public SelectorEntry<T> setButtonHeight(int buttonHeight) {
        this.buttonHeight = buttonHeight;
        requestLayout();
        return this;
    }

    private void refreshLabels() {
        for (int i = 0; i < options.size(); i++) {
            T option = options.get(i);
            Component label = labelProvider.apply(option);
            buttons.get(i).setMessage(isSelected(option) ? label.copy().withStyle(SELECTED_STYLE) : label);
        }
    }

    @Override
    public int getHeight() {
        return buttonHeight;
    }

    @Override
    protected void arrange() {
        int count = buttons.size();
        int budget = Math.max(0, width - BUTTON_GAP * (count - 1));
        int preferred = 0;
        for (int preferredWidth : preferredWidths) {
            preferred += preferredWidth;
        }

        int cursor = x;
        for (int i = 0; i < count; i++) {
            // Labels decide the width; only a row that overflows the content is scaled down.
            int buttonWidth = preferred <= budget ? preferredWidths[i] : budget * preferredWidths[i] / preferred;
            Button button = buttons.get(i);
            button.setWidth(buttonWidth);
            button.setHeight(buttonHeight);
            button.setPosition(cursor, y);
            cursor += buttonWidth + BUTTON_GAP;
        }
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        for (Button button : buttons) {
            consumer.accept(button);
        }
    }
}
