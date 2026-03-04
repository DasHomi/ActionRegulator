package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.List;

public class TargetEntitiesSectionComponent extends RegistrySectionComponent {

    private static final List<String> ALL_ENTITIES = BuiltInRegistries.ENTITY_TYPE.keySet()
            .stream().map(Object::toString).sorted().toList();

    public TargetEntitiesSectionComponent(RuleModule rule) {
        super(
                "actionregulator.ui.section.targetEntities",
                4,
                () -> rule.invertTargetEntities,
                v -> rule.invertTargetEntities = v,
                rule.targetEntities,
                ALL_ENTITIES,
                "entity"
        );
    }
}

