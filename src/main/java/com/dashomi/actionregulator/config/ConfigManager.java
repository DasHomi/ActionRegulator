package com.dashomi.actionregulator.config;

import com.dashomi.actionregulator.ActionregulatorClient;
import com.dashomi.actionregulator.config.migrations.ConfigMigration;
import com.dashomi.actionregulator.config.migrations.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
    private static final List<ConfigMigration> MIGRATIONS = List.of(
            new Migration_0_1_0_to_0_2_0(),
            new Migration_0_2_0_to_0_3_0(),
            new Migration_0_3_0_to_0_4_0()
    );

    public static Path exportRule(RuleModule rule) throws IOException {
        String safeName = rule.name
                .replaceAll("[^a-zA-Z0-9_\\-]", "_")
                .replaceAll("_+", "_");
        if (safeName.isBlank()) safeName = "module";

        return saveExport(safeName, List.of(rule));
    }

    public static Path exportAllRules(List<RuleModule> rules) throws IOException {
        return saveExport("all_rules", rules);
    }

    private static Path saveExport(String baseName, List<RuleModule> rules) throws IOException {
        Path exportsDir = FabricLoader.getInstance()
                .getConfigDir()
                .resolve("actionregulator")
                .resolve("exports");
        Files.createDirectories(exportsDir);

        Path file = exportsDir.resolve(baseName + ".json");
        int counter = 1;
        while (Files.exists(file)) {
            file = exportsDir.resolve(baseName + "_" + counter + ".json");
            counter++;
        }

        JsonObject envelope = buildExportEnvelope(rules);

        try (Writer writer = Files.newBufferedWriter(file)) {
            GSON.toJson(envelope, writer);
        }
        return file;
    }

    public static List<RuleModule> importRules(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            JsonObject wrapper = normalizeImportJson(parsed);

            applyMigrations(wrapper);

            List<RuleModule> importedRules = new ArrayList<>();
            JsonArray migratedRules = wrapper.getAsJsonArray("rules");
            for (JsonElement ruleElement : migratedRules) {
                RuleModule imported = GSON.fromJson(ruleElement, RuleModule.class);
                normalizeRuleLists(imported);
                importedRules.add(imported);
            }
            return importedRules;
        }
    }

    private static JsonObject normalizeImportJson(JsonElement parsed) throws IOException {
        if (parsed == null || parsed.isJsonNull() || !parsed.isJsonObject()) {
            throw new IOException("Unsupported import JSON format: expected object with configVersion and rules[]");
        }

        JsonObject wrapper = parsed.getAsJsonObject().deepCopy();

        if (!wrapper.has("configVersion") || !wrapper.get("configVersion").isJsonPrimitive() || !wrapper.get("configVersion").getAsJsonPrimitive().isString()) {
            throw new IOException("Import JSON must contain a string configVersion");
        }

        if (!wrapper.has("rules") || !wrapper.get("rules").isJsonArray()) {
            throw new IOException("Import JSON must contain rules[]");
        }

        JsonArray rulesArray = wrapper.getAsJsonArray("rules");
        if (rulesArray.isEmpty()) {
            throw new IOException("No rule modules found in import file");
        }

        for (JsonElement ruleElement : rulesArray) {
            if (!ruleElement.isJsonObject()) {
                throw new IOException("Import JSON rules[] entries must be objects");
            }
        }

        return wrapper;
    }

    private static JsonObject buildExportRuleJson(RuleModule rule) {
        JsonObject exportRule = GSON.toJsonTree(rule).getAsJsonObject();
        exportRule.addProperty("enabled", true);
        exportRule.addProperty("expanded", false);
        return exportRule;
    }

    private static JsonObject buildExportEnvelope(List<RuleModule> rules) {
        JsonObject envelope = new JsonObject();
        envelope.addProperty("configVersion", ActionregulatorClient.MOD_VERSION);
        JsonArray exportedRules = new JsonArray();
        for (RuleModule rule : rules) {
            exportedRules.add(buildExportRuleJson(rule));
        }
        envelope.add("rules", exportedRules);
        return envelope;
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
            if (config.rules != null) {
                for (RuleModule rule : config.rules) {
                    normalizeRuleLists(rule);
                }
            }

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
        if (config.rules == null) {
            config.rules = new ArrayList<>();
        } else {
            for (RuleModule rule : config.rules) {
                normalizeRuleLists(rule);
            }
        }
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

    public static void revealFile(Path file) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("explorer.exe", "/select,", file.toAbsolutePath().toString()).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", "-R", file.toAbsolutePath().toString()).start();
            } else {
                Path parent = file.getParent();
                if (parent != null) {
                    new ProcessBuilder("xdg-open", parent.toAbsolutePath().toString()).start();
                }
            }
        } catch (IOException ignored) {}
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

        List<ConfigMigration> sortedMigrations = new ArrayList<>(MIGRATIONS);
        sortedMigrations.sort(Comparator.comparing(ConfigMigration::getFromVersion, ConfigManager::compareVersions));

        for (ConfigMigration migration : sortedMigrations) {
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

    private static void normalizeRuleLists(RuleModule rule) {
        if (rule == null) {
            return;
        }

        if (rule.targetBlocks == null) rule.targetBlocks = new ArrayList<>();
        if (rule.handItems == null) rule.handItems = new ArrayList<>();
        if (rule.targetEntities == null) rule.targetEntities = new ArrayList<>();

        if (rule.targetBlockTags == null) rule.targetBlockTags = new ArrayList<>();
        if (rule.handItemTags == null) rule.handItemTags = new ArrayList<>();
        if (rule.targetEntityTypeTags == null) rule.targetEntityTypeTags = new ArrayList<>();
    }
}
