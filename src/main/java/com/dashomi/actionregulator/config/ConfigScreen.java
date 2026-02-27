package com.dashomi.actionregulator.config;

import com.dashomi.actionregulator.config.ui.RuleModuleUi;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ConfigScreen extends BaseOwoScreen<FlowLayout> {

    private final Screen parent;
    private final ActionRegulatorConfig config;
    private FlowLayout moduleList;

    public ConfigScreen(Screen parent) {
        this.parent = parent;
        this.config = ActionRegulatorConfig.get();
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, UIContainers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout root) {
        root.surface(Surface.VANILLA_TRANSLUCENT);
        root.padding(Insets.of(12));
        root.gap(8);
        root.horizontalAlignment(HorizontalAlignment.CENTER);

        LabelComponent title = UIComponents.label(Component.translatable("actionregulator.ui.title"));
        title.color(Color.ofArgb(0xFFFFFFFF));
        title.margins(Insets.bottom(6));
        root.child(title);

        ButtonComponent addBtn = UIComponents.button(
                Component.translatable("actionregulator.ui.buttons.addModule"), btn -> {
                    RuleModule newRule = new RuleModule();
                    config.rules.add(newRule);
                    moduleList.child(new RuleModuleUi(newRule).build(config, moduleList));
                });
        addBtn.margins(Insets.bottom(4));
        root.child(addBtn);

        moduleList = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        moduleList.gap(4);

        for (RuleModule rule : config.rules) {
            moduleList.child(new RuleModuleUi(rule).build(config, moduleList));
        }

        ScrollContainer<FlowLayout> scroll = UIContainers.verticalScroll(
                Sizing.fill(100), Sizing.expand(), moduleList);
        root.child(scroll);

        ButtonComponent doneBtn = UIComponents.button(
                Component.translatable("actionregulator.ui.buttons.done"), btn -> onClose());
        doneBtn.margins(Insets.top(6));
        root.child(doneBtn);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
        config.save();
    }
}
