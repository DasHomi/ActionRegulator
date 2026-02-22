package com.dashomi.actionregulator.config.migrations;

import com.google.gson.JsonObject;

public interface ConfigMigration {

    String getFromVersion();

    String getTargetVersion();

    void migrate(JsonObject json);
}
