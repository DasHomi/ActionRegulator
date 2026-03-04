package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.List;

public class TargetBlocksSectionComponent extends RegistrySectionComponent {

    private static final List<String> ALL_BLOCKS = BuiltInRegistries.BLOCK.keySet()
            .stream().map(Object::toString).sorted().toList();

    public TargetBlocksSectionComponent(RuleModule rule) {
        super(
                "actionregulator.ui.section.targetBlocks",
                4,
                () -> rule.invertTargetBlocks,
                v -> rule.invertTargetBlocks = v,
                rule.targetBlocks,
                ALL_BLOCKS,
                "block"
        );
    }
}

