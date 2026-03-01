package com.dashomi.actionregulator.config.migrations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Migration_0_1_0_to_0_2_0 implements ConfigMigration {

    @Override
    public String getFromVersion() {
        return "0.1.0";
    }

    @Override
    public String getTargetVersion() {
        return "0.2.0";
    }

    @Override
    public void migrate(JsonObject json) {
        if (json.has("rules")) {
            for (JsonElement element : json.getAsJsonArray("rules")) {
                if (element.isJsonObject()) {
                    JsonObject rule = element.getAsJsonObject();
                    if (!rule.has("expanded")) {
                        rule.add("expanded", new JsonPrimitive(true));
                    }

                    if (!rule.has("targetEntities")) {
                        rule.add("targetEntities", new JsonArray());
                    }

                    if (!rule.has("invertTargetBlocks")) {
                        rule.add("invertTargetBlocks", new JsonPrimitive(false));
                    }

                    if (!rule.has("invertHandItems")) {
                        rule.add("invertHandItems", new JsonPrimitive(false));
                    }

                    if (!rule.has("invertTargetEntities")) {
                        rule.add("invertTargetEntities", new JsonPrimitive(false));
                    }

                    if (!rule.has("targetMode")) {
                        boolean hasEntities = rule.has("targetEntities")
                                && !rule.getAsJsonArray("targetEntities").isEmpty();
                        rule.add("targetMode", new JsonPrimitive(hasEntities ? "ENTITIES" : "BLOCKS"));
                    }
                }
            }
        }
        json.addProperty("configVersion", getTargetVersion());
    }
}

