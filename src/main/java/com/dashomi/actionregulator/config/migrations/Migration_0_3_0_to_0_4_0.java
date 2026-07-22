package com.dashomi.actionregulator.config.migrations;

import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
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

                    ensureTagList(rule, "targetBlockTags");
                    ensureTagList(rule, "handItemTags");
                    ensureTagList(rule, "targetEntityTypeTags");

                    if (!rule.has("handItemDurabilityMode") || rule.get("handItemDurabilityMode").isJsonNull()) {
                        rule.add("handItemDurabilityMode", new JsonPrimitive("IGNORED"));
                    }

                    ensureIgnored(rule, "waterloggedCondition");

                    ensureIgnored(rule, "elytraFlyingCondition");
                    ensureIgnored(rule, "swimmingCondition");

                    ensureIgnored(rule, "healthConditionMode");
                    ensureIgnored(rule, "hungerConditionMode");
                    ensureThreshold(rule, "healthThreshold");
                    ensureThreshold(rule, "hungerThreshold");

                    ensureDefaultGameModes(rule);
                }
            }
        }
        json.addProperty("configVersion", getTargetVersion());
    }

    private static void ensureIgnored(JsonObject rule, String fieldName) {
        if (!rule.has(fieldName) || rule.get(fieldName).isJsonNull()) {
            rule.add(fieldName, new JsonPrimitive("IGNORED"));
        }
    }

    private static void ensureThreshold(JsonObject rule, String fieldName) {
        if (!rule.has(fieldName) || rule.get(fieldName).isJsonNull()) {
            rule.add(fieldName, new JsonPrimitive(-1));
        }
    }

    private static void ensureDefaultGameModes(JsonObject rule) {
        if (!rule.has("activeGameModes") || !rule.get("activeGameModes").isJsonArray()) {
            JsonArray gameModes = new JsonArray();
            gameModes.add("SURVIVAL");
            gameModes.add("CREATIVE");
            gameModes.add("ADVENTURE");
            rule.add("activeGameModes", gameModes);
        }
    }

    private static void ensureTagList(JsonObject rule, String fieldName) {
        if (!rule.has(fieldName) || !rule.get(fieldName).isJsonArray()) {
            rule.add(fieldName, new JsonArray());
        }
    }
}
