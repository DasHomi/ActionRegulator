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
import java.util.function.Supplier;
import java.util.List;
import com.dashomi.actionregulator.enums.CustomNameMode;
import io.wispforest.owo.ui.component.TextBoxComponent;

public abstract class RegistrySectionComponent {

    protected static final ButtonComponent.Renderer MODE_ON =
            ButtonComponent.Renderer.flat(0xFF2255AA, 0xFF3366CC, 0xFF1A4488);
    protected static final ButtonComponent.Renderer MODE_OFF =
            ButtonComponent.Renderer.flat(0xFF555555, 0xFF666666, 0xFF444444);

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

        buildExtraDropdowns(section);

        return section;
    }

    protected void buildExtraDropdowns(FlowLayout section) { }

    protected final FlowLayout buildDropdown(String title, Consumer<FlowLayout> filler) {
        FlowLayout content = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        content.gap(4);
        content.padding(Insets.of(4, 4, 6, 4));
        content.surface(Surface.flat(0x22FFFFFF));
        filler.accept(content);

        if (content.children().isEmpty()) return null;

        boolean[] open = { false };
        content.sizing(Sizing.fill(100), Sizing.fixed(0));

        ButtonComponent toggle = UIComponents.button(
                Component.literal("▶ " + title),
                b -> {
                    open[0] = !open[0];
                    b.setMessage(Component.literal((open[0] ? "▼ " : "▶ ") + title));
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

    protected void buildCustomNameOptions(FlowLayout container, Supplier<CustomNameMode> getter, Consumer<CustomNameMode> setter, Supplier<String> filterGetter, Consumer<String> filterSetter) {
        FlowLayout modeRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        modeRow.verticalAlignment(VerticalAlignment.CENTER);
        modeRow.gap(4);
        modeRow.child(UIComponents.label(
                Component.translatable("actionregulator.ui.customName.label"))
                .sizing(Sizing.content(), Sizing.content()));

        CustomNameMode[] modes = CustomNameMode.values();
        String[] labelKeys = {
                "actionregulator.ui.customName.any",
                "actionregulator.ui.customName.named",
                "actionregulator.ui.customName.unnamed"
        };
        ButtonComponent[] btns = new ButtonComponent[modes.length];
        for (int i = 0; i < modes.length; i++) {
            final CustomNameMode mode = modes[i];
            final int idx = i;
            btns[i] = UIComponents.button(Component.translatable(labelKeys[i]), b -> {
                setter.accept(mode);
                for (int j = 0; j < btns.length; j++)
                    btns[j].renderer(j == idx ? MODE_ON : MODE_OFF);
            });
            btns[i].sizing(Sizing.content(), Sizing.fixed(14));
            btns[i].renderer(getter.get() == modes[i] ? MODE_ON : MODE_OFF);
            modeRow.child(btns[i]);
        }
        container.child(modeRow);

        FlowLayout nameRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
        nameRow.verticalAlignment(VerticalAlignment.CENTER);
        nameRow.gap(6);
        nameRow.child(UIComponents.label(
                Component.translatable("actionregulator.ui.customName.input.label"))
                .sizing(Sizing.content(), Sizing.content()));
        TextBoxComponent nameInput = UIComponents.textBox(Sizing.expand(), filterGetter.get());
        nameInput.setMaxLength(64);
        nameInput.onChanged().subscribe(filterSetter::accept);
        nameRow.child(nameInput);
        container.child(nameRow);
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
