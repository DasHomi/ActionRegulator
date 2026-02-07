package com.dashomi.actionregulator.config;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;

import static com.dashomi.actionregulator.ActionregulatorClient.MOD_ID;

public class ConfigScreen extends BaseUIModelScreen<FlowLayout> {
    public ConfigScreen(Screen parent) {
        super(FlowLayout.class, DataSource.asset(Identifier.fromNamespaceAndPath(MOD_ID, "config_screen")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.childById(ButtonComponent.class, "the-button").onPress(button -> {
            System.out.println("click");
        });
    }
}
