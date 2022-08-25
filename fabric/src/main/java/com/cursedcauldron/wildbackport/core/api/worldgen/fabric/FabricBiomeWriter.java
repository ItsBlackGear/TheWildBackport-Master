package com.cursedcauldron.wildbackport.core.api.worldgen.fabric;

import com.cursedcauldron.wildbackport.core.api.worldgen.BiomeWriter;
import com.cursedcauldron.wildbackport.core.mixin.fabric.access.BiomeAccessor;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FabricBiomeWriter extends BiomeWriter {
    private BiomeSelectionContext selection;
    private BiomeModificationContext modification;

    public BiomeWriter build(BiomeSelectionContext selectionCtx, BiomeModificationContext modificationCtx) {
        this.selection = selectionCtx;
        this.modification = modificationCtx;
        return this;
    }

    @Override
    public ResourceLocation name() {
        return this.selection.getBiomeKey().location();
    }

    @Override
    public Biome.BiomeCategory category() {
        return ((BiomeAccessor)(Object)this.selection.getBiome()).callGetBiomeCategory();
    }

    @Override
    public void addFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
        this.modification.getGenerationSettings().addBuiltInFeature(step, feature.value());
    }

    @Override
    public void addSpawn(MobCategory category, EntityType<?> entityType, int weight, int minGroupSize, int maxGroupSize) {
        this.modification.getSpawnSettings().addSpawn(category, new MobSpawnSettings.SpawnerData(entityType, weight, minGroupSize, maxGroupSize));
    }
}