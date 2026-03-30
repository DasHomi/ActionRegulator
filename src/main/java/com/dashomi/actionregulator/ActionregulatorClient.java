package com.dashomi.actionregulator;

import com.dashomi.actionregulator.config.ActionRegulatorConfig;
import com.dashomi.actionregulator.listeners.*;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.*;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ActionregulatorClient implements ClientModInitializer {
    public static final String MOD_ID = "actionregulator";
    public static final String MOD_VERSION = "0.4.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyMapping temporaryBypassKey;

    public static boolean isTemporaryOverrideActive() {
        return temporaryBypassKey != null && temporaryBypassKey.isDown();
    }

    @Override
    public void onInitializeClient() {
        ActionRegulatorConfig.get();

        KeyMapping.Category ACTION_REGULATOR_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "category"));
        temporaryBypassKey = new KeyMapping("key.actionregulator.temporary_override", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, ACTION_REGULATOR_CATEGORY);
        KeyMappingHelper.registerKeyMapping(temporaryBypassKey);

        AttackBlockCallback.EVENT.register(AttackBlockEvent::attackBlockListener);
        AttackEntityCallback.EVENT.register(AttackEntityEvent::attackEntityListener);
        UseEntityCallback.EVENT.register(UseEntityEvent::useEntityListener);
        UseItemCallback.EVENT.register(UseItemEvent::useItemListener);
        UseBlockCallback.EVENT.register(UseBlockEvent::useBlockListener);
        ClientTickEvents.END_CLIENT_TICK.register(EndClientTickEvent::endClientTickListener);
    }
}
