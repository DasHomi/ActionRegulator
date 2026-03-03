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
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConfigScreen extends BaseOwoScreen<FlowLayout> {

    private final Screen parent;
    private final ActionRegulatorConfig config;
    private FlowLayout moduleList;
    private LabelComponent importErrorLabel;

    private static final ButtonComponent.Renderer ERROR_RENDERER =
            ButtonComponent.Renderer.flat(0xFF8B1A1A, 0xFFAA2222, 0xFF6B0F0F);
    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "actionregulator-error-reset");
                t.setDaemon(true);
                return t;
            });

    private void showImportError(ButtonComponent btn, String message) {
        btn.renderer(ERROR_RENDERER);
        if (importErrorLabel != null) {
            importErrorLabel.text(Component.literal("✘ " + (message != null ? message : "Unknown error"))
                    .withColor(0xFFFF5555));
        }
        SCHEDULER.schedule(() -> minecraft.execute(() -> {
            btn.renderer(ButtonComponent.Renderer.VANILLA);
            if (importErrorLabel != null) {
                importErrorLabel.text(Component.empty());
            }
        }), 3, TimeUnit.SECONDS);
    }

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

        FlowLayout header = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        header.verticalAlignment(VerticalAlignment.CENTER);
        header.gap(4);
        header.margins(Insets.bottom(2));

        LabelComponent title = UIComponents.label(Component.translatable("actionregulator.ui.title"));
        title.color(Color.ofArgb(0xFFFFFFFF));
        title.sizing(Sizing.expand(), Sizing.content());

        ButtonComponent addBtn = UIComponents.button(
                Component.translatable("actionregulator.ui.buttons.addModule"), btn -> {
                    RuleModule newRule = new RuleModule();
                    config.rules.add(newRule);
                    moduleList.child(new RuleModuleUi(newRule).build(config, moduleList));
                });

        ButtonComponent importBtn = UIComponents.button(
                Component.translatable("actionregulator.ui.buttons.importModule"), btn -> {
                    Thread t = new Thread(() -> {
                        Path exportsDir = net.fabricmc.loader.api.FabricLoader.getInstance()
                                .getConfigDir().resolve("actionregulator").resolve("exports");
                        String startPath = java.nio.file.Files.isDirectory(exportsDir)
                                ? exportsDir.toString() + java.io.File.separator
                                : null;

                        String chosen = TinyFileDialogs.tinyfd_openFileDialog(
                                "Import Rule Module", startPath,
                                org.lwjgl.PointerBuffer.allocateDirect(1).put(0,
                                        org.lwjgl.system.MemoryUtil.memUTF8("*.json")),
                                "JSON files (*.json)", false);

                        if (chosen == null) return;
                        try {
                            RuleModule imported = ConfigManager.importRule(Path.of(chosen));
                            config.rules.add(imported);
                            this.minecraft.execute(() ->
                                    moduleList.child(new RuleModuleUi(imported).build(config, moduleList)));
                        } catch (Exception e) {
                            com.dashomi.actionregulator.ActionregulatorClient.LOGGER.error(
                                    "Failed to import rule module", e);
                            this.minecraft.execute(() -> showImportError(btn, e.getMessage()));
                        }
                    }, "actionregulator-import");
                    t.setDaemon(true);
                    t.start();
                });

        header.child(title);
        importErrorLabel = UIComponents.label(Component.empty());
        importErrorLabel.sizing(Sizing.content(), Sizing.content());
        header.child(importErrorLabel);
        header.child(importBtn);
        header.child(addBtn);
        root.child(header);

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
        doneBtn.sizing(Sizing.fixed(200), Sizing.fixed(20));
        doneBtn.margins(Insets.top(4));
        root.child(doneBtn);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
        config.save();
    }
}
