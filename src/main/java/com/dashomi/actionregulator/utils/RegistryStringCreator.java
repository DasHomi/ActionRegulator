package com.dashomi.actionregulator.utils;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RegistryStringCreator {
    public static String getDimensionId(Level world) {
        return world.dimension().identifier().toString();
    }

    public static String getItemId(Player player, InteractionHand hand) {
        ItemStack handItem = player.getItemInHand(hand);
        return BuiltInRegistries.ITEM.getKey(handItem.getItem()).toString();
    }

    public static String getBlockId(Level world, BlockPos blockPos) {
        return getBlockId(world.getBlockState(blockPos));
    }

    public static String getBlockId(BlockState blockState) {
        return BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
    }

    public static String getEntityId(Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
    }

    public static List<String> getAllEnchantmentIds() {
        return lookupEnchantments()
                .map(registry -> registry.keySet().stream().map(Object::toString).sorted().toList())
                .orElseGet(List::of);
    }

    private static Optional<Registry<Enchantment>> lookupEnchantments() {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return Optional.empty();
        }
        return level.registryAccess().lookup(Registries.ENCHANTMENT);
    }
}

