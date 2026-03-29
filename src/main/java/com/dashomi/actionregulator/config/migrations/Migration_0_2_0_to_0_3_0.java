package com.dashomi.actionregulator.config.migrations;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Migration_0_2_0_to_0_3_0 implements ConfigMigration {

    @Override
    public String getFromVersion() {
        return "0.2.0";
    }

    @Override
    public String getTargetVersion() {
        return "0.3.0";
    }

    @Override
    public void migrate(JsonObject json) {
        if (json.has("rules")) {
            for (JsonElement element : json.getAsJsonArray("rules")) {
                if (element.isJsonObject()) {
                    JsonObject rule = element.getAsJsonObject();

                    if (!rule.has("handItemMode") || rule.get("handItemMode").isJsonNull()) {
                        rule.add("handItemMode", new JsonPrimitive("BOTH"));
                    }

                    if (!rule.has("handItemDurabilityThreshold") || rule.get("handItemDurabilityThreshold").isJsonNull()) {
                        rule.add("handItemDurabilityThreshold", new JsonPrimitive(-1));
                    }

                    if (!rule.has("handItemCustomNameMode") || rule.get("handItemCustomNameMode").isJsonNull()) {
                        rule.add("handItemCustomNameMode", new JsonPrimitive("FILTER"));
                    }

                    if (!rule.has("handItemCustomNameFilter") || rule.get("handItemCustomNameFilter").isJsonNull()) {
                        rule.add("handItemCustomNameFilter", new JsonPrimitive(""));
                    }

                    if (!rule.has("targetEntityCustomNameMode") || rule.get("targetEntityCustomNameMode").isJsonNull()) {
                        rule.add("targetEntityCustomNameMode", new JsonPrimitive("FILTER"));
                    }

                    if (!rule.has("targetEntityCustomNameFilter") || rule.get("targetEntityCustomNameFilter").isJsonNull()) {
                        rule.add("targetEntityCustomNameFilter", new JsonPrimitive(""));
                    }
                }
            }
        }
        json.addProperty("configVersion", getTargetVersion());
    }
}
