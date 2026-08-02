package com.dashomi.actionregulator.config.ui;

import com.dashomi.actionregulator.config.ui.widgets.SelectorEntry;
import com.dashomi.actionregulator.config.ui.widgets.TextEntry;
import com.dashomi.actionregulator.enums.NotificationType;
import com.dashomi.actionregulator.enums.TargetMode;
import com.dashomi.actionregulator.enums.TriggerType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ActionRegulatorModule extends CollapsibleSection {
    private static final Component NAME_LABEL = Component.translatable("actionregulator.ui.module.name");
    private static final Component ENABLED_LABEL =
            Component.translatable("actionregulator.ui.module.enabled").withStyle(ChatFormatting.GREEN);
    private static final Component DISABLED_LABEL =
            Component.translatable("actionregulator.ui.module.disabled").withStyle(ChatFormatting.RED);
    private static final Component TRIGGER_LABEL = Component.translatable("actionregulator.ui.section.trigger");
    private static final Component TARGET_LABEL = Component.translatable("actionregulator.ui.section.target");
    private static final Component NOTIFICATION_LABEL = Component.translatable("actionregulator.ui.section.notification");

    private static final int BODY_COLOR = 0x50000000;
    private static final int NAME_GAP = 4;
    private static final int MIN_NAME_WIDTH = 20;
    private static final int MAX_NAME_LENGTH = 64;
    private static final int ENABLED_WIDTH = 32;

    private final EditBox nameField;
    private final Button enabledButton;
    private final SelectorEntry<TriggerType> triggerSelector;
    private final SelectorEntry<TargetMode> targetSelector;
    private final SelectorEntry<NotificationType> notificationSelector;

    private Consumer<String> nameResponder;
    private Consumer<Boolean> enabledResponder;
    private boolean enabled = true;
    private int nameFieldWidth = -1;

    public ActionRegulatorModule(Font font, String name) {
        this(font, name, false);
    }

    public ActionRegulatorModule(Font font, String name, boolean expanded) {
        super(Component.literal(name), expanded);

        this.nameField = new EditBox(font, 0, 0, MIN_NAME_WIDTH, getHeaderHeight(), NAME_LABEL);
        this.nameField.setMaxLength(MAX_NAME_LENGTH);
        this.nameField.setValue(name);

        this.nameField.setResponder(this::onNameChanged);

        this.enabledButton = Button.builder(ENABLED_LABEL, button -> setEnabled(!this.enabled))
                .bounds(0, 0, ENABLED_WIDTH, getHeaderHeight())
                .build();

        addEntry(new TextEntry(font, TRIGGER_LABEL));
        this.triggerSelector = addEntry(SelectorEntry.of(font, TriggerType.class, ActionRegulatorModule::triggerLabel));

        addEntry(new TextEntry(font, TARGET_LABEL));
        this.targetSelector = addEntry(SelectorEntry.of(font, TargetMode.class, ActionRegulatorModule::targetLabel));

        addEntry(new TextEntry(font, NOTIFICATION_LABEL));
        this.notificationSelector =
                addEntry(SelectorEntry.of(font, NotificationType.class, ActionRegulatorModule::notificationLabel));
    }

    private static Component triggerLabel(TriggerType trigger) {
        return Component.translatable(switch (trigger) {
            case ON_ATTACK -> "actionregulator.ui.trigger.attack";
            case ON_USE -> "actionregulator.ui.trigger.use";
        });
    }

    private static Component targetLabel(TargetMode target) {
        return Component.translatable(switch (target) {
            case BLOCKS -> "actionregulator.ui.targetmode.blocks";
            case ENTITIES -> "actionregulator.ui.targetmode.entities";
        });
    }

    private static Component notificationLabel(NotificationType notification) {
        return Component.translatable(switch (notification) {
            case OFF -> "actionregulator.ui.notification.off";
            case SOUND -> "actionregulator.ui.notification.sound";
            case SYMBOL -> "actionregulator.ui.notification.symbol";
            case TEXT -> "actionregulator.ui.notification.text";
        });
    }

    public String getName() {
        return nameField.getValue();
    }

    public void setNameResponder(Consumer<String> nameResponder) {
        this.nameResponder = nameResponder;
    }

    private void onNameChanged(String name) {
        setTitle(Component.literal(name));
        if (nameResponder != null) {
            nameResponder.accept(name);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        enabledButton.setMessage(enabled ? ENABLED_LABEL : DISABLED_LABEL);
        if (enabledResponder != null) {
            enabledResponder.accept(enabled);
        }
    }

    public void setEnabledResponder(Consumer<Boolean> enabledResponder) {
        this.enabledResponder = enabledResponder;
    }

    public TriggerType getTriggerType() {
        return triggerSelector.getSelected();
    }

    public void setTriggerType(TriggerType trigger) {
        triggerSelector.setSelected(trigger);
    }

    /** Called with the new trigger whenever another one is picked. */
    public void setTriggerResponder(Consumer<TriggerType> triggerResponder) {
        triggerSelector.setResponder(triggerResponder);
    }

    public TargetMode getTargetMode() {
        return targetSelector.getSelected();
    }

    public void setTargetMode(TargetMode target) {
        targetSelector.setSelected(target);
    }

    /** Called with the new target whenever another one is picked. */
    public void setTargetResponder(Consumer<TargetMode> targetResponder) {
        targetSelector.setResponder(targetResponder);
    }

    public NotificationType getNotificationType() {
        return notificationSelector.getSelected();
    }

    public void setNotificationType(NotificationType notification) {
        notificationSelector.setSelected(notification);
    }

    /** Called with the new notification whenever another one is picked. */
    public void setNotificationResponder(Consumer<NotificationType> notificationResponder) {
        notificationSelector.setResponder(notificationResponder);
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
        int collapseWidth = Math.min(height, width);
        super.arrangeHeader(x, y, collapseWidth, height);

        int nameX = x + collapseWidth + NAME_GAP;
        // The name field gives up the space of the on/off button, which sits right of it.
        int nameWidth = Math.max(MIN_NAME_WIDTH, x + width - ENABLED_WIDTH - NAME_GAP - nameX);

        nameField.setWidth(nameWidth);
        nameField.setHeight(height);
        nameField.setPosition(nameX, y);

        enabledButton.setWidth(ENABLED_WIDTH);
        enabledButton.setHeight(height);
        enabledButton.setPosition(nameX + nameWidth + NAME_GAP, y);

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
        consumer.accept(enabledButton);
    }
}
