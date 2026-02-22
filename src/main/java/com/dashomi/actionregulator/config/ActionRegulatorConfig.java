package com.dashomi.actionregulator.config;

import java.util.ArrayList;
import java.util.List;

public class ActionRegulatorConfig {
    private static ActionRegulatorConfig INSTANCE;

    public List<RuleModule> rules = new ArrayList<>();
    public String configVersion = "0.0.0";

    public ActionRegulatorConfig() {}

    public static ActionRegulatorConfig getOrCreate() {
        if (INSTANCE == null) {
            INSTANCE = new ActionRegulatorConfig();
        }
        return INSTANCE;
    }
}
