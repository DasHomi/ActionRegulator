package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.core.registries.BuiltInRegistries;
import java.util.List;

public class TargetEntitiesSectionComponent extends RegistrySectionComponent {

    private static final List<String> ALL_ENTITIES = BuiltInRegistries.ENTITY_TYPE.keySet()
            .stream().map(Object::toString).sorted().toList();

    private final RuleModule rule;

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
        this.rule = rule;
    }

    @Override
    protected void buildExtraDropdowns(FlowLayout section) {
        FlowLayout dropdown = buildDropdown("Extra Options", container -> {
            buildCustomNameOptions(
                    container,
                    () -> rule.targetEntityCustomNameMode,
                    v -> rule.targetEntityCustomNameMode = v,
                    () -> rule.targetEntityCustomNameFilter,
                    v -> rule.targetEntityCustomNameFilter = v
            );
        });

        if (dropdown != null) section.child(dropdown);
    }
}
