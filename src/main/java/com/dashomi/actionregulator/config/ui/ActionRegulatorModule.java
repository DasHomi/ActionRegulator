package com.dashomi.actionregulator.config.ui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ActionRegulatorModule extends CollapsibleSection {
    private static final Component NAME_LABEL = Component.translatable("actionregulator.ui.module.name");

    private static final int BODY_COLOR = 0x50000000;
    private static final int NAME_GAP = 4;
    private static final int MIN_NAME_WIDTH = 20;
    private static final int MAX_NAME_LENGTH = 64;

    private final EditBox nameField;

    private Consumer<String> nameResponder;
    private int nameFieldWidth = -1;

    public ActionRegulatorModule(Font font, String name) {
        this(font, name, false);
    }

    public ActionRegulatorModule(Font font, String name, boolean expanded) {
        super(Component.literal(name), expanded);

        this.nameField = new EditBox(font, 0, 0, MIN_NAME_WIDTH, getHeaderHeight(), NAME_LABEL);
        this.nameField.setMaxLength(MAX_NAME_LENGTH);
        this.nameField.setValue(name);
        // Set last so seeding the value above does not already fire it.
        this.nameField.setResponder(this::onNameChanged);
    }

    public String getName() {
        return nameField.getValue();
    }

    /** Called with the new name on every edit. */
    public void setNameResponder(Consumer<String> nameResponder) {
        this.nameResponder = nameResponder;
    }

    private void onNameChanged(String name) {
        setTitle(Component.literal(name));
        if (nameResponder != null) {
            nameResponder.accept(name);
        }
    }

    @Override
    protected int getBodyColor() {
        return BODY_COLOR;
    }

    @Override
    protected Component createHeaderMessage() {
        return Component.translatable(isExpanded() ? EXPANDED_ARROW_KEY : COLLAPSED_ARROW_KEY);
    }

    @Override
    protected void arrangeHeader(int x, int y, int width, int height) {
        int toggleWidth = Math.min(height, width);
        super.arrangeHeader(x, y, toggleWidth, height);

        int nameX = x + toggleWidth + NAME_GAP;
        int nameWidth = Math.max(MIN_NAME_WIDTH, x + width - nameX);

        nameField.setWidth(nameWidth);
        nameField.setHeight(height);
        nameField.setPosition(nameX, y);

        if (nameWidth != nameFieldWidth) {
            nameFieldWidth = nameWidth;
            anchorNameAtStart();
        }
    }

    private void anchorNameAtStart() {
        int cursor = nameField.getCursorPosition();
        // Only a position at or before the current offset pulls the visible window back left.
        nameField.setCursorPosition(0);
        nameField.setCursorPosition(cursor);
        nameField.setHighlightPos(cursor);
    }

    @Override
    protected void visitHeaderContent(Consumer<AbstractWidget> consumer) {
        consumer.accept(nameField);
    }
}
