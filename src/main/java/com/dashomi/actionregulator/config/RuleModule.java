package com.dashomi.actionregulator.config;

import com.dashomi.actionregulator.enums.NotificationType;
import com.dashomi.actionregulator.enums.TriggerType;

import java.util.ArrayList;
import java.util.List;

public class RuleModule {
    public String name = "New Module";
    public boolean enabled = true;
    public TriggerType triggerType = TriggerType.ON_BLOCK_BREAK;
    public List<String> targetBlocks = new ArrayList<>();
    public List<String> handItems = new ArrayList<>();
    public List<String> activeDimensions = new ArrayList<>();
    public String notificationMessage = "";
    public NotificationType notificationType = NotificationType.OFF;

    public RuleModule() {}
}
