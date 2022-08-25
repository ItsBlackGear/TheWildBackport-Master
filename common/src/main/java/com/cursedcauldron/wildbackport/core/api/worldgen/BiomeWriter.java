package com.cursedcauldron.wildbackport.core.api.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Consumer;

//<>

public abstract class BiomeWriter {
    @SafeVarargs
    public final void add(Consumer<BiomeWriter> writer, ResourceKey<Biome>... biomes) {
        for (ResourceKey<Biome> biome : biomes) if (this.is(biome)) writer.accept(this);
    }

    public final void add(Consumer<BiomeWriter> writer, Biome.BiomeCategory... categories) {
        for (Biome.BiomeCategory category : categories) if (this.is(category)) writer.accept(this);
    }

    public boolean is(ResourceKey<Biome> biome) {
        return biome == ResourceKey.create(Registry.BIOME_REGISTRY, this.name());
    }

    public boolean is(Biome.BiomeCategory category) {
        return category == this.category();
    }

    public abstract ResourceLocation name();

    public abstract Biome.BiomeCategory category();

    public abstract void addFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature);

    public abstract void addSpawn(MobCategory category, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize);
}