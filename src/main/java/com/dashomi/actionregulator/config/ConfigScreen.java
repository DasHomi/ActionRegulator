package com.dashomi.actionregulator.config;

import com.dashomi.actionregulator.config.ui.ActionRegulatorModule;
import com.dashomi.actionregulator.config.ui.ScrollPanel;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable("actionregulator.ui.title");

    private static final int HEADER_HEIGHT = 36;
    private static final int FOOTER_HEIGHT = 36;
    private static final int MIN_PANEL_HEIGHT = 40;
    private static final int MIN_PANEL_WIDTH = 120;
    private static final int PANEL_MARGIN = 24;

    private final Screen parent;
    private final ActionRegulatorConfig config;

    private ScrollPanel panel;
    private double scrollAmount;

    public ConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.config = ActionRegulatorConfig.get();
    }

    @Override
    protected void init() {
        int panelWidth = Math.max(MIN_PANEL_WIDTH, this.width - 2 * PANEL_MARGIN);
        int panelHeight = Math.max(MIN_PANEL_HEIGHT, this.height - HEADER_HEIGHT - FOOTER_HEIGHT);

        panel = addRenderableWidget(new ScrollPanel((this.width - panelWidth) / 2, HEADER_HEIGHT, panelWidth, panelHeight));
        buildModules();
        panel.setScrollAmount(scrollAmount);

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds((this.width - Button.DEFAULT_WIDTH) / 2, this.height - 27, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT)
                .build());
    }

    private void buildModules() {
        for (RuleModule rule : config.rules) {
            ActionRegulatorModule module = panel.addEntry(new ActionRegulatorModule(this.font, rule.name, rule.expanded));
            module.setEnabled(rule.enabled);
            module.setEnabledResponder(enabled -> rule.enabled = enabled);

            module.setTriggerType(rule.triggerType);
            module.setTriggerResponder(trigger -> rule.triggerType = trigger);

            module.setTargetMode(rule.targetMode);
            module.setTargetResponder(target -> rule.targetMode = target);

            module.setNotificationType(rule.notificationType);
            module.setNotificationResponder(notification -> rule.notificationType = notification);

            module.setNameResponder(name -> rule.name = name);
        }
    }

    @Override
    protected void repositionElements() {
        // init() rebuilds the panel from scratch, so keep the scroll position across resizes.
        if (panel != null) {
            scrollAmount = panel.scrollAmount();
        }
        super.repositionElements();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        config.save();
        this.minecraft.gui.setScreen(parent);
    }
}
