package com.dashomi.actionregulator.config;

import com.dashomi.actionregulator.enums.*;

import java.util.ArrayList;
import java.util.List;

public class RuleModule {
    public String name = "New Module";
    public boolean enabled = true;
    public boolean expanded = true;
    public TriggerType triggerType = TriggerType.ON_ATTACK;
    public TargetMode targetMode = TargetMode.BLOCKS;

    // target block
    public List<String> targetBlocks = new ArrayList<>();
    public List<String> targetBlockTags = new ArrayList<>();
    public boolean invertTargetBlocks = false;
    public BlockConditionMode waterloggedCondition = BlockConditionMode.IGNORED;

    // hand item
    public List<String> handItems = new ArrayList<>();
    public List<String> handItemTags = new ArrayList<>();
    public boolean invertHandItems = false;
    public HandItemMode handItemMode = HandItemMode.BOTH;
    public int handItemDurabilityThreshold = -1;
    public HandItemDurabilityMode handItemDurabilityMode = HandItemDurabilityMode.IGNORED;
    public CustomNameMode handItemCustomNameMode = CustomNameMode.FILTER;
    public String handItemCustomNameFilter = "";

    // target entity
    public List<String> targetEntities = new ArrayList<>();
    public List<String> targetEntityTypeTags = new ArrayList<>();
    public boolean invertTargetEntities = false;
    public CustomNameMode targetEntityCustomNameMode = CustomNameMode.FILTER;
    public String targetEntityCustomNameFilter = "";

    // player conditions
    public PlayerConditionMode elytraFlyingCondition = PlayerConditionMode.IGNORED;
    public PlayerConditionMode swimmingCondition = PlayerConditionMode.IGNORED;
    public List<String> activeGameModes = new ArrayList<>(List.of("SURVIVAL", "CREATIVE", "ADVENTURE")); // set of enabled modes; empty = none
    public ThresholdMode healthConditionMode = ThresholdMode.IGNORED;
    public float healthThreshold = -1; // hearts (0-10); -1 = unset
    public ThresholdMode hungerConditionMode = ThresholdMode.IGNORED;
    public int hungerThreshold = -1; // food level (0-20); -1 = unset

    public List<String> activeDimensions = new ArrayList<>(List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"));
    public String notificationMessage = "";
    public NotificationType notificationType = NotificationType.OFF;

    public RuleModule() {}
}
