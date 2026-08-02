package com.dashomi.actionregulator.config.ui;

import com.dashomi.actionregulator.config.ui.widgets.WidgetEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Scrolling viewport that stacks {@link PanelEntry entries} vertically.
 *
 * <p> To update size call {@link refreshLayout() refreshLayout()} after adding/removing entries or changing their height.
 */
public class ScrollPanel extends AbstractContainerWidget {
    private static final int SCROLL_RATE = 12;
    private static final int SCROLLBAR_GAP = 4;
    private static final int BACKGROUND_COLOR = 0x60000000;

    private final List<PanelEntry> entries = new ArrayList<>();
    private final List<AbstractWidget> childWidgets = new ArrayList<>();

    private int padding = 6;
    private int entrySpacing = 4;
    private int totalContentHeight;

    public ScrollPanel(int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY, AbstractScrollArea.defaultSettings(SCROLL_RATE));
    }

    public <T extends PanelEntry> T addEntry(T entry) {
        entries.add(entry);
        entry.attach(this);
        refreshLayout();
        return entry;
    }

    public <T extends AbstractWidget> T addWidget(T widget) {
        return addWidget(widget, WidgetEntry.Alignment.FILL);
    }

    public <T extends AbstractWidget> T addWidget(T widget, WidgetEntry.Alignment alignment) {
        addEntry(new WidgetEntry(widget, alignment));
        return widget;
    }

    public void removeEntry(PanelEntry entry) {
        if (entries.remove(entry)) {
            entry.attach(null);
            refreshLayout();
        }
    }

    public void clearEntries() {
        for (PanelEntry entry : entries) {
            entry.attach(null);
        }
        entries.clear();
        setFocused((GuiEventListener) null);
        refreshLayout();
    }

    public List<PanelEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /** Width available to an entry*/
    public int contentWidth() {
        return Math.max(0, width - 2 * padding - scrollbarReserve());
    }

    /** Re-measures and re-stacks every entry, then re-collects the widgets that render and take input. */
    public void refreshLayout() {
        totalContentHeight = stackEntries();
        // Clamps against the new content height and re-places the entries at the resulting offset.
        setScrollAmount(scrollAmount());

        childWidgets.clear();
        for (PanelEntry entry : entries) {
            entry.visitWidgets(childWidgets::add);
        }

        GuiEventListener focused = getFocused();
        if (focused != null && !childWidgets.contains(focused)) {
            setFocused((GuiEventListener) null);
        }
    }

    /** Places every entry at its scrolled screen position and returns the total content height. */
    private int stackEntries() {
        int contentX = getX() + padding;
        int contentWidth = contentWidth();
        int top = getY() + padding - (int) scrollAmount();

        int cursor = top;
        for (PanelEntry entry : entries) {
            entry.place(contentX, cursor, contentWidth);
            cursor += entry.getHeight() + entrySpacing;
        }
        if (!entries.isEmpty()) {
            cursor -= entrySpacing;
        }

        return cursor - top + 2 * padding;
    }

    private int scrollbarReserve() {
        return scrollbarWidth() + SCROLLBAR_GAP;
    }

    @Override
    protected int contentHeight() {
        return totalContentHeight;
    }

    @Override
    public void setScrollAmount(double scrollAmount) {
        super.setScrollAmount(scrollAmount);
        // Entries live in screen space, so they have to follow the new offset.
        stackEntries();
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        refreshLayout();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        refreshLayout();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        refreshLayout();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        refreshLayout();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        refreshLayout();
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(getX(), getY(), getRight(), getBottom(), BACKGROUND_COLOR);

        graphics.enableScissor(getX(), getY(), getRight(), getBottom());
        for (PanelEntry entry : entries) {
            if (entry.getBottom() >= getY() && entry.getY() <= getBottom()) {
                entry.extract(graphics, mouseX, mouseY, delta);
            }
        }
        for (AbstractWidget widget : childWidgets) {
            widget.extractRenderState(graphics, mouseX, mouseY, delta);
        }
        graphics.disableScissor();

        extractScrollbar(graphics, mouseX, mouseY);
    }

    @Override
    public Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
        // Widgets scrolled out of the viewport keep their screen position, so clip the lookup.
        if (!isMouseOver(mouseX, mouseY)) {
            return Optional.empty();
        }

        for (AbstractWidget widget : childWidgets) {
            if (widget.isMouseOver(mouseX, mouseY)) {
                return Optional.of(widget);
            }
        }
        return Optional.empty();
    }

    @Override
    public void setFocused(GuiEventListener focused) {
        super.setFocused(focused);
        if (focused != null && Minecraft.getInstance().getLastInputType().isKeyboard()) {
            scrollIntoView(focused.getRectangle());
        }
    }

    private void scrollIntoView(ScreenRectangle rectangle) {
        int aboveTop = rectangle.top() - (getY() + padding);
        int belowBottom = rectangle.bottom() - (getBottom() - padding);
        if (aboveTop < 0) {
            setScrollAmount(scrollAmount() + aboveTop);
        } else if (belowBottom > 0) {
            setScrollAmount(scrollAmount() + belowBottom);
        }
    }

    @Override
    public ScreenRectangle getBorderForArrowNavigation(ScreenDirection direction) {
        GuiEventListener focused = getFocused();
        return focused != null
                ? focused.getBorderForArrowNavigation(direction)
                : new ScreenRectangle(getX(), getY(), width, contentHeight()).getBorder(direction);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return childWidgets;
    }

    @Override
    public Collection<? extends NarratableEntry> getNarratables() {
        return childWidgets;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}
