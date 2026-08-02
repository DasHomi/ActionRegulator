package com.dashomi.actionregulator.config.ui;

import com.dashomi.actionregulator.config.ui.widgets.WidgetEntry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base for every entry with a clickable header that shows or hides a body of nested
 * {@link PanelEntry entries}. Sections nest freely, so a section body may hold further sections.
 *
 * <p>Subclasses supply their own look through the protected hooks ({@link #getHeaderHeight()},
 * {@link #getBodyIndent()}, {@link #getBodyColor()}, {@link #createHeaderMessage()}) and react to
 * the toggle through {@link #onExpandedChanged(boolean)}. Toggling only changes {@link #getHeight()}
 * and which widgets are reported, re-stacking is handled by the panel.</p>
 */
public abstract class CollapsibleSection extends PanelEntry {
    protected static final String EXPANDED_ARROW_KEY = "actionregulator.ui.module.collapse";
    protected static final String COLLAPSED_ARROW_KEY = "actionregulator.ui.module.expand";

    private static final int HEADER_WIDGET_SPACING = 2;
    private static final int MIN_HEADER_WIDTH = 20;

    private final List<PanelEntry> children = new ArrayList<>();
    private final List<AbstractWidget> headerWidgets = new ArrayList<>();
    private final Button header;

    private Component title;
    private boolean expanded;
    private int entrySpacing = 4;

    protected CollapsibleSection(Component title, boolean expanded) {
        this.title = title;
        this.expanded = expanded;
        this.header = Button.builder(Component.empty(), button -> setExpanded(!this.expanded))
                .bounds(0, 0, MIN_HEADER_WIDTH, getHeaderHeight())
                .build();
        this.header.setMessage(createHeaderMessage());
    }

    // ---------------------------------------------------------------- content

    public <T extends PanelEntry> T addEntry(T entry) {
        children.add(entry);
        entry.attach(getPanel());
        requestLayout();
        return entry;
    }

    /** Adds a plain widget to the body, stretched over the full body width. */
    public <T extends AbstractWidget> T addWidget(T widget) {
        return addWidget(widget, WidgetEntry.Alignment.FILL);
    }

    public <T extends AbstractWidget> T addWidget(T widget, WidgetEntry.Alignment alignment) {
        addEntry(new WidgetEntry(widget, alignment));
        return widget;
    }

    /** Pins a widget to the right of the header. Widgets keep their own size and are laid out right to left. */
    public <T extends AbstractWidget> T addHeaderWidget(T widget) {
        headerWidgets.add(widget);
        requestLayout();
        return widget;
    }

    public void removeEntry(PanelEntry entry) {
        if (children.remove(entry)) {
            entry.attach(null);
            requestLayout();
        }
    }

    public void clearEntries() {
        for (PanelEntry child : children) {
            child.attach(null);
        }
        children.clear();
        requestLayout();
    }

    public List<PanelEntry> getEntries() {
        return Collections.unmodifiableList(children);
    }

    // ------------------------------------------------------------------ state

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        if (this.expanded == expanded) {
            return;
        }
        this.expanded = expanded;
        header.setMessage(createHeaderMessage());
        onExpandedChanged(expanded);
        requestLayout();
    }

    public Component getTitle() {
        return title;
    }

    public void setTitle(Component title) {
        this.title = title;
        header.setMessage(createHeaderMessage());
    }

    /** Vertical gap between two body entries. */
    public CollapsibleSection setEntrySpacing(int entrySpacing) {
        this.entrySpacing = entrySpacing;
        requestLayout();
        return this;
    }

    protected Button getHeader() {
        return header;
    }

    // ------------------------------------------------------------ look & hooks

    protected int getHeaderHeight() {
        return 20;
    }

    /** Horizontal offset of the body against the header. */
    protected int getBodyIndent() {
        return 8;
    }

    /** Space between the header and the first body entry, and below the last one. */
    protected int getBodyPadding() {
        return 4;
    }

    /** ARGB fill drawn behind the body while expanded. */
    protected int getBodyColor() {
        return 0x40000000;
    }

    /** Label of the header button.*/
    protected Component createHeaderMessage() {
        return Component.translatable(expanded ? EXPANDED_ARROW_KEY : COLLAPSED_ARROW_KEY)
                .append(" ")
                .append(title);
    }

    /** Called after the section was expanded or collapsed*/
    protected void onExpandedChanged(boolean expanded) {
    }

    /**
     * Places the header button inside the area left of the header widgets. Subclasses that put their
     * own widgets in the header shrink the button here and lay out the rest of the area themselves.
     */
    protected void arrangeHeader(int x, int y, int width, int height) {
        header.setPosition(x, y);
        header.setWidth(width);
        header.setHeight(height);
    }

    /** Reports the widgets a subclass placed in the header, right after the header button. */
    protected void visitHeaderContent(Consumer<AbstractWidget> consumer) {
    }

    // ----------------------------------------------------------------- layout

    @Override
    public int getHeight() {
        if (!expanded) {
            return getHeaderHeight();
        }

        int height = getHeaderHeight() + getBodyPadding();
        for (int i = 0; i < children.size(); i++) {
            if (i > 0) {
                height += entrySpacing;
            }
            height += children.get(i).getHeight();
        }
        return height + getBodyPadding();
    }

    @Override
    protected void arrange() {
        int headerHeight = getHeaderHeight();

        int headerRight = x + width;
        for (AbstractWidget widget : headerWidgets) {
            headerRight -= widget.getWidth();
            widget.setPosition(headerRight, y + (headerHeight - widget.getHeight()) / 2);
            headerRight -= HEADER_WIDGET_SPACING;
        }

        arrangeHeader(x, y, Math.max(MIN_HEADER_WIDTH, headerRight - x), headerHeight);

        if (!expanded) {
            return;
        }

        int indent = getBodyIndent();
        int childX = x + indent;
        int childWidth = Math.max(0, width - indent);
        int cursor = y + headerHeight + getBodyPadding();
        for (PanelEntry child : children) {
            child.place(childX, cursor, childWidth);
            cursor += child.getHeight() + entrySpacing;
        }
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        consumer.accept(header);
        visitHeaderContent(consumer);
        for (AbstractWidget widget : headerWidgets) {
            consumer.accept(widget);
        }
        if (expanded) {
            for (PanelEntry child : children) {
                child.visitWidgets(consumer);
            }
        }
    }

    @Override
    public void extract(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        if (!expanded) {
            return;
        }

        graphics.fill(x, y + getHeaderHeight(), x + width, getBottom(), getBodyColor());
        for (PanelEntry child : children) {
            child.extract(graphics, mouseX, mouseY, delta);
        }
    }

    @Override
    public void attach(ScrollPanel panel) {
        super.attach(panel);
        for (PanelEntry child : children) {
            child.attach(panel);
        }
    }
}
