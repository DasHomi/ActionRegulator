package com.dashomi.actionregulator.config;

import com.dashomi.actionregulator.ActionregulatorClient;
import com.dashomi.actionregulator.config.migrations.ConfigMigration;
import com.dashomi.actionregulator.config.migrations.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ConfigManager {
    private static final String CONFIG_FILE_NAME = "actionregulator.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Path exportRule(RuleModule rule) throws IOException {
        Path exportsDir = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("actionregulator")
                .resolve("exports");
        Files.createDirectories(exportsDir);

        String safeName = rule.name
                .replaceAll("[^a-zA-Z0-9_\\-]", "_")
                .replaceAll("_+", "_");
        if (safeName.isBlank()) safeName = "module";

        Path file = exportsDir.resolve(safeName + ".json");
        int counter = 1;
        while (Files.exists(file)) {
            file = exportsDir.resolve(safeName + "_" + counter + ".json");
            counter++;
        }

        JsonObject envelope = GSON.toJsonTree(rule).getAsJsonObject();
        envelope.addProperty("configVersion", ActionregulatorClient.MOD_VERSION);

        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(envelope, writer);
        }
        return file;
    }

    public static RuleModule importRule(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            JsonObject wrapper = new JsonObject();
            wrapper.add("configVersion", json.has("configVersion")
                    ? json.get("configVersion")
                    : new com.google.gson.JsonPrimitive("0.1.0"));

            com.google.gson.JsonArray rulesArray = new com.google.gson.JsonArray();
            JsonObject ruleJson = json.deepCopy();
            ruleJson.remove("configVersion");
            rulesArray.add(ruleJson);
            wrapper.add("rules", rulesArray);

            applyMigrations(wrapper);

            JsonObject migratedRule = wrapper.getAsJsonArray("rules").get(0).getAsJsonObject();
            return GSON.fromJson(migratedRule, RuleModule.class);
        }
    }

    private static List<ConfigMigration> buildMigrations() {
        List<ConfigMigration> migrations = new ArrayList<>();
        migrations.add(new Migration_0_1_0_to_0_2_0());
        return migrations;
    }

    public static ActionRegulatorConfig load() {
        Path configPath = getConfigPath();

        if (!Files.exists(configPath)) {
            ActionregulatorClient.LOGGER.info("No existing config found. Creating default config.");
            ActionRegulatorConfig defaultConfig = new ActionRegulatorConfig();
            save(defaultConfig);
            return defaultConfig;
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            ActionregulatorClient.LOGGER.info("Config found. Loaded existing config.");

            applyMigrations(json);

            ActionRegulatorConfig config = GSON.fromJson(json, ActionRegulatorConfig.class);

            config.configVersion = ActionregulatorClient.MOD_VERSION;
            save(config);

            return config;

        } catch (IOException e) {
            ActionregulatorClient.LOGGER.error("Failed to load config. Using defaults.", e);
            return new ActionRegulatorConfig();
        }
    }

    public static void save(ActionRegulatorConfig config) {
        config.configVersion = ActionregulatorClient.MOD_VERSION;
        Path configPath = getConfigPath();

        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(config, writer);
            }
            ActionregulatorClient.LOGGER.info("Config saved (version {}).", config.configVersion);
        } catch (IOException e) {
            ActionregulatorClient.LOGGER.error("Failed to save config.", e);
        }
    }

    private static Path getConfigPath() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve(CONFIG_FILE_NAME);
    }

    private static void applyMigrations(JsonObject json) {
        String storedVersion = json.has("configVersion")
                ? json.get("configVersion").getAsString()
                : "0.0.0";

        String currentVersion = ActionregulatorClient.MOD_VERSION;

        if (storedVersion.equals(currentVersion)) {
            return;
        }

        ActionregulatorClient.LOGGER.info(
                "Config version mismatch (stored: {}, mod: {}). Applying migrations…",
                storedVersion, currentVersion);

        List<ConfigMigration> migrations = buildMigrations();

        migrations.sort(Comparator.comparing(ConfigMigration::getFromVersion, ConfigManager::compareVersions));

        for (ConfigMigration migration : migrations) {
            String migratedVersion = json.has("configVersion")
                    ? json.get("configVersion").getAsString()
                    : "0.0.0";

            if (compareVersions(migratedVersion, migration.getFromVersion()) == 0) {
                ActionregulatorClient.LOGGER.info(
                        "Running migration {} → {}.",
                        migration.getFromVersion(), migration.getTargetVersion());
                migration.migrate(json);
            }
        }
    }

    private static int compareVersions(String a, String b) {
        String cleanA = a.split("-")[0];
        String cleanB = b.split("-")[0];

        String[] partsA = cleanA.split("\\.");
        String[] partsB = cleanB.split("\\.");

        int len = Math.max(partsA.length, partsB.length);
        for (int i = 0; i < len; i++) {
            int numA = i < partsA.length ? Integer.parseInt(partsA[i]) : 0;
            int numB = i < partsB.length ? Integer.parseInt(partsB[i]) : 0;
            if (numA != numB) return numA - numB;
        }
        return 0;
    }
}

