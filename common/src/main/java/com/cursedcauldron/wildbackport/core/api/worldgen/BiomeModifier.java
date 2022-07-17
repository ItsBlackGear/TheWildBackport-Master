package com.cursedcauldron.wildbackport.core.api.worldgen;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class BiomeModifier {
    private static final Map<Consumer<BiomeWriter>, ResourceKey<Biome>[]> FEATURES = new ConcurrentHashMap<>();
    public static final BiomeModifier INSTANCE = new BiomeModifier();

    @ExpectPlatform
    public static void setup() {
        throw new AssertionError();
    }

    public void register(BiomeWriter writer) {
        FEATURES.forEach(writer::add);
    }

    @SafeVarargs
    public static void add(Consumer<BiomeWriter> writer, ResourceKey<Biome>... biomes) {
        FEATURES.put(writer, biomes);
    }
}