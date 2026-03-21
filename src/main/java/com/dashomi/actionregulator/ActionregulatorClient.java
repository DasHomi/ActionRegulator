package com.dashomi.actionregulator;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.listeners.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.player.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ActionregulatorClient implements ClientModInitializer {
    public static final String MOD_ID = "actionregulator";
    public static final String MOD_VERSION = "0.3.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ActionRegulatorConfig.get();

        AttackBlockCallback.EVENT.register(AttackBlockEvent::attackBlockListener);
        AttackEntityCallback.EVENT.register(AttackEntityEvent::attackEntityListener);
        UseEntityCallback.EVENT.register(UseEntityEvent::useEntityListener);
        UseItemCallback.EVENT.register(UseItemEvent::useItemListener);
        UseBlockCallback.EVENT.register(UseBlockEvent::useBlockListener);
    }
}
