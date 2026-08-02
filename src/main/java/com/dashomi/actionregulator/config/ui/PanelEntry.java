package com.dashomi.actionregulator.config.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.function.Consumer;

/**
 * One item in the vertical stack of a {@link ScrollPanel}.
 *
 * <p>An entry owns its widgets and reports its own height. That height may change between layout
 * passes (a section collapsing, a text block re-wrapping); whenever it does, the entry calls
 * {@link #requestLayout()} so the panel re-stacks everything below it and clamps the scroll amount.</p>
 *
 * <p>Entries are laid out in screen space: {@link #place(int, int, int)} already includes the current
 * scroll offset, so widgets can be positioned and hit-tested without any coordinate translation.</p>
 */
public abstract class PanelEntry {
    protected int x;
    protected int y;
    protected int width;

    private ScrollPanel panel;

    /** Height this entry currently occupies.*/
    public abstract int getHeight();

    /** Positions owned widgets and nested entries.*/
    protected abstract void arrange();

    /**
     * Reports every widget that is currently interactive (not hidden), in tab order.
     */
    public abstract void visitWidgets(Consumer<AbstractWidget> consumer);

    /**
     * Draws this entry's decoration (backgrounds, labels, ...). The panel draws the widgets from
     * {@link #visitWidgets(Consumer)} on top afterward, so implementations only handle their own
     * chrome and forward the call to nested entries.
     */
    public void extract(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
    }

    /** Moves this entry into the given content slot.*/
    public final void place(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
        arrange();
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getWidth() {
        return width;
    }

    public final int getBottom() {
        return y + getHeight();
    }

    /**
     * Binds this entry to the panel that renders it, or to {@code null} when it is removed.
     * Called by the owner; entries that hold other entries forward it to their children.
     */
    public void attach(ScrollPanel panel) {
        this.panel = panel;
    }

    protected final ScrollPanel getPanel() {
        return panel;
    }

    /** Re-stacks the whole panel. Call after anything that changes this entry's height. */
    protected final void requestLayout() {
        if (panel != null) {
            panel.refreshLayout();
        }
    }
}