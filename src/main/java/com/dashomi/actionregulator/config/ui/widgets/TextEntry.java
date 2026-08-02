package com.dashomi.actionregulator.config.ui.widgets;

import com.dashomi.actionregulator.config.ui.PanelEntry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.function.Consumer;

/**
 * A block of text that wraps to the available width.
 */
public class TextEntry extends PanelEntry {
    public static final int DEFAULT_COLOR = 0xFFFFFFFF;
    public static final int MUTED_COLOR = 0xFFA0A0A0;

    private final Font font;
    private final int color;

    private Component text;
    private List<FormattedCharSequence> lines = List.of();
    private int wrappedWidth = -1;

    public TextEntry(Font font, Component text) {
        this(font, text, DEFAULT_COLOR);
    }

    public TextEntry(Font font, Component text, int color) {
        this.font = font;
        this.text = text;
        this.color = color;
    }

    public void setText(Component text) {
        this.text = text;
        this.wrappedWidth = -1;
        requestLayout();
    }

    @Override
    public int getHeight() {
        return Math.max(1, lines.size()) * font.lineHeight;
    }

    @Override
    protected void arrange() {
        int available = Math.max(1, width);
        if (available != wrappedWidth) {
            lines = font.split(text, available);
            wrappedWidth = available;
        }
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
    }

    @Override
    public void extract(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        int lineY = y;
        for (FormattedCharSequence line : lines) {
            graphics.text(font, line, x, lineY, color);
            lineY += font.lineHeight;
        }
    }
}
