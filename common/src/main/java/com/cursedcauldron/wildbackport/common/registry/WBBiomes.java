package com.cursedcauldron.wildbackport.common.registry;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.CoreRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Supplier;

public class WBBiomes {
    public static final CoreRegistry<Biome> BIOMES = CoreRegistry.create(BuiltinRegistries.BIOME, WildBackport.MOD_ID);

    public static final ResourceKey<Biome> MANGROVE_SWAMP   = create("mangrove_swamp", OverworldBiomes::theVoid);
    public static final ResourceKey<Biome> DEEP_DARK        = create("deep_dark", OverworldBiomes::theVoid);

    private static ResourceKey<Biome> create(String key, Supplier<Biome> biome) {
        BIOMES.register(key, biome);
        return ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(WildBackport.MOD_ID, key));
    }
}