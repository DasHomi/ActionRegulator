package com.dashomi.actionregulator.config.migrations;

import com.google.gson.JsonObject;

public class Migration_0_0_0_to_0_1_0 implements ConfigMigration {

    @Override
    public String getFromVersion() {
        return "0.0.0";
    }

    @Override
    public String getTargetVersion() {
        return "0.1.0";
    }

    @Override
    public void migrate(JsonObject json) {
        if (!json.has("rules")) {
            json.add("rules", new com.google.gson.JsonArray());
        }

        json.addProperty("configVersion", getTargetVersion());
    }
}

