package com.cursedcauldron.wildbackport.common.tag;

import com.cursedcauldron.wildbackport.WildBackport;
import com.cursedcauldron.wildbackport.core.api.TagRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class WBBiomeTags {
    public static final TagRegistry<Biome> TAGS = TagRegistry.create(BuiltinRegistries.BIOME, WildBackport.MOD_ID);

    public static final TagKey<Biome> SPAWNS_WARM_VARIANT_FROGS = TAGS.create("spawns_warm_variant_frogs");
    public static final TagKey<Biome> SPAWNS_COLD_VARIANT_FROGS = TAGS.create("spawns_cold_variant_frogs");
}