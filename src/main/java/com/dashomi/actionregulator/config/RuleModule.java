package com.dashomi.actionregulator.config;

import com.dashomi.actionregulator.enums.NotificationType;
import com.dashomi.actionregulator.enums.TriggerType;
import net.minecraft.references.Blocks;
import net.minecraft.references.Items;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;

public class RuleModule {
    public String name;
    public boolean enabled = true;
    public TriggerType triggerType = TriggerType.ON_BLOCK_BREAK;
    public List<Blocks> targetBlocks = new ArrayList<>();
    public List<Items> handItems = new ArrayList<>();
    public List<DimensionType> activeDimensions = new ArrayList<>();
    public String notificationMessage = "Action regulated!";
    public NotificationType notificationType = NotificationType.OFF;

    public RuleModule() {}
}
