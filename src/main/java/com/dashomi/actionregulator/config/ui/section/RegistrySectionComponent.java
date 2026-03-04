package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.ui.RegistryPickerComponent;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import net.minecraft.network.chat.Component;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.List;

public abstract class RegistrySectionComponent {

    private static final ButtonComponent.Renderer INVERT_ON =
            ButtonComponent.Renderer.flat(0xFF226622, 0xFF338833, 0xFF114411);
    private static final ButtonComponent.Renderer INVERT_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

    private final String labelKey;
    private final int topMargin;
    private final BooleanSupplier invertGetter;
    private final Consumer<Boolean> invertSetter;
    private final List<String> selected;
    private final List<String> allEntries;
    private final String registryType;

    protected RegistrySectionComponent(
            String labelKey,
            int topMargin,
            BooleanSupplier invertGetter,
            Consumer<Boolean> invertSetter,
            List<String> selected,
            List<String> allEntries,
            String registryType
    ) {
        this.labelKey = labelKey;
        this.topMargin = topMargin;
        this.invertGetter = invertGetter;
        this.invertSetter = invertSetter;
        this.selected = selected;
        this.allEntries = allEntries;
        this.registryType = registryType;
    }

    public FlowLayout build() {
        FlowLayout section = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        section.gap(4);

        section.child(buildHeader());
        section.child(new RegistryPickerComponent(selected, allEntries, registryType).build());

        FlowLayout extraContent = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        extraContent.gap(4);
        buildExtraOptions(extraContent);

        if (!extraContent.children().isEmpty()) {
            section.child(buildExtraOptionsDropdown(extraContent));
        }

        return section;
    }

    protected void buildExtraOptions(FlowLayout container) {
        // optionally filled by subclasses
    }

    private FlowLayout buildExtraOptionsDropdown(FlowLayout content) {
        content.padding(Insets.of(4, 4, 6, 4));
        content.surface(Surface.flat(0x22FFFFFF));

        boolean[] open = { false };
        content.sizing(Sizing.fill(100), Sizing.fixed(0));

        ButtonComponent toggle = UIComponents.button(
                Component.literal("▶ Extra Options"),
                b -> {
                    open[0] = !open[0];
                    b.setMessage(Component.literal(open[0] ? "▼ Extra Options" : "▶ Extra Options"));
                    content.sizing(Sizing.fill(100), open[0] ? Sizing.content() : Sizing.fixed(0));
                });
        toggle.sizing(Sizing.fill(100), Sizing.fixed(14));
        toggle.renderer(ButtonComponent.Renderer.flat(0xFF333333, 0xFF444444, 0xFF222222));

        FlowLayout wrapper = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        wrapper.gap(2);
        wrapper.margins(Insets.top(2));
        wrapper.child(toggle);
        wrapper.child(content);
        return wrapper;
    }

    private FlowLayout buildHeader() {
        FlowLayout row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        row.verticalAlignment(VerticalAlignment.CENTER);
        row.gap(4);
        if (topMargin > 0) row.margins(Insets.top(topMargin));

        LabelComponent label = UIComponents.label(Component.translatable(labelKey))
                .color(Color.ofArgb(0xFF1E648D));
        label.sizing(Sizing.expand(), Sizing.content());

        boolean[] state = { invertGetter.getAsBoolean() };
        ButtonComponent invertBtn = UIComponents.button(
                Component.translatable(state[0]
                        ? "actionregulator.ui.registryinvert.on"
                        : "actionregulator.ui.registryinvert.off"),
                b -> {
                    state[0] = !state[0];
                    invertSetter.accept(state[0]);
                    b.setMessage(Component.translatable(state[0]
                            ? "actionregulator.ui.registryinvert.on"
                            : "actionregulator.ui.registryinvert.off"));
                    b.renderer(state[0] ? INVERT_ON : INVERT_OFF);
                });
        invertBtn.sizing(Sizing.fixed(68), Sizing.fixed(14));
        invertBtn.renderer(state[0] ? INVERT_ON : INVERT_OFF);

        row.child(label);
        row.child(invertBtn);
        return row;
    }
}
