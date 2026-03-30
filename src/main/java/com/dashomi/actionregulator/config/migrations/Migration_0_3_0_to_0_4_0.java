package com.dashomi.actionregulator.config.migrations;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Migration_0_3_0_to_0_4_0 implements ConfigMigration {

    @Override
    public String getFromVersion() {
        return "0.3.0";
    }

    @Override
    public String getTargetVersion() {
        return "0.4.0";
    }

    @Override
    public void migrate(JsonObject json) {
        if (json.has("rules")) {
            for (JsonElement element : json.getAsJsonArray("rules")) {
                if (element.isJsonObject()) {
                    JsonObject rule = element.getAsJsonObject();
                }
            }
        }
        json.addProperty("configVersion", getTargetVersion());
    }
}
