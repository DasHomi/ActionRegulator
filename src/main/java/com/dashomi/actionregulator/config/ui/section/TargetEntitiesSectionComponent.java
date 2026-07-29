package com.dashomi.actionregulator.config.ui.section;

import com.dashomi.actionregulator.config.RuleModule;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import java.util.List;

public class TargetEntitiesSectionComponent extends RegistrySectionComponent {

    private static final List<String> ALL_ENTITIES = BuiltInRegistries.ENTITY_TYPE.keySet()
            .stream().map(Object::toString).sorted().toList();
    private static final List<String> ALL_ENTITY_TAGS = BuiltInRegistries.ENTITY_TYPE.getTags()
            .map(tag -> toTagSelector(tag.key().location().toString())).distinct().sorted().toList();

    private final RuleModule rule;

    public TargetEntitiesSectionComponent(RuleModule rule) {
        super(
                "actionregulator.ui.section.targetEntities",
                4,
                () -> rule.invertTargetEntities,
                v -> rule.invertTargetEntities = v,
                rule.targetEntities,
                ALL_ENTITIES,
                "entity",
                rule.targetEntityTypeTags,
                ALL_ENTITY_TAGS,
                "entity_tag"
        );
        this.rule = rule;
    }

    @Override
    protected void buildExtraDropdowns(FlowLayout section) {
        FlowLayout dropdown = buildDropdown(Component.translatable("actionregulator.ui.section.extraOptions"), container -> {
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
