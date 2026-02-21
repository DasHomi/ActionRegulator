package com.dashomi.actionregulator.config;

import com.dashomi.actionregulator.enums.NotificationType;
import com.dashomi.actionregulator.enums.TriggerType;

import java.util.ArrayList;
import java.util.List;

public class RuleModule {
    public String name = "New Module";
    public boolean enabled = true;
    public TriggerType triggerType = TriggerType.ON_BLOCK_BREAK;
    /** ResourceLocation strings, e.g. "minecraft:stone" */
    public List<String> targetBlocks = new ArrayList<>();
    /** ResourceLocation strings, e.g. "minecraft:diamond_sword" */
    public List<String> handItems = new ArrayList<>();
    /** Dimension keys, e.g. "minecraft:overworld" */
    public List<String> activeDimensions = new ArrayList<>();
    public String notificationMessage = "";
    public NotificationType notificationType = NotificationType.OFF;

    public RuleModule() {}
}
