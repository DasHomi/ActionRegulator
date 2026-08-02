package com.dashomi.actionregulator.config.ui;

import net.minecraft.network.chat.Component;

/**
 * A rule module in the main list: the top level {@link CollapsibleSection} that a
 * {@link ScrollPanel} stacks. Its body holds the module's controls.
 */
public class ActionRegulatorModule extends CollapsibleSection {
    private static final int BODY_COLOR = 0x50000000;

    public ActionRegulatorModule(Component title) {
        this(title, false);
    }

    public ActionRegulatorModule(Component title, boolean expanded) {
        super(title, expanded);
    }

    @Override
    protected int getBodyColor() {
        return BODY_COLOR;
    }
}
