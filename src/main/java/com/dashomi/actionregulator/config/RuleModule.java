package com.dashomi.actionregulator.config;

import com.dashomi.actionregulator.enums.NotificationType;
import com.dashomi.actionregulator.enums.TargetMode;
import com.dashomi.actionregulator.enums.TriggerType;
import java.util.ArrayList;
import java.util.List;

public class RuleModule {
    public String name = "New Module";
    public boolean enabled = true;
    public boolean expanded = true;
    public TriggerType triggerType = TriggerType.ON_ATTACK;
    public TargetMode targetMode = TargetMode.BLOCKS;
    public List<String> targetBlocks = new ArrayList<>();
    public boolean invertTargetBlocks = false;
    public List<String> handItems = new ArrayList<>();
    public boolean invertHandItems = false;
    public int handItemsMinDurability = -1;
    public List<String> targetEntities = new ArrayList<>();
    public boolean invertTargetEntities = false;
    public List<String> activeDimensions = new ArrayList<>(List.of("minecraft:overworld", "minecraft:the_nether", "minecraft:the_end"));
    public String notificationMessage = "";
    public NotificationType notificationType = NotificationType.OFF;

    public RuleModule() {}
}
