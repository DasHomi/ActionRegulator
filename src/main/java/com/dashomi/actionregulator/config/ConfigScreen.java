package com.dashomi.actionregulator.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable("actionregulator.ui.title");

    private final Screen parent;
    private final ActionRegulatorConfig config;

    public ConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.config = ActionRegulatorConfig.get();
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose())
                .bounds((this.width - Button.DEFAULT_WIDTH) / 2, this.height - 27, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT)
                .build());
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
