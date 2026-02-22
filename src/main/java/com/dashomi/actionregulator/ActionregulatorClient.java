package com.dashomi.actionregulator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ActionregulatorClient implements ClientModInitializer {
    public static final String MOD_ID = "actionregulator";
    public static final String MOD_VERSION = "0.1.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
    }
}
