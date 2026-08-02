package com.dashomi.actionregulator.config.ui.widgets;

import com.dashomi.actionregulator.config.ui.PanelEntry;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.function.Consumer;

/**
 * Wraps a single vanilla widget so it can sit in a panel or a section.
 */
public class WidgetEntry extends PanelEntry {
    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT,
        FILL
    }

    private final AbstractWidget widget;
    private final Alignment alignment;

    public WidgetEntry(AbstractWidget widget) {
        this(widget, Alignment.FILL);
    }

    public WidgetEntry(AbstractWidget widget, Alignment alignment) {
        this.widget = widget;
        this.alignment = alignment;
    }

    public AbstractWidget getWidget() {
        return widget;
    }

    @Override
    public int getHeight() {
        return widget.getHeight();
    }

    @Override
    protected void arrange() {
        if (alignment == Alignment.FILL) {
            widget.setWidth(Math.max(0, width));
        }

        int widgetX = switch (alignment) {
            case LEFT, FILL -> x;
            case CENTER -> x + (width - widget.getWidth()) / 2;
            case RIGHT -> x + width - widget.getWidth();
        };
        widget.setPosition(widgetX, y);
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        consumer.accept(widget);
    }
}
