package com.dashomi.actionregulator.config;

import com.dashomi.actionregulator.config.ui.ActionRegulatorModule;
import com.dashomi.actionregulator.config.ui.ScrollPanel;
import com.dashomi.actionregulator.config.ui.widgets.TextEntry;
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

    // debug, remove
    private static final int DUMMY_MODULE_COUNT = 12;

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
        buildEntries();
        panel.setScrollAmount(scrollAmount);

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds((this.width - Button.DEFAULT_WIDTH) / 2, this.height - 27, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT)
                .build());
    }

    // debugging method to check layouting
    private void buildEntries() {
        for (int i = 1; i <= DUMMY_MODULE_COUNT; i++) {
            ActionRegulatorModule module = panel.addEntry(new ActionRegulatorModule(Component.literal("Dummy 67-" + i), i <= 2));

            module.addEntry(new TextEntry(this.font, Component.literal("Placeholder line for module " + i), TextEntry.MUTED_COLOR));
            if (i % 3 == 0) {
                module.addEntry(new TextEntry(this.font, Component.literal(
                        "A longer placeholder that wraps over several lines once the window gets narrow, "
                                + "so the entry height changes with the panel width.")));
            }
            module.addWidget(Button.builder(Component.literal("Dummy button " + i), button -> {}).build());
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
